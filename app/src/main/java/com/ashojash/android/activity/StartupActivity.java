package com.ashojash.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.helper.LangHelper;
import com.ashojash.android.utils.AlarmUtils;

public class StartupActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LangHelper.setLang("fa");
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isFirstVisit = AppController.defaultPref.getBoolean("is_first_visit", true);
        String citySlug = AppController.defaultPref.getString("current_city_slug", null);
        Intent intent;
        String TAG = AppController.TAG;
        Log.d(TAG, "onResume: started StartupActivity");
        AlarmUtils.setRefreshTokenAlarm(AppController.context);
        if (isFirstVisit || (citySlug == null)) {
            AppController.editor.putBoolean("is_first_visit", false);
            AppController.editor.commit();
            intent = new Intent(AppController.currentActivity, CityListActivity.class);
        } else {
            intent = new Intent(AppController.currentActivity, MainActivity.class);
            intent.putExtra("current_city_slug", citySlug);
        }
        startActivity(intent);
        finish();
    }

}
