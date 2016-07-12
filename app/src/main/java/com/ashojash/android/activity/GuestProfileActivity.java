package com.ashojash.android.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import com.ashojash.android.R;
import com.ashojash.android.adapter.ViewpagerAdapter;
import com.ashojash.android.fragment.LoginFragment;
import com.ashojash.android.fragment.RegisterFragment;

public class GuestProfileActivity extends BottomToolbarActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest);
        setupViews();
        attach(this, savedInstanceState);
    }

    private void setupViews() {
        viewPager = (ViewPager) findViewById(R.id.viewpagerProfileActivity);
        tabLayout = (TabLayout) findViewById(R.id.tabsProfileActivity);
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
        if (extras != null)
            viewPager.setCurrentItem(extras.getInt("current_viewpager_tab", 0));
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("current_viewpager_tab", viewPager.getCurrentItem());
    }
}
