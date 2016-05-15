package com.ashojash.android.activity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.ActionMenuView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.HapticFeedbackConstants;
import android.view.MenuItem;
import android.widget.Toast;
import com.ashojash.android.R;
import com.ashojash.android.fragment.SearchFragment;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.webserver.VenueApi;
import com.mypopsy.drawable.SearchArrowDrawable;
import com.mypopsy.widget.FloatingSearchView;
import com.mypopsy.widget.internal.ViewUtils;

/*
* Checked for bus and json
* */
public class SearchActivity extends BaseActivity implements
        ActionMenuView.OnMenuItemClickListener {
    public FloatingSearchView mSearchView;
    private static final String VENUE_SEARCH_REQUEST_TAG = "VENUE_SEARCH";
    private SearchFragment searchFragment;
//    private static final String citySlug = AppController.defaultPref.getString("current_city_slug", null);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setupSearch();
        Bundle bundle = getIntent().getExtras();
        searchFragment = new SearchFragment();
        searchFragment.setArguments(bundle);
        addFragment(R.id.fragmentSearchContainer, searchFragment);
    }

    private void setupSearch() {
        mSearchView = (FloatingSearchView) findViewById(R.id.search);
        mSearchView.setOnMenuItemClickListener(this);
        mSearchView.setText(null);
        showSearchProgress(false);
        showClearButton(false);
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
        mSearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {
                int queryLength = query.length();
                showClearButton(queryLength > 0 && mSearchView.isActivated());
            }

            @Override
            public void afterTextChanged(Editable s) {

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
                    Toast.makeText(SearchActivity.this, R.string.search_enter_min_three_char_to_start_search, Toast.LENGTH_SHORT).show();
                    return;
                }
                mSearchView.setActivated(false);
                showSearchProgress(true);
                searchFragment.search(query.toString());
            }
        });
    }


    private void cancelSearch() {
        VenueApi.cancelSearch();
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

    private String TAG = AppController.TAG;

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
