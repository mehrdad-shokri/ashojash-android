package com.ashojash.android.event;

import com.google.android.gms.common.api.Status;

public class LocationEvents {
    public static class OnLocationServiceAvailable {

    }

    public static class OnLocationServiceNotAvailable {
        public Status status;

        public OnLocationServiceNotAvailable(Status status) {
            this.status = status;
        }
    }
}
