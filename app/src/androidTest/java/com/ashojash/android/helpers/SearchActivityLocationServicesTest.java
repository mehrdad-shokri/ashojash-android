package com.ashojash.android.helpers;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.ashojash.android.activity.SearchActivity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SearchActivityLocationServicesTest {
  @Rule
  ActivityTestRule<SearchActivity> activity =
      new ActivityTestRule<>(SearchActivity.class);

  @Test
  public void it_shows_location_services_error_when_gps_is_off() {

  }
}
