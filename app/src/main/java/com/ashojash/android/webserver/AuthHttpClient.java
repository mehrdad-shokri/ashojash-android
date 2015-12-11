package com.ashojash.android.webserver;

import com.ashojash.android.utils.AuthUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.SyncHttpClient;

public class AuthHttpClient {
    private static AsyncHttpClient authAsyncClient;
    private static SyncHttpClient authSyncClient;


    public static AsyncHttpClient getAuthAsyncHttpClientInstance() {
        if (authAsyncClient == null) {
            authAsyncClient = new AsyncHttpClient();
            authAsyncClient.addHeader("Authorization", "Bearer " + AuthUtils.getToken());
        }
        return authAsyncClient;
    }

    public static SyncHttpClient getAuthSyncHttpClient() {
        if (authSyncClient == null) {
            authSyncClient = new SyncHttpClient();
            authSyncClient.addHeader("Authorization", "Bearer " + AuthUtils.getToken());
        }
        return authSyncClient;
    }
}
