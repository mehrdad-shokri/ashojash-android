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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ashojash.android.R;
import com.ashojash.android.activity.VenueReviewsActivity;
import com.ashojash.android.adapter.VenueMenusAdapter;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.struct.StructMenu;
import com.ashojash.android.webserver.JsonParser;
import com.ashojash.android.webserver.WebServer;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VenueMenusFragment extends Fragment {
    private static final int REVIEWS_BASE_LIMIT = 10;
    private RecyclerView recyclerView;
    private String slug;

    public VenueMenusFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_venue_menus, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        slug = getActivity().getIntent().getStringExtra("slug");
        if (slug == null)
            getActivity().getFragmentManager().popBackStack();

    }

    @Override
    public void onResume() {
        super.onResume();
        setupViews();
        getReviews();
    }

    private String TAG = AppController.TAG;

    private void getReviews() {
        JsonObjectRequest request = WebServer.getVenueMenus(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject data = response.getJSONObject("data");
                    reviewList = new ArrayList<>();
                    reviewList = JsonParser.parseVenueMenus(data.getJSONArray("items"));
                    Intent intent = new Intent(getActivity(), VenueReviewsActivity.class);
                    adapter = new VenueMenusAdapter(reviewList, AppController.context, intent);
                    recyclerView.setAdapter(adapter);
                } catch (JSONException e) {
                    showErrorViews();
                    txtError.setText(R.string.error_retrieving_data);

                } catch (IllegalArgumentException e) {
                    txtError.setText(R.string.no_reviews_added_yet);
                    showErrorViews();
                } finally {
                    progressbar.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }, slug, REVIEWS_BASE_LIMIT);
        AppController.getInstance().addToRequestQueue(request, "VENUE_REVIEWS_REQUEST");
    }

    @Override
    public void onPause() {
        super.onPause();
        AppController.getInstance().cancelPendingRequests("VENUE_REVIEWS_REQUEST");
    }

    private RecyclerView.Adapter adapter;
    private List<StructMenu> reviewList;
    private TextView txtError;

    private void setupViews() {
        recyclerView = (RecyclerView) getView().findViewById(R.id.venueMenusRecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AppController.context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        progressbar = (ProgressBar) getView().findViewById(R.id.prgNearbyFragment);
        errorTxtContainer = (LinearLayout) getView().findViewById(R.id.errorViewVenueReviewFragment);
        txtError = (TextView) getView().findViewById(R.id.txtErrorVenueBasicReviewFragment);
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
