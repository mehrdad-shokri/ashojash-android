package com.ashojash.android.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ashojash.android.R;
import com.ashojash.android.adapter.CollectionSlideShowAdapter;
import com.ashojash.android.model.VenueCollection;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import me.relex.circleindicator.CircleIndicator;

public class CollectionsSlideShowFragment extends Fragment {

  private List<VenueCollection> collectionList;
  private ViewPager viewPager;
  private CollectionSlideShowAdapter slideShowAdapter;
  private CircleIndicator indicator;

  public CollectionsSlideShowFragment() {
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_collection_slideshow, container, false);
    return rootView;
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    viewPager = (ViewPager) getView().findViewById(R.id.collectionViewPager);
    indicator = (CircleIndicator) getView().findViewById(R.id.indicator);
    Bundle bundle = getArguments();
    Gson gson = new Gson();
    collectionList =
        gson.fromJson(bundle.getString("collections"), new TypeToken<ArrayList<VenueCollection>>() {
        }.getType());
    slideShowAdapter = new CollectionSlideShowAdapter(collectionList);
    viewPager.setAdapter(slideShowAdapter);
    indicator.setViewPager(viewPager);
  }
}
