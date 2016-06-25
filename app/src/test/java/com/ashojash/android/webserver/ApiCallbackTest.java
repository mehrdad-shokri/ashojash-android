package com.ashojash.android.webserver;

import com.ashojash.android.event.OnApiRequestErrorEvent;
import com.ashojash.android.model.ApiRequestError;
import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import retrofit2.Call;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class ApiCallbackTest {
    @Mock
    private Call call;
    @Mock
    private Throwable t;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void should_call_busProvider_to_post_event() {
        try {
//            given
            EventBus bus = Mockito.mock(EventBus.class);
            setFinalStatic(ApiCallback.class.getDeclaredField("BUS"), bus);
            final String ERROR_MESSAGE = "error message";
            when(t.getMessage()).thenReturn(ERROR_MESSAGE);
//          when
            ApiCallback apiCallback = Mockito.mock(ApiCallback.class, CALLS_REAL_METHODS);
            apiCallback.onFailure(call, t);
//            then
            verify(bus).post(new OnApiRequestErrorEvent(new ApiRequestError(ERROR_MESSAGE)));
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);
        // remove final modifier from field
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, newValue);
    }
}
