package com.ashojash.android.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.MenuItem;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ashojash.android.R;
import com.ashojash.android.adapter.VenueSearchResultAdapter;
import com.ashojash.android.fragment.HeroFragment;
import com.ashojash.android.fragment.NearbyFragment;
import com.ashojash.android.fragment.SelectedFragment;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.struct.StructVenue;
import com.ashojash.android.webserver.JsonParser;
import com.ashojash.android.webserver.WebServer;
import com.mypopsy.drawable.SearchArrowDrawable;
import com.mypopsy.widget.FloatingSearchView;
import com.mypopsy.widget.internal.ViewUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class MainActivity extends BottomToolbarActivity implements
        ActionMenuView.OnMenuItemClickListener {

    private String TAG = AppController.TAG;
    private FloatingSearchView mSearchView;
    private static final String VENUE_SEARCH_REQUEST_TAG = "VENUE_SEARCH";
    private static final String CITY_SLUG = AppController.defaultPref.getString("current_city_slug", null);

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
                    search(query.toString().trim());
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
//        mSearch.search(query);
        JsonObjectRequest request = WebServer.searchVenues(CITY_SLUG, query, 4, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                mSearchView.setAdapter();
                try {
                    List<StructVenue> venues = JsonParser.parseVenuesJsonObject(response);
                    Intent intent = new Intent(AppController.currentActivity, VenueActivity.class);
                    RecyclerView.Adapter adapter = new VenueSearchResultAdapter(venues, AppController.context, intent);
                    mSearchView.setAdapter(adapter);
                    showSearchProgress(false);
                    adapter.notifyDataSetChanged();
                    if (venues.size() == 0)
                        Toast.makeText(MainActivity.this, R.string.no_results_found, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    errorSearching();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorSearching();
            }

        });

        AppController.getInstance().addToRequestQueue(request, VENUE_SEARCH_REQUEST_TAG);
    }

    private void errorSearching() {
        showSearchProgress(false);
        Toast.makeText(MainActivity.this, R.string.error_retrieving_data, Toast.LENGTH_SHORT).show();
    }

    private void cancelSearch() {
        AppController.getInstance().cancelPendingRequests(VENUE_SEARCH_REQUEST_TAG);
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