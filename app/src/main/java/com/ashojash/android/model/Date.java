package com.ashojash.android.model;

import com.google.gson.annotations.SerializedName;

public class Date {
    public String date;

    @SerializedName("timezone_type")
    public int typezoneType;

    public String timezone;
}
