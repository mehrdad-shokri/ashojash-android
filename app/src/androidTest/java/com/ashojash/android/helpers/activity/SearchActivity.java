package com.ashojash.android.helpers.activity;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;
import com.ashojash.android.R;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@SmallTest @RunWith(AndroidJUnit4.class) public class SearchActivity {
  @Rule public ActivityTestRule<com.ashojash.android.activity.SearchActivity> instance =
      new ActivityTestRule<>(com.ashojash.android.activity.SearchActivity.class);

  @Test public void showsSoftKeyboardOnActivityStartup() {
    onView(withId(R.id.edtTermSearch)).check(matches(withHint(R.string.search_ashojash)));
    onView(withId(R.id.edtLocationSearch)).check(matches(withText(R.string.near_me)))
        .check(matches(withText(R.string.near_me)));
  }
}
