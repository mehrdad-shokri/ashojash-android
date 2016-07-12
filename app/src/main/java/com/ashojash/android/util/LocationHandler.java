package com.ashojash.android.util;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import com.google.android.gms.location.FusedLocationProviderApi;

public class LocationHandler extends IntentService {

    public LocationHandler() {
        super("com.ahsojash.android.debug");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final Location location = intent.getParcelableExtra(FusedLocationProviderApi.KEY_LOCATION_CHANGED);
        String TAG = "Location";
        Log.d(TAG, "onHandleIntent: got location: " + location.getLatitude() + " " + location.getLongitude());
    }
}
