package com.ashojash.android.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;
import com.ashojash.android.helper.AppController;

public class AshojashSnackbar {
    private static Context context = AppController.context;

    public static Snackbar make(Activity activity, int message, int duration) {
        return make(activity,context.getResources().getString(message), duration);
    }

    public static Snackbar make(Activity activity, String message, int duration) {
        View rootView = activity.findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(rootView, message, duration);

        View view = snackbar.getView();

        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        return snackbar;
    }

}
