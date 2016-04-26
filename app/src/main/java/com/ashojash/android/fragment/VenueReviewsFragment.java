package com.ashojash.android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.ashojash.android.activity.VenueReviewsActivity;
import com.ashojash.android.adapter.OnCardClickListener;
import com.ashojash.android.adapter.VenueReviewsAdapter;
import com.ashojash.android.event.OnApiResponseErrorEvent;
import com.ashojash.android.event.VenueApiEvents;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.Review;
import com.ashojash.android.utils.BusProvider;
import com.ashojash.android.webserver.VenueApi;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class VenueReviewsFragment extends Fragment {
    private RecyclerView recyclerView;
    private String venueSlug;

    public VenueReviewsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_venue_reviews, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        venueSlug = getActivity().getIntent().getStringExtra("slug");
        Log.d(TAG, "onCreate: venueSlug: " + venueSlug);
        if (venueSlug == null)
            getActivity().getFragmentManager().popBackStack();
    }

    @Override
    public void onResume() {
        super.onResume();
        setupViews();
        VenueApi.reviews(venueSlug);
    }

    private String TAG = AppController.TAG;

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
    public void onEvent(VenueApiEvents.OnVenueReviewsResponse event) {
        Log.d(TAG, "onReviewsReceived: on response");
        reviewList = event.reviewList;
        int reviewsCount = reviewList.size();
        progressbar.setVisibility(View.GONE);

        if (reviewsCount == 0) {
            txtError.setText(R.string.no_reviews_added_yet);
            showErrorViews();
            return;
        }
        adapter = new VenueReviewsAdapter(reviewList);
        adapter.setOnItemClickListener(new OnCardClickListener() {
            @Override
            public void onClick(Object model) {
                Intent intent = new Intent(getActivity(), VenueReviewsActivity.class);
//                    start a new activity showing all the details of review
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Subscribe
    public void onEvent(OnApiResponseErrorEvent event) {
        Log.d(TAG, "onResponseError: on Error");
        txtError.setText(R.string.error_retrieving_data);
        showErrorViews();
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    private VenueReviewsAdapter adapter;
    private List<Review> reviewList;
    private TextView txtError;

    private void setupViews() {
        recyclerView = (RecyclerView) getView().findViewById(R.id.venueReviewsRecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AppController.context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        progressbar = (ProgressBar) getView().findViewById(R.id.prgNearbyFragment);
        errorTxtContainer = (LinearLayout) getView().findViewById(R.id.errorViewVenue);
        txtError = (TextView) getView().findViewById(R.id.txtErrorVenue);
    }

    private void showErrorViews() {
        progressbar.setVisibility(View.GONE);
        errorTxtContainer.setVisibility(View.VISIBLE);
    }

    private LinearLayout errorTxtContainer;
    private ProgressBar progressbar;

    private void hideErrorViews() {
        progressbar.setVisibility(View.GONE);
        errorTxtContainer.setVisibility(View.GONE);
    }
}
