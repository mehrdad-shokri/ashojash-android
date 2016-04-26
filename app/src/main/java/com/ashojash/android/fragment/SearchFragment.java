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
import com.ashojash.android.R;
import com.ashojash.android.activity.SearchActivity;
import com.ashojash.android.activity.VenueActivity;
import com.ashojash.android.adapter.VenueSearchResultAdapter;
import com.ashojash.android.event.OnApiResponseErrorEvent;
import com.ashojash.android.event.VenueApiEvents;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.Venue;
import com.ashojash.android.utils.BusProvider;
import com.ashojash.android.webserver.VenueApi;
import com.mypopsy.widget.FloatingSearchView;
import org.greenrobot.eventbus.Subscribe;

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
                VenueApi.search(CITY_SLUG, query, 10);
            }
        });
        Bundle bundle = getArguments();
        if (bundle != null) {
            query = bundle.getString("query");
        }
    }

    public void search(String query) {
        VenueApi.search(CITY_SLUG, query, 10);
    }

    @Override
    public void onResume() {
        super.onResume();
        VenueApi.search(CITY_SLUG, query, 10);
    }

    @Subscribe
    private void onEvent(VenueApiEvents.OnSearchResultsReady event) {
        List<Venue> venues = event.venuePaginated.data;
        VenueSearchResultAdapter adapter = new VenueSearchResultAdapter(venues);
        adapter.setOnItemClickListener(new VenueSearchResultAdapter.OnItemClickListener() {
            @Override
            public void onClick(Venue venue) {
                Intent intent = new Intent(AppController.currentActivity, VenueActivity.class);
                intent.putExtra("slug", venue.slug);
                AppController.currentActivity.startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        showSearchProgress(false);
        showFloatingSearchProgress(false);
        hideErrorViews();
        if (venues.size() == 0)
            Toast.makeText(getActivity(), R.string.no_results_found, Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void onEvent(OnApiResponseErrorEvent event) {
        showSearchProgress(false);
        showErrorViews();
        /*AshojashSnackbar.make(getActivity(), R.string.error_retrieving_data, Snackbar.LENGTH_INDEFINITE).setAction(R.string.try_again, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VenueApi.search(query);
            }
        }).show();*/
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