package com.ashojash.android.webserver;

import com.ashojash.android.event.OnApiRequestErrorEvent;
import com.ashojash.android.event.OnApiResponseErrorEvent;
import com.ashojash.android.model.ApiRequestError;
import com.ashojash.android.util.BusUtil;
import com.ashojash.android.util.ErrorUtils;
import org.greenrobot.eventbus.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class ApiCallback<T> implements Callback<T> {
    private final static EventBus BUS;

    static {
        BUS = BusUtil.getInstance();
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        BUS.post(new OnApiRequestErrorEvent(new ApiRequestError(call, t.getMessage())));
    }

    protected void handleResponse(Response<?> response, Object event) {
        if (response.isSuccessful()) {
            BUS.post(event);
        } else {
            BUS.post(new OnApiResponseErrorEvent(ErrorUtils.parseError(response), event));
        }
    }
}