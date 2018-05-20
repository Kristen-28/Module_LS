package com.example.sakshi.demoapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mauthListener;

    private LinearLayout loginlayout, signuplayout;
    private TextInputEditText ed_login_email, ed_login_password, ed_signup_email, ed_signup_password;
    private TextInputLayout til_login_email, til_login_password, til_signup_email, til_signup_password;
    private Button btnSignin, btn_goto_signup, btn_signup, btn_goto_login;

    private SignInButton btn_google_signin;
    private LoginButton btn_fb_signin;
    private static final int GOOGLE_SIGN_IN = 1;

    private GoogleApiClient google_api_client;
    private CallbackManager callbackManager;

    private ProgressDialog progressDialog;
    private ValidityChecker validityChecker;

    private Google_signin m_google_signin;
    private Facebook_signin m_fb_signin;
    private Email_signin m_email_signin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        if (BuildConfig.DEBUG) {
            FacebookSdk.setIsDebugEnabled(true);
            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        }

        loginlayout = findViewById(R.id.login_layout);
        signuplayout = findViewById(R.id.signup_layout);

        til_login_email = findViewById(R.id.wrapperemail);
        til_login_password = findViewById(R.id.wrapperpassword);
        til_signup_email = findViewById(R.id.s_wrapperemail);
        til_signup_password = findViewById(R.id.s_wrapperpassword);

        ed_login_email = findViewById(R.id.ed_email);
        ed_login_password = findViewById(R.id.ed_password);
        ed_signup_email = findViewById(R.id.signup_email);
        ed_signup_password = findViewById(R.id.signup_pass);

        btnSignin = findViewById(R.id.b_signin);
        btn_goto_signup = findViewById(R.id.b_signup);
        btn_signup = findViewById(R.id.signup_details);
        btn_goto_login = findViewById(R.id.back_Login);

        btn_google_signin = findViewById(R.id.google_signin_btn);
        btn_fb_signin = findViewById(R.id.fb_login_btn);
        progressDialog = new ProgressDialog(this);


        loginlayout.setVisibility(View.VISIBLE);
        signuplayout.setVisibility(View.INVISIBLE);

        validityChecker = new ValidityChecker(ed_login_email, ed_login_password, ed_signup_email, ed_signup_password,
                til_login_email, til_login_password, til_signup_email, til_signup_password, getApplicationContext());

        m_google_signin = new Google_signin(getApplicationContext(), this);
        m_fb_signin = new Facebook_signin(getApplicationContext(), this);
        m_email_signin = new Email_signin(getApplicationContext(), this);

        mAuth = FirebaseAuth.getInstance();
        mauthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.e(TAG, "User Signed in");
                    startActivity(new Intent(MainActivity.this, Sucess.class));
                } else {
                    Log.e(TAG, "User Signed out");
                }
            }
        };


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        google_api_client = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(MainActivity.this, "Some error ocurred. Please try again!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        btn_goto_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginlayout.setVisibility(View.INVISIBLE);
                signuplayout.setVisibility(View.VISIBLE);
            }
        });

        btn_goto_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signuplayout.setVisibility(View.INVISIBLE);
                loginlayout.setVisibility(View.VISIBLE);
            }
        });

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String login_val_email = til_login_email.getEditText().getText().toString();
                final String login_val_pass = til_login_password.getEditText().getText().toString();

                if (!validityChecker.check_details(login_val_email, login_val_pass, loginlayout)) {
                    progressDialog.setMessage("Loggin in...");
                    progressDialog.show();
                    m_email_signin.signin(login_val_email, login_val_pass);
                } else {
                    Toast.makeText(MainActivity.this, "Please correct the fields in red and try again.", Toast.LENGTH_SHORT).show();
                }

            }
        });


        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String signup_val_email = til_signup_email.getEditText().getText().toString();
                final String signup_val_pass = til_signup_password.getEditText().getText().toString();

                if (!validityChecker.check_details(signup_val_email, signup_val_pass, signuplayout)) {
                    progressDialog.setMessage("Registering user...");
                    progressDialog.show();
                    m_email_signin.signup_user(signup_val_email, signup_val_pass);
                } else
                    Toast.makeText(MainActivity.this, "Please correct the fields in red and try again. ", Toast.LENGTH_SHORT).show();
            }
        });

        btn_google_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Logging in..");
                progressDialog.show();

                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(google_api_client);
                startActivityForResult(signInIntent, GOOGLE_SIGN_IN);

            }
        });

        callbackManager = CallbackManager.Factory.create();
        btn_fb_signin.setReadPermissions("email", "public_profile");
        btn_fb_signin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e(TAG, "facebook:onSuccess:" + loginResult);
                m_fb_signin.handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.e(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "facebook:onError", error);
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.e(TAG, currentUser.getUid());
            mAuth.addAuthStateListener(mauthListener);
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);

        if (requestCode == GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                m_google_signin.firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }
}
