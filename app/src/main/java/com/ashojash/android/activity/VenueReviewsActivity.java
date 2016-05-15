package com.ashojash.android.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import com.ashojash.android.R;
import com.ashojash.android.fragment.VenueReviewsFragment;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.Venue;

/*
* Completed
* */
public class VenueReviewsActivity extends BaseActivity {
    private Toolbar toolbar;
    private String slug;
    private Venue venue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_reviews);
        slug = getIntent().getStringExtra("slug");
        if (slug == null) finish();
        venue = AppController.gson.fromJson(getIntent().getStringExtra("venue"),Venue.class);
        setupViews();
        VenueReviewsFragment fragment = new VenueReviewsFragment();
        addFragment(R.id.venueReviewContainer, fragment);
    }

    private void setupViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbarTop);
        ((TextView) toolbar.findViewById(R.id.txtToolbarTitle)).setText(R.string.title_reviews);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
