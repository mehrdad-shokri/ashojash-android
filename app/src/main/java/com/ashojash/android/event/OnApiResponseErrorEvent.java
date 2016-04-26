package com.ashojash.android.event;

import com.ashojash.android.model.ApiResponseError;

public class OnApiResponseErrorEvent {
    public ApiResponseError error;

    public OnApiResponseErrorEvent(ApiResponseError error) {
        this.error = error;
    }
}
