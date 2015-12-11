package com.ashojash.android.utils;

import com.ashojash.android.ui.UiUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthValidator {
    //    error types
    public static final int REG_NOT_MATCH = -1;
    public static final int FIELD_REQUIRED = -2;
    public static final int FIELD_UNDER_LIMIT = -3;
    public static final int FIELD_EXCEEDS_LIMIT = -4;

    public static int validateUsername(String username) {
        final String NAME_PATTERN = "^[a-zA-Z0-9-]*$";
        Pattern pattern = Pattern.compile(NAME_PATTERN);
        Matcher matcher;
        matcher = pattern.matcher(username);
        if (username.length() == 0)
            return FIELD_REQUIRED;
        if (!matcher.matches())
            return REG_NOT_MATCH; //not validated in view
        if (username.length() < 4)
            return FIELD_UNDER_LIMIT;
        if (username.length() > 255)
            return FIELD_EXCEEDS_LIMIT;
        return 0;
    }

    public static int validateLogin(String login) {
        if (login.length() == 0)
            return FIELD_REQUIRED;
        return 0;
    }

    public static int validateName(String name) {
        if (name.length() == 0)
            return FIELD_REQUIRED;
        if (!UiUtils.isTextInPersian(name))
            return REG_NOT_MATCH;
        if (name.length() < 4)
            return FIELD_UNDER_LIMIT;
        if (name.length() > 255)
            return FIELD_EXCEEDS_LIMIT;
        return 0;
    }

    public static int validateEmail(String email) {
        final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher;
        matcher = pattern.matcher(email);
        if (email.length() == 0)
            return FIELD_REQUIRED;
        else if (!matcher.matches())
            return REG_NOT_MATCH;
        return 0;
    }

    public static int validatePassword(String password) {
        if (password.length() == 0)
            return FIELD_REQUIRED;
        if (password.length() < 6)
            return FIELD_UNDER_LIMIT;
        if (password.length() > 255)
            return FIELD_EXCEEDS_LIMIT;
        return 0;
    }

    public static int validateLoginPassword(String password) {
        if (password.length() == 0)
            return FIELD_REQUIRED;
        return 0;
    }
}
