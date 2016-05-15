package com.ashojash.android.fragment;

import android.os.Bundle;
import com.ashojash.android.R;
import com.ashojash.android.helper.AppController;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

public class VenueMiniMapFragment extends SupportMapFragment {
    private GoogleMap mMap;
    public LatLng venueLatLng;

    public VenueMiniMapFragment() {
    }

    private OnMapClickListener listener;

    public interface OnMapClickListener {
        void onClick();
    }

    public void setOnMapClickListener(OnMapClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMap = this.getMap();
        if (mMap != null) {
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
                    if (listener != null)
                        listener.onClick();
                    return true;
                }
            });
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    if (listener != null)
                        listener.onClick();
                }
            });
        }
    }
}
