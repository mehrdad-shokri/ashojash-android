package com.ashojash.android.activity;

import android.Manifest;
import android.content.Intent;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.util.AlarmUtil;
import com.ashojash.android.util.PermissionUtil;

/*
* Checked for bus and json
* */
public class SplashActivity extends BaseActivity {

    @Override
    protected void onResume() {
        super.onResume();
        boolean isFirstVisit = AppController.defaultPref.getBoolean("is_first_visit", true);
        boolean hasShownIntro = AppController.defaultPref.getBoolean("has_shown_intro", false);
        boolean hasLocationPermission = PermissionUtil.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        String citySlug = AppController.defaultPref.getString("current_city_slug", null);
        Intent intent;
        AlarmUtil.setRefreshTokenAlarm(AppController.context);
        if (!(hasShownIntro && hasLocationPermission)) {
            intent = new Intent(this, AshojashIntroActivity.class);
            intent.putExtra("HAS_SHOWN_INTRO", hasShownIntro);
            intent.putExtra("HAS_LOCATION_PERMISSION", hasLocationPermission);
            AppController.editor.putBoolean("has_shown_intro", true).commit();
        } else if (isFirstVisit || (citySlug == null)) {
            intent = new Intent(AppController.currentActivity, CityListActivity.class);
            AppController.editor.putBoolean("is_first_visit", false).commit();
        } else {
            intent = new Intent(AppController.currentActivity, MainActivity.class);
            intent.putExtra("current_city_slug", citySlug);
        }
        startActivity(intent);
        finish();
    }
}
