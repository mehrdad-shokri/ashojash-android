package com.ashojash.android.activity;

import android.Manifest;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import com.ashojash.android.R;
import com.ashojash.android.event.LocationEvents;
import com.ashojash.android.event.PermissionEvents;
import com.ashojash.android.fragment.LocationPermissionNotAvailableFragment;
import com.ashojash.android.fragment.LocationServiceNotAvailableFragment;
import com.ashojash.android.fragment.NearbyVenuesFragment;
import com.ashojash.android.fragment.TagsSuggestionFragment;
import com.ashojash.android.util.BusProvider;
import com.ashojash.android.util.LocationRequestUtil;
import com.ashojash.android.util.LocationUtil;
import com.ashojash.android.util.PermissionUtil;
import com.ashojash.android.webserver.TagApi;
import com.ashojash.android.webserver.VenueApi;
import com.wang.avi.AVLoadingIndicatorView;
import org.greenrobot.eventbus.Subscribe;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class SearchActivity extends BottomToolbarActivity {
  private EditText edtTermSearch;
  private EditText edtLocationSearch;
  private ViewGroup errorView;
  private ViewGroup contentSurvey;
  private AVLoadingIndicatorView progressbar;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search);
    setupViews(savedInstanceState);
    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
  }

  @Override protected void onResume() {
    super.onResume();
    if (!PermissionUtil.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
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

  private void requestLocationService() {
    LocationRequestUtil locationRequestUtil = new LocationRequestUtil(this);
    locationRequestUtil.settingsRequest();
  }

  private static final String TAG = "SearchActivity";

  @Subscribe public void onEvent(LocationEvents.OnLocationServiceAvailable e) {
    resetView();
    setPending(true);
    final TagsSuggestionFragment tagsSuggestionFragment = new TagsSuggestionFragment();
    final NearbyVenuesFragment nearbyVenuesFragment = new NearbyVenuesFragment();
    LocationUtil util = new LocationUtil();
    util.getLocation(this, new LocationUtil.LocationResult() {
      @Override public void gotLocation(Location location) {
        Log.d(TAG, "gotLocation: " + location.toString());
        VenueApi.nearby(location.getLatitude(), location.getLongitude(), .5, 10);
        TagApi.suggestions();
      }
    });
    addFragment(R.id.contentSurvey, tagsSuggestionFragment);
    addFragment(R.id.contentSurvey, nearbyVenuesFragment);
  }

  @Subscribe public void onEvent(LocationEvents.OnLocationServiceNotAvailable e) {
    Fragment fragment = new LocationServiceNotAvailableFragment();
    Bundle bundle = new Bundle();
    bundle.putParcelable(LocationServiceNotAvailableFragment.STATUS_KEY, e.status);
    fragment.setArguments(bundle);
    setupErrors(fragment);
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
    //mapView.setVisibility(GONE);
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
  }

  private void setupViews(Bundle savedInstanceState) {
    attach(this, savedInstanceState);
    edtTermSearch = (EditText) findViewById(R.id.edtTermSearch);
    edtLocationSearch = (EditText) findViewById(R.id.edtLocationSearch);
    errorView = (ViewGroup) findViewById(R.id.error);
    contentSurvey = (ViewGroup) findViewById(R.id.contentSurvey);
    progressbar = (AVLoadingIndicatorView) findViewById(R.id.progressbar);
  }
}
