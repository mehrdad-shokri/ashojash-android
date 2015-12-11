package com.ashojash.android.webserver;

import com.ashojash.android.utils.AuthUtils;
import com.loopj.android.http.AsyncHttpClient;

public final class AuthAsyncHttpClient {
    private static AsyncHttpClient authClient;

    private AuthAsyncHttpClient() {
    }

    public static AsyncHttpClient getInstance() {
        if (authClient == null) {
            authClient = new AsyncHttpClient();
            authClient.addHeader("Authorization", "Bearer " + AuthUtils.getToken());
        }
        return authClient;
    }
}
