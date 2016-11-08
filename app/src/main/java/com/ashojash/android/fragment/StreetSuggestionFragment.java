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
import com.ashojash.android.adapter.NearbyStreetAdapter;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.Street;
import java.util.List;

public class StreetSuggestionFragment extends Fragment {

  private RecyclerView recyclerView;
  private NearbyStreetAdapter adapter;
  private List<Street> nearbyStreets;
  private ChangesListener changesListener;
  private List<Street> streets;

  public interface ChangesListener {

    void onSearchActionRequested(String term);
  }

  public void setOnChangesListener(ChangesListener listener) {
    this.changesListener = listener;
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_nearby_street, container, false);
    return rootView;
  }

  @Override public void onResume() {
    super.onResume();
    recyclerView = (RecyclerView) getView().findViewById(R.id.streetSuggestRecyclerView);
    recyclerView.setLayoutManager(
        new LinearLayoutManager(AppController.context, LinearLayoutManager.VERTICAL, false));
    ViewGroup viewGroup = (ViewGroup) getView().findViewById(R.id.nearMeLayout);

    viewGroup.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (changesListener != null) {
          changesListener.onSearchActionRequested(getResources().getString(R.string.near_me));
        }
      }
    });
  }

  public void setStreets(List<Street> streets) {
    this.streets = streets;
    adapter = new NearbyStreetAdapter(streets);
    recyclerView.setAdapter(adapter);
  }
}
