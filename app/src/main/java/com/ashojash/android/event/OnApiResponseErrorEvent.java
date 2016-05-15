package com.ashojash.android.event;

import com.ashojash.android.model.ApiResponseError;

public class OnApiResponseErrorEvent<T> {
    public ApiResponseError error;
    public Object object;

    public OnApiResponseErrorEvent(ApiResponseError error) {
        this.error = error;
    }

    public OnApiResponseErrorEvent(ApiResponseError error, Object object) {
        this(error);
        this.object = object;
    }
}
