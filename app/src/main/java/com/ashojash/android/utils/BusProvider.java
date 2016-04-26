package com.ashojash.android.utils;

import org.greenrobot.eventbus.EventBus;

public final class BusProvider {
    private static final EventBus bus;

    static {
        bus = EventBus.getDefault();
    }

    private BusProvider() {
    }

    public static EventBus getInstance() {
        return bus;
    }
    public static void register(Object object)
    {
        bus.register(object);
    }
    public static  void unregister(Object object)
    {
        bus.unregister(object);
    }
}
