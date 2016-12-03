package com.ashojash.android.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ashojash.android.R;
import com.ashojash.android.adapter.NearbyStreetAdapter;
import com.ashojash.android.adapter.OnCardClickListener;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.Street;
import java.util.List;

public class StreetSuggestionFragment extends Fragment {

  private RecyclerView recyclerView;
  private NearbyStreetAdapter adapter;
  private OnCardClickListener onCardClickListener;

  public void setOnCardClickListener(OnCardClickListener onCardClickListener) {
    this.onCardClickListener = onCardClickListener;
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
    recyclerView.setNestedScrollingEnabled(false);
    ViewGroup viewGroup = (ViewGroup) getView().findViewById(R.id.nearMeLayout);
    viewGroup.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (onCardClickListener != null) {
          Street street = new Street();
          street.name = getResources().getString(R.string.near_me);
          onCardClickListener.onClick(street);
        }
      }
    });
  }

  public void setStreets(List<Street> streets) {
    adapter = new NearbyStreetAdapter(streets);
    Log.d("Ashojash", "setStreets: " + (onCardClickListener == null));
    adapter.setOnItemSelectionListener(onCardClickListener);
    recyclerView.setAdapter(adapter);
  }
}