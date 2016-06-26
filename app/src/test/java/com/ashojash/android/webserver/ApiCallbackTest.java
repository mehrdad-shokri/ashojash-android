package com.ashojash.android.webserver;

<<<<<<< HEAD
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Every.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;


public class ApiCallbackTest {
=======
import com.ashojash.android.event.OnApiRequestErrorEvent;
import com.ashojash.android.event.OnApiResponseErrorEvent;
import com.ashojash.android.model.ApiRequestError;
import com.ashojash.android.model.ApiResponseError;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import retrofit2.Call;
import retrofit2.Response;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.mockito.Mockito.*;

public class ApiCallbackTest {
    private static ApiCallback apiCallback;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeClass
    public static void setupClass() throws Exception {
        apiCallback = mock(ApiCallback.class, CALLS_REAL_METHODS);
    }

    @Test
    public void should_call_busProvider_to_post_error_event() throws Exception {
        EventBus bus = setupBus();
//            given
        Call call = mock(Call.class);
        Throwable t = mock(Throwable.class);
        final String ERROR_MESSAGE = "error message";
        when(t.getMessage()).thenReturn(ERROR_MESSAGE);
//          when
        apiCallback.onFailure(call, t);
//            then
        verify(bus).post(new OnApiRequestErrorEvent(any(ApiRequestError.class)));
    }

    @Test
    public void should_post_success_event_on_successful_response() throws Exception {
        EventBus bus = setupBus();
        Response response = Response.success(null);
//        when
        apiCallback.handleResponse(response, null);
//        then
        verify(bus).post(any(Object.class));
    }

    @Test
    public void should_post_error_event_on_failure() throws Exception {
//        given
        Response response = Response.error(404, ResponseBody.create(
                MediaType.parse("application/json"),
                "{\"key\":[\"test\"]}"
        ));
        EventBus bus = setupBus();
        Object object = new Object();
//        when
        apiCallback.handleResponse(response, object);
//        then
        verify(bus).post(new OnApiResponseErrorEvent(((ApiResponseError) anyObject()), object));
    }

    private EventBus setupBus() throws Exception {
        EventBus bus = mock(EventBus.class);
        setFinalStatic(ApiCallback.class.getDeclaredField("BUS"), bus);
        return bus;
    }

    public static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);
        // remove final modifier from field
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, newValue);
    }
>>>>>>> release/1.0.3
}
