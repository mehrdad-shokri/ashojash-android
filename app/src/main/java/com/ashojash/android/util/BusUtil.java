package com.ashojash.android.util;

import org.greenrobot.eventbus.EventBus;

public final class BusUtil {
    private static final EventBus bus;

    private BusUtil() {
    }

    static {
        bus = EventBus.getDefault();
    }

    public static EventBus getInstance() {
        return bus;
    }
}
