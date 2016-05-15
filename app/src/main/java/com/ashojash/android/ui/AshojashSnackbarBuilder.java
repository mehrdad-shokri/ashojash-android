package com.ashojash.android.ui;

import android.app.Activity;
import android.view.View;

public class AshojashSnackbarBuilder {
    private String message;
    private int messageInt;
    private Activity activity;
    private int duration;
    private View rootView;

    public AshojashSnackbarBuilder() {
    }

    public AshojashSnackbarBuilder message(int message) {
        this.messageInt = message;
        return this;
    }

    public AshojashSnackbarBuilder message(String message) {
        this.message = message;
        return this;
    }

    public AshojashSnackbarBuilder rootView(View view) {
        this.rootView = view;
        return this;
    }

    public AshojashSnackbarBuilder duration(int duration) {
        this.duration = duration;
        return this;
    }

    public AshojashSnackbarBuilder activity(Activity activity) {
        this.activity = activity;
        return this;
    }
}
