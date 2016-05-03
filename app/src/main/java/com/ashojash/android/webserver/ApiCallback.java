package com.ashojash.android.webserver;

import com.ashojash.android.event.OnApiRequestErrorEvent;
import com.ashojash.android.event.OnApiResponseErrorEvent;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.ApiRequestError;
import com.ashojash.android.utils.BusProvider;
import com.ashojash.android.utils.ErrorUtils;
import org.greenrobot.eventbus.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class ApiCallback<T> implements Callback<T> {
    private final static EventBus BUS;

    static {
        BUS = BusProvider.getInstance();
    }

    String TAG = AppController.TAG;

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        BusProvider.getInstance().post(new OnApiRequestErrorEvent(new ApiRequestError(t.getMessage())));
    }

    protected void handleResponse(Response<?> response, Object object) {
        if (response.isSuccessful()) {
            BusProvider.getInstance().post(object);
        } else {
            BUS.post(new OnApiResponseErrorEvent(ErrorUtils.parseError(response)));
        }
    }
}