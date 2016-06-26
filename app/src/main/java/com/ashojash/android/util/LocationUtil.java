package com.ashojash.android.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class LocationUtil {
    Timer timer;
    LocationManager lm;
    LocationResult locationResult;
    boolean gps_enabled = false;
    boolean network_enabled = false;

    public boolean getLocation(Context context, LocationResult result) throws SecurityException {
        locationResult = result;
        if (lm == null)
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        //don't start listeners if no provider is enabled
        if (!gps_enabled && !network_enabled) {

            return false;
        }
        if (gps_enabled)
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsLocationListener);
        if (network_enabled)
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, networkLocationListener);
        timer = new Timer();
        timer.schedule(new LastLocationGetter(), 20000);
        return true;
    }

    class LastLocationGetter extends TimerTask {
        @Override
        public void run() throws SecurityException {
            lm.removeUpdates(gpsLocationListener);
            lm.removeUpdates(networkLocationListener);
            Location networkLocation = null, gpsLocation = null;
            if (gps_enabled)
                gpsLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (network_enabled)
                networkLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            //if there are both values use the latest one
            if (gpsLocation != null && networkLocation != null) {
                if (gpsLocation.getTime() > networkLocation.getTime())
                    locationResult.gotLocation(gpsLocation);
                else
                    locationResult.gotLocation(networkLocation);
                return;
            }

            if (gpsLocation != null) {
                locationResult.gotLocation(gpsLocation);
                return;
            }
            if (networkLocation != null) {
                locationResult.gotLocation(networkLocation);
                return;
            }
            locationResult.gotLocation(null);
        }
    }

    public void cancelTimer() throws SecurityException {
        if (timer != null)
            timer.cancel();
        lm.removeUpdates(gpsLocationListener);
        lm.removeUpdates(networkLocationListener);
    }

    public static abstract class LocationResult {
        public abstract void gotLocation(Location location);
    }

    /*
    * INSTANCE VARIABLES
    * */
    LocationListener gpsLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) throws SecurityException {
            timer.cancel();
            locationResult.gotLocation(location);
            lm.removeUpdates(this);
            lm.removeUpdates(networkLocationListener);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    LocationListener networkLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) throws SecurityException {
            timer.cancel();
            locationResult.gotLocation(location);
            lm.removeUpdates(this);
            lm.removeUpdates(gpsLocationListener);
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };
}