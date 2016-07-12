package com.ashojash.android.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.ashojash.android.event.OnApiRequestErrorEvent;
import com.ashojash.android.event.OnApiResponseErrorEvent;
import com.ashojash.android.event.UserApiEvents;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.Token;
import com.ashojash.android.util.AuthUtil;
import com.ashojash.android.util.BusUtil;
import com.ashojash.android.webserver.UserApi;
import org.greenrobot.eventbus.Subscribe;

public class RefreshTokenService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onStart(Intent intent, int startId) {
//        LocalBroadcastManager.getInstance(this).
        super.onStart(intent, startId);
        if (!BusUtil.getInstance().isRegistered(this))
            BusUtil.getInstance().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BusUtil.getInstance().unregister(this);
    }

    @Subscribe
    public void onEvent(UserApiEvents.OnTokenRefreshed event) {
        Token token = event.token;
        AuthUtil.updateTokenPayload(token);
    }

    @Subscribe
    public void onEvent(OnApiResponseErrorEvent event) {
        AuthUtil.logoutUser();
    }

    @Subscribe
    public void onEvent(OnApiRequestErrorEvent event) {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String token = AppController.obsPref.getString("token", null);
        if (token != null) {
            UserApi.refreshToken(token);
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
