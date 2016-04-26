package com.ashojash.android.activity;

import android.support.v7.widget.Toolbar;
import com.ashojash.android.R;

/*
* Checked for bus and json
* */
public class ToolbarActivity extends BaseActivity {
    private Toolbar toolbarTop;

    @Override
    protected void onResume() {
        super.onResume();
        setupTopToolbar();
    }

    private void setupTopToolbar() {
        toolbarTop = (Toolbar) findViewById(R.id.toolbarTop);
        toolbarTop.setTitle("");
        setSupportActionBar(toolbarTop);
        try {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        } catch (NullPointerException e) {
        }
    }
}
