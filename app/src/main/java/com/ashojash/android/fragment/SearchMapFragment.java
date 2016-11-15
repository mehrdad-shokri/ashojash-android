package com.ashojash.android.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.ashojash.android.R;
import com.ashojash.android.activity.VenueActivity;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.Venue;
import com.ashojash.android.util.LocationUtil;
import com.ashojash.android.util.UiUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import java.util.HashMap;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class SearchMapFragment extends Fragment implements OnMapReadyCallback {
  public static final int NEARBY_DEFAULT_DISTANCE = 3;
  public static final int NEARBY_DEFAULT_LIMIT = 30;
  private ViewGroup progressView;
  private ViewGroup searchAreaView;
  private Location location;
  final Marker[] lastTouchedMarker = { null };
  MapView mMapView;
  private GoogleMap mMap;
  private LatLng lastKnownLatLng;
  private float lastKnownBearing;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_search_maps, container, false);
    mMapView = (MapView) v.findViewById(R.id.map);
    mMapView.onCreate(savedInstanceState);
    mMapView.onResume();
    MapsInitializer.initialize(getActivity().getApplicationContext());
    return v;
  }

  @Override public void onResume() {
    super.onResume();
    setupViews();
  }

  HashMap<LatLng, Venue> venueHashMap = new HashMap<>();

  private void startVenueActivity(Marker marker) {
    Venue venue = venueHashMap.get(marker.getPosition());
    String slug = venue.slug;
    Intent intent = new Intent(getActivity(), VenueActivity.class);
    lastTouchedMarker[0] = null;
    intent.putExtra("slug", slug);
    intent.putExtra("venue", AppController.gson.toJson(venue));
    startActivity(intent);
  }

  private void setupViews() {
    searchAreaView = (ViewGroup) getView().findViewById(R.id.btnSearchArea);
    progressView = (ViewGroup) getView().findViewById(R.id.progressbar);
    mMapView.getMapAsync(this);
  }

  private void setPending(boolean isPending) {
    if (isPending) {
      progressView.setVisibility(VISIBLE);
    } else {
      progressView.setVisibility(GONE);
    }
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  @Override public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(32.4279, 53.6880), 5));
    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    //getLocation(util, true);
    try {
      mMap.setMyLocationEnabled(true);
    } catch (SecurityException e) {
      //            no need to catch, permissions already handled onCreate
    }
    mMap.getUiSettings().setMyLocationButtonEnabled(false);
    mMap.getUiSettings().setMapToolbarEnabled(false);
    mMap.getUiSettings().setCompassEnabled(true);
    mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
      @Override public View getInfoWindow(final Marker marker) {
        LatLng position = marker.getPosition();
        final Venue venue = venueHashMap.get(position);
        View v = AppController.layoutInflater.inflate(R.layout.card_marker_info, null);
        TextView txtVenueName = (TextView) v.findViewById(R.id.txtVenueName);
        TextView txtVenueAddress = (TextView) v.findViewById(R.id.txtVenueAddress);
        final ImageView imgVenue = (ImageView) v.findViewById(R.id.imgVenueImg);
        Glide.with(AppController.context)
            .load(UiUtil.setUrlWidth(venue.photo.url, (int) UiUtil.dp2px(100)))
            .asBitmap()
            .override(500, 500)
            .fitCenter()
            .listener(new RequestListener<String, Bitmap>() {
              @Override
              public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target,
                  boolean isFromMemoryCache, boolean isFirstResource) {
                if (!isFromMemoryCache) marker.showInfoWindow();
                return false;
              }

              @Override public boolean onException(Exception e, String model, Target<Bitmap> target,
                  boolean isFirstResource) {
                e.printStackTrace();
                return false;
              }
            })
            .into(imgVenue);
        txtVenueName.setText(venue.name);
        txtVenueAddress.setText(venue.location.address);
        return v;
      }

      @Override public View getInfoContents(final Marker marker) {
        return null;
      }
    });

    mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

      @Override public void onInfoWindowClick(Marker marker) {
        startVenueActivity(marker);
      }
    });
    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

      @Override public boolean onMarkerClick(Marker marker) {
        String TAG = AppController.TAG;
        if ((lastTouchedMarker[0] != null) && (lastTouchedMarker[0].equals(marker))) {
          startVenueActivity(marker);
          return true;
        }
        lastTouchedMarker[0] = marker;
        return false;
      }
    });
    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
      @Override public void onMapClick(LatLng latLng) {
        lastTouchedMarker[0] = null;
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

  byte counter = 0;

  public void onFabClick() {
    if (lastKnownLatLng != null) {
      if (counter == 0) {
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
            new CameraPosition(lastKnownLatLng, 18, 75, lastKnownBearing)));
        counter++;
      } else if (counter == 1) {
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
            new CameraPosition(lastKnownLatLng, 19, 0, 0)));
        counter++;
      } else if (counter == 2) {
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(LocationUtil.toBounds(lastKnownLatLng, 2000),
                0));
        counter = 0;
      }
    }
  }
}