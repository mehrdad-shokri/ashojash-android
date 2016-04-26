package com.ashojash.android.event;

import com.ashojash.android.model.ApiResponseError;
import com.ashojash.android.model.ValidationError;

public final class ErrorEvents {
    private ErrorEvents() {
    }

    public static class OnUserLoginFailed {
        public ApiResponseError error;

        public OnUserLoginFailed(ApiResponseError error) {
            this.error = error;
        }
    }

    public static class OnUserRegistrationFailed {
        public ValidationError error;

        public OnUserRegistrationFailed(ValidationError error) {
            this.error = error;
        }
    }

    public static class OnReviewAddFailed {
        public ApiResponseError error;

        public OnReviewAddFailed(ApiResponseError error) {
            this.error = error;
        }
    }

}
