package com.ashojash.android.util;

import android.os.NetworkOnMainThreadException;
import com.ashojash.android.utils.ConvertUtil;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.InOrder;

import java.util.IllegalFormatException;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class ConverterUtil {

    @Test
    public void convert_fahrenheit_to_celsius() {
        float actual = ConvertUtil.celsiusToFahrenheit(100);
        final float EXPECTED = 212;
//        assertEquals("Conversion from celsius to fahrenheit failed", actual, EXPECTED, .01);
        assertThat("the reason to be equal", "this is a string", allOf(is("first matcher"), is("second matcher")));
        Basre
    }

    @Test
    public void convert_celsius_to_fahrenheit() {
        List<String> mock = mock(List.class);
        when(mock.get(999)).thenThrow(IndexOutOfBoundsException.class);
        doThrow(IllegalArgumentException.class).when(mock).clear();
        ArgumentCaptor<List> argument = ArgumentCaptor.forClass(List.class);
        verify(mock).addAll(argument.capture());
        BDDMockito.given(mock.size()).willReturn(1);
//        when
        when(mock.size()).thenReturn(1);
        BDDMockito.then(mock).should(times(2)).size();
        verify(mock, never()).clear();
        verifyZeroInteractions(mock);
        when(mock.get(2)).thenReturn("a", "b", "c");
        when(mock.get(2)).thenReturn("1").thenThrow(IllegalAccessException.class);

    }
}
