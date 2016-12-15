package com.ashojash.android.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.HashMap;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class SearchMapFragment extends Fragment implements OnMapReadyCallback {
  private ViewGroup progressView;
  //private LatLng lastRequestedVenuesLatLng;
  private ViewGroup searchAreaView;
  final Marker[] lastTouchedMarker = { null };
  MapView mMapView;
  private GoogleMap mMap;
  private LatLng lastKnownLatLng;
  private float lastKnownBearing;
  private SearchMapFragment.onSearchRequested onSearchRequested;

  public interface onSearchRequested {
    void onSearchRequested(Location location, double distance);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_search_maps, container, false);
    mMapView = (MapView) v.findViewById(R.id.map);
    try {
      MapsInitializer.initialize(getActivity().getApplicationContext());
      mMapView.onCreate(savedInstanceState);
    } catch (Exception e) {
      e.printStackTrace();
    }
    mMapView.onResume();
    mMapView.getMapAsync(this);
    searchAreaView = (ViewGroup) v.findViewById(R.id.btnSearchArea);
    progressView = (ViewGroup) v.findViewById(R.id.progressbar);
    return v;
  }

  @Override public void onResume() {
    super.onResume();
    mMapView.onResume();
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

  private void setPending(boolean isPending) {
    if (isPending) {
      progressView.setVisibility(VISIBLE);
    } else {
      progressView.setVisibility(GONE);
    }
  }

  public void setLocation(Location location) {
    lastKnownBearing = location.getBearing();
    lastKnownLatLng = new LatLng(location.getLatitude(), location.getLongitude());
    updateMapView();
  }

  @Override public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(32.4279, 53.6880), 5));
    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    mMap.getUiSettings().setMyLocationButtonEnabled(false);
    mMap.getUiSettings().setMapToolbarEnabled(false);
    mMap.getUiSettings().setCompassEnabled(false);
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
    AppController.HANDLER.postDelayed(new Runnable() {
      @Override public void run() {
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
          @Override public void onCameraChange(final CameraPosition cameraPosition) {
            searchAreaView.setVisibility(VISIBLE);
            final LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
            searchAreaView.setOnClickListener(new View.OnClickListener() {
              @Override public void onClick(View v) {
                if (onSearchRequested != null) {
                  searchAreaView.setVisibility(GONE);
                  setPending(true);
                  LatLng southWestLatLng = bounds.southwest;
                  LatLng northEastLatLng = bounds.northeast;
                  LatLng centerLatLng = cameraPosition.target;
                  Location centerLocation = new Location("center");
                  centerLocation.setLatitude(centerLatLng.latitude);
                  centerLocation.setLongitude(centerLatLng.longitude);
                  Location northEastLocation = new Location("north east");
                  northEastLocation.setLatitude(northEastLatLng.latitude);
                  northEastLocation.setLongitude(northEastLatLng.longitude);
                  Location southWestLocation = new Location("south west location");
                  southWestLocation.setLatitude(southWestLatLng.latitude);
                  southWestLocation.setLongitude(southWestLatLng.longitude);
                  float northEastDistance = centerLocation.distanceTo(northEastLocation);
                  float southWestDistance = centerLocation.distanceTo(southWestLocation);
                  double farthestDistance =
                      (northEastDistance > southWestDistance) ? northEastDistance
                          : southWestDistance;
                  onSearchRequested.onSearchRequested(centerLocation, farthestDistance);
                }
              }
            });
          }
        });
      }
    }, 3000);
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

  public void updateMapView() {
    AppController.HANDLER.post(new Runnable() {
      @Override public void run() {
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
    });
  }

  public void setVenues(final List<Venue> venues) {
    setPending(false);
    mMap.clear();
    AsyncTask.execute(new Runnable() {
      int i = 1;

      @Override public void run() {
        for (final Venue venue : venues) {
          LatLng position = new LatLng(venue.location.lat, venue.location.lng);
          venueHashMap.put(position, venue);
          AppController.HANDLER.post(new Runnable() {
            @Override public void run() {
              mMap.addMarker(
                  new MarkerOptions().position(new LatLng(venue.location.lat, venue.location.lng))
                      .icon(BitmapDescriptorFactory.fromBitmap(
                          UiUtil.writeOnDrawable(R.drawable.ic_map_pin,
                              UiUtil.toPersianNumber(String.valueOf(i))).getBitmap())));
              i++;
            }
          });
        }
      }
    });
  }

  public void setOnSearchRequested(onSearchRequested onSearchRequested) {
    this.onSearchRequested = onSearchRequested;
  }
}