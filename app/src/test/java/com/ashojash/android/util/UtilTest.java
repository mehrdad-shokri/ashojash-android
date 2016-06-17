package com.ashojash.android.util;

import android.content.Context;
import android.test.suitebuilder.annotation.LargeTest;
import com.ashojash.android.utils.Util;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.FileOutputStream;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;


public class UtilTest {
    @Mock
    Context context;
    @Mock
    FileOutputStream fileOutputStream;

    @Before
    public void let() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @LargeTest
    public void writeMethodShouldWriteTwiceInWriteMethod() {
        try {
            when(context.openFileOutput(anyString(), anyInt())).thenReturn(fileOutputStream);
            Util.writeConfiguration(context);
            verify(context, times(1)).openFileOutput(anyString(), anyInt());
            verify(fileOutputStream, atLeast(2)).write(any(byte[].class));
        } catch (Exception exception) {
            exception.printStackTrace();
            fail();
        }

    }

}