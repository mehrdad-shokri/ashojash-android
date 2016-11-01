package com.ashojash.android.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ashojash.android.R;
import com.ashojash.android.adapter.TagSuggestionAdapter;
import com.ashojash.android.model.Venue;
import java.util.List;

public class NearbyVenuesFragment extends Fragment {
  private RecyclerView recyclerView;
  private List<Venue> venues;
  private TagSuggestionAdapter adapter;

  public NearbyVenuesFragment() {
  }

  public void setVenues(List<Venue> venues) {
    this.venues = venues;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_nearby_places, container, false);
    return rootView;
  }
}
