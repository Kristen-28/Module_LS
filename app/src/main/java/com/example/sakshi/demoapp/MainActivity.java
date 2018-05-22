package com.example.sakshi.demoapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
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

    private LinearLayout ed_signups_layout, ed_logins_layout, signup_bottom_layout, login_bottom_layout, login_heading_layout, signup_heading_layout, signup_here_layout;
    private TextInputLayout til_login_email, til_login_password, til_signup_email, til_signup_password;
    private Button btnSignin, btn_signup, btn_goto_login, btn_phone_auth;

    private SignInButton btn_google_signin;
    private LoginButton btn_fb_signin;
    private static final int GOOGLE_SIGN_IN = 1;

    private GoogleApiClient google_api_client;
    private CallbackManager callbackManager;

    private ProgressDialog progressDialog;
    private ValidityChecker validityChecker;

    private GoogleSignin m_google_signin;
    private FacebookSignin m_fb_signin;
    private EmailSigninSignup m_email_signinSignup;
    private TextView btn_goto_signup;

    private CardView m_card_view;
    private int layout_detect = 0;   //0 for login,1 for signup

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        if (BuildConfig.DEBUG) {
            FacebookSdk.setIsDebugEnabled(true);
            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        }

        login_heading_layout = findViewById(R.id.login_heading);
        signup_heading_layout = findViewById(R.id.signup_heading);
        login_bottom_layout = findViewById(R.id.login_bottom_referral_layout);
        signup_bottom_layout = findViewById(R.id.signup_bottom_layout);
        ed_logins_layout = findViewById(R.id.edittext_logins);
        ed_signups_layout = findViewById(R.id.edittext_signups);
        signup_here_layout = findViewById(R.id.signup_here_layout);

        til_login_email = findViewById(R.id.wrapperemail);
        til_login_password = findViewById(R.id.wrapperpassword);
        til_signup_email = findViewById(R.id.s_wrapperemail);
        til_signup_password = findViewById(R.id.s_wrapperpassword);

        btnSignin = findViewById(R.id.b_signin);
        btn_goto_signup = findViewById(R.id.b_signup);
        btn_signup = findViewById(R.id.signup_details);
        btn_goto_login = findViewById(R.id.back_Login);
        btn_phone_auth = findViewById(R.id.btn_phone_auth);

        btn_google_signin = findViewById(R.id.google_signin_btn);
        btn_fb_signin = findViewById(R.id.fb_login_btn);
        progressDialog = new ProgressDialog(this);

        m_card_view = findViewById(R.id.card_view);

        setlayouts(layout_detect);

        TextView textView = (TextView) btn_google_signin.getChildAt(0);
        textView.setText("Google");                        //setting text google to google button

        validityChecker = new ValidityChecker(til_login_email, til_login_password, til_signup_email, til_signup_password, getApplicationContext());
        m_google_signin = new GoogleSignin(getApplicationContext(), this);
        m_fb_signin = new FacebookSignin(getApplicationContext(), this);
        m_email_signinSignup = new EmailSigninSignup(getApplicationContext(), this);

        mAuth = FirebaseAuth.getInstance();
        mauthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.e(TAG, "User Signed in");
                    progressDialog.dismiss();
                    startActivity(new Intent(MainActivity.this, SuccessActivity.class));
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

                setlayouts(1);
            }
        });

        btn_goto_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setlayouts(0);

            }
        });

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String login_val_email = til_login_email.getEditText().getText().toString();
                final String login_val_pass = til_login_password.getEditText().getText().toString();

                if (!validityChecker.check_details(login_val_email, login_val_pass, 0)) {
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                    m_email_signinSignup.signin(login_val_email, login_val_pass);

                    til_login_email.getEditText().setText("");
                    til_login_password.getEditText().setText("");

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

                if (!validityChecker.check_details(signup_val_email, signup_val_pass, 1)) {
                    progressDialog.setMessage("Registering user...");
                    progressDialog.show();
                    m_email_signinSignup.signup_user(signup_val_email, signup_val_pass);

                } else
                    Toast.makeText(MainActivity.this, "Please correct the fields in red and try again. ", Toast.LENGTH_SHORT).show();
            }
        });

        btn_google_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Loading..");
                progressDialog.show();

                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(google_api_client);
                startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
            }
        });

        callbackManager = CallbackManager.Factory.create();
        btn_fb_signin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e(TAG, "facebook:onSuccess:" + loginResult);
                m_fb_signin.handleFacebookAccessToken(loginResult.getAccessToken());
                progressDialog.setMessage("Logging in...");
                progressDialog.show();
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


        btn_phone_auth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.dismiss();
                startActivity(new Intent(MainActivity.this, PhoneAuthActivity.class));
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
            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...)
        if (requestCode == GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                GoogleSignInAccount account = task.getResult(ApiException.class);
                m_google_signin.firebaseAuthWithGoogle(account);

            } catch (ApiException e) {

                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    public void setlayouts(int layout_detect) {
        if (layout_detect == 0) {
            login_heading_layout.setVisibility(View.VISIBLE);
            login_bottom_layout.setVisibility(View.VISIBLE);
            ed_logins_layout.setVisibility(View.VISIBLE);
            signup_here_layout.setVisibility(View.VISIBLE);
            signup_heading_layout.setVisibility(View.GONE);
            signup_bottom_layout.setVisibility(View.GONE);
            ed_signups_layout.setVisibility(View.GONE);
        } else {
            signup_heading_layout.setVisibility(View.VISIBLE);
            signup_bottom_layout.setVisibility(View.VISIBLE);
            ed_signups_layout.setVisibility(View.VISIBLE);
            login_heading_layout.setVisibility(View.GONE);
            login_bottom_layout.setVisibility(View.GONE);
            ed_logins_layout.setVisibility(View.GONE);
            signup_here_layout.setVisibility(View.GONE);
        }
    }
}
