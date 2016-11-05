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
import com.ashojash.android.adapter.TagSearchSuggestionAdapter;
import com.ashojash.android.adapter.VenueSearchSuggestionAdapter;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.Tag;
import com.ashojash.android.model.Venue;
import com.ashojash.android.model.VenueTagCombined;
import java.util.List;

public class VenueTagFragment extends Fragment {

  private RecyclerView tagRecyclerView;
  private RecyclerView venueRecyclerView;

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_venue_tag_combined, container, false);
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setupViews();
  }

  public void setVenueTags(VenueTagCombined venueTagCombined) {
    List<Venue> venueList = venueTagCombined.venues;
    List<Tag> tagList = venueTagCombined.tags;
    TagSearchSuggestionAdapter tagSuggestionAdapter = new TagSearchSuggestionAdapter(tagList);
    tagRecyclerView.setAdapter(tagSuggestionAdapter);
    VenueSearchSuggestionAdapter venueSearchSuggestionAdapter = new VenueSearchSuggestionAdapter(venueList);
    venueRecyclerView.setAdapter(venueSearchSuggestionAdapter);
  }

  private void setupViews() {
    RecyclerView.LayoutManager tagLayoutManager = new LinearLayoutManager(AppController.context);
    tagRecyclerView = (RecyclerView) getView().findViewById(R.id.tagSuggestionRecyclerView);
    tagRecyclerView.setLayoutManager(tagLayoutManager);
    tagRecyclerView.setHasFixedSize(true);
    tagRecyclerView.setNestedScrollingEnabled(false);
    venueRecyclerView = (RecyclerView) getView().findViewById(R.id.venueSuggestionRecyclerView);
    RecyclerView.LayoutManager venueLayoutManager = new LinearLayoutManager(AppController.context);
    venueRecyclerView.setLayoutManager(venueLayoutManager);
    venueRecyclerView.setHasFixedSize(true);
    venueRecyclerView.setNestedScrollingEnabled(false);
  }
}
