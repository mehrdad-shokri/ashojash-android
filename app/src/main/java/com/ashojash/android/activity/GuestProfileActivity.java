package com.ashojash.android.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import com.ashojash.android.R;
import com.ashojash.android.adapter.ViewpagerAdapter;
import com.ashojash.android.fragment.LoginFragment;
import com.ashojash.android.fragment.RegisterFragment;
import com.ashojash.android.helper.AppController;

public class GuestProfileActivity extends BottomToolbarActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest);
        toolbar = (Toolbar) findViewById(R.id.toolbarTop);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();
        super.setupBottomToolbarLayoutActive(2);
        viewPager = (ViewPager) findViewById(R.id.viewpagerProfileActivity);
        setupRegisterViewpager(viewPager, savedInstanceState);
        tabLayout = (TabLayout) findViewById(R.id.tabsProfileActivity);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void setupRegisterViewpager(ViewPager viewPager, Bundle savedInstanceState) {
        ViewpagerAdapter adapter = new ViewpagerAdapter(getSupportFragmentManager());
        RegisterFragment registerFragment;
        LoginFragment loginFragment;
        Bundle bundle = new Bundle();
        if (getCallingActivity() != null)
            bundle.putString("calling_activity", getCallingActivity().getShortClassName());
        registerFragment = new RegisterFragment();
        loginFragment = new LoginFragment();
        registerFragment.setArguments(bundle);
        loginFragment.setArguments(bundle);
        adapter.addFragment(loginFragment, getResources().getString(R.string.signin));
        adapter.addFragment(registerFragment, getResources().getString(R.string.signup));
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Log.d(AppController.TAG, "setupRegisterViewpager: " + extras.getInt("current_viewpager_tab", 0));
            viewPager.setCurrentItem(extras.getInt("current_viewpager_tab", 0));
        }
        viewPager.setAdapter(adapter);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("current_viewpager_tab", viewPager.getCurrentItem());
    }
}
