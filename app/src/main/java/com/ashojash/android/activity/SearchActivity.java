package com.ashojash.android.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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
import com.ashojash.android.fragment.SearchMapFragment;
import com.ashojash.android.fragment.SearchResultFragment;
import com.ashojash.android.fragment.StreetSuggestionFragment;
import com.ashojash.android.fragment.TagsSuggestionFragment;
import com.ashojash.android.fragment.VenueTagFragment;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.Street;
import com.ashojash.android.model.Tag;
import com.ashojash.android.model.Venue;
import com.ashojash.android.model.VenueTagCombined;
import com.ashojash.android.util.BusProvider;
import com.ashojash.android.util.LocationRequestUtil;
import com.ashojash.android.util.LocationUtil;
import com.ashojash.android.util.PermissionUtil;
import com.ashojash.android.util.UiUtil;
import com.ashojash.android.webserver.SearchApi;
import com.ashojash.android.webserver.TagApi;
import com.ashojash.android.webserver.VenueApi;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.wang.avi.AVLoadingIndicatorView;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.Subscribe;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.ashojash.android.R.id.edtLocationSearch;
import static com.ashojash.android.R.id.edtTermSearch;

public class SearchActivity extends BottomToolbarActivity {
  private ViewGroup errorView;
  private ViewGroup tagSuggestionView;
  private ViewGroup venueTagView;
  private ViewGroup streetsView;
  private ViewGroup nearbyVenuesView;
  private ViewGroup searchResultsView;
  private ViewGroup mapsView;
  private AVLoadingIndicatorView progressbar;
  private FloatingActionButton mapFab;
  private FloatingActionButton listFab;
  private FloatingActionButton myLocationFab;
  private LatLng lastKnownLatLng;
  private double DEFAULT_SEARCH_DISTANCE = .5;
  private int NEARBY_SEARCH_LIMIT = 30;
  private String lastSearchedTerm = "";
  private String lastSearchedLocationTerm = "";
  private boolean searchedForStreetWhileLocationUnknown;
  private boolean searchedForTermWhileLocationUnknown;
  private boolean performedSearchWhileLocationUnknown;
  private static final String TAG = "SearchActivityFAB";
  private TagsSuggestionFragment tagsSuggestionFragment;
  private NearbyVenuesFragment nearbyVenuesFragment;
  private StreetSuggestionFragment streetSuggestFragment;
  private SearchResultFragment searchResultFragment;
  private SearchBarFragment searchBarFragment;
  private VenueTagFragment venueTagFragment;
  private SearchMapFragment mapFragment;
  private List<Street> nearbyStreets = new ArrayList<>();
  private boolean isLocationUnknown = true;
  private boolean isLocationServiceAvailable = false;
  private static final int SEARCH_LIST_STATE = 1;
  private static final int SEARCH_MAP_STATE = 2;
  private int searchState = 1;
  private boolean performedSearch = false;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search);
    setupViews(savedInstanceState);
    if (!PermissionUtil.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
      resetView();
      setupErrors(new LocationPermissionNotAvailableFragment());
    } else {
      requestLocationService();
    }
    final View activityRootView = findViewById(R.id.rootView);
    activityRootView.getViewTreeObserver()
        .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
          @Override
          public void onGlobalLayout() {
            int heightDiff =
                activityRootView.getRootView().getHeight() - activityRootView.getHeight();
            if (heightDiff > UiUtil.dp2px(200)) {
              hideBottombar();
            } else {
              showBottombar();
            }
          }
        });
  }

  @Override protected void onResume() {
    super.onResume();
    if (isLocationServiceAvailable) {
      resetView();
      if (searchState == SEARCH_MAP_STATE) {
        mapsView.setVisibility(VISIBLE);
      } else if (!performedSearch) {
        tagSuggestionView.setVisibility(VISIBLE);
        nearbyVenuesView.setVisibility(VISIBLE);
      } else {
        venueTagView.setVisibility(VISIBLE);
      }
    }
  }

  @Subscribe public void onEvent(PermissionEvents.OnPermissionGranted e) {
    if (e.permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
      resetView();
      requestLocationService();
    }
  }

  @Subscribe public void onEvent(LocationEvents.OnLocationServiceAvailable e) {
    isLocationServiceAvailable = true;
    resetView();
    nearbyVenuesView.setVisibility(VISIBLE);
    tagSuggestionView.setVisibility(VISIBLE);
    mapFab.setVisibility(VISIBLE);
    LocationUtil util = new LocationUtil();
    util.getLocation(this, new LocationUtil.LocationResult() {
      @Override public void gotLocation(Location location) {
        isLocationUnknown = false;
        mapFragment.setLocation(location);
        lastKnownLatLng = new LatLng(location.getLatitude(), location.getLongitude());
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

  @Override protected void onStart() {
    super.onStart();
    BusProvider.getInstance().register(this);
  }

  @Override protected void onStop() {
    super.onStop();
    BusProvider.getInstance().unregister(this);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Log.d(TAG, "onActivityResult: " + requestCode);
    Log.d(TAG, "onActivityResult: " + resultCode);
    Log.d(TAG, "onActivityResult: " + (resultCode == Activity.RESULT_OK));
    if (requestCode == LocationRequestUtil.REQUEST_CODE) {
      if (resultCode == Activity.RESULT_OK) {
        BusProvider.getInstance().post(new LocationEvents.OnLocationServiceAvailable());
      }
    }
  }

  private void performSearch(String query, String location) {
    performedSearch = true;
    if (lastSearchedLocationTerm.isEmpty()) {
      ((EditText) findViewById(edtLocationSearch)).setText(R.string.near_me);
    }
    if (listFab.getVisibility() == VISIBLE) {
      myLocationFab.setVisibility(VISIBLE);
    }
    findViewById(R.id.searchResultFramelayout).requestFocus();
    resetView();
    setPending(true);
    View view = this.getCurrentFocus();
    if (view != null) {
      InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    if (isLocationUnknown) {
      performedSearchWhileLocationUnknown = true;
      return;
    }
    SearchApi.performSearch(lastKnownLatLng.latitude, lastKnownLatLng.longitude, query, location);
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
    venueTagView.setVisibility(GONE);
    nearbyVenuesView.setVisibility(GONE);
    streetsView.setVisibility(GONE);
    searchResultsView.setVisibility(GONE);
    mapsView.setVisibility(GONE);
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
    mapFragment = new SearchMapFragment();
    mapFragment.setOnSearchRequested(new SearchMapFragment.onSearchRequested() {
      @Override public void onSearchRequested(Location location, double distance) {
        Log.d(TAG, "onSearchRequested: "
            + location.getLatitude()
            + " "
            + location.getLongitude()
            + " "
            + distance);
        VenueApi.nearby(location.getLatitude(), location.getLongitude(), distance,
            NEARBY_SEARCH_LIMIT);
      }
    });
    addFragment(R.id.termFrameLayout, searchBarFragment);
    addFragment(R.id.tagSuggestionFramelayout, tagsSuggestionFragment);
    addFragment(R.id.nearbyVenuesFramelayout, nearbyVenuesFragment);
    addFragment(R.id.venueTagFramelayout, venueTagFragment);
    addFragment(R.id.streetSuggestFrameLayout, streetSuggestFragment);
    addFragment(R.id.searchResultFramelayout, searchResultFragment);
    addFragment(R.id.mapsFramelayout, mapFragment);
    errorView = (ViewGroup) findViewById(R.id.error);
    tagSuggestionView = (ViewGroup) findViewById(R.id.tagSuggestionFramelayout);
    venueTagView = (ViewGroup) findViewById(R.id.venueTagFramelayout);
    streetsView = (ViewGroup) findViewById(R.id.streetSuggestFrameLayout);
    nearbyVenuesView = (ViewGroup) findViewById(R.id.nearbyVenuesFramelayout);
    searchResultsView = (ViewGroup) findViewById(R.id.searchResultFramelayout);
    mapsView = (ViewGroup) findViewById(R.id.mapsFramelayout);
    progressbar = (AVLoadingIndicatorView) findViewById(R.id.progressbar);
    mapFab = (FloatingActionButton) findViewById(R.id.fabMapView);
    listFab = (FloatingActionButton) findViewById(R.id.fabListView);
    myLocationFab = (FloatingActionButton) findViewById(R.id.myLocationFab);
    mapFab.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        resetView();
        myLocationFab.setVisibility(VISIBLE);
        mapsView.setVisibility(VISIBLE);
        listFab.setVisibility(VISIBLE);
        mapFab.setVisibility(GONE);
        mapsView.requestFocus();
        searchState = SEARCH_MAP_STATE;
      }
    });
    listFab.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        resetView();
        mapFab.setVisibility(VISIBLE);
        listFab.setVisibility(GONE);
        myLocationFab.setVisibility(GONE);
        searchState = SEARCH_LIST_STATE;
        searchResultsView.requestFocus();
        searchResultsView.setVisibility(VISIBLE);
      }
    });
    myLocationFab.setImageDrawable(
        new IconicsDrawable(AppController.context, GoogleMaterial.Icon.gmd_my_location).sizeDp(22)
            .color(Color.parseColor("#666666")));
    myLocationFab.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        mapFragment.updateMapView();
      }
    });
    tagsSuggestionFragment.setOnCardClickListener(new OnCardClickListener() {
      @Override public void onClick(Object model) {
        Tag tag = (Tag) model;
        ((EditText) findViewById(edtTermSearch)).setText(tag.name);
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
        performedSearch = false;
        if (isLocationUnknown) return;
        lastSearchedTerm = term;
        myLocationFab.setVisibility(GONE);
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
        performedSearch = false;
        if (isLocationUnknown) return;
        lastSearchedLocationTerm = term;
        myLocationFab.setVisibility(GONE);
        String nearMe = getResources().getString(R.string.near_me);
        resetView();
        streetsView.setVisibility(VISIBLE);
        if (lastSearchedLocationTerm.equals(nearMe) || lastSearchedLocationTerm.isEmpty()) {
          SearchApi.cancelStreetSuggest();
          streetSuggestFragment.setStreets(nearbyStreets);
        } else {
          if (lastKnownLatLng != null) {
            //SearchApi.cancelStreetSuggest();
            SearchApi.suggestStreet(term, lastKnownLatLng.latitude, lastKnownLatLng.longitude);
          } else {
            searchedForStreetWhileLocationUnknown = true;
          }
        }
      }

      @Override public void onTermFocusChanged(boolean hasFocus) {
        if (isLocationUnknown) return;
        if (hasFocus) {
          myLocationFab.setVisibility(GONE);
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
        if (isLocationUnknown) return;
        if (hasFocus) {
          myLocationFab.setVisibility(GONE);
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
        if (isLocationServiceAvailable) {
          performSearch(term, location);
        }
      }
    });
    streetSuggestFragment.setOnCardClickListener(new OnCardClickListener() {
      @Override public void onClick(Object model) {
        Street street = (Street) model;
        ((EditText) findViewById(edtLocationSearch)).setText(street.name);
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

  @Subscribe public void onEvent(LocationEvents.OnLocationServiceNotAvailable e) {
    resetView();
    mapFab.setVisibility(GONE);
    Fragment fragment = new LocationServiceNotAvailableFragment();
    Bundle bundle = new Bundle();
    bundle.putParcelable(LocationServiceNotAvailableFragment.STATUS_KEY, e.status);
    fragment.setArguments(bundle);
    setupErrors(fragment);
  }

  @Subscribe public void onEvent(SearchApiEvents.onSearchResultsReady e) {
    setPending(false);
    searchResultFragment.setVenues(e.venues);
    mapFragment.setVenues(e.venues);
    if (searchState == SEARCH_LIST_STATE) {
      searchResultsView.setVisibility(VISIBLE);
    } else {
      mapsView.setVisibility(VISIBLE);
    }
  }

  @Subscribe public void onEvent(SearchApiEvents.OnSuggestResultsReady e) {
    VenueTagCombined combined = e.venueTagCombined;
    venueTagFragment.setVenueTags(combined);
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
    searchResultFragment.setVenues(e.venueList);
    mapFragment.setVenues(e.venueList);
  }

  @Override public void onBackPressed() {
    if (searchResultsView.getVisibility() == VISIBLE || mapsView.getVisibility() == VISIBLE) {
      resetView();
      nearbyVenuesView.setVisibility(VISIBLE);
      tagSuggestionView.setVisibility(VISIBLE);
      myLocationFab.setVisibility(GONE);
      listFab.setVisibility(GONE);
      mapFab.setVisibility(VISIBLE);
    } else {
      getBottombar().selectTabAtPosition(0, true);
    }
  }

  public void onTabReselected() {
    resetView();
    nearbyVenuesView.setVisibility(VISIBLE);
    tagSuggestionView.setVisibility(VISIBLE);
    myLocationFab.setVisibility(GONE);
    listFab.setVisibility(GONE);
    mapFab.setVisibility(VISIBLE);
  }
}