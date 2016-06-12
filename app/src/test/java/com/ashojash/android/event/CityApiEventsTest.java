package com.ashojash.android.event;

import android.content.Context;
import com.ashojash.android.R;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CityApiEventsTest {
    private static String FAKE_STRING = "Hello world";

    @Mock
    Context mockContext;

    @Test
    public void read_string_from_context() {
        Mockito.when(mockContext.getString(R.string.hello_world)).thenReturn(FAKE_STRING);
        String result = mockContext.getString(R.string.hello_world);
        MatcherAssert.assertThat(result, CoreMatchers.is(FAKE_STRING));
    }


}