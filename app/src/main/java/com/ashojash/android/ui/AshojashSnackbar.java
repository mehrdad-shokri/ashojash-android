package com.ashojash.android.ui;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.ashojash.android.helper.AppController;

public class AshojashSnackbar {

    private static Snackbar make(Activity activity, String message, int duration) {
        View rootView = activity.findViewById(android.R.id.content);
        return make(rootView, message, duration);
    }

    private static Snackbar make(View rootView, String message, int duration) {
        Snackbar snackbar = Snackbar.make(rootView, message, duration);
        View view = snackbar.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        return snackbar;
    }

    public static class AshojashSnackbarBuilder {
        private static final Resources res = AppController.context.getResources();

        private String message;
        private int messageId = -1;
        private int duration;
        private Activity activity;
        private View view;

        private AshojashSnackbarBuilder() {
        }

        public AshojashSnackbarBuilder(View view) {
            this.view = view;
        }

        public AshojashSnackbarBuilder(Activity activity) {
            this.activity = activity;
        }

        public AshojashSnackbarBuilder message(int message) {
            this.messageId = message;
            return this;
        }

        public AshojashSnackbarBuilder message(String message) {
            this.message = message;
            return this;
        }

        public AshojashSnackbarBuilder duration(int duration) {
            this.duration = duration;
            return this;
        }

        public Snackbar build() {
            if (messageId != -1)
                message = res.getString(messageId);
            if (view != null) {
                return AshojashSnackbar.make(view, message, duration);
            } else {
                return AshojashSnackbar.make(activity, message, duration);
            }
        }
    }
}
