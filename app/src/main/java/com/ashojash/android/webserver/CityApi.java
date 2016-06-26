package com.ashojash.android.webserver;

import com.ashojash.android.event.CityApiEvents;
import com.ashojash.android.model.City;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;

import java.util.List;

public class CityApi extends BaseApi {
    private CityApi() {
    }

    public static void getAllCities() {
        CityApi.EndPoints api = RETROFIT.create(CityApi.EndPoints.class);
        Call<List<City>> call = api.all();
        call.enqueue(new ApiCallback<List<City>>() {
            @Override
            public void onResponse(Call<List<City>> call, Response<List<City>> response) {
                handleResponse(response, new CityApiEvents.OnAllCitiesAvailable(response.body()));
            }
        });
    }

    private interface EndPoints {

        @GET("city/all")
        Call<List<City>> all();
    }
}
