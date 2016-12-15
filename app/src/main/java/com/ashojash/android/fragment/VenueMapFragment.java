package com.ashojash.android.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ashojash.android.R;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.Venue;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.Locale;

import static com.ashojash.android.helper.AppController.context;

public class VenueMapFragment extends Fragment implements OnMapReadyCallback {
  private GoogleMap mMap;
  private LatLng venueLatLng;
  private Venue venue;
  private MapView mMapView;

  public VenueMapFragment() {
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_venue_map, container, false);
    mMapView = (MapView) v.findViewById(R.id.venueMap);
    try {
      MapsInitializer.initialize(getActivity().getApplicationContext());
      mMapView.onCreate(savedInstanceState);
    } catch (Exception e) {
      e.printStackTrace();
    }
    mMapView.onResume();
    mMapView.getMapAsync(this);
    return v;
  }

  @Override public void onResume() {
    super.onResume();
    mMapView.onResume();
  }

  @Override public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;
    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    Marker venueMarker = mMap.addMarker(new MarkerOptions().position(venueLatLng)
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin)));
    venueMarker.setFlat(true);
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(venueLatLng, 10));
    mMap.getUiSettings().setAllGesturesEnabled(false);
    mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
      @Override public void onMapClick(LatLng latLng) {
        float lat = venue.location.lat;
        float lng = venue.location.lng;
        String uri =
            String.format(Locale.ENGLISH, "geo:%f,%f?q=%f,%f(%s)", lat, lng, lat, lng, venue.name);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
          context.startActivity(intent);
        }
      }
    });
  }

  @Override
  public void onPause() {
    super.onPause();
    mMapView.onPause();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    mMapView.onDestroy();
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    mMapView.onLowMemory();
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setupViews();
    String slug = getActivity().getIntent().getStringExtra("slug");
    if (slug == null) getActivity().getFragmentManager().popBackStack();
    venue =
        AppController.gson.fromJson(getActivity().getIntent().getStringExtra("venue"), Venue.class);
    venueLatLng = new LatLng(venue.location.lat, venue.location.lng);
  }

 /* private SupportMapFragment mMapFragment;

  private void setupVenueMap() {
    mMapFragment = new SupportMapFragment() {
      @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getMapAsync(new OnMapReadyCallback() {
          @Override public void onMapReady(GoogleMap mMap) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            Marker hamburg = mMap.addMarker(new MarkerOptions().position(venueLatLng)
                .icon(BitmapDescriptorFactory.fromBitmap(
                    new IconicsDrawable(AppController.context).icon(
                        GoogleMaterial.Icon.gmd_local_dining)
                        .color(getResources().getColor(R.color.colorMapIcon))
                        .sizeDp(24)
                        .toBitmap())));
            hamburg.setFlat(true);
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
              @Override public boolean onMarkerClick(Marker marker) {
                return true;
              }
            });
            // Move the camera instantly to hamburg with a zoom of 15.
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(venueLatLng, 10));
            mMap.getUiSettings().setAllGesturesEnabled(false);
            // Zoom in, animating the camera.
            mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
          }
        });
      }
    };
    getChildFragmentManager().beginTransaction().add(R.id.venueMap, mMapFragment).commit();
  }*/

  private void setupViews() {

  }
}
