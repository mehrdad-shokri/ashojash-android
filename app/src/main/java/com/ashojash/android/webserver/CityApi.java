package com.ashojash.android.webserver;

import android.util.Log;
import com.ashojash.android.event.CityApiEvents;
import com.ashojash.android.event.OnApiResponseErrorEvent;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.ApiRequestError;
import com.ashojash.android.model.ApiResponseError;
import com.ashojash.android.model.City;
import com.ashojash.android.utils.BusProvider;
import com.ashojash.android.utils.ErrorUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;

import java.util.List;

public class CityApi {
    private static final Retrofit retrofit;

    static {
        retrofit = UrlController.getInstance();
    }

    private static String TAG = AppController.TAG;

    public static void getAllCities() {
        CityApi.EndPoints api = retrofit.create(CityApi.EndPoints.class);
        Call<List<City>> call = api.all();
        call.enqueue(new Callback<List<City>>() {
            @Override
            public void onResponse(Call<List<City>> call, Response<List<City>> response) {
                if (response.isSuccessful()) {
                    BusProvider.getInstance().post(new CityApiEvents.OnAllCitiesAvailable(response.body()));
                } else {
                    ApiResponseError apiError = ErrorUtils.parseError(response);
                    BusProvider.getInstance().post(new OnApiResponseErrorEvent(apiError));
                }
            }

            @Override
            public void onFailure(Call<List<City>> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t.getMessage());
                BusProvider.getInstance().post(new ApiRequestError(t.getMessage()));
            }
        });
    }

    private interface EndPoints {

        @GET("city/all")
        Call<List<City>> all();
    }
}
