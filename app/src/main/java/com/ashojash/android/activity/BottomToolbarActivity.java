package com.ashojash.android.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import com.ashojash.android.R;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.util.AuthUtil;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;
import com.roughike.bottombar.OnTabClickListener;

public class BottomToolbarActivity extends BaseActivity {
    private BottomBar mBottomBar;
    private static final int ICON_SIZE = 26;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mBottomBar != null)
            mBottomBar.onSaveInstanceState(outState);
    }

    protected void attachShy(final int position, Activity activity, Bundle savedInstanceState, CoordinatorLayout coordinatorLayout, View contentView) {
        mBottomBar = BottomBar.attachShy(coordinatorLayout, contentView, savedInstanceState);
        setupBottomBar(position, activity);
    }

    protected void attach(int position, Activity activity, Bundle savedInstanceState) {
        mBottomBar = BottomBar.attach(activity, savedInstanceState);
        setupBottomBar(position, activity);
    }

    private void setupBottomBar(final int position, final Activity activity) {
        mBottomBar.setDefaultTabPosition(position);
        int homeColor = 0, profileColor = 0;
        switch (position) {
            case 0:
                homeColor = R.color.bottom_nav_item_selected;
                profileColor = R.color.bottom_nav_item_unselected;
                break;
            case 1:
                homeColor = R.color.bottom_nav_item_unselected;
                profileColor = R.color.bottom_nav_item_selected;
                break;
        }
        Resources res = AppController.context.getResources();
        if (AuthUtil.isUserLoggedIn()) {
            mBottomBar.setItems(

                    new BottomBarTab(new IconicsDrawable(AppController.context).icon(GoogleMaterial.Icon.gmd_home).sizeDp(ICON_SIZE
                    ).color(res.getColor(homeColor)), null)
            );
        } else {
            mBottomBar.setItems(
                    new BottomBarTab(new IconicsDrawable(AppController.context).icon(GoogleMaterial.Icon.gmd_home).sizeDp(ICON_SIZE
                    ).color(res.getColor(homeColor)), null),
                    new BottomBarTab(new IconicsDrawable(AppController.context).icon(GoogleMaterial.Icon.gmd_account_circle).sizeDp(ICON_SIZE).color(res.getColor(profileColor)), null)
            );
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
                    public void onTabSelected(int selectedTab) {
                        if (position == 0 && selectedTab == 1) {
                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    Intent intent = new Intent(activity, GuestProfileActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(intent);
                                    overridePendingTransition(0, 0);
                                    activity.finish();
                                }
                            });
                        } else if (position == 1 && selectedTab == 0) {
                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    Intent intent = new Intent(activity, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(intent);
                                    overridePendingTransition(0, 0);
                                    activity.finish();
                                }
                            });
                        }
                    }

                    @Override
                    public void onTabReSelected(int position) {
                        if (activity instanceof MainActivity) {
                            NestedScrollView nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);
                            ObjectAnimator.ofInt(nestedScrollView, "scrollY", 0).setDuration(200).start();
                        } else if (activity instanceof CollectionActivity) {
                            activity.finish();
                            activity.overridePendingTransition(0, 0);
                        }
                    }
                });
            }
        });
    }
}
