package com.ashojash.android.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import com.ashojash.android.R;
import com.ashojash.android.event.LocationEvents;
import com.ashojash.android.event.PermissionEvents;
import com.ashojash.android.event.SearchApiEvents;
import com.ashojash.android.fragment.LocationPermissionNotAvailableFragment;
import com.ashojash.android.fragment.LocationServiceNotAvailableFragment;
import com.ashojash.android.fragment.NearbyVenuesFragment;
import com.ashojash.android.fragment.SearchBarFragment;
import com.ashojash.android.fragment.StreetSuggestionFragment;
import com.ashojash.android.fragment.TagsSuggestionFragment;
import com.ashojash.android.fragment.VenueTagFragment;
import com.ashojash.android.model.Street;
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
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.Subscribe;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class SearchActivity extends BottomToolbarActivity {

  private ViewGroup errorView;
  private ViewGroup tagSuggestion;
  private ViewGroup venueTagView;
  private ViewGroup streetsView;
  private ViewGroup nearbyVenues;
  private AVLoadingIndicatorView progressbar;
  private LatLng lastKnownLatLng;
  private VenueTagFragment venueTagFragment;
  private double DEFAULT_SEARCH_DISTANCE;
  private int NEARBY_SEARCH_LIMIT;
  private String lastSearchedTerm = "";
  private String lastSearchedStreetTerm = "";
  private boolean searchedForStreetWhileLocationUnknown;
  private boolean searchedForTermWhileLocationUnknown;
  private boolean performedSearchWhileLocationUnknown;
  private TagsSuggestionFragment tagsSuggestionFragment;
  private NearbyVenuesFragment nearbyVenuesFragment;
  private StreetSuggestionFragment streetSuggestFragment;
  private SearchBarFragment searchBarFragment;
  private List<Street> nearbyStreets = new ArrayList<>();

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
    LocationUtil util = new LocationUtil();
    util.getLocation(this, new LocationUtil.LocationResult() {
      @Override public void gotLocation(Location location) {
        lastKnownLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        DEFAULT_SEARCH_DISTANCE = .5;
        NEARBY_SEARCH_LIMIT = 8;
        VenueApi.nearby(location.getLatitude(), location.getLongitude(), DEFAULT_SEARCH_DISTANCE,
            NEARBY_SEARCH_LIMIT);
        TagApi.suggestions();
        SearchApi.nearbyStreets(lastKnownLatLng.latitude, lastKnownLatLng.longitude);
        if (searchedForTermWhileLocationUnknown
            && !lastSearchedTerm.isEmpty()) {
          SearchApi.suggestVenueTag(lastSearchedTerm, lastKnownLatLng.latitude,
              lastKnownLatLng.longitude);
        }
        if (searchedForStreetWhileLocationUnknown
            && !lastSearchedStreetTerm.isEmpty()) {
          SearchApi.cancelStreetSuggest();
          SearchApi.suggestStreet(lastSearchedStreetTerm, lastKnownLatLng.latitude,
              lastKnownLatLng.longitude);
        }
        if (performedSearchWhileLocationUnknown) {
          performSearch(lastSearchedTerm,
              lastSearchedStreetTerm);
        }
      }
    });
  }

  private void performSearch(String term, String location) {
    tagSuggestion.setVisibility(GONE);
    nearbyVenues.setVisibility(GONE);
    streetsView.setVisibility(GONE);
    venueTagView.setVisibility(GONE);
    setPending(true);
    if (lastKnownLatLng == null) return;
    SearchApi.performSearch(lastKnownLatLng.latitude, lastKnownLatLng.longitude, term, location);
  }

  @Subscribe public void onEvent(SearchApiEvents.onSearchResultsReady e) {
    setPending(false);
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
    tagSuggestion.setVisibility(VISIBLE);
    nearbyVenues.setVisibility(VISIBLE);
  }

  private void requestLocationService() {
    LocationRequestUtil locationRequestUtil = new LocationRequestUtil(this);
    locationRequestUtil.settingsRequest();
  }

  private void setupViews(Bundle savedInstanceState) {
    attach(this, savedInstanceState);
    errorView = (ViewGroup) findViewById(R.id.error);
    tagSuggestion = (ViewGroup) findViewById(R.id.tagSuggestionFramelayout);
    venueTagView = (ViewGroup) findViewById(R.id.venueTagFramelayout);
    streetsView = (ViewGroup) findViewById(R.id.streetSuggestFrameLayout);
    nearbyVenues = (ViewGroup) findViewById(R.id.nearbyVenuesFramelayout);
    progressbar = (AVLoadingIndicatorView) findViewById(R.id.progressbar);
    searchBarFragment = new SearchBarFragment();
    tagsSuggestionFragment = new TagsSuggestionFragment();
    nearbyVenuesFragment = new NearbyVenuesFragment();
    streetSuggestFragment = new StreetSuggestionFragment();
    venueTagFragment = new VenueTagFragment();
    searchBarFragment.setOnTermChanged(new SearchBarFragment.OnTermChanged() {

      @Override public void onTermChanged(String term) {
        lastSearchedTerm = term;
        if (lastSearchedTerm.isEmpty()) {
          nearbyVenues.setVisibility(VISIBLE);
          tagSuggestion.setVisibility(VISIBLE);
          venueTagView.setVisibility(GONE);
        } else {
          nearbyVenues.setVisibility(GONE);
          tagSuggestion.setVisibility(GONE);
          venueTagView.setVisibility(VISIBLE);
          if (lastKnownLatLng != null) {
            SearchApi.suggestVenueTag(lastSearchedTerm, lastKnownLatLng.latitude,
                lastKnownLatLng.longitude);
          } else {
            searchedForTermWhileLocationUnknown = true;
          }
        }
      }

      @Override public void onLocationTermChanged(String term) {
        lastSearchedStreetTerm = term;
        String nearMe = getResources().getString(R.string.near_me);
        if (lastSearchedStreetTerm.equals(nearMe) || lastSearchedStreetTerm.isEmpty()) {
          SearchApi.cancelStreetSuggest();
          streetSuggestFragment.setStreets(nearbyStreets);
        } else {
          if (lastKnownLatLng != null) {
            SearchApi.cancelStreetSuggest();
            SearchApi.suggestStreet(term, lastKnownLatLng.latitude, lastKnownLatLng.longitude);
          } else {
            searchedForStreetWhileLocationUnknown = true;
          }
        }
      }

      @Override public void onTermFocusChanged(boolean hasFocus) {
        if (hasFocus) {
          streetsView.setVisibility(GONE);
          if (!lastSearchedTerm.isEmpty()) {
            tagSuggestion.setVisibility(GONE);
            nearbyVenues.setVisibility(GONE);
            venueTagView.setVisibility(VISIBLE);
          } else {
            tagSuggestion.setVisibility(VISIBLE);
            nearbyVenues.setVisibility(VISIBLE);
            venueTagView.setVisibility(GONE);
          }
        }
      }

      @Override public void onLocationFocusChanged(boolean hasFocus, EditText editText) {
        if (hasFocus) {
          if (editText.getText().toString().equals(
              getResources().getString(R.string.near_me))) {
            editText.selectAll();
            editText.setSelectAllOnFocus(true);
          } else {
            editText.setSelectAllOnFocus(false);
          }
          streetsView.setVisibility(VISIBLE);
          nearbyVenues.setVisibility(GONE);
          venueTagView.setVisibility(GONE);
          tagSuggestion.setVisibility(GONE);
        }
      }

      @Override public void onSubmit(String term, String location) {
        performSearch(term, location);
      }
    });
    streetSuggestFragment.setOnChangesListener(new StreetSuggestionFragment.ChangesListener() {
      @Override public void onSearchActionRequested(String term) {
        ((EditText) findViewById(R.id.edtLocationSearch)).setText(term);
        performSearch(lastSearchedTerm, term);
      }
    });
    addFragment(R.id.termFrameLayout, searchBarFragment);
    addFragment(R.id.tagSuggestionFramelayout, tagsSuggestionFragment);
    addFragment(R.id.nearbyVenuesFramelayout, nearbyVenuesFragment);
    addFragment(R.id.venueTagFramelayout, venueTagFragment);
    addFragment(R.id.streetSuggestFrameLayout, streetSuggestFragment);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == LocationRequestUtil.REQUEST_CODE) {
      switch (resultCode) {
        case Activity.RESULT_OK:
          BusProvider.getInstance().post(new LocationEvents.OnLocationServiceAvailable());
      }
    }
  }

  @Subscribe public void onEvent(SearchApiEvents.OnNearbyStreetsResultsReady e) {
    nearbyStreets = e.streets;
    streetSuggestFragment.setStreets(nearbyStreets);
  }

  @Subscribe public void onEvent(SearchApiEvents.OnStreetSuggestResultsReady e) {
    streetSuggestFragment.setStreets(e.streets);
  }
}
