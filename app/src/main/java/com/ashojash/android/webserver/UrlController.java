package com.ashojash.android.webserver;

import com.ashojash.android.utils.AuthUtils;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public final class UrlController {
//        private static String BASE_URL = "http://www.ashojash.com/api/";
    private static String BASE_URL = "http://192.168.43.148/api/";
    private final static Retrofit RETROFIT;
    private final static Retrofit AUTH_RETROFIT;

    private UrlController() {
    }

    static {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
        OkHttpClient httpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder()
                        .header("ACCEPT", "application/vnd.ashojash.v2+json")
                        .method(original.method(), original.body());

                return chain.proceed(requestBuilder.build());
            }
        }).build();
        RETROFIT = builder.client(httpClient).build();
        OkHttpClient authHttpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                final String TOKEN = AuthUtils.getToken();
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder()
                        .header("Authorization", "Bearer " + TOKEN)
                        .header("ACCEPT", "application/vnd.ashojash.v2+json")
                        .method(original.method(), original.body());

                return chain.proceed(requestBuilder.build());
            }
        }).build();

        AUTH_RETROFIT = builder.client(authHttpClient).build();
    }

    public static Retrofit getInstance() {
        return RETROFIT;
    }

    public static Retrofit getAuthInstance() {
        return AUTH_RETROFIT;
    }

}

