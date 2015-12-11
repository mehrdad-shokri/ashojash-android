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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ashojash.android.R;
import com.ashojash.android.activity.VenueActivity;
import com.ashojash.android.adapter.VenueAdapter;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.struct.StructVenue;
import com.ashojash.android.webserver.JsonParser;
import com.ashojash.android.webserver.WebServer;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectedFragment extends Fragment {
    private ProgressBar selectedProgressbar;
    private TextView txtSelectedError;
    private LinearLayout retryView;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private String citySlug;
    private List<StructVenue> venueList;

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
        txtSelectedError = (TextView) getView().findViewById(R.id.txtErrorSelectedFragment);
        retryView = (LinearLayout) getView().findViewById(R.id.retryViewSelectedFragment);
        retryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideErrorViews();
                getSelectedVenues();
            }
        });
        Bundle bundle = getArguments();
        if (bundle != null) {
            citySlug = bundle.getString("current_city_slug");
        }
        getSelectedVenues();
//        getAnotherUserLocation();
//        getUserLocation();
//       get current location of user
//        request nearby venues
    }


    private void getSelectedVenues() {
        JsonObjectRequest jsonObjectRequest = WebServer.getEliteVenue(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    venueList = JsonParser.parseVenuesJsonObject(response);
                    Intent intent = new Intent(getActivity(), VenueActivity.class);
                    adapter = new VenueAdapter(venueList, AppController.context, intent);
                    recyclerView.setAdapter(adapter);
                    selectedProgressbar.setVisibility(View.GONE);
                } catch (JSONException e) {
                    showErrorViews();
                    txtSelectedError.setText(R.string.error_retrieving_data);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showErrorViews();
                txtSelectedError.setText(R.string.error_retrieving_data);
            }
        }, citySlug);
        AppController.getInstance().addToRequestQueue(jsonObjectRequest, "SELECTED_FRAGMENT");
    }

    private void showErrorViews() {
        selectedProgressbar.setVisibility(View.GONE);
        retryView.setVisibility(View.VISIBLE);
    }

    private void hideErrorViews() {
        retryView.setVisibility(View.GONE);
        selectedProgressbar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();
        AppController.getInstance().cancelPendingRequests("SELECTED_FRAGMENT");
    }

}
