package com.ashojash.android.webserver;

import android.util.Log;
import com.ashojash.android.event.ErrorEvents;
import com.ashojash.android.event.OnApiResponseErrorEvent;
import com.ashojash.android.event.UserApiEvents;
import com.ashojash.android.event.VenueApiEvents;
import com.ashojash.android.model.*;
import com.ashojash.android.utils.BusProvider;
import com.ashojash.android.utils.ErrorUtils;
import com.google.gson.Gson;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.greenrobot.eventbus.EventBus;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.*;

import java.io.File;
import java.io.IOException;

public class UserApi {

    private static Call<User> loginCall;
    private static Call<UserRegistered> registerCall;
    private static Call<User> googleCall;
    private static Call<Void> uploadPhotoCall;
    private static Call<Void> addReviewCall;

    private UserApi() {
    }

    private static final Retrofit RETROFIT;
    private static final Retrofit AUTH_RETROFIT;
    private static final UserApi.Endpoints API;
    private static final UserApi.Endpoints AUTH_API;
    private static final EventBus BUS;

    static {
        RETROFIT = UrlController.getInstance();
        API = RETROFIT.create(UserApi.Endpoints.class);
        AUTH_RETROFIT = UrlController.getAuthInstance();
        AUTH_API = AUTH_RETROFIT.create(UserApi.Endpoints.class);
        BUS = BusProvider.getInstance();
    }

    public static void google(String authCode) {
        googleCall = API.google(authCode);
        googleCall.enqueue(new ApiCallback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                handleResponse(response, new UserApiEvents.OnUserGoogleHandled(response.body()));
            }
        });
    }

    public static void login(String login, String password) {
        loginCall = API.login(login, password);
        loginCall.enqueue(new ApiCallback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (call.isCanceled()) return;
                if (response.isSuccessful()) {
                    BUS.post(new UserApiEvents.OnUserLoggedIn(response.body()));
                } else {
                    if (response.code() == 400) {
                        BUS.post(new ErrorEvents.OnUserLoginFailed(ErrorUtils.parseError(response)));
                    } else {
                        BUS.post(new OnApiResponseErrorEvent(new ApiResponseError()));
                    }
                }
            }
        });
    }

    public static void cancelLogin() {
        if (loginCall != null) loginCall.cancel();
    }

    public static void register(String name, String username, String password, String email) {
        registerCall = API.register(name, username, password, email);
        registerCall.enqueue(new ApiCallback<UserRegistered>() {
            @Override
            public void onResponse(Call<UserRegistered> call, Response<UserRegistered> response) {
                if (call.isCanceled()) return;
                if (response.isSuccessful()) {
                    BUS.post(new UserApiEvents.onUserRegistered(response.body()));
                } else {
                    if (response.code() == 400) {
                        ApiResponseError validationError = ErrorUtils.parseError(response);
                        Log.d(TAG, "onResponse: " + validationError.message);
                        Gson gson = new Gson();
                        BUS.post(new ErrorEvents.OnUserRegistrationFailed(gson.fromJson(validationError.message, ValidationError.class)));
                    } else {
                        BUS.post(new OnApiResponseErrorEvent(new ApiResponseError()));
                    }
                }
            }
        });
    }


    public static void cancelRegister() {
        if (registerCall != null) registerCall.cancel();
    }

    public static void addReview(String venueSlug, int quality, int cost, int decor, String comment) {
        addReviewCall = AUTH_API.addReview(comment, quality, cost, decor, venueSlug);
        addReviewCall.enqueue(new ApiCallback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d(TAG, "onResponse: " + response.isSuccessful());
                if (response.isSuccessful()) {
                    BUS.post(new VenueApiEvents.OnReviewAdded());
                } else {
                    if (response.code() == 400) {
                        BUS.post(new ErrorEvents.OnReviewAddFailed(ErrorUtils.parseError(response)));
                    } else {
                        BUS.post(new OnApiResponseErrorEvent(new ApiResponseError()));
                    }
//                handleResponse(response, new VenueApiEvents.OnReviewAdded());
                }
            }
        });
    }
    public static void cancelReviewCall() {
        if (addReviewCall != null) addReviewCall.cancel();
    }

    public static void uploadPhoto(File file, String venueSlug) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        uploadPhotoCall = AUTH_API.addPhoto(part, venueSlug);
        try {
            Response<Void> response = uploadPhotoCall.execute();
            if (response.isSuccessful()) {
                BUS.post(new VenueApiEvents.OnPhotoAdded());
            } else {
                ApiResponseError validationError = ErrorUtils.parseError(response);
                BUS.post(new OnApiResponseErrorEvent(validationError));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*uploadPhotoCall.enqueue(new ApiCallback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }
        });*/
    }
/*
    public static void uploadPhotos(File[] files, String venueSlug) {
        for (int i = 0; i < files.length; i++) {
            uploadPhotoCall = AUTH_API.addPhoto(files[i], venueSlug);
            uploadPhotoCall.enqueue(new ApiCallback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                }
            });
        }
    }*/

    public static void cancelPhotoUploadCall() {
        if (uploadPhotoCall != null) uploadPhotoCall.cancel();
    }


    public static void refreshToken(String token) {
        Call<Token> call = AUTH_API.refreshToken(token);
        call.enqueue(new ApiCallback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                handleResponse(response, new UserApiEvents.OnTokenRefreshed(response.body()));
            }
        });
    }


    private interface Endpoints {
        @FormUrlEncoded
        @POST("auth/register")
        Call<UserRegistered> register(@Field("name") String name, @Field("username") String username, @Field("password") String password, @Field("email") String email);

        @FormUrlEncoded
        @POST("auth/login?include=token")
        Call<User> login(@Field("login") String login, @Field("password") String password);

        @FormUrlEncoded
        @POST("auth/google?include=googleOAuth,token")
        Call<User> google(@Field("auth-code") String authCode);

        @POST("auth/refreshToken")
        Call<Token> refreshToken(@Field("token") String token);

        @FormUrlEncoded
        @POST("user/review/add")
        Call<Void> addReview(@Field("comment") String comment, @Field("quality") int quality, @Field("cost") int cost, @Field("decor") int decor, @Field("slug") String slug);

        @Multipart
        @POST("user/addVenuePhoto/{venueSlug}")
        Call<Void> addPhoto(@Part MultipartBody.Part file, @Path("venueSlug") String venueSlug);
    }
}
