package com.ashojash.android.event;

import com.ashojash.android.model.ApiRequestError;

public class OnApiRequestErrorEvent {
    public ApiRequestError error;

    public OnApiRequestErrorEvent(ApiRequestError error) {
        this.error = error;
    }
}
