package com.ashojash.android.webserver;

import com.ashojash.android.event.VenueApiEvents;
import com.ashojash.android.model.*;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;


public final class VenueApi {

    private static Call<Venue> indexCall;
    private static Call<VenuePaginated> searchCall;
    private static Call<List<Review>> reviewsCall;

    private VenueApi() {
    }

    private final static Retrofit RETROFIT;

    private final static Endpoints API;

    static {
        RETROFIT = UrlController.getInstance();
        API = RETROFIT.create(Endpoints.class);
    }

    public static void search(String citySlug, String query, int limit) {
        searchCall = API.search(citySlug, query, limit);
        searchCall.enqueue(new ApiCallback<VenuePaginated>() {

            @Override
            public void onResponse(Call<VenuePaginated> call, Response<VenuePaginated> response) {
                if (call.isCanceled()) return;
                handleResponse(response, new VenueApiEvents.OnSearchResultsReady(response.body()));
            }
        });
    }

    public static void cancelSearch() {
        if (searchCall != null) searchCall.cancel();
    }


    public static void index(String venueSlug) {
        indexCall = API.index(venueSlug);
        indexCall.enqueue(new ApiCallback<Venue>() {
            @Override
            public void onResponse(Call<Venue> call, Response<Venue> response) {
                if (call.isCanceled()) return;
                handleResponse(response, new VenueApiEvents.OnVenueIndexResultsReady(response.body()));
            }
        });
    }

    public static void canelIndex() {
        if (indexCall != null) indexCall.cancel();
    }


    public static void topVenues(String citySlug) {
        Call<List<Venue>> call = API.topVenues(citySlug);
        call.enqueue(new ApiCallback<List<Venue>>() {
            @Override
            public void onResponse(Call<List<Venue>> call, Response<List<Venue>> response) {
                handleResponse(response, new VenueApiEvents.OnTopVenuesResult(response.body()));

            }
        });
    }

    public static void nearbyVenues(String citySlug, double lat, double lng) {
        Call<List<Venue>> call = API.nearby(citySlug, lat, lng);
        call.enqueue(new ApiCallback<List<Venue>>() {
            @Override
            public void onResponse(Call<List<Venue>> call, Response<List<Venue>> response) {
                handleResponse(response, new VenueApiEvents.OnNearbyVenuesResult(response.body()));
            }
        });
    }

    public static void selectedVenues(String citySlug) {
        Call<List<Venue>> call = API.selected(citySlug);
        call.enqueue(new ApiCallback<List<Venue>>() {
            @Override
            public void onResponse(Call<List<Venue>> call, Response<List<Venue>> response) {
                handleResponse(response, new VenueApiEvents.OnSelectedVenuesResult(response.body()));
            }
        });
    }

    public static void menus(String venueSlug) {
        Call<List<Menu>> call = API.menus(venueSlug);
        call.enqueue(new ApiCallback<List<Menu>>() {
            @Override
            public void onResponse(Call<List<Menu>> call, Response<List<Menu>> response) {
                handleResponse(response, new VenueApiEvents.OnVenueMenusResponse(response.body()));
            }
        });
    }

    public static void photos(String venueSlug) {
        Call<List<Photo>> call = API.photos(venueSlug);
        call.enqueue(new ApiCallback<List<Photo>>() {
            @Override
            public void onResponse(Call<List<Photo>> call, Response<List<Photo>> response) {
                handleResponse(response, new VenueApiEvents.OnVenuePhotosResponse(response.body()));
            }
        });
    }

    private static final String TAG = "VenueApi";

    public static void reviews(String venueSlug) {
        reviewsCall = API.reviews(venueSlug);
        reviewsCall.enqueue(new ApiCallback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                if (call.isCanceled()) return;
                handleResponse(response, new VenueApiEvents.OnVenueReviewsResponse(response.body()));
            }
        });
    }

    public static void cancelReviewsCall() {
        if (reviewsCall != null) reviewsCall.cancel();
    }

    private interface Endpoints {
        //list all venueOrm related API endpoints
        @GET("{citySlug}/venue/top")
        Call<List<Venue>> topVenues(@Path("citySlug") String citySlug);

        @GET("{citySlug}/venue/nearby/lat/{lat}/lng/{lng}")
        Call<List<Venue>> nearby(@Path("citySlug") String citySlug, @Path("lat") double lat, @Path("lng") double lng);

        @GET("{citySlug}/venue/selected")
        Call<List<Venue>> selected(@Path("citySlug") String citySlug);

        @GET("venue/search/city/{citySlug}")
        Call<VenuePaginated> search(@Path("citySlug") String citySlug, @Query("q") String query, @Query("l") int limit);

        @GET("venue/{venueSlug}?include=menus,photos,reviews")
        Call<Venue> index(@Path("venueSlug") String venueSlug);

        @GET("venue/{venueSlug}/reviews")
        Call<List<Review>> reviews(@Path("venueSlug") String venueSlug);

        @GET("venue/{venueSlug}/menus")
        Call<List<Menu>> menus(@Path("venueSlug") String venueSlug);

        @GET("venue/{venueSlug}/photos")
        Call<List<Photo>> photos(@Path("venueSlug") String venueSlug);
    }


}
