package com.silpe.vire.slip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.silpe.vire.slip.components.DoubleBackHandler;
import com.silpe.vire.slip.dtos.Validator;

public class Registration extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mRePassView;
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
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new Registration.PasswordSubmitListener());

        mRePassView = (EditText) findViewById(R.id.password);
        mRePassView.setOnEditorActionListener(new Registration.PasswordSubmitListener());

        // Bind the attempt login listener to the login button
        Button signInButton = (Button) findViewById(R.id.email_sign_in_button);
        signInButton.setOnClickListener(new Registration.LoginButtonListener());

        // Obtain a reference to the loading indicator
        mProgressView = findViewById(R.id.login_progress);

        // Obtain the Firebase Authentication listeners and create the listener
        mAuth = FirebaseAuth.getInstance();






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
        String repass =  mPasswordView.getText().toString();

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
        } else if (!Validator.isValidPassword(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(repass)) {
            mRePassView.setError(getString(R.string.error_field_required));
            focusView = mRePassView;
            cancel = true;
        } else if (password != repass) {
            mRePassView.setError(getString(R.string.error_notsame_password));
            focusView = mRePassView;
            cancel = true;
        }



        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mTaskInProgress = true;
            doRegister(email, password);
        }
    }


    //This can be modularized
    private class PasswordSubmitListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
            final boolean action = id == R.id.login || id == EditorInfo.IME_NULL;
            if (action) attemptLogin();
            return action;
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
                    Toast.makeText(Registration.this, R.string.register_failed, Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                    isSuccessful = false;
                }
            }
        });
        mTaskInProgress = false;
        return isSuccessful;
    }
}
