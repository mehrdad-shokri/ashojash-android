package com.ashojash.android.webserver;

import android.support.annotation.Nullable;
import android.util.Log;
import com.ashojash.android.R;
import com.ashojash.android.event.SearchApiEvents;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.Street;
import com.ashojash.android.model.Venue;
import com.ashojash.android.model.VenueTagCombined;
import java.util.List;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class SearchApi extends BaseApi {
  private final static SearchApi.Endpoints API;
  private static Call<VenueTagCombined> suggestCall;
  private static Call<List<Street>> streetSuggestCall;
  private static Call<List<Street>> nearbyStreetsCall;
  private static Call<List<Venue>> searchCall;

  static {
    API = RETROFIT.create(SearchApi.Endpoints.class);
  }

  private SearchApi() {
  }

  private static final String TAG = "SearchApi";

  public static void suggestVenueTag(String query, double lat, double lng) {
    Log.d(TAG, "suggestVenueTag: " + query + " " + lat + " " + lng);

    suggestCall = API.suggestVenueTag(query, lat, lng);
    suggestCall.enqueue(new ApiCallback<VenueTagCombined>() {
      @Override public void onFailure(Call<VenueTagCombined> call, Throwable t) {
        super.onFailure(call, t);
      }

      private static final String TAG = "SearchApi";

      @Override
      public void onResponse(Call<VenueTagCombined> call, Response<VenueTagCombined> response) {
        if (call.isCanceled()) return;
        handleResponse(response, new SearchApiEvents.OnSuggestResultsReady(response.body()));
      }
    });
  }

  public static void suggestStreet(String query, double lat, double lng) {
    streetSuggestCall = API.suggestStreet(query, lat, lng);
    streetSuggestCall.enqueue(new ApiCallback<List<Street>>() {
      @Override public void onResponse(Call<List<Street>> call, Response<List<Street>> response) {
        if (call.isCanceled()) return;

        Log.d(TAG, "onResponse: " + call.isCanceled());
        handleResponse(response, new SearchApiEvents.OnStreetSuggestResultsReady(response.body()));
      }
    });
  }

  public static void nearbyStreets(double lat, double lng) {
    nearbyStreetsCall = API.nearbyStreets(lat, lng);
    nearbyStreetsCall.enqueue(new ApiCallback<List<Street>>() {
      @Override public void onResponse(Call<List<Street>> call, Response<List<Street>> response) {
        if (call.isCanceled()) return;
        handleResponse(response, new SearchApiEvents.OnNearbyStreetsResultsReady(response.body()));
      }
    });
  }

  public static void performSearch(double lat, double lng, String query, String streetName) {
    if (streetName.equals(AppController.context.getResources().getString(R.string.near_me))) {
      streetName = null;
    }
    searchCall = API.search(lat, lng, query, streetName);
    searchCall.enqueue(new ApiCallback<List<Venue>>() {
      @Override public void onResponse(Call<List<Venue>> call, Response<List<Venue>> response) {
        if (call.isCanceled()) return;
        handleResponse(response, new SearchApiEvents.onSearchResultsReady(response.body()));
      }
    });
  }

  public static void cancelSuggest() {
    if (suggestCall != null) suggestCall.cancel();
  }

  public static void cancelStreetSuggest() {
    Log.d(TAG, "cancelStreetSuggest: canceling street search call");
    if (streetSuggestCall != null) {
      Log.d(TAG, "cancelStreetSuggest: canceling street search call");
      streetSuggestCall.cancel();
    }
  }

  private interface Endpoints {
    @FormUrlEncoded @POST("term/suggest") Call<VenueTagCombined> suggestVenueTag(
        @Field("query") String query, @Field("lat") double lat, @Field("lng") double lng);

    @FormUrlEncoded @POST("location/suggest") Call<List<Street>> suggestStreet(
        @Field("query") String query, @Field("lat") double lat, @Field("lng") double lng);

    @FormUrlEncoded @POST("location/nearby") Call<List<Street>> nearbyStreets(
        @Field("lat") double lat, @Field("lng") double lng);

    @FormUrlEncoded @POST("venue/search") Call<List<Venue>> search(
        @Field("lat") double lat, @Field("lng") double lng, @Nullable @Field("query") String query,
        @Nullable @Field("streetName") String streetName);
  }
}
