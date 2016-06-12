package com.ashojash.android.event;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class EventTest {
    public static final String TEST_STRING = "This is a string";
    public static final long TEST_LONG = 12345678L;
    private CityApiEvents.OnAllCitiesAvailable events;

    @Before
    public void setup() {
        events = new CityApiEvents.OnAllCitiesAvailable();
    }

    @Test
    public void some_fake_test() {


    }

}
