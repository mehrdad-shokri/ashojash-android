package com.ashojash.android.activity;

import android.content.Intent;
import android.os.Bundle;
import com.ashojash.android.R;
import com.ashojash.android.fragment.HeroFragment;
import com.ashojash.android.fragment.NearbyFragment;
import com.ashojash.android.fragment.SelectedFragment;
import com.ashojash.android.helper.AppController;


public class MainActivity extends BottomToolbarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        super.setupBottomToolbarLayoutActive(0);
        String citySlug = AppController.defaultPref.getString("current_city_slug", null);
        if (citySlug == null) {
            Intent intent = new Intent(AppController.currentActivity, CityListActivity.class);
            startActivity(intent);
            finish();
        }
        Bundle bundle = new Bundle();
        bundle.putString("current_city_slug", citySlug);
        if (savedInstanceState == null) {
            HeroFragment heroFragment = (HeroFragment) addFragment(R.id.fragmentHeroContainer, new HeroFragment());
            heroFragment.setArguments(bundle);
            NearbyFragment nearbyFragment = (NearbyFragment) addFragment(R.id.fragmentNearbyContainer, new NearbyFragment());
            nearbyFragment.setArguments(bundle);
            SelectedFragment selectedFragment = (SelectedFragment) addFragment(R.id.fragmentTopVenuesContainer, new SelectedFragment());
            selectedFragment.setArguments(bundle);
        }
    }
}