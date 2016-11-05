package com.ashojash.android.util;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.ashojash.android.event.LocationEvents;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.*;

public class LocationRequestUtil
    implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
  public static final int REQUEST_CODE = 1000;
  private GoogleApiClient googleApiClient;

  public LocationRequestUtil(Activity activity) {
    googleApiClient = new GoogleApiClient.Builder(activity)
        .addApi(LocationServices.API)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this).build();
  }

  public void settingsRequest() {
    googleApiClient.connect();
    LocationRequest locationRequest = LocationRequest.create();
    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    locationRequest.setInterval(1000L);
    locationRequest.setFastestInterval(5 * 1000L);
    LocationSettingsRequest.Builder builder =
        new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

    //**************************
    builder.setAlwaysShow(true); //this is the key ingredient
    //**************************

    PendingResult<LocationSettingsResult> result =
        LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());

    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
      @Override
      public void onResult(LocationSettingsResult result) {
        final Status status = result.getStatus();
        Log.d("TAG", "onResult: " + result.getStatus());
        switch (status.getStatusCode()) {
          case LocationSettingsStatusCodes.SUCCESS:
            BusProvider.getInstance().post(new LocationEvents.OnLocationServiceAvailable());
            break;
          case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
            BusProvider.getInstance()
                .post(new LocationEvents.OnLocationServiceNotAvailable(status));
            break;
          case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
            break;
        }
      }
    });
  }

  @Override
  public void onConnected(@Nullable Bundle bundle) {
        /*try {
            PendingIntent pendingIntent = PendingIntent.getService(activity, 0,
                    new Intent(activity, LocationHandler.class),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient, locationRequest, pendingIntent);
        } catch (SecurityException e) {

        }*/
  }

  @Override
  public void onConnectionSuspended(int i) {
  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
  }
}