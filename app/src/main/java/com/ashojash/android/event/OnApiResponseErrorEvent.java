package com.ashojash.android.event;

import com.ashojash.android.model.ApiResponseError;

public class OnApiResponseErrorEvent {
    public ApiResponseError error;
    public Object object;

    public OnApiResponseErrorEvent(ApiResponseError error, Object event) {
        this.error = error;
        this.object = event;
    }
}
