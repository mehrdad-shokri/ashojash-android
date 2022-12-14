package com.ashojash.android.ui;

import com.ashojash.android.util.UiUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class UiUtilTest {

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void should_convert_number_to_a_persian_number() {
        String number = "0123456789 hello world";
        String result = UiUtil.toPersianNumber(number);
        assertThat(result, equalTo("۰۱۲۳۴۵۶۷۸۹ hello world"));
    }

    @Test
    public void should_append_width_to_a_url() {
        int px = 50;
        String url = "example.com/user/photo/mehrdad";
        String expectedUrl = UiUtil.setUrlWidth(url, px);
        assertThat(expectedUrl, is(url + "?w=50"));
    }

    @Test
    public void should_format_currency_correctly() {
        String price = "15000";
        String expected = UiUtil.formatCurrency(price);
        assertThat(expected, is("15,000"));

        price = "100";
        expected = UiUtil.formatCurrency(price);
        assertThat(expected, is("100"));
    }


}