package com.ashojash.android.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.ashojash.android.R;

public class SecondActivity extends BaseActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }
}
