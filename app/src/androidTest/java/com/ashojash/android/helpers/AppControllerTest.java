package com.ashojash.android.helpers;

import android.support.test.runner.AndroidJUnit4;
import com.ashojash.android.helper.AppController;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class AppControllerTest {
    @Test
    public void should_cipher_strings() {
        String actual = "hello world";
        AppController.obsEditor.putString("cipher", actual).commit();
        assertThat(actual, is(AppController.obsPref.getString("cipher", null)));
    }

    @Test
    public void should_have_dp_set_correctly() {
        assertThat(AppController.widthDp, is(Matchers.greaterThan(0)));
        assertThat(AppController.heightDp, is(Matchers.greaterThan(0)));
    }
}
