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
import com.ashojash.android.event.VenueApiEvents;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.Venue;
import com.ashojash.android.util.BusProvider;
import com.wang.avi.AVLoadingIndicatorView;
import java.util.List;
import org.greenrobot.eventbus.Subscribe;

public class NearbyVenuesFragment extends Fragment {
  private RecyclerView recyclerView;
  private List<Venue> venues;
  private NearbyVenuesAdapter adapter;

  public NearbyVenuesFragment() {
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

  @Override public void onStart() {
    super.onStart();
    BusProvider.getInstance().register(this);
  }

  @Override public void onStop() {
    super.onStop();
    BusProvider.getInstance().unregister(this);
  }

  @Subscribe public void onEvent(VenueApiEvents.OnNearbyVenuesResult e) {
    this.venues = e.venueList;
    adapter = new NearbyVenuesAdapter(this.venues);
    recyclerView.setAdapter(adapter);
    ((AVLoadingIndicatorView) getView().findViewById(R.id.progressbar)).smoothToHide();
    recyclerView.setVisibility(View.VISIBLE);
  }
}
