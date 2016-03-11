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
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ashojash.android.R;
import com.ashojash.android.activity.SearchActivity;
import com.ashojash.android.activity.VenueActivity;
import com.ashojash.android.adapter.VenueSearchResultAdapter;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.struct.StructVenue;
import com.ashojash.android.webserver.JsonParser;
import com.ashojash.android.webserver.WebServer;
import com.mypopsy.widget.FloatingSearchView;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class SearchFragment extends Fragment {
    private static final String VENUE_SEARCH_REQUEST_TAG = "VENUE_SEARCH";
    private static final String CITY_SLUG = AppController.defaultPref.getString("current_city_slug", null);

    public SearchFragment() {
    }

    private ProgressBar selectedProgressbar;
    private TextView txtSelectedError;
    private LinearLayout retryView;
    private RecyclerView recyclerView;
    private String query;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        selectedProgressbar = (ProgressBar) getView().findViewById(R.id.prgSearchFragment);
        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerViewSearchFragmentSomeTestThing);
        txtSelectedError = (TextView) getView().findViewById(R.id.txtErrorSearchFragment);
        retryView = (LinearLayout) getView().findViewById(R.id.retryViewSearchFragment);
        txtSelectedError = (TextView) getView().findViewById(R.id.txtErrorSearchFragment);
        retryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideErrorViews();
                search(query);
            }
        });
        Bundle bundle = getArguments();
        if (bundle != null) {
            query = bundle.getString("query");
        }
//        getAnotherUserLocation();
//        getUserLocation();
//       get current location of user
//        request nearby venues
    }

    @Override
    public void onResume() {
        super.onResume();
        search(query);
    }

    public void search(String query) {
        final String TAG = AppController.TAG;
//        mSearch.search(query);
        JsonObjectRequest request = WebServer.searchVenues(CITY_SLUG, query, 20, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                mSearchView.setAdapter();
                try {
                    List<StructVenue> venues = JsonParser.parseVenuesJsonObject(response);
                    Intent intent = new Intent(AppController.currentActivity, VenueActivity.class);
                    RecyclerView.Adapter adapter = new VenueSearchResultAdapter(venues, AppController.context, intent);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    showSearchProgress(false);
                    showFloatingSearchProgress(false);
                    hideErrorViews();
                    if (venues.size() == 0)
                        Toast.makeText(getActivity(), R.string.no_results_found, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    errorSearching();
                    showErrorViews();
                    showSearchProgress(false);
                    showFloatingSearchProgress(false);
                    txtSelectedError.setText(R.string.error_connecting_to_server);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorSearching();
                showErrorViews();
                showFloatingSearchProgress(false);
                showSearchProgress(false);
                txtSelectedError.setText(R.string.error_retrieving_data);
            }
        });
        AppController.getInstance().addToRequestQueue(request, VENUE_SEARCH_REQUEST_TAG);
    }

    private void showErrorViews() {
        selectedProgressbar.setVisibility(View.GONE);
        retryView.setVisibility(View.VISIBLE);
    }

    private void hideErrorViews() {
        retryView.setVisibility(View.GONE);
        selectedProgressbar.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
        AppController.getInstance().cancelPendingRequests(VENUE_SEARCH_REQUEST_TAG);
    }

    private void showSearchProgress(boolean show) {
//        mSearchView.getMenu().findItem(R.id.menu_progress).setVisible(show);
        if (show)
            selectedProgressbar.setVisibility(View.VISIBLE);
        else {
            selectedProgressbar.setVisibility(View.GONE);
        }

    }

    private void errorSearching() {
        showSearchProgress(false);
        Toast.makeText(getActivity(), R.string.error_retrieving_data, Toast.LENGTH_SHORT).show();
    }

    public void showFloatingSearchProgress(boolean show) {
        try {
            SearchActivity activity = (SearchActivity) getActivity();
            FloatingSearchView searchView = activity.mSearchView;
            if (searchView != null) {
                searchView.getMenu().findItem(R.id.menu_progress).setVisible(show);
            }

        } catch (ClassCastException e) {
//            class was not called from searchActivity
        }
    }
}