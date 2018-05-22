package com.example.sakshi.demoapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneAuthActivity extends AppCompatActivity {

    private EditText ed_phone_number, ed_otp;
    private ProgressBar progressBar_phone, progressBar_otp;
    private Button send_code_button;
    private static final String TAG = "PhoneAuthActivity";
    private FirebaseAuth mAuth;
    private TextView tv_some_error;
    private LinearLayout linear_layout_otp;
    private int btn_type=0;                   //0 for send //1 for verify code

    private String m_verification_id;
    private PhoneAuthProvider.ForceResendingToken m_resend_token;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mcallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);

        ed_phone_number = findViewById(R.id.ed_phone_number);
        ed_otp = findViewById(R.id.ed_otp_enter);

        progressBar_phone = findViewById(R.id.progressbar_phone);
        progressBar_otp = findViewById(R.id.progressbar_code);

        tv_some_error=findViewById(R.id.tv_some_error);

        linear_layout_otp=findViewById(R.id.linear_layout_code);

        send_code_button = findViewById(R.id.btn_send_code);
        mAuth = FirebaseAuth.getInstance();

        mcallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Log.e(TAG, "Ccompleted");
                signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.e(TAG, "Failed");
                Log.e(TAG, e.getMessage());
                tv_some_error.setText("Invalid request");
                tv_some_error.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                m_verification_id = verificationId;
                m_resend_token = token;

                progressBar_phone.setVisibility(View.INVISIBLE);
                linear_layout_otp.setVisibility(View.VISIBLE);

                btn_type=1;
                send_code_button.setText(R.string.code_verify);
                send_code_button.setEnabled(true);
                // ...
            }

        };



        send_code_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (btn_type == 0) {
                    String m_phone_number = ed_phone_number.getText().toString();
                    String send_phone_no = "+91".concat(m_phone_number);

                    progressBar_phone.setVisibility(View.VISIBLE);
                    ed_phone_number.setEnabled(false);
                    send_code_button.setEnabled(false);


                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            send_phone_no,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            PhoneAuthActivity.this,               // Activity (for callback binding)
                            mcallbacks);
                }
                else
                {
                    ed_otp.setVisibility(View.VISIBLE);
                    String verification_code = ed_otp.getText().toString();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(m_verification_id, verification_code);
                    signInWithPhoneAuthCredential(credential);
                }
            }

        });

    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(PhoneAuthActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Log.e(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            startActivity(new Intent(PhoneAuthActivity.this, SuccessActivity.class));
                            finish();

                        } else {
                            Log.e(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            tv_some_error.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
    }

}
