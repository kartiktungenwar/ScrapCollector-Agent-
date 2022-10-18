package com.techflux.oyebhangarwalaagent.validation;

import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pUnIt on 7/9/2016.
 */
public class MyValidator {
    private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String REQUIRED_MESSEGE = "Filed Required";

    // validating email id
    public static boolean isValidEmail(EditText editText) {
        String str1 = editText.getText().toString().trim();
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(str1);
        if (matcher.matches()) {
            editText.setError(null);
            return true;
        }
        editText.setError("Enter Valid Email Id");
        return false;
    }

    // validating password with retype password
    public static boolean isValidPassword(EditText editText) {
        String str2 = editText.getText().toString().trim();
        if (str2 != null && str2.length() > 3) {
            editText.setError(null);
            return true;
        }
        editText.setError("Enter Valid Password");
        return false;
    }

    public static boolean isValidRequired(EditText editText) {
        String str = editText.getText().toString().trim();
        if (str.length() == 0) {

            editText.setError(REQUIRED_MESSEGE);
            return false;
        }
        editText.setError(null);
        return true;
    }

    public static boolean isValidMobile(EditText editText) {

        String strMobile = editText.getText().toString().trim();
       /* if (strMobile.equals(null)) {
            return true;
        }*/
            if (strMobile != null && strMobile.length() == 15 || strMobile != null && strMobile.length() == 10) {
                return true;
            }

        editText.setError("Enter Valid Mobile Number");
        return false;
    }
}
