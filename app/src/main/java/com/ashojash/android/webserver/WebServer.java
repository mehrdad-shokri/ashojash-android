package com.ashojash.android.webserver;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

public class WebServer {
    private static RetryPolicy retryPolicy;

    static {
        retryPolicy = new DefaultRetryPolicy(15000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    }

    public static JsonObjectRequest getCityList(Response.Listener<JSONObject> onSuccess, Response.ErrorListener onError) {
        JsonObjectRequest request = new JsonObjectRequest(UrlController.getCityListMethod(),
                UrlController.getCityListURL(), onSuccess, onError);
        request.setRetryPolicy(retryPolicy);
        return request;
    }

    public static JsonObjectRequest getCityTopVenues(Response.Listener<JSONObject> onSuccess, Response.ErrorListener onError, String citySlug) {
        JsonObjectRequest request = new JsonObjectRequest(UrlController.getCityTopVenuesMethod(), UrlController.getCityTopVenuesURL(citySlug), onSuccess, onError);
        request.setRetryPolicy(retryPolicy);
        return request;
    }

    public static JsonObjectRequest getCityNearbyVenues(Response.Listener<JSONObject> onSuccess, Response.ErrorListener onError, String citySlug, double lat, double lng) {
        JsonObjectRequest request = new JsonObjectRequest(UrlController.getCityNearbyVenuesMethod(), UrlController.getCityNearbyVenuesURL(citySlug, lat, lng), onSuccess, onError);
        request.setRetryPolicy(retryPolicy);
        return request;
    }

    public static JsonObjectRequest getEliteVenue(Response.Listener<JSONObject> onSuccess, Response.ErrorListener onError, String citySlug) {
        JsonObjectRequest request = new JsonObjectRequest(UrlController.getCitySelectedVenuesMethod(), UrlController.getCitySelectedVenuesUrl(citySlug), onSuccess, onError);
        request.setRetryPolicy(retryPolicy);
        return request;
    }

    public static JsonObjectRequest getVenueBasicInfo(Response.Listener<JSONObject> onSuccess, Response.ErrorListener onError, String venueSlug) {
        JsonObjectRequest request = new JsonObjectRequest(UrlController.getVenueMethod(), UrlController.getVenueUrl(venueSlug), onSuccess, onError);
        request.setRetryPolicy(retryPolicy);
        return request;
    }

    public static JsonObjectRequest postRegisterUser(Response.Listener<JSONObject> onSuccess, Response.ErrorListener onError, String name, String username, String password, String email) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
            jsonObject.put("email", email);
            jsonObject.put("password", password);
            jsonObject.put("name", name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(UrlController.getUserRegisterMethod(), UrlController.getUserRegisterUrl(), jsonObject.toString(), onSuccess, onError);
        request.setRetryPolicy(retryPolicy);
        return request;
    }

    public static JsonObjectRequest postGoogleAuthCode(Response.Listener<JSONObject> onSuccess, Response.ErrorListener onError, String authCode) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("auth-code", authCode);
        } catch (JSONException ignored) {
        }

        JsonObjectRequest request = new JsonObjectRequest(UrlController.getGoogleOauthSendAuthCodeMethod(), UrlController.getUserGoogleOauthAuthCodeUrl(), jsonObject.toString(), onSuccess, onError);
        request.setRetryPolicy(retryPolicy);
        return request;
    }

    public static JsonObjectRequest postSignInUser(Response.Listener<JSONObject> onSuccess, Response.ErrorListener onError, String login, String password) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("login", login);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(UrlController.getUserLoginMethod(), UrlController.getUserLoginUrl(), jsonObject.toString(), onSuccess, onError);
        request.setRetryPolicy(retryPolicy);
        return request;
    }

    public static JsonObjectRequest refreshToken(Response.Listener<JSONObject> onSuccess, Response.ErrorListener onError) {
        JsonObjectRequest request = new AuthJsonObjectRequest(UrlController.getRefreshTokenMethod(), UrlController.getRefreshTokenUrl(), onSuccess, onError);
        request.setRetryPolicy(retryPolicy);
        return request;
    }

    private static AsyncHttpClient client = new AsyncHttpClient();

    private static void UploadVenuePhotos(String venueSlug, File[] files, AsyncHttpResponseHandler handler) throws FileNotFoundException {
        RequestParams params = new RequestParams();
        params.put("file", files);
        params.setHttpEntityIsRepeatable(true);
        AuthAsyncHttpClient.getInstance().post(UrlController.getVenueUploadPhotoUrl(venueSlug), params, handler);
    }

    public static void UploadVenuePhoto(String venueSlug, File file, AsyncHttpResponseHandler handler) throws FileNotFoundException {
        RequestParams params = new RequestParams();
        params.put("file", file);
        params.setHttpEntityIsRepeatable(true);
//        params.setUseJsonStreamer(true);
        AuthHttpClient.getAuthSyncHttpClient().post(UrlController.getVenueUploadPhotoUrl(venueSlug), params, handler);
    }

    public static JsonObjectRequest getVenueReviews(Response.Listener<JSONObject> onSuccess, Response.ErrorListener onError, String slug, int limit) {
        JsonObjectRequest request = new JsonObjectRequest(UrlController.getVenueReviewsMethod(), UrlController.getVenueReviewsUrl(slug, limit), onSuccess, onError);
        request.setRetryPolicy(retryPolicy);
        return request;
    }

    public static JsonObjectRequest getVenueMenus(Response.Listener<JSONObject> onSuccess, Response.ErrorListener onError, String slug, int limit) {
        JsonObjectRequest request = new JsonObjectRequest(UrlController.getVenueMenusMethod(), UrlController.getVenueMenusUrl(slug, limit), onSuccess, onError);
        request.setRetryPolicy(retryPolicy);
        return request;
    }

    public static JsonObjectRequest getVenuePhotos(Response.Listener<JSONObject> onSuccess, Response.ErrorListener onError, String slug, int limit) {
        JsonObjectRequest request = new JsonObjectRequest(UrlController.getVenuePhotosMethod(), UrlController.getVenuePhotosUrl(slug, limit), onSuccess, onError);
        request.setRetryPolicy(retryPolicy);
        return request;
    }

    public static JsonObjectRequest uploadVenueReview(String venueSlug, Response.Listener<JSONObject> onSuccess, Response.ErrorListener onError, String review, float qualityRating, float costRating, float decorRating) {
        JSONObject object = new JSONObject();
        try {
            object.put("comment", review);
            object.put("quality", qualityRating);
            object.put("cost", costRating);
            object.put("decor", decorRating);
            object.put("slug", venueSlug);
            JsonObjectRequest request = new AuthJsonObjectRequest(UrlController.getVenueReviewUploadMethod(), UrlController.getVenueReviewUploadUrl(venueSlug), object, onSuccess, onError);
            request.setRetryPolicy(retryPolicy);
            return request;

        } catch (JSONException e) {
            return null;
        }
    }

    public static JsonObjectRequest searchVenues(String citySlug, String query, int limit, Response.Listener<JSONObject> onSuccess, Response.ErrorListener onError) {
        JsonObjectRequest request = new JsonObjectRequest(UrlController.getVenuePhotosMethod(), UrlController.getSearchVenuesUrl(citySlug, query, limit), onSuccess, onError);
        request.setRetryPolicy(retryPolicy);
        return request;
    }
}
