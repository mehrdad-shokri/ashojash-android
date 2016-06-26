package com.ashojash.android.model;

import retrofit2.Call;

public class ApiRequestError<T> {
    public String message;
    public Call<T> call;

    public ApiRequestError(Call<T> call,String message) {
        this.message = message;
        this.call = call;
    }
}
