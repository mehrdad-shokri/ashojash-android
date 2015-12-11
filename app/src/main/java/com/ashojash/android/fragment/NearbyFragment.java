package com.ashojash.android.fragment;


import android.content.Intent;
import android.location.Location;
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
import com.ashojash.android.utils.LocationUtil;
import com.ashojash.android.webserver.JsonParser;
import com.ashojash.android.webserver.WebServer;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NearbyFragment extends Fragment {
    private ProgressBar nearbyProgressbar;
    private TextView txtNearbyError;
    private LinearLayout retryView;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private double lat;
    private double lng;
    private String citySlug;
    private List<StructVenue> venueList;

    public NearbyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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

//        getAnotherUserLocation();
//        getUserLocation();
//       get current location of user
//        request nearby venues
    }

    private void setupViews() {
        nearbyProgressbar = (ProgressBar) getView().findViewById(R.id.prgNearbyFragment);
        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerViewNearbyFragment);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        txtNearbyError = (TextView) getView().findViewById(R.id.txtErrorNearbyFragment);
        retryView = (LinearLayout) getView().findViewById(R.id.errorViewVenueReviewFragment);
        retryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideErrorViews();
                getUserLocation();
            }
        });
    }

    private LocationUtil locationUtil;

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
                JsonObjectRequest jsonObjectRequest = WebServer.getCityNearbyVenues(new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        json object
                        try {
                            venueList = JsonParser.parseVenuesJsonObject(response);
                            Intent intent = new Intent(getActivity(), VenueActivity.class);
                            adapter = new VenueAdapter(venueList, AppController.context, intent);
                            recyclerView.setAdapter(adapter);
                            if (venueList.size() == 0)
                                throw new IllegalArgumentException("no nearby venues");
                            nearbyProgressbar.setVisibility(View.GONE);
//                            update view
                        } catch (JSONException e) {
                            showErrorViews();
                            txtNearbyError.setText(R.string.error_retrieving_data);
                        } catch (IllegalArgumentException e) {
                            showErrorViews();
                            txtNearbyError.setText(R.string.no_nearby_venues);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showErrorViews();
                        txtNearbyError.setText(R.string.error_retrieving_data);
                    }
                }, citySlug, lat, lng);
                AppController.getInstance().addToRequestQueue(jsonObjectRequest, "NEARBY_FRAGMENT");
            }
        };
        locationUtil = new LocationUtil();
        locationUtil.getLocation(AppController.currentActivity, locationResult);
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
        AppController.getInstance().cancelPendingRequests("NEARBY_FRAGMENT");
    }
}
