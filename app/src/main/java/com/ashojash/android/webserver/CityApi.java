package com.ashojash.android.webserver;

import com.ashojash.android.event.CityApiEvents;
import com.ashojash.android.model.City;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;

import java.util.List;

public class CityApi {
    private static final Retrofit retrofit;

    static {
        retrofit = UrlController.getInstance();
    }


    public static void getAllCities() {
        CityApi.EndPoints api = retrofit.create(CityApi.EndPoints.class);
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
