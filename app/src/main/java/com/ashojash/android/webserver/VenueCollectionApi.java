package com.ashojash.android.webserver;

import com.ashojash.android.event.VenueCollectionEvents;
import com.ashojash.android.model.VenueCollection;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

public class VenueCollectionApi extends BaseApi {
    private static final EndPoints API;
    private static Call<List<VenueCollection>> collectionsCall;
    private static Call<VenueCollection> indexCall;

    static {
        API = RETROFIT.create(EndPoints.class);
    }


    private VenueCollectionApi() {
    }

    public static void collections(String citySlug) {
        collectionsCall = API.collection(citySlug);
        collectionsCall.enqueue(new ApiCallback<List<VenueCollection>>() {
            @Override
            public void onResponse(Call<List<VenueCollection>> call, Response<List<VenueCollection>> response) {
                if (!call.isCanceled())
                    handleResponse(response, new VenueCollectionEvents.OnVenueCollectionsResponse(response.body()));
            }
        });
    }

    public static void index(String citySlug, String collectionSlug) {
        indexCall = API.index(citySlug, collectionSlug);
        indexCall.enqueue(new ApiCallback<VenueCollection>() {
            @Override
            public void onResponse(Call<VenueCollection> call, Response<VenueCollection> response) {
                if (!call.isCanceled())
                    handleResponse(response, new VenueCollectionEvents.OnVenueCollectionVenuesResponse(response.body()));
            }
        });
    }

    public static void cancelCollectionsCall() {
        if (collectionsCall != null) collectionsCall.cancel();
    }

    public static void cancelIndex() {
        if (indexCall != null) indexCall.cancel();
    }


    private interface EndPoints {
        @GET("city/{citySlug}/collections")
        Call<List<VenueCollection>> collection(@Path("citySlug") String citySlug);

        @GET("city/{citySlug}/collection/{collectionSlug}")
        Call<VenueCollection> index(@Path("citySlug") String citySlug, @Path("collectionSlug") String collectionSlug);

    }
}
