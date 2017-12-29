package com.walkap.x_android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.walkap.x_android.R;
import com.walkap.x_android.dao.userDao.UserDao;
import com.walkap.x_android.dao.userDao.UserDaoImpl;
import com.walkap.x_android.model.user.User;
import com.walkap.x_android.model.user.concreteBuilder.UserBuilder;
import com.walkap.x_android.model.user.concreteBuilder.UserDirector;

public class SignInActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    private EditText mEmailField;
    private EditText mPasswordField;
    private Button mSignInButton;
    private Button mSignUpButton;
    private SignInButton mSignInButtonGoogle;
    private TextView mForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        // Views
        mEmailField = (EditText) findViewById(R.id.eT_email);
        mPasswordField = (EditText) findViewById(R.id.eT_password);
        mSignInButton = (Button) findViewById(R.id.sign_in_button_email);
        mSignUpButton = (Button) findViewById(R.id.sign_up_button_email);
        mForgotPassword = (TextView) findViewById(R.id.forgot_password);
        // Click listeners
        mSignInButton.setOnClickListener(this);
        mSignUpButton.setOnClickListener(this);
        mForgotPassword.setOnClickListener(this);
        //Google Sign in
        mSignInButtonGoogle = (SignInButton) findViewById(R.id.sign_in_button);
        mSignInButtonGoogle.setOnClickListener(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check auth on Activity start
        if (mFirebaseAuth.getCurrentUser() != null) {
            onAuthSuccess(mFirebaseAuth.getCurrentUser());
        }
    }

    /**
     * This  method is called when the goole button
     * is pushed
     */
    private void signInGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * This method get the activity result and invoke
     * firebaseAuthWithGoogle method id successful
     *
     * @param requestCode - int
     * @param resultCode  - int
     * @param data        - Intent
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign-In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign-In failed
                Log.e(TAG, "Google Sign-In failed.");
            }
        }
    }

    /**
     * This method is used to perform the google SignIn
     *
     * @param account - GoogleSignInAccount
     */
    private void firebaseAuthWithGoogle(final GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGooogle:" + account.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            UserDirector director = new UserDirector();
                            UserBuilder builder = director.buildUser(account.getEmail(), account.getGivenName(), "Student");
                            builder.setSurname(account.getFamilyName()).setUserId(getFirebaseUserId());
                            User user = builder.getMyUser();
                            //TODO we should use the same id used from google to write up on the database
                            Log.d(TAG, "firebaseAuthWithGoogle() " + user.getEmail() + " " + user.getName() + " " + user.getSurname() + " " + user.getType());
                            writeNewUser(user);
                        }
                    }
                });
    }

    /**
     * This method is used to sign in a user
     * from the email and password form
     */
    private void signIn() {
        Log.d(TAG, "signIn");
        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(SignInActivity.this, "Sign In Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * This method is used to sign up a new user
     * from the email and password form
     */
    private void signUp() {
        Log.d(TAG, "signUp");
        if (!validateForm()) {
            return;
        }
        showProgressDialog();
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();
        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());
                        hideProgressDialog();
                        FirebaseUser firebaseUser = task.getResult().getUser();
                        if (task.isSuccessful()) {
                            onAuthSuccess(firebaseUser);
                            UserDirector director = new UserDirector();
                            UserBuilder builder = director.buildUser(firebaseUser.getEmail(), firebaseUser.getDisplayName(), "Student");
                            builder.setUserId(firebaseUser.getUid());
                            User user = builder.getMyUser();
                            // Write new user
                            writeNewUser(user);
                        } else {
                            Toast.makeText(SignInActivity.this, "Sign Up Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void onAuthSuccess(FirebaseUser user) {
        // Go to MainActivity
        startActivity(new Intent(SignInActivity.this, MainActivity.class));
        finish();
    }

    /**
     * This method validate the form, based on simple email pattern
     *
     * @return - boolean
     */
    private boolean validateForm() {
        boolean result = true;

        final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        final String email = mEmailField.getText().toString();

        mEmailField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!email.matches(emailPattern)) {
                    mEmailField.setError(getResources().getString(R.string.invalid_email));
                } else {
                    mEmailField.setError(null);
                }
            }
        });

        if (TextUtils.isEmpty(mEmailField.getText().toString())) {
            mEmailField.setError(getResources().getString(R.string.required));
            result = false;
        } else {
            mEmailField.setError(null);
        }

        if (TextUtils.isEmpty(mPasswordField.getText().toString())) {
            mPasswordField.setError(getResources().getString(R.string.required));
            result = false;
        } else {
            mPasswordField.setError(null);
        }

        if (mPasswordField.getText().length() < 6) {
            mPasswordField.setError(getResources().getString(R.string.at_least_6_chars));
            result = false;
        }

        return result;
    }

    /**
     * This method insert new user in the firestore database if he doesn't exists
     *
     * @param user - User
     */
    private void writeNewUser(User user) {
        UserDao userDao = new UserDaoImpl();
        userDao.addUserIfNotPresent(user);
        //go to main activity
        startActivity(new Intent(SignInActivity.this, MainActivity.class));
        finish();
    }

    /**
     * This method is an event listener and redirect
     * to another activity based on the button pushed
     *
     * @param v - View
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button_email:
                signIn();
                break;
            case R.id.sign_up_button_email:
                signUp();
                break;
            case R.id.sign_in_button:
                signInGoogle();
                break;
            case R.id.forgot_password:
                startActivity(new Intent(SignInActivity.this, ForgotPasswordActivity.class));
        }
    }
}