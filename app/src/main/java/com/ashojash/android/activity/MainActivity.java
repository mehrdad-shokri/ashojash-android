package com.ashojash.android.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.ActionMenuView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.ashojash.android.R;
import com.ashojash.android.adapter.VenueSearchResultAdapter;
import com.ashojash.android.event.OnApiResponseErrorEvent;
import com.ashojash.android.event.VenueApiEvents;
import com.ashojash.android.fragment.HeroFragment;
import com.ashojash.android.fragment.NearbyFragment;
import com.ashojash.android.fragment.SelectedFragment;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.Venue;
import com.ashojash.android.ui.AshojashSnackbar;
import com.ashojash.android.utils.BusProvider;
import com.ashojash.android.webserver.VenueApi;
import com.mypopsy.drawable.SearchArrowDrawable;
import com.mypopsy.widget.FloatingSearchView;
import com.mypopsy.widget.internal.ViewUtils;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/*
* Checked for bus and json
* */
public class MainActivity extends BottomToolbarActivity implements
        ActionMenuView.OnMenuItemClickListener {

    private String TAG = AppController.TAG;
    private FloatingSearchView mSearchView;
    private static final String CITY_SLUG = AppController.defaultPref.getString("current_city_slug", null);
    private String searchQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        super.setupBottomToolbarLayoutActive(0);
        setupSearch();

        if (CITY_SLUG == null) {
            Intent intent = new Intent(AppController.currentActivity, CityListActivity.class);
            startActivity(intent);
            finish();
        }
        Bundle bundle = new Bundle();
        bundle.putString("current_city_slug", CITY_SLUG);
        if (savedInstanceState == null) {
            HeroFragment heroFragment = (HeroFragment) addFragment(R.id.fragmentHeroContainer, new HeroFragment());
            heroFragment.setArguments(bundle);
            NearbyFragment nearbyFragment = (NearbyFragment) addFragment(R.id.fragmentNearbyContainer, new NearbyFragment());
            nearbyFragment.setArguments(bundle);
            SelectedFragment selectedFragment = (SelectedFragment) addFragment(R.id.fragmentTopVenuesContainer, new SelectedFragment());
            selectedFragment.setArguments(bundle);
        }
    }

    private void setupSearch() {
        mSearchView = (FloatingSearchView) findViewById(R.id.search);
        mSearchView.setOnMenuItemClickListener(this);
        mSearchView.setText(null);
        showClearButton(false);
        showSearchProgress(false);
        updateNavigationIcon();
        mSearchView.setOnSearchFocusChangedListener(new FloatingSearchView.OnSearchFocusChangedListener() {
            @Override
            public void onFocusChanged(final boolean focused) {
                boolean textEmpty = mSearchView.getText().length() == 0;
                showClearButton(focused && !textEmpty);
                if (!focused) {
                    showSearchProgress(false);
                    cancelSearch();
                }
            }
        });
        mSearchView.setOnIconClickListener(new FloatingSearchView.OnIconClickListener() {
            @Override
            public void onNavigationClick() {
                mSearchView.setActivated(!mSearchView.isActivated());
            }
        });

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSearchAction(CharSequence query) {
                if (query.length() < 3) {
                    Toast.makeText(MainActivity.this, R.string.search_enter_min_three_char_to_start_search, Toast.LENGTH_SHORT).show();
                    return;
                }
                mSearchView.setActivated(false);
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                Log.d(TAG, "onSearchAction: query: " + query);
                intent.putExtra("query", String.valueOf(query));
                startActivity(intent);
            }
        });
        mSearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {
                int queryLength = query.length();
                showClearButton(queryLength > 0 && mSearchView.isActivated());
                if (queryLength >= 3) {
                    cancelSearch();
                    showSearchProgress(mSearchView.isActivated());
                    searchQuery = query.toString().trim();
                    search(searchQuery);
                } else {
                    cancelSearch();
                    showSearchProgress(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        mSearchView.setItemAnimator(new CustomSuggestionItemAnimator(mSearchView));
    }

    private void search(String query) {
        VenueApi.search(CITY_SLUG, query, 4);
    }

    @Subscribe
    public void onEvent(VenueApiEvents.OnSearchResultsReady event) {
//        mSearchView.setAdapter();
        Log.d(TAG, "onSearchResultsAvailable: " + event.venuePaginated.data.size());
        List<Venue> venueList = event.venuePaginated.data;
        VenueSearchResultAdapter adapter = new VenueSearchResultAdapter(venueList);
        adapter.setOnItemClickListener(new VenueSearchResultAdapter.OnItemClickListener() {
            @Override
            public void onClick(Venue venue) {
                Intent intent = new Intent(AppController.currentActivity, VenueActivity.class);
                intent.putExtra("slug", venue.slug);
                AppController.currentActivity.startActivity(intent);
            }
        });
        mSearchView.setAdapter(adapter);
        if (venueList.size() == 0)
            AshojashSnackbar.make(this, R.string.no_results_found, Snackbar.LENGTH_LONG);
        showSearchProgress(false);
        adapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onEvent(OnApiResponseErrorEvent event) {
        showSearchProgress(false);
        AshojashSnackbar.make(this, R.string.error_retrieving_data, Snackbar.LENGTH_LONG).setAction(R.string.try_again, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search(searchQuery);
            }
        }).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BusProvider.getInstance().unregister(this);
    }

    private void cancelSearch() {
//        AppController.getInstance().cancelPendingRequests(VENUE_SEARCH_REQUEST_TAG);
    }

    private void updateNavigationIcon() {
        Context context = mSearchView.getContext();
        Drawable drawable = new SearchArrowDrawable(context);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ViewUtils.getThemeAttrColor(context, R.attr.colorControlNormal));
        mSearchView.setIcon(drawable);
    }

    private void showSearchProgress(boolean show) {
        mSearchView.getMenu().findItem(R.id.menu_progress).setVisible(show);
    }

    private void showClearButton(boolean show) {
        mSearchView.getMenu().findItem(R.id.menu_clear).setVisible(show);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Log.d(TAG, "onMenuItemClick: ");
        switch (item.getItemId()) {
            case R.id.menu_clear:
                mSearchView.setText(null);
                mSearchView.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mSearchView.isActivated())
            mSearchView.setActivated(false);
        else
            super.onBackPressed();
    }
}