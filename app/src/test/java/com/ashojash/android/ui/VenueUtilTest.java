package com.ashojash.android.ui;

import com.ashojash.android.R;
import com.ashojash.android.util.VenueUtil;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class VenueUtilTest {

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void should_format_venue_score_double() {
        double score = 0;
        String expected = VenueUtil.getVenueScoreText(score);
        assertThat(expected, is("-"));
    }

    @Test
    public void should_return_level_0_for_invalid_scores() {
        assertThat(R.drawable.level_0, is(VenueUtil.getVenueScoreDrawableId(5.1)));
        assertThat(R.drawable.level_0, is(VenueUtil.getVenueScoreDrawableId(.9)));
    }

    @Test
    public void should_return_dollar_sign_in_correct_order() {
        String result = VenueUtil.getCostSign(3);
        String expected = "<font color=#666>$ $ $ </font> <font color=#c0c0c0>$ $ </font>";
        assertThat(result, is(Matchers.equalToIgnoringWhiteSpace(expected)));
    }
}