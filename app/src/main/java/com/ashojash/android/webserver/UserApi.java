package com.ashojash.android.webserver;

import com.ashojash.android.event.ErrorEvents;
import com.ashojash.android.event.OnApiResponseErrorEvent;
import com.ashojash.android.event.UserApiEvents;
import com.ashojash.android.event.VenueApiEvents;
import com.ashojash.android.model.*;
import com.ashojash.android.util.BusUtil;
import com.ashojash.android.util.ErrorUtils;
import com.google.gson.Gson;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.greenrobot.eventbus.EventBus;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.*;

import java.io.File;
import java.io.IOException;

public class UserApi extends BaseApi {

    private static Call<User> loginCall;
    private static Call<UserRegistered> registerCall;
    private static Call<User> googleCall;
    private static Call<Void> uploadPhotoCall;
    private static Call<Void> addReviewCall;

    private UserApi() {
    }

    private static final UserApi.Endpoints API;
    private static final UserApi.Endpoints AUTH_API;
    private static final EventBus BUS;

    static {
        API = RETROFIT.create(UserApi.Endpoints.class);
        AUTH_API = AUTH_RETROFIT.create(UserApi.Endpoints.class);
        BUS = BusUtil.getInstance();
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
                if (response.code() == 400) {
                    BUS.post(new ErrorEvents.OnUserLoginFailed(ErrorUtils.parseError(response)));
                    return;
                }
                handleResponse(response, new UserApiEvents.OnUserLoggedIn(response.body()));
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

                                     if (response.code() == 400) {
                                         ApiResponseError validationError = ErrorUtils.parseError(response);
                                         Gson gson = new Gson();
                                         BUS.post(new ErrorEvents.OnUserRegistrationFailed(gson.fromJson(validationError.message, ValidationError.class)));
                                         return;
                                     }
                                     handleResponse(response, new UserApiEvents.onUserRegistered(response.body()));
                                 }
                             }

        );
    }


    public static void cancelRegister() {
        if (registerCall != null) registerCall.cancel();
    }

    public static void addReview(String venueSlug, int quality, int cost, int decor, String comment) {
        addReviewCall = AUTH_API.addReview(comment, quality, cost, decor, venueSlug);
        addReviewCall.enqueue(new ApiCallback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    BUS.post(new VenueApiEvents.OnReviewAdded());
                } else {
                    if (response.code() == 400) {
                        BUS.post(new ErrorEvents.OnReviewAddFailed(ErrorUtils.parseError(response)));
                    } else {
                        handleResponse(response, new VenueApiEvents.OnReviewAddFailed());
                    }
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
                BUS.post(new OnApiResponseErrorEvent(validationError, new VenueApiEvents.onPhotoUploadValidationFailed()));
            }
        } catch (IOException e) {
            BUS.post(new OnApiResponseErrorEvent(new ApiResponseError(), new VenueApiEvents.onPhotoUploadValidationFailed()));
        }
    }

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

        @FormUrlEncoded
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
