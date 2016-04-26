package com.ashojash.android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.activeandroid.query.Select;
import com.ashojash.android.R;
import com.ashojash.android.activity.VenueInfoActivity;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.orm.VenueOrm;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;


public class VenueBasicInfoFragment extends Fragment {
    private TextView txtVenueName;
    private TextView txtVenuePrice;
    private LatLng venueLatLng;
    private TextView txtBtnMoreInfo;
    private String slug;

    public VenueBasicInfoFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_venue_basic_info, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupViews();
        setupVenueMap();
        slug = getActivity().getIntent().getStringExtra("slug");
        if (slug == null)
            getActivity().getFragmentManager().popBackStack();
        VenueOrm venueOrm = new Select().from(VenueOrm.class).where("slug =?", slug).executeSingle();
        venueLatLng = new LatLng(venueOrm.lat, venueOrm.lng);
        txtVenuePrice.setText(Html.fromHtml(getCostSign(venueOrm.cost)));
        txtVenueName.setText(venueOrm.name);
    }

    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;

    private void setupVenueMap() {
        final String TAG = AppController.TAG;
        mMapFragment = new SupportMapFragment() {
            @Override
            public void onActivityCreated(Bundle savedInstanceState) {
                super.onActivityCreated(savedInstanceState);
                mMap = mMapFragment.getMap();
                if (mMap != null) {
                    Log.d(TAG, "onActivityCreated: fragment is not null");
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    Marker marker = mMap.addMarker(new MarkerOptions().position(venueLatLng)
                            .icon(BitmapDescriptorFactory.fromBitmap(new IconicsDrawable(AppController.context)
                                    .icon(GoogleMaterial.Icon.gmd_local_dining)
                                    .color(getResources().getColor(R.color.colorMapIcon))
                                    .sizeDp(24).toBitmap())));
                    marker.setFlat(true);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(venueLatLng, 10));
                    mMap.getUiSettings().setAllGesturesEnabled(false);
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            startVenueInfoActivity();
                            return true;
                        }
                    });
                    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            startVenueInfoActivity();
                        }
                    });
                }
            }
        };
        getChildFragmentManager().beginTransaction().add(R.id.venueMap, mMapFragment).commit();
    }

    private void setupViews() {
        txtVenueName = (TextView) getView().findViewById(R.id.txtVenueNameVenueBasicFragment);
        txtVenuePrice = (TextView) getView().findViewById(R.id.txtVenuePriceVenueBasicFragment);
        txtBtnMoreInfo = (TextView) getView().findViewById(R.id.btnVenueMoreInfoVenueBasicFragment);
        FrameLayout rootLayout = (FrameLayout) getView().findViewById(R.id.cardVenueBasicInfo);
        rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startVenueInfoActivity();
            }
        });
    }

    private void startVenueInfoActivity() {
        Intent intent = new Intent(getActivity(), VenueInfoActivity.class);
        intent.putExtra("slug", slug);
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
