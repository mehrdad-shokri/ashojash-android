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
import com.ashojash.android.adapter.VenueBasicReviewsAdapter;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.struct.StructReview;
import com.ashojash.android.ui.UiUtils;
import com.ashojash.android.webserver.JsonParser;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


public class VenueBasicReviewFragment extends Fragment {
    private LinearLayout errorTxtContainer;
    private ProgressBar progressbar;
    private RecyclerView recyclerView;
    private TextView btnSeeAllReviews;
    private View rootLayout;
    private List<StructReview> reviewList;
    private RecyclerView.Adapter adapter;
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
        rootLayout = getView().findViewById(R.id.reviewRootLayoutVenueReviewFragment);
        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerViewReviewsVenueReviewFragment);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AppController.context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        btnSeeAllReviews = (TextView) getView().findViewById(R.id.btnSellAllVenueReviewFragment);
        btnSeeAllReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startVenueReviewActivity();
            }
        });
        progressbar = (ProgressBar) getView().findViewById(R.id.prgNearbyFragment);
        txtError = (TextView) getView().findViewById(R.id.txtErrorVenueBasicReviewFragment);
        errorTxtContainer = (LinearLayout) getView().findViewById(R.id.errorViewVenueReviewFragment);

    }

    private void startVenueReviewActivity() {
        Intent intent = new Intent(getActivity(), VenueReviewsActivity.class);
        intent.putExtra("slug", slug);
        startActivity(intent);
    }


    public void onDataReceived(JSONArray response, int reviewsCount) {
        try {
            if (response == null)
                throw new JSONException("json parsing exception");
            reviewList = new ArrayList<>();
            reviewList = JsonParser.parseVenueReviews(response);
            if (reviewsCount == 0)
                throw new IllegalArgumentException("no reviews yet :(");
            btnSeeAllReviews.setText(UiUtils.toPersianNumber(getString(R.string.venue_see_all_reviews).replace("{{venueReviewsCount}}", String.valueOf(reviewsCount))));
            Intent intent = new Intent(getActivity(), VenueReviewsActivity.class);
            adapter = new VenueBasicReviewsAdapter(reviewList, AppController.context, intent);
            recyclerView.setAdapter(adapter);
            rootLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) UiUtils.convertDpToPixel(reviewList.size() * 75 + 100)));
            btnSeeAllReviews.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            hideErrorViews();

        } catch (JSONException e) {
            showErrorViews();
            txtError.setText(R.string.error_retrieving_data);
//            recyclerView.setVisibility(View.GONE);
//            btnSeeAllReviews.setVisibility(View.GONE);
//            rootLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) UiUtils.convertDpToPixel(150)));

        } catch (IllegalArgumentException e) {
            txtError.setText(R.string.no_reviews_added_yet);
//            btnSeeAllReviews.setVisibility(View.GONE);
//            recyclerView.setVisibility(View.GONE);
//            getView().findViewById(R.id.reviewRootLayoutVenueReviewFragment).setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) UiUtils.convertDpToPixel(150)));
            showErrorViews();
        } finally {
            progressbar.setVisibility(View.GONE);
        }
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
