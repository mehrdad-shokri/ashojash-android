package com.ashojash.android.util;

import com.ashojash.android.event.PermissionEvents;
import org.greenrobot.eventbus.Subscribe;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class EventProvider {

    public EventProvider() {
    }

    @Subscribe
    public void onReceive(PermissionEvents.OnPermissionDenied e) {
        if (e.permission.equals(ACCESS_FINE_LOCATION)) {

        }
    }
}
