package com.silpe.vire.slip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silpe.vire.slip.dtos.User;
import com.silpe.vire.slip.dtos.Validator;
import com.silpe.vire.slip.models.SessionModel;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email and password. The user
 * can navigate to the register screen from here or login and enter the
 * {@code MainActivity}.
 * <p>
 * The login screen will provide the following behaviour. It will indicate
 * to the user any errors associated with the input values on the input
 * fields. Upon successful login, direct the user to the {@code MainActivity}.
 * Should the user desire an account, direct him to the {@code RegisterActivity}.
 * <p>
 * {@code LoginActivity} implements {@code LoaderCallbacks<Cursor>} in order
 * to provide autocomplete options for the user's email.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * ID to identify the request made to access contacts.
     * Access to contacts allows the app to prefill the email field.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * The email input text field.
     */
    private AutoCompleteTextView mEmailView;
    /**
     * The password input text field.
     */
    private EditText mPasswordView;
    /**
     * The overlay view containing a darkened background and
     * the progress indicator.
     */
    private View mProgressView;

    /**
     * A flag indicating whether the user is currently
     * trying to log in.
     */
    private boolean mTaskInProgress = false;

    /**
     * Firebase Authentication instance.
     */
    private FirebaseAuth mAuth;
    /**
     * An authentication state listener that fires
     * when the user's login status changes.
     */
    private FirebaseAuth.AuthStateListener mAuthListener;

    /**
     * Create the login screen layout. Bind listeners to the login and register
     * buttons. Bind a listener to the {@code FirebaseAuth} instance that
     * directs the user to the {@code MainActivity} upon successful login.
     *
     * @param savedInstanceState the previous activity state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Obtain the email input field and attempt autocompletion
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        // Obtain the password input field and bind the action listener
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new PasswordSubmitListener());

        // Bind the attempt login listener to the login button
        Button signInButton = (Button) findViewById(R.id.email_sign_in_button);
        signInButton.setOnClickListener(new LoginButtonListener());

        // Obtain a reference to the loading indicator
        mProgressView = findViewById(R.id.login_progress);

        // Obtain the Firebase Authentication listeners and create the listener
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new UserStateListener();
    }

    /**
     * When this activity starts, bind the user state listener.
     */
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    /**
     * When this activity has stopped, remove the user state listener.
     */
    @Override
    public void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    /**
     * This method will attempt to fill the autocomplete
     * array with email options, if permission is obtained
     * to access the user's contacts.
     */
    private void populateAutoComplete() {
        if (!mayRequestContacts()) return;
        getLoaderManager().initLoader(0, null, this);
    }

    /**
     * This method will return true if the app currently
     * has permission to access the user's contacts. Otherwise
     * it will attempt to obtain that permission and fire
     * an event upon success or failure.
     *
     * @return true if permission has been granted
     */
    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) return true;
        if (shouldShowRequestPermissionRationale(READ_CONTACTS))
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new RequestPermissionListener());
        else requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        return false;
    }

    /**
     * If permission has been granted to access contacts, retrieve
     * the email list and populate the autocomplete array.
     *
     * @param requestCode  the request type dispatched
     * @param permissions  an array of permissions
     * @param grantResults an array containing the granted permissions
     */
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS
                && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            populateAutoComplete();
        }

    }

    /**
     * This event is fired when the app initializes the email autocomplete
     * options loader. This will return a loader that will retrieve
     * the email addresses stored in the user's contacts.
     *
     * @param i      loader number
     * @param bundle loader options
     * @return a loader that retrieves the emails
     */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY),
                new String[]{ContactsContract.CommonDataKinds.Email.ADDRESS,
                        ContactsContract.CommonDataKinds.Email.IS_PRIMARY},
                ContactsContract.Contacts.Data.MIMETYPE + " = ?",
                new String[]{ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE},
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    /**
     * Upon finished loading, populate the autocomplete list
     * with the retrieved emails.
     *
     * @param cursorLoader the loader
     * @param cursor       the currency cursor
     */
    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(0));
            cursor.moveToNext();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(LoginActivity.this,
                android.R.layout.simple_dropdown_item_1line, emails);
        mEmailView.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }




    /**
     * Pause any ongoing asynchronous tasks when the activity
     * is on pause.
     */
    @Override
    public void onPause() {
        super.onPause();
    }


    // TODO CLEAN UP CODE
    private void attemptLogin() {
        if (mTaskInProgress) {
            return;
        }
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password)) {
            if (!Validator.isValidPassword(password)) {
                mPasswordView.setError(getString(R.string.error_invalid_password));
                focusView = mPasswordView;
                cancel = true;
            }
        } else {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!Validator.isValidEmail(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mTaskInProgress = true;
            doAttempt(email, password);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(R.integer.shortAnimationTime);
        if (show) {
            mProgressView.setVisibility(View.VISIBLE);
        }
        mProgressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (!show) {
                            mProgressView.setVisibility(View.GONE);
                        }
                    }
                });
    }

    // TODO Remove this and add a register button
    private void doAttempt(final String email, final String password) {
        Task<ProviderQueryResult> fetchEmailTask = mAuth.fetchProvidersForEmail(email).addOnCompleteListener(this, new OnCompleteListener<ProviderQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                LoginActivity.this.mTaskInProgress = false;
                if (task.isSuccessful() && task.getResult().getProviders() != null) {
                    if (task.getResult().getProviders().size() > 0) {
                        doLogin(email, password);
                    } else {
                        doRegister(email, password);
                    }
                } else {
                    // TODO Error handling
                    LoginActivity.this.showProgress(false);
                }
            }
        });
    }
    //lol, debugging purpose:
    boolean isSuccess;
    boolean doRegister(final String email, final String password) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                LoginActivity.this.mTaskInProgress = false;
                Log.d(getClass().getCanonicalName(), "createUserWithEmail:onComplete:" + task.isSuccessful());
                isSuccess = true;
                if (!task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, R.string.register_failed, Toast.LENGTH_SHORT).show();
                    LoginActivity.this.showProgress(false);
                    isSuccess = false;
                }

            }
        });
        return isSuccess;
    }

    private void doLogin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                LoginActivity.this.mTaskInProgress = false;
                Log.d(getClass().getCanonicalName(), "signInWithEmail:onComplete:" + task.isSuccessful());
                if (!task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, R.string.auth_failed, Toast.LENGTH_SHORT).show();
                    LoginActivity.this.showProgress(false);
                } else {
                    // gotoMainActivity();
                }
            }
        });
    }

    private void gotoMainActivity() {
        // Get the user from the database
        FirebaseUser user = mAuth.getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        if (user != null) {
            ref = ref.child(getString(R.string.database_users)).child(user.getUid());
            ValueEventListener userListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    SessionModel.get().setUser(user, LoginActivity.this);
                    Toast.makeText(LoginActivity.this, R.string.auth_success, Toast.LENGTH_SHORT).show();
                    LoginActivity.this.showProgress(false);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    LoginActivity.this.startActivity(intent);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            };
            ref.addListenerForSingleValueEvent(userListener);
        }
    }
    // END TODO

    /**
     * This listener will attempt to log in the user
     * when he submits the password field.
     */
    private class PasswordSubmitListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
            final boolean action = id == R.id.login || id == EditorInfo.IME_NULL;
            if (action) attemptLogin();
            return action;
        }
    }

    /**
     * This listener will attempt to log in the user
     * when its onClick event is fired.
     */
    private class LoginButtonListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            attemptLogin();
        }
    }

    /**
     * This listener will fire when the user acknowledges the
     * motivation behind requesting contact access. Note that
     * we require API Version 23, but this listener should
     * never be invoked if the API is below Version 23.
     */
    private class RequestPermissionListener implements View.OnClickListener {
        @Override
        @TargetApi(Build.VERSION_CODES.M)
        public void onClick(View v) {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
    }

    /**
     * This listener retrieve the user's information from
     * the databsae, store it in the session object, and
     * direct the user to the {@code MainActivity}.
     */
    private class UserStateListener implements FirebaseAuth.AuthStateListener {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                ref = ref.child(getString(R.string.database_users)).child(user.getUid());
                ref.addListenerForSingleValueEvent(new UserValueListener(LoginActivity.this));
            }
            /*
             * TODO
             * -- Handle edge cases where
             *    -> The user may be logged in while in this screen
             *    -> The user may become logged out in this screen
             */
        }
    }


    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            this.finishAffinity();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

}

/**
 * This listener will acquire the user information and
 * direct the to the {@code MainActivity}, after setting
 * the user session.
 */
class UserValueListener implements ValueEventListener {

    private final LoginActivity context;

    UserValueListener(LoginActivity context) {
        this.context = context;
    }

    @Override
    public void onDataChange(DataSnapshot snapshot) {
        User user = snapshot.getValue(User.class);
        if (user == null) {
            Intent intent = new Intent(context, RegisterActivity.class);
            context.showProgress(false);
            context.startActivity(intent);
        } else {
            SessionModel.get().setUser(user, context);
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.showProgress(false);
            context.startActivity(intent);
        }
    }

    @Override
    public void onCancelled(DatabaseError error) {
        Log.d(getClass().getCanonicalName(), error.toString());
        /*
         * TODO
         * -- Handle an error in which the database request
         *    for user information is disconnected
         *    -> Indicate the user a connection issue
         *    -> Prompt him to log in again
         */
        context.showProgress(false);
    }

}