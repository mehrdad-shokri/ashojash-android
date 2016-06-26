package com.ashojash.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.ashojash.android.util.AlarmUtil;

public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmUtil.setRefreshTokenAlarm(context);
    }
}
