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
import com.ashojash.android.utils.AuthUtils;
import com.ashojash.android.utils.BusProvider;
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
        super.onStart(intent, startId);
        if (!BusProvider.getInstance().isRegistered(this))
            BusProvider.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BusProvider.unregister(this);
    }

    String TAG = AppController.TAG;

    @Subscribe
    public void onEvent(UserApiEvents.OnTokenRefreshed event) {
        Token token = event.token;
        AuthUtils.updateTokenPayload(token);
    }

    @Subscribe
    public void onEvent(OnApiResponseErrorEvent event) {
        AuthUtils.logoutUser();
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
