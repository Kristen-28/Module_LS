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
    private TextInputLayout til_login_email, til_login_pass, til_signup_email, til_signup_pass;
    private static int error_email = 0, error_pass = 0;

    ValidityChecker(TextInputLayout til_l_email, TextInputLayout til_l_pass,
                    TextInputLayout til_s_email, TextInputLayout til_s_pass,
                    Context c) {

        this.til_login_email = til_l_email;
        this.til_login_pass = til_l_pass;
        this.til_signup_email = til_s_email;
        this.til_signup_pass = til_s_pass;

        this.context = c;

    }

    public boolean check_details(String val_email, String val_pass, int layout_detect) {

        if (TextUtils.isEmpty(val_email)) {
            if (layout_detect == 0) {
                til_login_email.setError(context.getResources().getString(R.string.empty_string_error));
                til_login_email.getEditText().getBackground().setColorFilter(context.getResources().getColor(R.color.Red), PorterDuff.Mode.SRC_ATOP);
            } else if (layout_detect == 1) {
                til_signup_email.setError(context.getResources().getString(R.string.empty_string_error));
                til_signup_email.getEditText().getBackground().setColorFilter(context.getResources().getColor(R.color.Red), PorterDuff.Mode.SRC_ATOP);
            }

            error_email = 1;
        } else if (!isValidEmail(val_email)) {
            if (layout_detect == 0) {
                til_login_email.setError(context.getResources().getString(R.string.regex_error));
                til_login_email.getEditText().getBackground().setColorFilter(context.getResources().getColor(R.color.Red), PorterDuff.Mode.SRC_ATOP);
            } else if (layout_detect == 1) {
                til_signup_email.setError(context.getResources().getString(R.string.regex_error));
                til_signup_email.getEditText().getBackground().setColorFilter(context.getResources().getColor(R.color.Red), PorterDuff.Mode.SRC_ATOP);
            }

            error_email = 1;
        } else {

            error_email = 0;
            til_login_email.setError(null);
            til_login_email.getEditText().getBackground().clearColorFilter();
            til_signup_email.setError(null);
            til_signup_email.getEditText().getBackground().clearColorFilter();

        }
        if (TextUtils.isEmpty(val_pass)) {
            if (layout_detect == 0) {

                til_login_pass.setError(context.getResources().getString(R.string.empty_string_error));
                til_login_pass.getEditText().getBackground().setColorFilter(context.getResources().getColor(R.color.Red), PorterDuff.Mode.SRC_ATOP);
            } else if (layout_detect == 1) {
                til_signup_pass.setError(context.getResources().getString(R.string.empty_string_error));
                til_signup_pass.getEditText().getBackground().setColorFilter(context.getResources().getColor(R.color.Red), PorterDuff.Mode.SRC_ATOP);
            }
            error_pass = 1;
        } else {

            error_pass = 0;
            til_login_pass.setError(null);
            til_login_pass.getEditText().getBackground().clearColorFilter();
            til_signup_pass.setError(null);
            til_signup_pass.getEditText().getBackground().clearColorFilter();

        }

        if (error_email == 1 || error_pass == 1) return true;
        else return false;
    }

    public static boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
