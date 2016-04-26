package com.ashojash.android.model;

import com.google.gson.annotations.SerializedName;

public class ApiResponseError {
    @SerializedName("status_code")
    public int statusCode;

    public String message;
}
