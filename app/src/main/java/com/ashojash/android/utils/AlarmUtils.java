package com.ashojash.android.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.service.RefreshTokenService;

import java.util.Random;

public class AlarmUtils {

    private static AlarmManager alarmManager;

    private static AlarmManager getInstance(Context context) {
        if (alarmManager == null)
            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        return alarmManager;
    }


    public static void setRefreshTokenAlarm(Context context) {
        boolean isRefreshTokenSet = PendingIntent.getService(AppController.context, AppController.REFRESH_TOKEN_REQUEST_CODE, new Intent(AppController.context, RefreshTokenService.class), PendingIntent.FLAG_NO_CREATE) != null;
        if (!isRefreshTokenSet) {
            int randomJitter = new Random().nextInt(14400000);
            long interval = AlarmManager.INTERVAL_HALF_DAY + randomJitter;
            long triggerAt = System.currentTimeMillis() + interval;
            PendingIntent refreshTokenPendingIntent = PendingIntent.getService(AppController.context, AppController.REFRESH_TOKEN_REQUEST_CODE, new Intent(AppController.context, RefreshTokenService.class), PendingIntent.FLAG_UPDATE_CURRENT);
            getInstance(context).setRepeating(AlarmManager.RTC_WAKEUP, triggerAt, interval, refreshTokenPendingIntent);
        }
    }
}
