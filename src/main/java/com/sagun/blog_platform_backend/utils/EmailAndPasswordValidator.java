package com.sagun.blog_platform_backend.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailAndPasswordValidator {
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()\\-+])(?=\\S+$).{8,50}$";
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final Pattern passwordPattern = Pattern.compile(PASSWORD_PATTERN);
    private static final  Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);
    public static boolean isStrongPassword(String password){
        if(password==null) return false;

        if(password.length()<8 || password.length()>50) return false;
        Matcher matcher = passwordPattern.matcher(password);
        return matcher.matches();
    }

    public static boolean isValidEmail(String email){
        if(email==null) return false;
        Matcher matcher = emailPattern.matcher(email);
        return matcher.matches();
    }

}







