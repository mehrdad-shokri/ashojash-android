package com.ashojash.android.fragment;


import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.ashojash.android.R;
import com.ashojash.android.activity.VenueActivity;
import com.ashojash.android.adapter.VenueAdapter;
import com.ashojash.android.event.OnApiRequestErrorEvent;
import com.ashojash.android.event.OnApiResponseErrorEvent;
import com.ashojash.android.event.VenueApiEvents;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.Venue;
import com.ashojash.android.utils.BusProvider;
import com.ashojash.android.utils.LocationUtil;
import com.ashojash.android.webserver.VenueApi;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class NearbyFragment extends Fragment {
    private ProgressBar nearbyProgressbar;
    private TextView txtNearbyError;
    private LinearLayout retryView;
    private RecyclerView recyclerView;
    private VenueAdapter adapter;
    private double lat;
    private double lng;
    private String citySlug;
    private List<Venue> venueList;

    public NearbyFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nearby_venues, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupViews();
        Bundle bundle = getArguments();
        if (bundle != null) {
            citySlug = bundle.getString("current_city_slug");
        }
        getUserLocation();
    }

    private void setupViews() {
        nearbyProgressbar = (ProgressBar) getView().findViewById(R.id.prgNearbyFragment);
        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerViewNearbyFragment);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        txtNearbyError = (TextView) getView().findViewById(R.id.txtErrorNearbyFragment);
        retryView = (LinearLayout) getView().findViewById(R.id.errorViewVenue);
        retryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideErrorViews();
                getUserLocation();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        BusProvider.getInstance().unregister(this);
    }

    private LocationUtil locationUtil;


    @Subscribe
    public void onEvent(VenueApiEvents.OnNearbyVenuesResult event) {
        venueList = event.venueList;
        adapter = new VenueAdapter(venueList);
        adapter.setOnItemClickListener(new VenueAdapter.OnItemClickListener() {
            @Override
            public void onClick(Venue venue) {
                Intent intent = new Intent(getActivity(), VenueActivity.class);
                intent.putExtra("slug", venue.slug);
                AppController.currentActivity.startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        try {
            if (venueList.size() == 0)
                throw new IllegalArgumentException("no nearby venues");
        } catch (IllegalArgumentException e) {
            txtNearbyError.setText(R.string.no_nearby_venues);
            showErrorViews();
        }
        nearbyProgressbar.setVisibility(View.GONE);
    }

    @Subscribe
    public void onEvent(OnApiResponseErrorEvent event) {
        showErrorViews();
        txtNearbyError.setText(R.string.error_retrieving_data);
    }

    @Subscribe
    public void onEvent(OnApiRequestErrorEvent event) {
        showErrorViews();
        txtNearbyError.setText(R.string.error_retrieving_data);
    }

    private void getUserLocation() {

        LocationUtil.LocationResult locationResult = new LocationUtil.LocationResult() {
            @Override
            public void gotLocation(Location location) {
                if (location == null) {
                    AppController.HANDLER.post(new Runnable() {
                        @Override
                        public void run() {
                            showErrorViews();
                            txtNearbyError.setText(R.string.error_retrieving_location);
                        }
                    });
                    return;
                }
                lat = location.getLatitude();
                lng = location.getLongitude();
                VenueApi.nearbyVenues(citySlug, lat, lng);
            }
        };

        String TAG = "LOCATION";
        locationUtil = new LocationUtil();
        try {
            boolean isGetLocationSuccess = locationUtil.getLocation(AppController.context, locationResult);
            Log.d(TAG, "getUserLocation: in try : " + isGetLocationSuccess);
        } catch (SecurityException e) {
            Log.d(TAG, "getUserLocation: securityException");
        }
    }

    private void showErrorViews() {
        nearbyProgressbar.setVisibility(View.GONE);
        retryView.setVisibility(View.VISIBLE);
    }

    private void hideErrorViews() {
        retryView.setVisibility(View.GONE);
        nearbyProgressbar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();
        locationUtil.cancelTimer();
    }
}
