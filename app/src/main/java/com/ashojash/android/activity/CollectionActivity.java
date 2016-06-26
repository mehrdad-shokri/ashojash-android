package com.ashojash.android.activity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.ashojash.android.R;
import com.ashojash.android.adapter.CollectionVenuesAdapter;
import com.ashojash.android.event.OnApiResponseErrorEvent;
import com.ashojash.android.event.VenueCollectionEvents;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.Venue;
import com.ashojash.android.model.VenueCollection;
import com.ashojash.android.ui.AshojashSnackbar;
import com.ashojash.android.util.BusUtil;
import com.ashojash.android.webserver.VenueCollectionApi;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class CollectionActivity extends BottomToolbarActivity {

    private String citySlug = AppController.citySlug;
    private String CollectionSlug;
    private VenueCollection collection;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private ImageView imageView;
    private ProgressBar progressBar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AshojashSnackbar.AshojashSnackbarBuilder builder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        CollectionSlug = getIntent().getStringExtra("slug");
        collection = AppController.gson.fromJson(getIntent().getStringExtra("collection"), VenueCollection.class);
        setupView();
        attachShy(0, this, savedInstanceState, (CoordinatorLayout) findViewById(R.id.rootView), findViewById(R.id.nestedScrollView));
        requestCollectionVenues();
    }

    private void setupView() {
        builder = new AshojashSnackbar.AshojashSnackbarBuilder(findViewById(R.id.rootView));
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AppController.context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        imageView = (ImageView) findViewById(R.id.backdrop);
        imageView.setColorFilter(Color.rgb(130, 130, 130), PorterDuff.Mode.MULTIPLY);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(collection.name);
        setSupportActionBar(toolbar);
        IconicsDrawable errorIcon = new IconicsDrawable(AppController.context).icon(GoogleMaterial.Icon.gmd_error).sizeDp(12).color(AppController.context.getResources().getColor(R.color.text_primary));
        Glide.with(AppController.context).load(collection.photo.url).centerCrop().placeholder(R.drawable.city_list_loader).error(errorIcon).diskCacheStrategy(DiskCacheStrategy.RESULT).into(imageView);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void onEvent(VenueCollectionEvents.OnVenueCollectionVenuesResponse response) {
        List<Venue> venues = response.collection.venues;
        CollectionVenuesAdapter adapter = new CollectionVenuesAdapter(venues);
        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
    }

    @Subscribe
    public void onEvent(OnApiResponseErrorEvent event) {
        progressBar.setVisibility(View.GONE);
        builder.message(R.string.error_retrieving_data).duration(Snackbar.LENGTH_INDEFINITE).build().setAction(R.string.try_again, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                requestCollectionVenues();
            }
        }).show();
    }

    private void requestCollectionVenues() {
        VenueCollectionApi.index(citySlug, CollectionSlug);
    }

    @Override
    protected void onStart() {
        super.onStart();
        BusUtil.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        BusUtil.getInstance().unregister(this);
    }
}
