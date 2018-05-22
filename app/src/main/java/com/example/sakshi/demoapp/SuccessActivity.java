package com.example.sakshi.demoapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SuccessActivity extends AppCompatActivity {

    private Button btn_signout;
    private TextView userid;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sucess);


        userid=findViewById(R.id.textToken);
        mAuth=FirebaseAuth.getInstance();
        btn_signout=findViewById(R.id.signout);

        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if(firebaseAuth.getCurrentUser()!=null)
                    {
                        String x= "Hi user "+ mAuth.getCurrentUser().getUid();
                        userid.setText(x);
                    }
            }
        };


        btn_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Toast.makeText(SuccessActivity.this, "Signing out..", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SuccessActivity.this,MainActivity.class));

                if(com.facebook.Profile.getCurrentProfile() != null)
                LoginManager.getInstance().logOut();

            }
        });
    }

    @Override
    protected void onStart() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mAuth.addAuthStateListener(authStateListener);
        super.onStart();
    }
}
