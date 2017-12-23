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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.walkap.x_android.R;
import com.walkap.x_android.model.user.User;

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

    //Start google scripts

    private void signInGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

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

                            mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                final String userId = getUid();
                                String email = account.getEmail();
                                String name = account.getGivenName();
                                String surname = account.getFamilyName();
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    boolean found = false;

                                    for(DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()){
                                        User user = noteDataSnapshot.getValue(User.class);
                                        if(user.getEmail().equals(email)){
                                            found = true;
                                            Log.d(TAG, "the user is here, go to main");
                                            break;
                                        }
                                    }
                                    Log.d(TAG, "User found: " + found);

                                    if(!found){
                                        // Write new user
                                        writeNewUser(userId, email, name, surname, "", "", "");
                                    }
                                    else{
                                        startActivity(new Intent(SignInActivity.this, MainActivity.class));
                                        finish();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.d(TAG, "Database error");
                                }
                            });
                        }
                    }
                });
    }

    //End google scripts


    //start email password scripts
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

                        FirebaseUser user = task.getResult().getUser();

                        if (task.isSuccessful()) {
                            onAuthSuccess(user);
                            // Write new user
                            writeNewUser(user.getUid(), user.getEmail(), "", "", "", "", "");
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
                if(!email.matches(emailPattern)){
                    mEmailField.setError(getResources().getString(R.string.invalid_email));
                }else{
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

        if(mPasswordField.getText().length() < 6){
            mPasswordField.setError(getResources().getString(R.string.at_least_6_chars));
            result = false;
        }

        return result;
    }

    //end email and password scripts

    // [START basic_write]
    private void writeNewUser(String userId, String email, String name, String surname, String university, String faculty, String degreeCourse) {
        User user = new User(email, name, surname, university, faculty, degreeCourse);
        mDatabase.child("users").child(userId).setValue(user);

        Log.d("*** writeNewUser ***", "  " + userId + "  " + email);

        //go to main activity
        startActivity(new Intent(SignInActivity.this, MainActivity.class));
        finish();

    }
    // [END basic_write]

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
