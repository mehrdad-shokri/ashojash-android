package com.ashojash.android.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import com.ashojash.android.R;
import com.ashojash.android.adapter.ViewpagerAdapter;
import com.ashojash.android.fragment.LoginFragment;
import com.ashojash.android.fragment.RegisterFragment;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.utils.AuthUtils;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;
import com.roughike.bottombar.OnTabClickListener;

/*
* Checked for bus and json
* */
public class GuestProfileActivity extends BaseActivity {

    //    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private BottomBar mBottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest);
        viewPager = (ViewPager) findViewById(R.id.viewpagerProfileActivity);
        setupRegisterViewpager(viewPager, savedInstanceState);
        tabLayout = (TabLayout) findViewById(R.id.tabsProfileActivity);
        tabLayout.setupWithViewPager(viewPager);
        setupBottomBar(savedInstanceState);
    }

    private void setupBottomBar(Bundle savedInstanceState) {
        mBottomBar = BottomBar.attach( findViewById(R.id.rootView), savedInstanceState);
        if (AuthUtils.isUserLoggedIn()) {
            mBottomBar.setItems(
                    new BottomBarTab(new IconicsDrawable(AppController.context).icon(GoogleMaterial.Icon.gmd_home).sizeDp(25).color(AppController.context.getResources().getColor(R.color.text_primary)), R.string.title_home)
            );
            mBottomBar.mapColorForTab(0, ContextCompat.getColor(this, R.color.colorAccent));
        } else {
            mBottomBar.setItems(
                    new BottomBarTab(new IconicsDrawable(AppController.context).icon(GoogleMaterial.Icon.gmd_home).sizeDp(25).color(AppController.context.getResources().getColor(R.color.text_primary)), R.string.title_home),
                    new BottomBarTab(new IconicsDrawable(AppController.context).icon(GoogleMaterial.Icon.gmd_account_circle).sizeDp(25).color(AppController.context.getResources().getColor(R.color.text_primary)), R.string.title_profile)
            );
            mBottomBar.mapColorForTab(0, ContextCompat.getColor(this, R.color.colorAccent));
            mBottomBar.mapColorForTab(1, 0xFF5D4037);
        }
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mBottomBar.setOnTabClickListener(new OnTabClickListener() {
                    @Override
                    public void onTabSelected(int position) {
                        if (position == 0) {
                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(130);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    Intent intent = new Intent(GuestProfileActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(intent);
                                    overridePendingTransition(0, 0);
                                    finish();
                                }
                            });
                        }
                    }

                    @Override
                    public void onTabReSelected(int position) {
                        // The user reselected a tab at the specified position!
                    }
                });
            }
        });
        mBottomBar.selectTabAtPosition(1, false);
        mBottomBar.mapColorForTab(0, ContextCompat.getColor(this, R.color.colorAccent));
        mBottomBar.mapColorForTab(1, 0xFF5D4037);
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
        if (extras != null)
            viewPager.setCurrentItem(extras.getInt("current_viewpager_tab", 0));
        viewPager.setAdapter(adapter);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("current_viewpager_tab", viewPager.getCurrentItem());
        mBottomBar.onSaveInstanceState(outState);
    }
}
