package com.ashojash.android.fragment;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ashojash.android.R;
import com.ashojash.android.adapter.HeroPagerAdapter;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.struct.StructVenue;
import com.ashojash.android.ui.Colors;
import com.ashojash.android.webserver.JsonParser;
import com.ashojash.android.webserver.WebServer;
import com.viewpagerindicator.CirclePageIndicator;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class HeroFragment extends Fragment {

    private String citySlug;
    private List<StructVenue> venueList;
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
                getTopVenues();
            }
        });
        Bundle bundle = getArguments();
        if (bundle != null) {
            citySlug = bundle.getString("current_city_slug");
        }
        getTopVenues();

//        setPending();
//        getUserLocation();
//        top locations in city


    }

    private void getTopVenues() {
        final String TAG = AppController.TAG;

        final JsonObjectRequest request = WebServer.getCityTopVenues(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    venueList = JsonParser.parseVenuesJsonObject(response);
                    heroPagerAdapter = new HeroPagerAdapter(venueList);
                    heroViewpager.setAdapter(heroPagerAdapter);
                    circlePageIndicator.setViewPager(heroViewpager);
                    progressbar.setVisibility(View.GONE);
                } catch (JSONException e) {
                    Log.d(TAG, "onResponse: fucked in json parsing");
                    txtHeroError.setText(R.string.error_retrieving_data);
                    showErrorViews();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onResponse: fucked in get data");
                Log.d(TAG, "onResponse : " + error.getMessage());
                Log.d(TAG, "onResponse : " + (heroPagerAdapter == null));
                progressbar.setVisibility(View.GONE);
                txtHeroError.setText(R.string.error_retrieving_data);
                showErrorViews();
            }
        }, citySlug);

        AppController.getInstance().addToRequestQueue(request, "HERO_FRAGMENT");
    }

    @Override
    public void onPause() {
        super.onPause();
        AppController.getInstance().cancelPendingRequests("HERO_FRAGMENT");
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
