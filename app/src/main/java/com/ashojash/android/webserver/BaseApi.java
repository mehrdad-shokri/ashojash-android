package com.ashojash.android.webserver;

import retrofit2.Retrofit;

public class BaseApi {
    protected final static Retrofit RETROFIT;

    static {
        RETROFIT = UrlController.getInstance();
    }
}
