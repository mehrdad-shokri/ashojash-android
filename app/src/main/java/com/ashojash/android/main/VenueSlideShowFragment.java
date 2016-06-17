package com.ashojash.android.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ashojash.android.R;
import com.ashojash.android.adapter.VenueSlideShowAdapter;
import com.ashojash.android.model.VenueCollection;
import com.google.gson.Gson;
import me.relex.circleindicator.CircleIndicator;

public class VenueSlideShowFragment extends Fragment {

    private VenueCollection collection;
    private ViewPager viewPager;
    private VenueSlideShowAdapter slideShowAdapter;
    private CircleIndicator indicator;

    public VenueSlideShowFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_venue_slideshow, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewPager = (ViewPager) getView().findViewById(R.id.venueViewPager);
        indicator = (CircleIndicator) getView().findViewById(R.id.indicator);
        Bundle bundle = getArguments();
        Gson gson = new Gson();
        collection = gson.fromJson(bundle.getString("collection"), VenueCollection.class);
        slideShowAdapter = new VenueSlideShowAdapter(collection.venues);
        viewPager.setAdapter(slideShowAdapter);
        indicator.setViewPager(viewPager);
    }
}
