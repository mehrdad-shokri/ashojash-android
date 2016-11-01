package com.ashojash.android.activity;

import android.support.v7.widget.Toolbar;
import com.ashojash.android.R;

public class ToolbarActivity extends BaseActivity {
  private Toolbar toolbarTop;

  @Override protected void onResume() {
    super.onResume();
    setupTopToolbar();
  }

  private void setupTopToolbar() {
    toolbarTop = (Toolbar) findViewById(R.id.toolbarTop);
    toolbarTop.setTitle("");
    setSupportActionBar(toolbarTop);
    getSupportActionBar().setDisplayShowHomeEnabled(true);
  }
}
