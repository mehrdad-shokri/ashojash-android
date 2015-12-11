package com.ashojash.android.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.ashojash.android.R;
import com.ashojash.android.fragment.VenueReviewsFragment;

public class VenueReviewsActivity extends BaseActivity {
    private Toolbar toolbar;
    private String slug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_reviews);
        setupViews();
        slug = getIntent().getStringExtra("slug");
        if (slug == null) finish();
        VenueReviewsFragment fragment = new VenueReviewsFragment();
        addFragment(R.id.venueReviewContainer, fragment);
    }

    private void setupViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbarTop);
        toolbar.setTitle("Venue name");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setSupportActionBar(toolbar);
    }
}
