package com.ashojash.android.webserver;

import android.net.Uri;
import com.android.volley.Request;

public final class UrlController {

    private static final String SCHEME = "http";
    private static final String AUTHORITY = "www.site.mil";
    private static final String API_NAME = "api";
    private static final String API_VERSION = "v1";

    private static final int TOP_VENUES_METHOD = Request.Method.GET;
    private static final int CITY_LIST_METHOD = Request.Method.GET;
    private static final int CITY_NEARBY_VENUES_METHOD = Request.Method.GET;
    private static final int CITY_SELECTED_VENUES_METHOD = Request.Method.GET;
    private static final int USER_REGISTER_METHOD = Request.Method.POST;
    private static final int GOOGLE_OAUTH_SEND_TOKEN_ID_METHOD = Request.Method.POST;
    private static final int USER_LOGIN_METHOD = Request.Method.POST;
    private static final int VENUE_INFO_METHOD = Request.Method.GET;
    private static final int REFRESH_TOKEN_METHOD = Request.Method.POST;
    private static final int VENUE_REVIEWS_METHOD = Request.Method.GET;
    private static final int VENUE_MENUS_METHOD = Request.Method.GET;
    private static final int VENUE_PHOTOS_METHOD = Request.Method.GET;
    private static final int VENUE_UPLOAD_REVIEW_METHOD = Request.Method.POST;

    public static int getCityTopVenuesMethod() {
        return TOP_VENUES_METHOD;
    }

    public static int getCityListMethod() {
        return CITY_LIST_METHOD;
    }

    public static int getCityNearbyVenuesMethod() {
        return CITY_NEARBY_VENUES_METHOD;
    }

    public static int getCitySelectedVenuesMethod() {
        return CITY_SELECTED_VENUES_METHOD;
    }

    public static int getGoogleOauthSendAuthCodeMethod() {
        return GOOGLE_OAUTH_SEND_TOKEN_ID_METHOD;
    }

    public static int getUserRegisterMethod() {
        return USER_REGISTER_METHOD;
    }


    public static int getUserLoginMethod() {
        return USER_LOGIN_METHOD;
    }

    public static int getVenueMethod() {
        return VENUE_INFO_METHOD;
    }

    public static int getRefreshTokenMethod() {
        return REFRESH_TOKEN_METHOD;
    }

    public static int getVenueReviewsMethod() {
        return VENUE_REVIEWS_METHOD;
    }

    public static int getVenueMenusMethod() {
        return VENUE_MENUS_METHOD;
    }

    public static int getVenuePhotosMethod() {
        return VENUE_PHOTOS_METHOD;
    }


    public static String getCityListURL() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(API_NAME)
                .appendPath(API_VERSION)
                .appendPath("city")
                .appendPath("list")
                .build();
        return builder.toString();
    }

    public static String getCityTopVenuesURL(String citySlug) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(API_NAME)
                .appendPath(API_VERSION)
                .appendPath(citySlug)
                .appendPath("venue")
                .appendPath("top")
                .build();
        return builder.toString();
    }

    public static String getCityNearbyVenuesURL(String citySlug, double lat, double lng) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(API_NAME)
                .appendPath(API_VERSION)
                .appendPath(citySlug)
                .appendPath("venue")
                .appendPath("nearby")
                .appendPath("lat")
                .appendPath(String.valueOf(lat))
                .appendPath("lng")
                .appendPath(String.valueOf(lng))
                .build();
        return builder.toString();
    }

    public static String getCitySelectedVenuesUrl(String citySlug) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(API_NAME)
                .appendPath(API_VERSION)
                .appendPath(citySlug)
                .appendPath("venue")
                .appendPath("selected")
                .build();
        return builder.toString();
    }

    public static String getUserRegisterUrl() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(API_NAME)
                .appendPath(API_VERSION)
                .appendPath("auth")
                .appendPath("register")
                .build();
        return builder.toString();
    }

    public static String getUserGoogleOauthAuthCodeUrl() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(API_NAME)
                .appendPath(API_VERSION)
                .appendPath("auth")
                .appendPath("google")
                .build();

        return builder.toString();
    }


    public static String getUserLoginUrl() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(API_NAME)
                .appendPath(API_VERSION)
                .appendPath("auth")
                .appendPath("login")
                .build();

        return builder.toString();
    }

    public static String getVenueUrl(String slug) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(API_NAME)
                .appendPath(API_VERSION)
                .appendPath("venue")
                .appendPath(slug)
                .build();

        return builder.toString();
    }

    public static String getRefreshTokenUrl() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(API_NAME)
                .appendPath(API_VERSION)
                .appendPath("auth")
                .appendPath("refresh-token")
                .build();
        return builder.toString();
    }

    public static String getVenueUploadPhotoUrl(String venueSlug) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(API_NAME)
                .appendPath(API_VERSION)
                .appendPath("user")
                .appendPath("add-venue-photo")
                .appendPath(venueSlug)
                .build();
        return builder.toString();
    }

    public static String getVenueReviewsUrl(String venueSlug, int limit) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(API_NAME)
                .appendPath(API_VERSION)
                .appendPath("venue")
                .appendPath(venueSlug)
                .appendPath("reviews")
                .appendQueryParameter("limit", String.valueOf(limit))
                .build();
        return builder.toString();
    }

    public static String getVenueMenusUrl(String venueSlug, int limit) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(API_NAME)
                .appendPath(API_VERSION)
                .appendPath("venue")
                .appendPath(venueSlug)
                .appendPath("menus")
                .appendQueryParameter("limit", String.valueOf(limit))
                .build();
        return builder.toString();
    }

    public static String getVenuePhotosUrl(String venueSlug, int limit) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(API_NAME)
                .appendPath(API_VERSION)
                .appendPath("venue")
                .appendPath(venueSlug)
                .appendPath("photos")
                .appendQueryParameter("limit", String.valueOf(limit))
                .build();
        return builder.toString();
    }

    public static int getVenueReviewUploadMethod() {
        return VENUE_UPLOAD_REVIEW_METHOD;
    }

    public static String getVenueReviewUploadUrl(String venueSlug) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(API_NAME)
                .appendPath(API_VERSION)
                .appendPath("user")
                .appendPath("review")
                .appendPath("add")
                .build();
        return builder.toString();
    }
}
