package com.ashojash.android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import com.ashojash.android.model.Venue;
import com.ashojash.android.ui.UiUtils;
import com.ashojash.android.utils.BusProvider;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;


public class VenueBasicReviewFragment extends Fragment {
    private LinearLayout errorTxtContainer;
    private ProgressBar progressbar;
    private RecyclerView recyclerView;
    private TextView btnSeeMore;
    private View rootLayout;
    private List<Review> reviewList;
    private VenueReviewsAdapter adapter;
    private TextView txtError;
    private String slug;

    public VenueBasicReviewFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_venue_basic_review, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        slug = getActivity().getIntent().getStringExtra("slug");
        if (slug == null)
            getActivity().getFragmentManager().popBackStack();
        setupViews();
    }

    private void setupViews() {
        rootLayout = getView().findViewById(R.id.rootView);
        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerViewReviewsVenueReviewFragment);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AppController.context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        btnSeeMore = (TextView) getView().findViewById(R.id.btnSellAllVenueReviewFragment);
        btnSeeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startVenueReviewActivity();
            }
        });
        progressbar = (ProgressBar) getView().findViewById(R.id.prgNearbyFragment);
        txtError = (TextView) getView().findViewById(R.id.txtErrorVenue);
        errorTxtContainer = (LinearLayout) getView().findViewById(R.id.errorViewVenue);

    }

    private void startVenueReviewActivity() {
        Intent intent = new Intent(getActivity(), VenueReviewsActivity.class);
        intent.putExtra("slug", slug);
        intent.putExtra("venue", getActivity().getIntent().getStringExtra("venue"));
        startActivity(intent);
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
    public void onEvent(OnApiResponseErrorEvent event) {
        btnSeeMore.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        getView().findViewById(R.id.rootView).setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) UiUtils.dp2px(80)));
        showErrorViews();
        txtError.setText(R.string.error_retrieving_data);
    }

    @Subscribe
    public void onEvent(VenueApiEvents.OnVenueIndexResultsReady event) {
        Venue venue = event.venue;
        reviewList = venue.reviews;
        int reviewsCount = venue.reviewsCount;
        progressbar.setVisibility(View.GONE);
        if (reviewsCount == 0) {
            txtError.setText(R.string.no_reviews_added_yet);
            btnSeeMore.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            showErrorViews();
            getView().findViewById(R.id.rootView).setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) UiUtils.dp2px(80)));
            return;
        }
        btnSeeMore.setText(UiUtils.toPersianNumber(getString(R.string.venue_see_all_reviews).replace("{{venueReviewsCount}}", String.valueOf(reviewsCount))));
        adapter = new VenueReviewsAdapter(reviewList);
        adapter.setOnItemClickListener(new OnCardClickListener() {
            @Override
            public void onClick(Object model) {
                startVenueReviewActivity();
            }
        });
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setAdapter(adapter);
        rootLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) UiUtils.dp2px(reviewList.size() * 75 + 100)));
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
//        recyclerView.setLayoutManager(layoutManager);
        btnSeeMore.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        hideErrorViews();
    }


    private void showErrorViews() {
        progressbar.setVisibility(View.GONE);
        errorTxtContainer.setVisibility(View.VISIBLE);
    }

    private void hideErrorViews() {
        progressbar.setVisibility(View.GONE);
        errorTxtContainer.setVisibility(View.GONE);
    }
}
