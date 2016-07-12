package com.ashojash.android.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import com.ashojash.android.helper.AppController;

/*
* Checked for bus and json
* */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        AppController.currentActivity = this;
        super.onResume();
    }

    protected Fragment addFragment(int container, Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(container, fragment);
        fragmentTransaction.commit();
        return fragment;
    }

    protected void removeFragment(Fragment fragment) {
        if (fragment == null) return;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
    }
}
