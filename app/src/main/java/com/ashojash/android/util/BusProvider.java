package com.ashojash.android.util;

import org.greenrobot.eventbus.EventBus;

public final class BusProvider {
    private static final EventBus bus;

    private BusProvider() {
    }

    static {
        bus = EventBus.getDefault();
    }

    public static EventBus getInstance() {
        return bus;
    }
}
