package com.ashojash.android.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.ashojash.android.R;
import com.ashojash.android.adapter.OnCardClickListener;
import com.ashojash.android.event.LocationEvents;
import com.ashojash.android.event.PermissionEvents;
import com.ashojash.android.event.SearchApiEvents;
import com.ashojash.android.event.TagApiEvents;
import com.ashojash.android.event.VenueApiEvents;
import com.ashojash.android.fragment.LocationPermissionNotAvailableFragment;
import com.ashojash.android.fragment.LocationServiceNotAvailableFragment;
import com.ashojash.android.fragment.NearbyVenuesFragment;
import com.ashojash.android.fragment.SearchBarFragment;
import com.ashojash.android.fragment.SearchResultFragment;
import com.ashojash.android.fragment.StreetSuggestionFragment;
import com.ashojash.android.fragment.TagsSuggestionFragment;
import com.ashojash.android.fragment.VenueTagFragment;
import com.ashojash.android.model.Street;
import com.ashojash.android.model.Tag;
import com.ashojash.android.model.Venue;
import com.ashojash.android.model.VenueTagCombined;
import com.ashojash.android.util.BusProvider;
import com.ashojash.android.util.LocationRequestUtil;
import com.ashojash.android.util.LocationUtil;
import com.ashojash.android.util.PermissionUtil;
import com.ashojash.android.webserver.SearchApi;
import com.ashojash.android.webserver.TagApi;
import com.ashojash.android.webserver.VenueApi;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.wang.avi.AVLoadingIndicatorView;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.Subscribe;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class SearchActivity extends BottomToolbarActivity {

  private ViewGroup errorView;
  private ViewGroup tagSuggestionView;
  private ViewGroup venueTagView;
  private ViewGroup streetsView;
  private ViewGroup nearbyVenuesView;
  private ViewGroup searchResultsView;
  private AVLoadingIndicatorView progressbar;
  private LatLng lastKnownLatLng;
  private double DEFAULT_SEARCH_DISTANCE;
  private int NEARBY_SEARCH_LIMIT;
  private String lastSearchedTerm = "";
  private String lastSearchedLocationTerm = "";
  private boolean searchedForStreetWhileLocationUnknown;
  private boolean searchedForTermWhileLocationUnknown;
  private boolean performedSearchWhileLocationUnknown;

  private TagsSuggestionFragment tagsSuggestionFragment;
  private NearbyVenuesFragment nearbyVenuesFragment;
  private StreetSuggestionFragment streetSuggestFragment;
  private SearchResultFragment searchResultFragment;
  private SearchBarFragment searchBarFragment;
  private VenueTagFragment venueTagFragment;
  private List<Street> nearbyStreets = new ArrayList<>();
  private boolean isLocationUnknown = true;
  private EditText edtTermSearch;
  private EditText edtLocationSearch;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search);
    setupViews(savedInstanceState);
    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    if (!PermissionUtil.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
      resetView();
      setupErrors(new LocationPermissionNotAvailableFragment());
    } else {
      requestLocationService();
    }
  }

  @Subscribe public void onEvent(PermissionEvents.OnPermissionGranted e) {
    if (e.permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
      resetView();
      requestLocationService();
    }
  }

  @Subscribe public void onEvent(LocationEvents.OnLocationServiceAvailable e) {
    resetView();
    nearbyVenuesView.setVisibility(VISIBLE);
    tagSuggestionView.setVisibility(VISIBLE);
    isLocationUnknown = false;
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
            && !lastSearchedLocationTerm.isEmpty()) {
          SearchApi.cancelStreetSuggest();
          SearchApi.suggestStreet(lastSearchedLocationTerm, lastKnownLatLng.latitude,
              lastKnownLatLng.longitude);
        }
        if (performedSearchWhileLocationUnknown) {
          performSearch(lastSearchedTerm, lastSearchedLocationTerm);
        }
      }
    });
  }

  private static final String TAG = "SearchActivity";

  private void performSearch(String query, String location) {
    Log.d(TAG, "performSearch: " + query);
    Log.d(TAG, "performSearch: " + location);
    resetView();
    setPending(true);
    //findViewById(R.id.edtTermSearch).clearFocus();
    findViewById(R.id.searchResultFramelayout).requestFocus();
    View view = this.getCurrentFocus();
    if (view != null) {
      InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    if (lastKnownLatLng == null) return;
    SearchApi.performSearch(lastKnownLatLng.latitude, lastKnownLatLng.longitude, query, location);
  }

  @Subscribe public void onEvent(SearchApiEvents.onSearchResultsReady e) {
    setPending(false);
    searchResultsView.setVisibility(VISIBLE);
    searchResultFragment.setVenues(e.venues);
  }

  @Subscribe public void onEvent(LocationEvents.OnLocationServiceNotAvailable e) {
    resetView();
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
    errorView.setVisibility(VISIBLE);
    addFragment(R.id.error, fragment);
  }

  private void setPending(boolean showProgress) {
    if (showProgress) {
      progressbar.setVisibility(VISIBLE);
    } else {
      progressbar.setVisibility(GONE);
    }
  }

  private void resetView() {
    removeFragment(getSupportFragmentManager().findFragmentById(R.id.error));
    setPending(false);
    errorView.setVisibility(GONE);
    tagSuggestionView.setVisibility(GONE);
    nearbyVenuesView.setVisibility(GONE);
    venueTagView.setVisibility(GONE);
    streetsView.setVisibility(GONE);
    searchResultsView.setVisibility(GONE);
  }

  private void requestLocationService() {
    LocationRequestUtil locationRequestUtil = new LocationRequestUtil(this);
    locationRequestUtil.settingsRequest();
  }

  private void setupViews(Bundle savedInstanceState) {
    attach(this, savedInstanceState);
    //lastSearchedLocationTerm = getString(R.string.near_me);
    searchBarFragment = new SearchBarFragment();
    searchResultFragment = new SearchResultFragment();
    tagsSuggestionFragment = new TagsSuggestionFragment();
    nearbyVenuesFragment = new NearbyVenuesFragment();
    streetSuggestFragment = new StreetSuggestionFragment();
    venueTagFragment = new VenueTagFragment();
    addFragment(R.id.termFrameLayout, searchBarFragment);
    addFragment(R.id.tagSuggestionFramelayout, tagsSuggestionFragment);
    addFragment(R.id.nearbyVenuesFramelayout, nearbyVenuesFragment);
    addFragment(R.id.venueTagFramelayout, venueTagFragment);
    addFragment(R.id.streetSuggestFrameLayout, streetSuggestFragment);
    addFragment(R.id.searchResultFramelayout, searchResultFragment);
    errorView = (ViewGroup) findViewById(R.id.error);
    tagSuggestionView = (ViewGroup) findViewById(R.id.tagSuggestionFramelayout);
    venueTagView = (ViewGroup) findViewById(R.id.venueTagFramelayout);
    streetsView = (ViewGroup) findViewById(R.id.streetSuggestFrameLayout);
    nearbyVenuesView = (ViewGroup) findViewById(R.id.nearbyVenuesFramelayout);
    searchResultsView = (ViewGroup) findViewById(R.id.searchResultFramelayout);
    progressbar = (AVLoadingIndicatorView) findViewById(R.id.progressbar);
    edtTermSearch = (EditText) findViewById(R.id.edtTermSearch);
    edtLocationSearch = (EditText) findViewById(R.id.edtLocationSearch);
    tagsSuggestionFragment.setOnCardClickListener(new OnCardClickListener() {
      @Override public void onClick(Object model) {
        Tag tag = (Tag) model;
        ((EditText) findViewById(R.id.edtTermSearch)).setText(tag.name);
        performSearch(tag.name, lastSearchedLocationTerm);
      }
    });
    nearbyVenuesFragment.setOnCardClickListener(new OnCardClickListener() {
      @Override public void onClick(Object model) {
        startVenueActivity((Venue) model);
      }
    });
    searchResultFragment.setOnCardClickListener(new OnCardClickListener() {
      @Override public void onClick(Object model) {
        Log.d(TAG, "onClick:");
        startVenueActivity((Venue) model);
      }
    });
    venueTagFragment.setOnItemClickListener(new VenueTagFragment.OnItemClickListener() {
      @Override public void onTagItemClickListener(Tag tag) {
        searchBarFragment.setTermText(tag.name);
        performSearch(tag.name, lastSearchedLocationTerm);
      }

      @Override public void onVenueItemClickListener(Venue venue) {
        startVenueActivity(venue);
      }
    });
    searchBarFragment.setOnTermChanged(new SearchBarFragment.OnTermChanged() {
      @Override public void onTermChanged(String term) {
        if (isLocationUnknown) return;
        lastSearchedTerm = term;
        if (lastSearchedTerm.isEmpty()) {
          resetView();
          nearbyVenuesView.setVisibility(VISIBLE);
          tagSuggestionView.setVisibility(VISIBLE);
        } else {
          resetView();
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
        if (isLocationUnknown) return;
        lastSearchedLocationTerm = term;
        String nearMe = getResources().getString(R.string.near_me);
        resetView();
        streetsView.setVisibility(VISIBLE);
        if (lastSearchedLocationTerm.equals(nearMe) || lastSearchedLocationTerm.isEmpty()) {
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
        if (isLocationUnknown) return;
        if (hasFocus) {
          resetView();
          if (!lastSearchedTerm.isEmpty()) {
            venueTagView.setVisibility(VISIBLE);
          } else {
            tagSuggestionView.setVisibility(VISIBLE);
            nearbyVenuesView.setVisibility(VISIBLE);
          }
        }
      }

      @Override public void onLocationFocusChanged(boolean hasFocus, EditText editText) {
        if (isLocationUnknown) {
          return;
        }
        if (hasFocus) {
          resetView();
          streetsView.setVisibility(VISIBLE);
          if (editText.getText().toString().equals(
              getResources().getString(R.string.near_me))) {
            editText.selectAll();
            editText.setSelectAllOnFocus(true);
          } else {
            editText.setSelectAllOnFocus(false);
          }
        }
      }

      @Override public void onSubmit(String term, String location) {
        performSearch(term, location);
      }
    });
    streetSuggestFragment.setOnCardClickListener(new OnCardClickListener() {
      @Override public void onClick(Object model) {
        Log.d("Ashojash", "setStreets: onStreet card click");
        Street street = (Street) model;
        ((EditText) findViewById(R.id.edtLocationSearch)).setText(street.name);
        performSearch(lastSearchedTerm, street.name);
      }
    });
  }

  private void startVenueActivity(Venue model) {
    Venue venue = model;
    Intent intent = new Intent(SearchActivity.this, VenueActivity.class);
    intent.putExtra("venue", new Gson().toJson(venue));
    intent.putExtra("slug", venue.slug);
    overridePendingTransition(0, 0);
    startActivity(intent);
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

  @Subscribe public void onEvent(TagApiEvents.OnTagsSuggestionsReady e) {
    tagsSuggestionFragment.setTags(e.tags);
  }

  @Subscribe public void onEvent(VenueApiEvents.OnNearbyVenuesResult e) {
    nearbyVenuesFragment.setVenues(e.venueList);
  }

  @Override protected void onResume() {
    super.onResume();
  }
}
