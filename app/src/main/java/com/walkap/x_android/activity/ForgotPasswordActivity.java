package com.walkap.x_android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.walkap.x_android.R;

public class ForgotPasswordActivity extends BaseActivity implements View.OnClickListener {

    private final String TAG = "ForgotPasswordActivity";

    private EditText mYourEmail;
    private TextView mResetPassword;

    private FirebaseAuth auth;
    private String emailAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        //Get the editText
        mYourEmail = (EditText) findViewById(R.id.your_email);
        //Get button reset password
        mResetPassword = (TextView) findViewById(R.id.reset_password_button);
        mResetPassword.setOnClickListener(this);
    }

    /**
     * This method is used to validate the field form
     *
     * @return - boolean
     */
    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(mYourEmail.getText().toString())) {
            mYourEmail.setError(getResources().getString(R.string.required));
            result = false;
        } else {
            mYourEmail.setError(null);
        }
        return result;
    }

    /**
     * This method reset the password just in case the form validate
     */
    public void resetPassword() {
        if (!validateForm()) {
            return;
        }
        //Init firebase auth
        auth = FirebaseAuth.getInstance();
        //Get string from edit text
        emailAddress = mYourEmail.getText().toString();
        auth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Email sent.");
                    Toast.makeText(ForgotPasswordActivity.this, R.string.email_sent, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ForgotPasswordActivity.this, MainActivity.class));
                } else {
                    Log.d(TAG, "Email error.");
                    Toast.makeText(ForgotPasswordActivity.this, R.string.email_not_sent, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * This method is ha listener for the form button
     *
     * @param view - View
     */
    @Override
    public void onClick(View view) {
        resetPassword();
    }
}


