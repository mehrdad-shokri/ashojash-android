package com.ashojash.android.fragment;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.ashojash.android.R;
import com.ashojash.android.adapter.HeroPagerAdapter;
import com.ashojash.android.event.OnApiRequestErrorEvent;
import com.ashojash.android.event.OnApiResponseErrorEvent;
import com.ashojash.android.event.VenueApiEvents;
import com.ashojash.android.model.Venue;
import com.ashojash.android.ui.Colors;
import com.ashojash.android.utils.BusProvider;
import com.ashojash.android.webserver.VenueApi;
import com.viewpagerindicator.CirclePageIndicator;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class HeroFragment extends Fragment {

    private String citySlug;
    private List<Venue> venueList;
    private LinearLayout retryView;
    private CirclePageIndicator circlePageIndicator;
    private ViewPager heroViewpager;
    private ProgressBar progressbar;
    private TextView txtHeroError;
    private HeroPagerAdapter heroPagerAdapter;

    public HeroFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_hero, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        progressbar = (ProgressBar) getView().findViewById(R.id.prgHeroFragment);
        heroViewpager = (ViewPager) getView().findViewById(R.id.heroViewpager);
        circlePageIndicator = (CirclePageIndicator) getView().findViewById(R.id.heroViewpagerIndicator);
        circlePageIndicator.setCentered(false);
        txtHeroError = (TextView) getView().findViewById(R.id.txtErrorHeroFragment);
        retryView = (LinearLayout) getView().findViewById(R.id.retryViewHeroFragment);
        retryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideErrorViews();
                VenueApi.topVenues(citySlug);
            }

        });
        Bundle bundle = getArguments();
        if (bundle != null) {
            citySlug = bundle.getString("current_city_slug");
        }
        VenueApi.topVenues(citySlug);
    }

    @Override
    public void onStart() {
        super.onStart();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    public void onEvent(VenueApiEvents.OnTopVenuesResult event) {
        venueList = event.venueList;
        heroPagerAdapter = new HeroPagerAdapter(venueList);
        heroViewpager.setAdapter(heroPagerAdapter);
        circlePageIndicator.setViewPager(heroViewpager);
        progressbar.setVisibility(View.GONE);
    }

    @Subscribe
    public void onEvent(OnApiResponseErrorEvent event) {
        txtHeroError.setText(R.string.error_retrieving_data);
        showErrorViews();
    }

    @Subscribe
    public void onEvent(OnApiRequestErrorEvent event) {
        progressbar.setVisibility(View.GONE);
        txtHeroError.setText(R.string.error_retrieving_data);
        showErrorViews();
    }

    private void showErrorViews() {
        progressbar.setVisibility(View.GONE);
        retryView.setVisibility(View.VISIBLE);
    }

    private void hideErrorViews() {
        retryView.setVisibility(View.GONE);
        progressbar.setVisibility(View.VISIBLE);
    }

    private void setPending() {
        progressbar.getIndeterminateDrawable().setColorFilter(Color.parseColor(Colors.PROGRESSBAR_COLOR), PorterDuff.Mode.SRC_IN);
    }
}
