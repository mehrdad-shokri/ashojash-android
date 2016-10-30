package com.ashojash.android.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.ashojash.android.R;
import com.ashojash.android.event.PermissionEvents;
import com.ashojash.android.fragment.LocationAccessAgreementFragment;
import com.ashojash.android.fragment.LocationPermissionNotAvailableFragment;
import com.ashojash.android.util.BusProvider;
import com.heinrichreimersoftware.materialintro.app.NavigationPolicy;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import org.greenrobot.eventbus.Subscribe;

public class AshojashIntroActivity extends BaseIntroActivity {

  private boolean hasLocationPermission;
  private boolean hasShownIntro;


  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    autoplay(3500L);
    Intent intent = getIntent();
    hasLocationPermission = intent.getBooleanExtra("HAS_LOCATION_PERMISSION", false);
    hasShownIntro = intent.getBooleanExtra("HAS_SHOWN_INTRO", false);
    if (!hasShownIntro) {
      addSlide(new FragmentSlide.Builder().background(R.color.color_material_intro_everything)
          .backgroundDark(R.color.color_dark_material_intro_everything)
          .fragment(R.layout.fragment_intro_everything)
          .build());
      addSlide(new FragmentSlide.Builder().background(R.color.color_material_location)
          .backgroundDark(R.color.color_dark_material_location)
          .fragment(R.layout.fragment_intro_location)
          .build());
      addSlide(new FragmentSlide.Builder().background(R.color.color_material_special_offers)
          .backgroundDark(R.color.color_dark_material_special_offers)
          .fragment(R.layout.fragment_intro_special_offers)
          .build());
      addSlide(new FragmentSlide.Builder().background(R.color.color_material_reviews)
          .backgroundDark(R.color.color_dark_material_reviews)
          .fragment(R.layout.fragment_intro_reviews)
          .build());
    }
    if (!hasLocationPermission) {
      Log.d("Ashojash", "onCreate: Adding dynamic location agreement");
      addSlide(new FragmentSlide.Builder().background(R.color.color_material_white)
          .backgroundDark(R.color.color_dark_material_white)
          .fragment(new LocationPermissionNotAvailableFragment())
          .build());
    } else {
      Log.d("Ashojash", "onCreate: Adding static location agreement");
      addSlide(new FragmentSlide.Builder().background(R.color.color_material_white)
          .backgroundDark(R.color.color_dark_material_white)
          .fragment(new LocationAccessAgreementFragment())
          .build());
    }
    final int slidesCount = getSlides().size();
    if (slidesCount == 1 && !hasLocationPermission) {
      setButtonBackVisible(false);
      setPagerIndicatorVisible(false);
    }
    setNavigationPolicy(new NavigationPolicy() {
      @Override public boolean canGoForward(int position) {
        if (!hasLocationPermission && position == slidesCount - 1) {
          cancelAutoplay();
          return false;
        }
        return true;
      }

      @Override public boolean canGoBackward(int position) {
        return true;
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

  @Subscribe public void onEvent(PermissionEvents.OnPermissionGranted event) {
    Log.d("Ashojash", "onEvent: Permission granted");
    if (event.permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
      hasLocationPermission = true;
      startSplashActivity();
    }
  }

  private void startSplashActivity() {
    Intent intent = new Intent(this, SplashActivity.class);
    startActivity(intent);
    overridePendingTransition(0, 0);
    finish();
  }
}
