package com.silpe.vire.slip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silpe.vire.slip.dtos.User;
import com.silpe.vire.slip.dtos.Validator;
import com.silpe.vire.slip.models.SessionModel;

public class RegistrationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private AutoCompleteTextView mEmailView;
    private TextInputEditText mPasswordView;
    private TextInputEditText mRePassView;
    private View mProgressView;
    private TextView mTextView;
    private boolean mTaskInProgress = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Obtain the email input field and attempt autocompletion
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        // Obtain the password input field and bind the action listener
        mPasswordView = (TextInputEditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new RegistrationActivity.PasswordSubmitListener());

        mRePassView = (TextInputEditText) findViewById(R.id.repass);
        mRePassView.setOnEditorActionListener(new RegistrationActivity.PasswordSubmitListener());

        // Bind the attempt login listener to the login button
        Button signInButton = (Button) findViewById(R.id.login_button);
        signInButton.setOnClickListener(new RegistrationActivity.LoginButtonListener());

        // Obtain a reference to the loading indicator
        mProgressView = findViewById(R.id.register_progress);

        // Obtain the Firebase Authentication listeners and create the listener
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new UserStateListener();

    }
    private class UserStateListener implements FirebaseAuth.AuthStateListener {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                ref = ref.child(getString(R.string.database_users)).child(user.getUid());
                ref.addListenerForSingleValueEvent(new RegistrationActivity.UserValueListener(RegistrationActivity.this));
            }
            /*
             * TODO
             * -- Handle edge cases where
             *    -> The user may be logged in while in this screen
             *    -> The user may become logged out in this screen
             */
        }
    }

    //This can be modularized
    private class PasswordSubmitListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
            final boolean action =
                    (id == R.id.login || id == EditorInfo.IME_NULL);
            if (action)
                attemptLogin();
            return action;
        }
    }


    private class LoginButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            attemptLogin();
        }
    }

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

    // TODO CLEAN UP CODE
    private void attemptLogin() {
        if (mTaskInProgress) {
            return;
        }
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mRePassView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String repass =  mRePassView.getText().toString();
        Log.d ("YOLO", password + ":" +repass);

        boolean cancel = false;
        View focusView = null;

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

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
            Log.d ("YOLO", "pasEmpt");
        } else if (!Validator.isValidPassword(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
            Log.d ("YOLO", "NotValid");
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(repass)) {
            mRePassView.setError(getString(R.string.error_field_required));
            focusView = mRePassView;
            cancel = true;
            Log.d ("YOLO", "NoRepas");
        } else if (!password.equals(repass)) {
            mRePassView.setError(getString(R.string.error_notsame_password));
            focusView = mRePassView;
            cancel = true;
            Log.d ("YOLO", "NotMatch");
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mTaskInProgress = true;
            doRegister(email, password);
        }
    }

    boolean isSuccessful = true;
    boolean doRegister(final String email, final String password) {
        isSuccessful = true;
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                Log.d(getClass().getCanonicalName(), "createUserWithEmail:onComplete:" + task.isSuccessful());
                if (!task.isSuccessful()) {
                    Toast.makeText(RegistrationActivity.this, R.string.register_failed, Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                    isSuccessful = false;
                }

            }
        });
        return isSuccessful;
    }
    class UserValueListener implements ValueEventListener {

        private final RegistrationActivity context;

        UserValueListener(RegistrationActivity context) {
            this.context = context;
        }

        @Override
        public void onDataChange(DataSnapshot snapshot) {
            User user = snapshot.getValue(User.class);
            if (user == null) {
                Intent intent = new Intent(context, RegisterManually.class);
                context.showProgress(false);
                context.startActivity(intent);
            } else {
                //doesnt exist?
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
            *   TODO
            *   -- Handle an error in which the database request
            *   for user information is disconnected
            *   -> Indicate the user a connection issue
            *   -> Prompt him to log in again
            */
            context.showProgress(false);
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(context, R.string.login_error_retrievalFailure, Toast.LENGTH_SHORT).show();
        }
    }
}
