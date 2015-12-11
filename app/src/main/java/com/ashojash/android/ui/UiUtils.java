package com.ashojash.android.ui;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.ashojash.android.helper.AppController;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class UiUtils {
    private static String[] persianNumbers = new String[]{"۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹"};
    private final static String PERSIAN_SENTENCE_REGEX = "[\\u0600-\\u06FF\\uFB8A\\u067E\\u0686\\u06AF .!?:)(,;1234567890%-_@]+$";
    private final static String PERSIAN_NAME_REGEX = "[\\u0600-\\u06FF\\uFB8A\\u067E\\u0686\\u06AF ]+$";

    public static void hideKeyboard() {
        View view = AppController.currentActivity.getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) AppController.context.getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static String toPersianNumber(String text) {
        if (text.length() == 0)
            return "";
        String out = "";
        int length = text.length();
        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            if ('0' <= c && c <= '9') {
                int number = Integer.parseInt(String.valueOf(c));
                out += persianNumbers[number];
            } else if (c == '٫') {
                out += '،';
            } else {
                out += c;
            }

        }
        return out;
    }

    public static float convertDpToPixel(float dp) {
        Resources resources = Resources.getSystem();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    public static float convertPixelsToDp(float px) {
        Resources resources = Resources.getSystem();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    public static String formatCurrency(String digits) {
        String result = "";
        int len = digits.length();
        int nDigits = 0;

        for (int i = len - 1; i >= 0; i--) {
            result = digits.charAt(i) + result;
            nDigits++;
            if (((nDigits % 3) == 0) && (i > 0)) {
                result = "," + result;
            }
        }
        return (result);
    }

    public static boolean isTextInPersian(String text) {
        Pattern pattern = Pattern.compile(PERSIAN_SENTENCE_REGEX);
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }

}
