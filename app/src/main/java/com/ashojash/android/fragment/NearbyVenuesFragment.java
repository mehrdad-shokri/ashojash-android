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
import com.ashojash.android.adapter.NearbyVenuesAdapter;
import com.ashojash.android.adapter.OnCardClickListener;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.Venue;
import com.wang.avi.AVLoadingIndicatorView;
import java.util.List;

public class NearbyVenuesFragment extends Fragment {
  private RecyclerView recyclerView;
  private List<Venue> venues;
  private NearbyVenuesAdapter adapter;
  private OnCardClickListener onCardClickListener;

  public NearbyVenuesFragment() {
  }

  public void setOnCardClickListener(OnCardClickListener onCardClickListener) {
    this.onCardClickListener = onCardClickListener;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_nearby_venues, container, false);
    return rootView;
  }

  @Override public void onResume() {
    super.onResume();
    setupViews();
  }

  private void setupViews() {
    recyclerView = (RecyclerView) getView().findViewById(R.id.nearbyVenuesRecyclerView);
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AppController.context);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setHasFixedSize(true);
    recyclerView.setNestedScrollingEnabled(false);
  }

  public void setVenues(List<Venue> venues) {
    this.venues = venues;
    adapter = new NearbyVenuesAdapter(this.venues);
    adapter.setonCardClickListener(onCardClickListener);
    recyclerView.setAdapter(adapter);
    ((AVLoadingIndicatorView) getView().findViewById(R.id.progressbar)).smoothToHide();
    recyclerView.setVisibility(View.VISIBLE);
  }
}
