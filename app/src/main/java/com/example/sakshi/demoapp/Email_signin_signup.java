package com.example.sakshi.demoapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by sakshi on 20/5/18.
 */

public class Email_signin_signup {

    private FirebaseAuth mAuth;
    private MainActivity activity;
    private Context context;

    private static final String TAG="Email Sign in";


    Email_signin_signup(Context context, MainActivity activity)
    {
        this.context=context;
        this.activity=activity;

        mAuth=FirebaseAuth.getInstance();

    }
    public void signin(String email,String password)
    {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Log.d(TAG, "signInWithEmail:success");
                            Toast.makeText(context, "Login Sucessful!", Toast.LENGTH_SHORT).show();

                        } else {

                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(context, "Oops something went wrong. Please retry!", Toast.LENGTH_SHORT).show();
                        }


                    }
                });
    }



    public void signup_user(String val_email,String val_pass)
    {
        mAuth.createUserWithEmailAndPassword(val_email,val_pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful())
                {
                    Intent intent=new Intent(context,Sucess.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    Log.d(TAG,"User signed up sucessfully");
                }
            }
        });

    }


}
