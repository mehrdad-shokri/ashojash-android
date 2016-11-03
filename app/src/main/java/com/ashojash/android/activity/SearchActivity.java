package com.ashojash.android.activity;

import android.Manifest;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;
import com.ashojash.android.R;
import com.ashojash.android.event.LocationEvents;
import com.ashojash.android.event.PermissionEvents;
import com.ashojash.android.event.SearchApiEvents;
import com.ashojash.android.fragment.LocationPermissionNotAvailableFragment;
import com.ashojash.android.fragment.LocationServiceNotAvailableFragment;
import com.ashojash.android.fragment.NearbyVenuesFragment;
import com.ashojash.android.fragment.SearchBarFragment;
import com.ashojash.android.fragment.TagsSuggestionFragment;
import com.ashojash.android.fragment.VenueTagFragment;
import com.ashojash.android.model.VenueTagCombined;
import com.ashojash.android.util.BusProvider;
import com.ashojash.android.util.LocationRequestUtil;
import com.ashojash.android.util.LocationUtil;
import com.ashojash.android.util.PermissionUtil;
import com.ashojash.android.webserver.SearchApi;
import com.ashojash.android.webserver.TagApi;
import com.ashojash.android.webserver.VenueApi;
import com.google.android.gms.maps.model.LatLng;
import com.wang.avi.AVLoadingIndicatorView;
import org.greenrobot.eventbus.Subscribe;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class SearchActivity extends BottomToolbarActivity {

  private ViewGroup errorView;
  private ViewGroup contentSurvey;
  private ViewGroup venueTagView;
  private ViewGroup nearbyVenues;
  private AVLoadingIndicatorView progressbar;
  private LatLng lastKnownLatLng;
  private VenueTagFragment venueTagFragment;
  private double DEFAULT_SEARCH_DISTANCE;
  private int NEARBY_SEARCH_LIMIT;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search);
    setupViews(savedInstanceState);
    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    if (!PermissionUtil.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
      setupErrors(new LocationPermissionNotAvailableFragment());
    } else {
      requestLocationService();
    }
    SearchBarFragment searchBarFragment = new SearchBarFragment();
    searchBarFragment.setOnTermChanged(new SearchBarFragment.OnTermChanged() {
      @Override public void onTermChanged(String term) {
        if (term.isEmpty()) {
          nearbyVenues.setVisibility(VISIBLE);
          contentSurvey.setVisibility(VISIBLE);
          venueTagView.setVisibility(GONE);
        } else {
          SearchApi.cancelSuggest();
          nearbyVenues.setVisibility(GONE);
          contentSurvey.setVisibility(GONE);
          venueTagView.setVisibility(VISIBLE);
          if (lastKnownLatLng != null) {
            SearchApi.suggest(term, lastKnownLatLng.latitude, lastKnownLatLng.longitude);
          } else {
            Log.d(TAG, "onTermChanged: unknown location");
          }
        }
      }
    });
    addFragment(R.id.termFrameLayout, searchBarFragment);
  }

  @Override protected void onResume() {
    super.onResume();
  }

  @Subscribe public void onEvent(PermissionEvents.OnPermissionGranted e) {
    if (e.permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
      resetView();
      requestLocationService();
    }
  }

  private static final String TAG = "SearchActivity";

  @Subscribe public void onEvent(LocationEvents.OnLocationServiceAvailable e) {
    resetView();
    //setPending(true);
    final TagsSuggestionFragment tagsSuggestionFragment = new TagsSuggestionFragment();
    final NearbyVenuesFragment nearbyVenuesFragment = new NearbyVenuesFragment();
    venueTagFragment = new VenueTagFragment();
    LocationUtil util = new LocationUtil();
    util.getLocation(this, new LocationUtil.LocationResult() {
      @Override public void gotLocation(Location location) {
        lastKnownLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        DEFAULT_SEARCH_DISTANCE = .5;
        NEARBY_SEARCH_LIMIT = 8;
        VenueApi.nearby(location.getLatitude(), location.getLongitude(), DEFAULT_SEARCH_DISTANCE,
            NEARBY_SEARCH_LIMIT);
        TagApi.suggestions();
      }
    });
    addFragment(R.id.tagSuggestionFramelayout, tagsSuggestionFragment);
    addFragment(R.id.nearbyVenuesFramelayout, nearbyVenuesFragment);
    addFragment(R.id.venueTagFramelayout, venueTagFragment);
  }

  @Subscribe public void onEvent(LocationEvents.OnLocationServiceNotAvailable e) {
    Fragment fragment = new LocationServiceNotAvailableFragment();
    Bundle bundle = new Bundle();
    bundle.putParcelable(LocationServiceNotAvailableFragment.STATUS_KEY, e.status);
    fragment.setArguments(bundle);
    setupErrors(fragment);
  }

  @Subscribe public void onEvent(SearchApiEvents.OnSuggestResultsReady e) {
    VenueTagCombined combined = e.venueTagCombined;
    venueTagFragment.setVenueTags(combined);
  }

  @Override protected void onStart() {
    super.onStart();
    BusProvider.getInstance().register(this);
  }

  @Override protected void onStop() {
    super.onStop();
    BusProvider.getInstance().unregister(this);
  }

  private void setupErrors(Fragment fragment) {
    resetView();
    errorView.setVisibility(VISIBLE);
    addFragment(R.id.error, fragment);
  }

  private void setPending(boolean showProgress) {
    if (showProgress) {
      progressbar.show();
    } else {
      progressbar.hide();
    }
  }

  private void resetView() {
    removeFragment(getSupportFragmentManager().findFragmentById(R.id.error));
    errorView.setVisibility(GONE);
    contentSurvey.setVisibility(VISIBLE);
    nearbyVenues.setVisibility(VISIBLE);
  }

  private void requestLocationService() {
    LocationRequestUtil locationRequestUtil = new LocationRequestUtil(this);
    locationRequestUtil.settingsRequest();
  }

  private void setupViews(Bundle savedInstanceState) {
    attach(this, savedInstanceState);
    errorView = (ViewGroup) findViewById(R.id.error);
    contentSurvey = (ViewGroup) findViewById(R.id.tagSuggestionFramelayout);
    venueTagView = (ViewGroup) findViewById(R.id.venueTagFramelayout);
    nearbyVenues = (ViewGroup) findViewById(R.id.nearbyVenuesFramelayout);
    progressbar = (AVLoadingIndicatorView) findViewById(R.id.progressbar);
  }
}
