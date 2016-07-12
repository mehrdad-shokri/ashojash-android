package com.ashojash.android.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.ashojash.android.R;
import com.ashojash.android.event.LocationEvents;
import com.ashojash.android.event.OnApiResponseErrorEvent;
import com.ashojash.android.event.PermissionEvents;
import com.ashojash.android.event.VenueApiEvents;
import com.ashojash.android.fragment.LocationPermissionNotAvailableFragment;
import com.ashojash.android.fragment.LocationServiceNotAvailableFragment;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.Venue;
import com.ashojash.android.ui.AshojashSnackbar;
import com.ashojash.android.util.*;
import com.ashojash.android.webserver.VenueApi;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MapsActivity extends BottomToolbarActivity implements OnMapReadyCallback {
  public static final int NEARBY_DEFAULT_DISTANCE = 3;
  public static final int NEARBY_DEFAULT_LIMIT = 30;
  private GoogleMap mMap;
  private View mapView;
  private SupportMapFragment mapFragment;
  private FloatingActionButton myLocationFab;
  private MapsActivity instance = this;
  private LatLng lastKnownLatLng;
  private LatLng lastRequestedVenuesLatLng;
  private float lastKnownBearing;
  private ViewGroup errorView;
  private ViewGroup progressView;
  private ViewGroup searchAreaView;
  final Marker[] lastTouchedMarker = { null };

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_maps);
    setupViews(savedInstanceState);
    if (!PermissionUtil.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
      setupErrors(new LocationPermissionNotAvailableFragment());
    } else {
      requestLocationService();
    }
  }

  private void requestLocationService() {
    LocationRequestUtil util = new LocationRequestUtil(this);
    util.settingsRequest();
  }

  @Subscribe public void onEvent(PermissionEvents.OnPermissionGranted e) {
    if (e.permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
      resetView();
      requestLocationService();
    }
  }

  @Subscribe public void onEvent(LocationEvents.OnLocationServiceNotAvailable e) {
    Fragment fragment = new LocationServiceNotAvailableFragment();
    Bundle bundle = new Bundle();
    bundle.putParcelable(LocationServiceNotAvailableFragment.STATUS_KEY, e.status);
    fragment.setArguments(bundle);
    setupErrors(fragment);
  }

  @Subscribe public void onEvent(LocationEvents.OnLocationServiceAvailable e) {
    resetView();
    mapFragment.getMapAsync(this);
    setPending(true);
  }

  HashMap<LatLng, Venue> venueHashMap = new HashMap<>();

  @Subscribe public void onEvent(final VenueApiEvents.OnNearbyVenuesResult e) {
    searchAreaView.setVisibility(GONE);
    mMap.clear();
    AsyncTask.execute(new Runnable() {
      List<Venue> venues = e.venueList;
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
    //        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(lastRequestedVenuesLatLng, 19, 0, 0)));
    setPending(false);
  }

  @Subscribe public void onEvent(OnApiResponseErrorEvent e) {
    setFinishOnTouchOutside(false);
    new AshojashSnackbar.AshojashSnackbarBuilder(findViewById(R.id.rootView)).duration(
        Snackbar.LENGTH_SHORT)
        .message(R.string.error_retrieving_data)
        .build()
        .setAction(R.string.try_again, new View.OnClickListener() {
          @Override public void onClick(View v) {
            VenueApi.nearby(lastKnownLatLng.latitude, lastKnownLatLng.longitude,
                NEARBY_DEFAULT_DISTANCE, NEARBY_DEFAULT_LIMIT);
          }
        })
        .show();
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == LocationRequestUtil.REQUEST_CODE) {
      switch (resultCode) {
        case Activity.RESULT_OK:
          BusProvider.getInstance().post(new LocationEvents.OnLocationServiceAvailable());
          break;
        case Activity.RESULT_CANCELED:
          new AshojashSnackbar.AshojashSnackbarBuilder(findViewById(R.id.rootView)).duration(
              Snackbar.LENGTH_LONG)
              .message(R.string.error_location_service_not_turned_on)
              .build()
              .show();
          break;
      }
    }
  }

  @Override public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(32.4279, 53.6880), 5));
    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    final LocationUtil util = new LocationUtil();
    getLocation(util, true);
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
        View v = getLayoutInflater().inflate(R.layout.card_marker_info, null);
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
    myLocationFab.setOnClickListener(new View.OnClickListener() {
      byte counter = 0;

      @Override public void onClick(View v) {
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
          getLocation(util, false);
        } else {
          getLocation(util, true);
        }
      }
    });
  }

  private void startVenueActivity(Marker marker) {
    Venue venue = venueHashMap.get(marker.getPosition());
    String slug = venue.slug;
    Intent intent = new Intent(MapsActivity.this, VenueActivity.class);
    lastTouchedMarker[0] = null;
    intent.putExtra("slug", slug);
    intent.putExtra("venue", AppController.gson.toJson(venue));
    startActivity(intent);
  }

  private void getLocation(LocationUtil util, final boolean isLastKnownLocationUnknown) {
    util.getLocation(instance, new LocationUtil.LocationResult() {
      @Override public void gotLocation(Location location) {
        LatLng temp = new LatLng(35.803279, 51.447910);
        VenueApi.nearby(temp.latitude, temp.longitude, NEARBY_DEFAULT_DISTANCE,
            NEARBY_DEFAULT_LIMIT);
        AppController.HANDLER.post(new Runnable() {
          @Override public void run() {
            myLocationFab.setVisibility(VISIBLE);
          }
        });
        lastKnownBearing = location.getBearing();
        lastKnownLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        lastRequestedVenuesLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (isLastKnownLocationUnknown) {
          AppController.HANDLER.post(new Runnable() {
            @Override public void run() {
              mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                  LocationUtil.toBounds(lastRequestedVenuesLatLng, 2000), 0));
            }
          });
        }
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
                    (northEastDistance > southWestDistance) ? northEastDistance : southWestDistance;
                lastRequestedVenuesLatLng =
                    new LatLng(centerLatLng.latitude, centerLatLng.longitude);
                searchAreaView.setVisibility(GONE);
                VenueApi.nearby(centerLocation.getLatitude(), centerLocation.getLongitude(),
                    farthestDistance, NEARBY_DEFAULT_LIMIT);
                setPending(true);
              }
            });
          }
        });
      }
    }, 3000);
  }

  @Override protected void onStart() {
    super.onStart();
    BusProvider.getInstance().register(this);
  }

  @Override protected void onStop() {
    super.onStop();
    BusProvider.getInstance().unregister(this);
  }

  private void setupViews(@Nullable Bundle savedInstanceState) {
    attach(this, savedInstanceState);
    mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    errorView = (ViewGroup) findViewById(R.id.error);
    searchAreaView = (ViewGroup) findViewById(R.id.btnSearchArea);
    mapView = findViewById(R.id.map);
    myLocationFab = (FloatingActionButton) findViewById(R.id.myLocationFab);
    myLocationFab.setImageDrawable(
        new IconicsDrawable(this, GoogleMaterial.Icon.gmd_my_location).sizeDp(22)
            .color(Color.parseColor("#666666")));
    progressView = (ViewGroup) findViewById(R.id.progressbar);
  }

  private void setPending(boolean isPending) {
    if (isPending) {
      progressView.setVisibility(VISIBLE);
      myLocationFab.setVisibility(GONE);
    } else {
      progressView.setVisibility(GONE);
      myLocationFab.setVisibility(VISIBLE);
    }
  }

  private void resetView() {
    removeFragment(getSupportFragmentManager().findFragmentById(R.id.error));
    mapView.setVisibility(VISIBLE);
    errorView.setVisibility(GONE);
  }

  private void setupErrors(Fragment fragment) {
    resetView();
    mapView.setVisibility(GONE);
    errorView.setVisibility(VISIBLE);
    addFragment(R.id.error, fragment);
  }
}