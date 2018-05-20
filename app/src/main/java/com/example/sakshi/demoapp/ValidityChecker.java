package com.example.sakshi.demoapp;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.LinearLayout;


/**
 * Created by sakshi on 20/5/18.
 */

public class ValidityChecker {
    private Context context;
    private TextInputLayout til_login_email,til_login_pass,til_signup_email,til_signup_pass;
    private TextInputEditText ed_login_email,ed_login_pass,ed_signup_email,ed_signup_pass;

    private static int error_email=0,error_pass=0;

    ValidityChecker(TextInputEditText ed_l_email, TextInputEditText ed_l_pass,
                    TextInputEditText ed_s_email, TextInputEditText ed_s_pass,
                    TextInputLayout til_l_email,TextInputLayout til_l_pass,
                    TextInputLayout til_s_email, TextInputLayout til_s_pass,
                    Context c)
    {
        this.ed_login_email=ed_l_email;
        this.ed_login_pass=ed_l_pass;
        this.ed_signup_email=ed_s_email;
        this.ed_signup_pass=ed_s_pass;

        this.til_login_email=til_l_email;
        this.til_login_pass=til_l_pass;
        this.til_signup_email=til_s_email;
        this.til_signup_pass=til_s_pass;

        this.context=c;

    }

    public boolean check_details(String val_email,String val_pass,LinearLayout layout)
    {

        if(TextUtils.isEmpty(val_email))
        {
            if(layout.getId()==R.id.login_layout)
            {
                til_login_email.setError(context.getResources().getString(R.string.empty_string_error));
                ed_login_email.getBackground().setColorFilter(context.getResources().getColor(R.color.Red), PorterDuff.Mode.SRC_ATOP);
            }
            else if(layout.getId()==R.id.signup_layout)
            {
                til_signup_email.setError(context.getResources().getString(R.string.empty_string_error));
                ed_signup_email.getBackground().setColorFilter(context.getResources().getColor(R.color.Red), PorterDuff.Mode.SRC_ATOP);
            }

            error_email=1;
        }
        else if(!isValidEmail(val_email))
        {
            if(layout.getId()==R.id.login_layout)
            {
                til_login_email.setError(context.getResources().getString(R.string.regex_error));
                ed_login_email.getBackground().setColorFilter(context.getResources().getColor(R.color.Red), PorterDuff.Mode.SRC_ATOP);
            }
            else if(layout.getId()==R.id.signup_layout)
            {
                til_signup_email.setError(context.getResources().getString(R.string.regex_error));
                ed_signup_email.getBackground().setColorFilter(context.getResources().getColor(R.color.Red), PorterDuff.Mode.SRC_ATOP);
            }

            error_email=1;
        }
        else {

            error_email = 0;
            til_login_email.setError(null);
            ed_login_email.getBackground().clearColorFilter();
            til_signup_email.setError(null);
            ed_signup_email.getBackground().clearColorFilter();

        }


        if(TextUtils.isEmpty(val_pass))
        {
            if(layout.getId()==R.id.login_layout) {

                til_login_pass.setError(context.getResources().getString(R.string.empty_string_error));
                ed_login_pass.getBackground().setColorFilter(context.getResources().getColor(R.color.Red), PorterDuff.Mode.SRC_ATOP);
            }
            else if(layout.getId()==R.id.signup_layout)
            {
                til_signup_pass.setError(context.getResources().getString(R.string.empty_string_error));
                ed_signup_pass.getBackground().setColorFilter(context.getResources().getColor(R.color.Red), PorterDuff.Mode.SRC_ATOP);

            }

            error_pass=1;
        }
        else {

            error_pass=0;
            til_login_pass.setError(null);
            ed_login_pass.getBackground().clearColorFilter();
            til_signup_pass.setError(null);
            ed_signup_pass.getBackground().clearColorFilter();


        }

        if(error_email==1 || error_pass==1) return true;
        else return false;
    }

    public static boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

}
