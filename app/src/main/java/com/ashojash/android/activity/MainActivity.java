package com.ashojash.android.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.ActionMenuView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.ashojash.android.R;
import com.ashojash.android.adapter.VenueSearchResultAdapter;
import com.ashojash.android.event.OnApiResponseErrorEvent;
import com.ashojash.android.event.VenueApiEvents;
import com.ashojash.android.event.VenueCollectionEvents;
import com.ashojash.android.fragment.*;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.Venue;
import com.ashojash.android.model.VenueCollection;
import com.ashojash.android.ui.AshojashSnackbar;
import com.ashojash.android.ui.UiUtils;
import com.ashojash.android.utils.BusProvider;
import com.ashojash.android.webserver.VenueApi;
import com.ashojash.android.webserver.VenueCollectionApi;
import com.google.gson.Gson;
import com.mypopsy.drawable.SearchArrowDrawable;
import com.mypopsy.widget.FloatingSearchView;
import com.mypopsy.widget.internal.ViewUtils;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BottomToolbarActivity implements
        ActionMenuView.OnMenuItemClickListener {

    private FloatingSearchView mSearchView;
    private static String CITY_SLUG = AppController.citySlug;
    private String searchQuery;
    private ArrayList<VenueCollection> collectionsSlideShow, collectionsVerticalNormal, collectionsHero;
    //    private ArrayList<Venue> venueSlideShow, venueHeroBig, venueHeroNormal;
    private ArrayList<VenueCollection> venueSlideShow, venueHeroBig, venueHeroNormal, venueVerticalNormal;
    private ViewGroup contentContainer;
    private Boolean isFirstGroupAdded = false;
    private AshojashSnackbar.AshojashSnackbarBuilder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupSearch();
        attachShy(0, this, savedInstanceState, (CoordinatorLayout) findViewById(R.id.rootView), findViewById(R.id.mainActivityNestedScrollView));
        contentContainer = (ViewGroup) findViewById(R.id.contentContainer);
        builder = new AshojashSnackbar.AshojashSnackbarBuilder(findViewById(R.id.rootView));
        if (CITY_SLUG == null) {
            Intent intent = new Intent(MainActivity.this, CityListActivity.class);
            startActivity(intent);
            finish();
        }
        VenueCollectionApi.collections(CITY_SLUG);
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

    }

    private void search(String query) {
        VenueApi.search(CITY_SLUG, query, 4);
    }

    @Subscribe
    public void onEvent(VenueApiEvents.OnSearchResultsReady event) {
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
            builder.message(R.string.no_results_found).duration(Snackbar.LENGTH_LONG).build().show();
        showSearchProgress(false);
        adapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onEvent(VenueCollectionEvents.OnVenueCollectionsResponse response) {
        List<VenueCollection> collections = response.venueCollections;
        handleCollection(collections);
        addViewsToActivity();
        showMainProgressBar(false);
    }

    @Subscribe
    public void onEvent(OnApiResponseErrorEvent event) {
        builder.duration(Snackbar.LENGTH_INDEFINITE).message(R.string.error_retrieving_data);

        if (event.object instanceof VenueCollectionEvents.OnVenueCollectionsResponse) {
            showMainProgressBar(false);
            builder.build().setAction(R.string.try_again, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    VenueCollectionApi.collections(CITY_SLUG);
                    showMainProgressBar(true);
                }
            }).show();
        } else if (event.object instanceof VenueApiEvents.OnSearchResultsReady) {
            showSearchProgress(false);
            builder.build().setAction(R.string.try_again, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    search(searchQuery);
                    showSearchProgress(true);
                }
            }).show();
        }
    }

    private void addViewsToActivity() {
        final Gson gson = new Gson();

        if (venueHeroBig.size() > 0) {
            VenueHeroBigFragment fragment = new VenueHeroBigFragment();
            Bundle bundle = new Bundle();
            bundle.putString("collection", gson.toJson(venueHeroBig.get(0)));
            fragment.setArguments(bundle);
            FrameLayout view = new FrameLayout(this);
            view.setId(R.id.venueHeroBig);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.TOP;
            handlePadding(view);
            handleMargins(params);
            addFragment(R.id.venueHeroBig, fragment);
            contentContainer.addView(view, params);
        }

        if (venueHeroNormal.size() > 0) {
            VenueHeroNormalFragment fragment = new VenueHeroNormalFragment();
            Bundle bundle = new Bundle();
            bundle.putString("collection", gson.toJson(venueHeroNormal.get(0)));
            fragment.setArguments(bundle);
            FrameLayout view = new FrameLayout(this);
            view.setId(R.id.venueHeroNormal);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            handlePadding(view);
            handleMargins(params);
            addFragment(R.id.venueHeroNormal, fragment);
            contentContainer.addView(view, params);
        }
        if (collectionsHero.size() > 0) {
            CollectionHeroFragment fragment = new CollectionHeroFragment();
            Bundle bundle = new Bundle();
            bundle.putString("collection", gson.toJson(collectionsHero.get(0)));
            fragment.setArguments(bundle);
            FrameLayout view = new FrameLayout(this);
            view.setId(R.id.collectionHero);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            handlePadding(view);
            handleMargins(params);
            addFragment(R.id.collectionHero, fragment);
            contentContainer.addView(view, params);
        }
        if (venueSlideShow.size() > 0) {
            VenueSlideShowFragment fragment = new VenueSlideShowFragment();
            Bundle bundle = new Bundle();
            bundle.putString("collection", gson.toJson(venueSlideShow.get(0)));
            fragment.setArguments(bundle);
            FrameLayout view = new FrameLayout(this);
            view.setId(R.id.venuesSlideShow);
            addFragment(R.id.venuesSlideShow, fragment);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            handlePadding(view);
            contentContainer.addView(view, params);
        }

        if (collectionsSlideShow.size() > 0) {
            CollectionsSlideShowFragment fragment = new CollectionsSlideShowFragment();
            Bundle bundle = new Bundle();
            bundle.putString("collections", gson.toJson(collectionsSlideShow));
            fragment.setArguments(bundle);
            FrameLayout view = new FrameLayout(this);
            addFragment(R.id.collectionsSlideshow, fragment);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            view.setId(R.id.collectionsSlideshow);
            contentContainer.addView(view, params);
        }
        if (venueVerticalNormal.size() > 0) {
            VenueVerticalFragment fragment = new VenueVerticalFragment();
            Bundle bundle = new Bundle();
            bundle.putString("collection", gson.toJson(venueVerticalNormal.get(0)));
            fragment.setArguments(bundle);
            FrameLayout view = new FrameLayout(this);
            view.setId(R.id.venuesVerticalNormal);
            addFragment(R.id.venuesVerticalNormal, fragment);
            handlePadding(view);
            contentContainer.addView(view, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        }
        if (collectionsVerticalNormal.size() > 0) {
            CollectionVerticalFragment fragment = new CollectionVerticalFragment();
            Bundle bundle = new Bundle();
            bundle.putString("collections", gson.toJson(collectionsVerticalNormal));
            fragment.setArguments(bundle);
            FrameLayout view = new FrameLayout(this);
            view.setId(R.id.collectionsVerticalNormal);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            handlePadding(view);
            handleMargins(params);
            addFragment(R.id.collectionsVerticalNormal, fragment);
            contentContainer.addView(view, params);
        }
    }

    private void handlePadding(FrameLayout view) {
        if (isFirstGroupAdded) {
            view.setPadding(0, (int) UiUtils.dp2px(20), 0, 0);
        } else {
            isFirstGroupAdded = true;
        }
    }

    private void handleMargins(FrameLayout.LayoutParams view) {
        if (isFirstGroupAdded) {
            view.setMargins(0, (int) UiUtils.dp2px(15), 0, 0);
        } else {
            isFirstGroupAdded = true;
        }
    }

    private void handleCollection(List<VenueCollection> collections) {
        venueSlideShow = new ArrayList<>();
        venueHeroBig = new ArrayList<>();
        venueHeroNormal = new ArrayList<>();
        venueVerticalNormal = new ArrayList<>();
        collectionsSlideShow = new ArrayList<>();
        collectionsVerticalNormal = new ArrayList<>();
        collectionsHero = new ArrayList<>();

        for (VenueCollection collection : collections) {
            switch (collection.type) {
                case 1:
                    collectionsSlideShow.add(collection);
                    break;
                case 2:
                    collectionsVerticalNormal.add(collection);
                    break;
                case 3:
                    collectionsHero.add(collection);
                    break;
                case 4:
                    venueSlideShow.add(collection);
                    break;
                case 5:
                    venueHeroBig.add(collection);
                    break;
                case 6:
                    venueHeroNormal.add(collection);
                    break;
                /*case 10:
                    venueHorizontalThumbnail.addAll(collection.venues);
                    break;*/
                /*case 11:
                    VenueVerticalThumbnail.addAll(collection.venues);
                    break;*/
                case 7:
                    venueVerticalNormal.add(collection);
                    break;
               /* case 13:
                    venueVerticalGridView.addAll(collection.venues);
                    break;*/
               /* case 14:
                    venueHorizontalNormal.addAll(collection.venues);
                    break;*/
            }
        }
    }

    private static final String TAG = "MainActivity";

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

    private void showMainProgressBar(boolean show) {
        findViewById(R.id.progressbar).setVisibility(show ? View.VISIBLE : View.GONE);
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