package com.ashojash.android.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import com.ashojash.android.R;
import com.ashojash.android.db.VenueDb;
import com.ashojash.android.fragment.VenuePhotosFragment;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.orm.VenueOrm;

/*
* Web server
* */
public class VenuePhotosActivity extends BaseActivity {
    private Toolbar toolbar;
    private String slug;
    private VenueOrm venueOrm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_photos);
        slug = getIntent().getStringExtra("slug");
        if (slug == null) finish();
        venueOrm = VenueDb.findBySlugOrFail(slug);
        setupViews();
        VenuePhotosFragment fragment = new VenuePhotosFragment();
        addFragment(R.id.venuePhotosContainer, fragment);
    }

    private void setupViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbarTop);
        toolbar.setTitle(venueOrm.name);
        setSupportActionBar(toolbar);
        final String TAG = AppController.TAG;
        Log.d(TAG, "setupViews: " + (getSupportActionBar() == null));
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
