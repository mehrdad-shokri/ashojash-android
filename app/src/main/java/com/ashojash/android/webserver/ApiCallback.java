package com.ashojash.android.webserver;

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
        BusProvider.getInstance().post(new ApiRequestError(t.getMessage()));
    }


    /*protected void handleAuthResponse(Response<?> response, Object object) {
        if (response.isSuccessful()) {
            BusProvider.getInstance().post(object);
        } else {
            if (response.code() == 400) {
                Log.d(TAG, "handleAuthResponse: firing validation error ");
                ValidationError validationError = ErrorUtils.parseValidationError(response);
                BusProvider.getInstance().post(new UserApiEvents.OnUserLoginFailed(validationError));
            } else {
                BUS.post(new OnApiResponseErrorEvent(new ApiResponseError()));
            }
        }
    }*/

    protected void handleResponse(Response<?> response, Object object) {
        if (response.isSuccessful()) {
            BusProvider.getInstance().post(object);
        } else {
            BUS.post(new OnApiResponseErrorEvent(ErrorUtils.parseError(response)));
        }
    }
}