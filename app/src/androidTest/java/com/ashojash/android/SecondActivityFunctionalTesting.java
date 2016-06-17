package com.ashojash.android;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;
import com.ashojash.android.activity.SecondActivity;

public class SecondActivityFunctionalTesting extends ActivityInstrumentationTestCase2<SecondActivity> {

    private static final String NEW_TEXT = "new text";

    public SecondActivityFunctionalTesting() {
        super(SecondActivity.class);
    }

    public void testSetText() throws Exception {
        SecondActivity activity = getActivity();

        // search for the textView
        final TextView textView = (TextView) activity
                .findViewById(R.id.resultText);

        // set text
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                textView.setText(NEW_TEXT);
            }
        });
        getInstrumentation().waitForIdleSync();
        assertEquals("Text incorrect", NEW_TEXT, textView.getText().toString());
    }

}
