package com.ashojash.android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.ashojash.android.R;
import com.ashojash.android.activity.VenueInfoActivity;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.Venue;
import com.google.android.gms.maps.model.LatLng;


public class VenueBasicInfoFragment extends Fragment {
    private TextView txtVenueName;
    private TextView txtVenuePrice;
    private LatLng venueLatLng;
    private String slug;
    private Venue venue;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_venue_basic_info, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        slug = getActivity().getIntent().getStringExtra("slug");
        venue = AppController.gson.fromJson(getActivity().getIntent().getStringExtra("venue"), Venue.class);
        venueLatLng = new LatLng(venue.location.lat, venue.location.lng);
        if (slug == null)
            getActivity().getFragmentManager().popBackStack();
        setupViews();
        setupVenueMap();
    }

    private VenueMiniMapFragment mMapFragment;

    private void setupVenueMap() {
        mMapFragment = new VenueMiniMapFragment();
        mMapFragment.venueLatLng = venueLatLng;
        mMapFragment.setOnMapClickListener(new VenueMiniMapFragment.OnMapClickListener() {
            @Override
            public void onClick() {
                startVenueInfoActivity();
            }
        });
        getChildFragmentManager().beginTransaction().add(R.id.venueMap, mMapFragment).commit();
    }

    private void setupViews() {
        txtVenueName = (TextView) getView().findViewById(R.id.txtVenueNameVenueBasicFragment);
        txtVenuePrice = (TextView) getView().findViewById(R.id.txtVenuePriceVenueBasicFragment);
        FrameLayout rootLayout = (FrameLayout) getView().findViewById(R.id.cardVenueBasicInfo);
        rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startVenueInfoActivity();
            }
        });
        txtVenuePrice.setText(Html.fromHtml(getCostSign(venue.cost)));
        txtVenueName.setText(venue.name);
    }

    private void startVenueInfoActivity() {
        Intent intent = new Intent(getActivity(), VenueInfoActivity.class);
        intent.putExtra("slug", slug);
        intent.putExtra("venue", getActivity().getIntent().getStringExtra("venue"));
        startActivity(intent);
    }


    private String getCostSign(int cost) {
        String dollars = "<font color=#2c8019>";
        for (int i = 0; i < cost; i++) {
            dollars += "$ ";
        }
        dollars += "</font> <font color=#c0c0c0>";
        for (int i = 0; i < 5 - cost; i++) {
            dollars += "$ ";
        }
        dollars += "</font>";
        return dollars;
    }
}
