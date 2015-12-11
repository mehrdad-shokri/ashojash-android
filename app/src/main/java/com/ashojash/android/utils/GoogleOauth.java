package com.ashojash.android.utils;

import android.support.v4.app.FragmentActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;

public class GoogleOauth {
    private static GoogleApiClient client;

    public static GoogleApiClient getGoogleApiClientInstane(FragmentActivity activity, GoogleApiClient.OnConnectionFailedListener listener, GoogleSignInOptions options) {
        if (client == null) {

            client = new GoogleApiClient.Builder(activity)
                    .enableAutoManage(activity/* FragmentActivity */, listener /* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                    .build();
        } else {
            client.disconnect();
            client = new GoogleApiClient.Builder(activity)
                    .enableAutoManage(activity/* FragmentActivity */, listener /* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                    .build();
        }
        return client;
    }


}