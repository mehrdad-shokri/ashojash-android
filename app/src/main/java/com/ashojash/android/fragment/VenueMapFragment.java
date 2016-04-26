package com.ashojash.android.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.activeandroid.query.Select;
import com.ashojash.android.R;
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

public class VenueMapFragment extends Fragment {
    private LatLng venueLatLng;

    public VenueMapFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_venue_map, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupViews();
        setupVenueMap();
        String slug = getActivity().getIntent().getStringExtra("slug");
        if (slug == null)
            getActivity().getFragmentManager().popBackStack();
        VenueOrm venueOrm = new Select().from(VenueOrm.class).where("slug =?", slug).executeSingle();
        venueLatLng = new LatLng(venueOrm.lat, venueOrm.lng);
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
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    Marker hamburg = mMap.addMarker(new MarkerOptions().position(venueLatLng)
                            .icon(BitmapDescriptorFactory.fromBitmap(new IconicsDrawable(AppController.context)
                                    .icon(GoogleMaterial.Icon.gmd_local_dining)
                                    .color(getResources().getColor(R.color.colorMapIcon))
                                    .sizeDp(24).toBitmap())));
                    hamburg.setFlat(true);
                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            return true;
                        }
                    });
                    // Move the camera instantly to hamburg with a zoom of 15.
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(venueLatLng, 10));
                    mMap.getUiSettings().setAllGesturesEnabled(false);
                    // Zoom in, animating the camera.
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        Log.d(TAG, "setupVenueMap: " + (mMapFragment.getView() == null));
                    }
                }
            }
        };
        getChildFragmentManager().beginTransaction().add(R.id.venueMap, mMapFragment).commit();

    }

    private void setupViews() {

    }
}
