package com.ashojash.android.utils;

import com.ashojash.android.model.ApiResponseError;
import com.ashojash.android.webserver.UrlController;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

import java.io.IOException;
import java.lang.annotation.Annotation;

public class ErrorUtils {
    public static ApiResponseError parseError(Response<?> response) {
        Converter<ResponseBody, ApiResponseError> converter =
                UrlController.getInstance().responseBodyConverter(ApiResponseError.class, new Annotation[0]);
        ApiResponseError error;
        try {
            error = converter.convert(response.errorBody());
        } catch (IOException e) {
            return new ApiResponseError();
        }
        return error;
    }
}
