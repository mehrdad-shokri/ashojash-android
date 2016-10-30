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
import com.mikepenz.iconics.IconicsDrawable;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;
import com.roughike.bottombar.OnTabClickListener;

import static com.mikepenz.google_material_typeface_library.GoogleMaterial.Icon.gmd_face;
import static com.mikepenz.google_material_typeface_library.GoogleMaterial.Icon.gmd_home;
import static com.mikepenz.google_material_typeface_library.GoogleMaterial.Icon.gmd_search;

public class BottomToolbarActivity extends BaseActivity {
  private static final int MAIN_ACTIVITY_BOTTOMBAR_POSITION = 0;
  private static final int MAPS_ACTIVITY_BOTTOMBAR_POSITION = 1;
  private static final int PROFILE_ACTIVITY_BOTTOMBAR_POSITION = 2;
  public BottomBar mBottomBar;
  private static final int ICON_SIZE = 26;
  private int SELECTED_TAB_COLOR;

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if (mBottomBar != null) mBottomBar.onSaveInstanceState(outState);
  }

  protected void attachShy(Activity activity, Bundle savedInstanceState,
      CoordinatorLayout coordinatorLayout, View contentView) {
    mBottomBar = BottomBar.attachShy(coordinatorLayout, contentView, savedInstanceState);
    int position = getActivityPosition(activity);
    setupBottomBar(position, activity);
  }

  protected void attach(Activity activity, Bundle savedInstanceState) {
    int position = getActivityPosition(activity);
    mBottomBar = BottomBar.attach(activity, savedInstanceState);
    setupBottomBar(position, activity);
  }

  private int getActivityPosition(Activity activity) {
    int position = 0;
    if (activity instanceof GuestProfileActivity) {
      position = PROFILE_ACTIVITY_BOTTOMBAR_POSITION;
    } else if (activity instanceof CollectionActivity) {
      position = MAIN_ACTIVITY_BOTTOMBAR_POSITION;
    } else if (activity instanceof MapsActivity) position = MAPS_ACTIVITY_BOTTOMBAR_POSITION;
    return position;
  }

  private void setupBottomBar(final int position, final Activity activity) {
    mBottomBar.setDefaultTabPosition(position);
    mBottomBar.setActiveTabColor(R.color.bottom_nav_item_selected);
    int collectionColor = 0, profileColor = 0, mapColor = 0;
    final Resources res = AppController.context.getResources();
    final int selectedColor = res.getColor(R.color.bottom_nav_item_selected);
    int unselectedColor = res.getColor(R.color.bottom_nav_item_unselected);
    switch (position) {
      case MAIN_ACTIVITY_BOTTOMBAR_POSITION:
        collectionColor = selectedColor;
        profileColor = mapColor = unselectedColor;
        break;
      case MAPS_ACTIVITY_BOTTOMBAR_POSITION:
        mapColor = selectedColor;
        collectionColor = profileColor = selectedColor;
        break;
      case PROFILE_ACTIVITY_BOTTOMBAR_POSITION:
        profileColor = selectedColor;
        mapColor = collectionColor = unselectedColor;
        break;
    }

    final IconicsDrawable mainDrawable =
        new IconicsDrawable(this).icon(gmd_home).color(collectionColor).sizeDp(24);
    final IconicsDrawable mapDrawable = new IconicsDrawable(AppController.context).icon(gmd_search)
        .color(mapColor)
        .sizeDp(ICON_SIZE);
    final IconicsDrawable profileDrawable =
        new IconicsDrawable(AppController.context).icon(gmd_face)
            .color(profileColor)
            .sizeDp(ICON_SIZE);
    BottomBarTab mainTab = new BottomBarTab(mainDrawable, null);
    BottomBarTab mapTab = new BottomBarTab(mapDrawable, null);
    BottomBarTab profileTab = new BottomBarTab(profileDrawable, null);
    if (AuthUtil.isUserLoggedIn()) {
      mBottomBar.setItems(mainTab, mapTab);
    } else {
      mBottomBar.setItems(mainTab, mapTab, profileTab);
    }
    AsyncTask.execute(new Runnable() {
      @Override public void run() {
        mBottomBar.setOnTabClickListener(new OnTabClickListener() {
          @Override public void onTabSelected(int selectedTab) {
            if (position != MAIN_ACTIVITY_BOTTOMBAR_POSITION
                && selectedTab == MAIN_ACTIVITY_BOTTOMBAR_POSITION) {
              mainDrawable.color(selectedColor);
              AsyncTask.execute(new Runnable() {
                @Override public void run() {
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
            } else if (position != MAPS_ACTIVITY_BOTTOMBAR_POSITION
                && selectedTab == MAPS_ACTIVITY_BOTTOMBAR_POSITION) {
              mapDrawable.color(selectedColor);
              AsyncTask.execute(new Runnable() {
                @Override public void run() {
                  try {
                    Thread.sleep(100);
                  } catch (InterruptedException e) {
                    e.printStackTrace();
                  }
                  Intent intent = new Intent(activity, MapsActivity.class);
                  intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                  startActivity(intent);
                  overridePendingTransition(0, 0);
                  activity.finish();
                }
              });
            } else if (position != PROFILE_ACTIVITY_BOTTOMBAR_POSITION
                && selectedTab == PROFILE_ACTIVITY_BOTTOMBAR_POSITION) {
              profileDrawable.color(selectedColor);
              AsyncTask.execute(new Runnable() {
                @Override public void run() {
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
            }
          }

          @Override public void onTabReSelected(int position) {
            if (activity instanceof MainActivity) {
              NestedScrollView nestedScrollView =
                  (NestedScrollView) findViewById(R.id.nestedScrollView);
              ObjectAnimator.ofInt(nestedScrollView, "scrollY", 0).setDuration(150).start();
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
