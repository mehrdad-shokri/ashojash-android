package com.ashojash.android.webserver;

import retrofit2.Retrofit;

public class BaseApi {
    protected final static Retrofit RETROFIT;
    protected final static Retrofit AUTH_RETROFIT;

    static {
        RETROFIT = UrlController.getInstance();
        AUTH_RETROFIT = UrlController.getAuthInstance();
    }
}
