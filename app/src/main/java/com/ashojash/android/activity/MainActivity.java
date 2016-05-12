package com.ashojash.android.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.ActionMenuView;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.ashojash.android.utils.AuthUtils;
import com.ashojash.android.utils.BusProvider;
import com.ashojash.android.webserver.VenueApi;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mypopsy.drawable.SearchArrowDrawable;
import com.mypopsy.widget.FloatingSearchView;
import com.mypopsy.widget.internal.ViewUtils;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;
import com.roughike.bottombar.OnTabClickListener;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class MainActivity extends ToolbarActivity implements
        ActionMenuView.OnMenuItemClickListener {

    private FloatingSearchView mSearchView;
    private static String CITY_SLUG;
    private String searchQuery;
    private BottomBar mBottomBar;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mBottomBar.onSaveInstanceState(outState);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CITY_SLUG = AppController.defaultPref.getString("current_city_slug", null);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupSearch();
        setupBottomBar(savedInstanceState);
        if (CITY_SLUG == null) {
            Intent intent = new Intent(MainActivity.this, CityListActivity.class);
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


    private void setupBottomBar(Bundle savedInstanceState) {
        mBottomBar = BottomBar.attachShy((CoordinatorLayout) findViewById(R.id.rootView), findViewById(R.id.scrollingContent), savedInstanceState);
        if (AuthUtils.isUserLoggedIn()) {
            mBottomBar.setItems(
                    new BottomBarTab(new IconicsDrawable(AppController.context).icon(GoogleMaterial.Icon.gmd_home).sizeDp(25).color(AppController.context.getResources().getColor(R.color.text_primary)), R.string.title_home)
            );
            mBottomBar.mapColorForTab(0, ContextCompat.getColor(this, R.color.colorAccent));
        } else {
            mBottomBar.setItems(
                    new BottomBarTab(new IconicsDrawable(AppController.context).icon(GoogleMaterial.Icon.gmd_home).sizeDp(25).color(AppController.context.getResources().getColor(R.color.text_primary)), R.string.title_home),
                    new BottomBarTab(new IconicsDrawable(AppController.context).icon(GoogleMaterial.Icon.gmd_account_circle).sizeDp(25).color(AppController.context.getResources().getColor(R.color.text_primary)), R.string.title_profile)
            );
            mBottomBar.mapColorForTab(0, ContextCompat.getColor(this, R.color.colorAccent));
            mBottomBar.mapColorForTab(1, 0xFF5D4037);
        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mBottomBar.setOnTabClickListener(new OnTabClickListener() {
                    @Override
                    public void onTabSelected(int position) {
                        if (position == 1) {
                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(130);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    Intent intent = new Intent(MainActivity.this, GuestProfileActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(intent);
                                    overridePendingTransition(0, 0);
                                    finish();
                                }
                            });
                        }
                    }

                    @Override
                    public void onTabReSelected(int position) {
                    }
                });

            }
        });

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
        drawable.mutate();
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