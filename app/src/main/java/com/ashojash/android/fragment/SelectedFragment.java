package com.ashojash.android.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.ashojash.android.R;
import com.ashojash.android.activity.VenueActivity;
import com.ashojash.android.adapter.VenueAdapter;
import com.ashojash.android.event.OnApiResponseErrorEvent;
import com.ashojash.android.event.VenueApiEvents;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.Venue;
import com.ashojash.android.utils.BusProvider;
import com.ashojash.android.webserver.VenueApi;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectedFragment extends Fragment {
    private ProgressBar selectedProgressbar;
    private TextView txtSelectedError;
    private LinearLayout retryView;
    private RecyclerView recyclerView;
    private VenueAdapter adapter;
    private String citySlug;
    private List<Venue> venueList;

    public SelectedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_selected_venues, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        selectedProgressbar = (ProgressBar) getView().findViewById(R.id.prgSelectedFragment);
        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerViewSelectedFragment);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        txtSelectedError = (TextView) getView().findViewById(R.id.txtErrorSelectedFragment);
        retryView = (LinearLayout) getView().findViewById(R.id.retryViewSelectedFragment);
        retryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideErrorViews();
                VenueApi.selectedVenues(citySlug);
            }
        });
        Bundle bundle = getArguments();
        if (bundle != null) {
            citySlug = bundle.getString("current_city_slug");
        }
        VenueApi.selectedVenues(citySlug);
//        getAnotherUserLocation();
//        getUserLocation();
//       get current location of user
//        request nearby venues
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

    @Subscribe
    public void onResponseError(OnApiResponseErrorEvent event) {
        showErrorViews();
        txtSelectedError.setText(R.string.error_retrieving_data);
    }

    @Subscribe
    public void onVenuesReceived(VenueApiEvents.OnSelectedVenuesResult event) {
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
        selectedProgressbar.setVisibility(View.GONE);
    }


    private void showErrorViews() {
        selectedProgressbar.setVisibility(View.GONE);
        retryView.setVisibility(View.VISIBLE);
    }

    private void hideErrorViews() {
        retryView.setVisibility(View.GONE);
        selectedProgressbar.setVisibility(View.VISIBLE);
    }


}
