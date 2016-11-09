package com.ashojash.android.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ashojash.android.R;
import com.ashojash.android.adapter.VenueSearchResultAdapter;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.Venue;
import java.util.ArrayList;
import java.util.List;

public class SearchResultFragment extends Fragment {
  private List<Venue> venues = new ArrayList<>();
  private RecyclerView recyclerView;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_search_results, container, false);
  }

  @Override public void onResume() {
    super.onResume();
    recyclerView = (RecyclerView) getView().findViewById(R.id.venuesSearchRecyclerView);
    recyclerView.setLayoutManager(
        new LinearLayoutManager(AppController.context, LinearLayoutManager.VERTICAL, false));
  }

  public void setVenues(List<Venue> venues) {
    VenueSearchResultAdapter adapter = new VenueSearchResultAdapter(venues);
    recyclerView.setAdapter(adapter);
    recyclerView.setNestedScrollingEnabled(false);
  }
}
