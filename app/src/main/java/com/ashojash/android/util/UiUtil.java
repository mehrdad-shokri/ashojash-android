package com.ashojash.android.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.ashojash.android.R;
import com.ashojash.android.helper.AppController;

public final class UiUtil {
  private static String[] persianNumbers =
      new String[] { "۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹" };

  public static void hideKeyboard() {
    View view = AppController.currentActivity.getCurrentFocus();
    if (view != null) {
      ((InputMethodManager) AppController.context.getSystemService(Context.INPUT_METHOD_SERVICE)).
          hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
  }

  public static String toPersianNumber(String text) {
    if (text.length() == 0) return "";
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

  public static float dp2px(float dp) {
    Resources resources = Resources.getSystem();
    DisplayMetrics metrics = resources.getDisplayMetrics();
    float px = dp * (metrics.densityDpi / 160f);
    return px;
  }

  public static float px2dp(float px) {
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

  public static String formatCurrency(int price) {
    return formatCurrency(String.valueOf(price));
  }

  public static String setUrlWidth(String url, int px) {
    String newUrl = url + ("?w=" + px);
    return newUrl;
  }

  public static int getStatusBarHeight(Context context) {
    return getStatusBarHeight(context, false);
  }

  private static int getStatusBarHeight(Context context, boolean force) {
    int result = 0;
    int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
    if (resourceId > 0) {
      result = context.getResources().getDimensionPixelSize(resourceId);
    }

    int dimenResult = context.getResources().getDimensionPixelSize(R.dimen.tool_bar_top_padding);
    if (dimenResult == 0 && !force) {
      return 0;
    } else {
      return result == 0 ? dimenResult : result;
    }
  }

  public static BitmapDrawable writeOnDrawable(int drawableId, String text) {
    Bitmap bm = BitmapFactory.decodeResource(AppController.context.getResources(), drawableId)
        .copy(Bitmap.Config.ARGB_8888, true);
    Paint paint = new Paint();
    paint.setStyle(Paint.Style.FILL);
    paint.setColor(Color.WHITE);
    paint.setTextAlign(Paint.Align.CENTER);
    paint.setTextSize(45);
    Canvas canvas = new Canvas(bm);
    int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2));
    canvas.drawText(text, bm.getWidth() / 2, /*bm.getHeight() / 2*/yPos, paint);
    return new BitmapDrawable(bm);
  }

  public static int getNavBarHeight() {
    Resources resources = AppController.context.getResources();
    int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
    if (resourceId > 0) {
      return resources.getDimensionPixelSize(resourceId);
    }
    return 0;
  }
  public static int getStatusBarHeight() {
    Resources resources = AppController.context.getResources();
    int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
    if (resourceId > 0) {
      return resources.getDimensionPixelSize(resourceId);
    }
    return 0;
  }
}
