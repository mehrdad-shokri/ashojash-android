package com.ashojash.android.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.ashojash.android.R;
import com.ashojash.android.fragment.VenueMenusFragment;
import com.ashojash.android.helper.AppController;

public class VenueMenusActivity extends BaseActivity {
    private Toolbar toolbar;
    private String slug;

    String TAG= AppController.TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_menus);
        setupViews();
        slug = getIntent().getStringExtra("slug");
        if (slug == null) finish();
        VenueMenusFragment fragment = new VenueMenusFragment();
        addFragment(R.id.venueMenusContainer, fragment);
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
