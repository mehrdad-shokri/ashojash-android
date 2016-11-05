package com.ashojash.android.webserver;

import com.ashojash.android.event.SearchApiEvents;
import com.ashojash.android.model.VenueTagCombined;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class SearchApi extends BaseApi {
  private final static SearchApi.Endpoints API;
  private static Call<VenueTagCombined> suggestCall;

  static {
    API = RETROFIT.create(SearchApi.Endpoints.class);
  }

  private SearchApi() {
  }

  private static final String TAG = "SearchApi";

  public static void suggest(String query, double lat, double lng) {
    suggestCall = API.search(query, lat, lng);
    suggestCall.enqueue(new ApiCallback<VenueTagCombined>() {
      @Override
      public void onResponse(Call<VenueTagCombined> call, Response<VenueTagCombined> response) {
        if (call.isCanceled()) return;
        handleResponse(response, new SearchApiEvents.OnSuggestResultsReady(response.body()));
      }
    });
  }

  public static void cancelSuggest() {
    if (suggestCall != null) suggestCall.cancel();
  }

  private interface Endpoints {
    @FormUrlEncoded @POST("term/suggest") Call<VenueTagCombined> search(
        @Field("query") String query, @Field("lat") double lat, @Field("lng") double lng);
  }
}
