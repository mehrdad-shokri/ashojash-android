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
import com.ashojash.android.adapter.TagSuggestionAdapter;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.Tag;
import com.ashojash.android.model.Venue;
import com.ashojash.android.model.VenueTagCombined;
import com.ashojash.android.util.BusProvider;
import java.util.List;

public class VenueTagFragment extends Fragment {
  private VenueTagCombined venueTagCombined;
  private List<Venue> venueList;
  private List<Tag> tagList;

  private RecyclerView tagRecyclerView;
  private RecyclerView venueRecyclerView;

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_venue_tag_combined, container, false);
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setupViews();
  }

  @Override public void onStart() {
    super.onStart();
    BusProvider.getInstance().register(this);
  }

  @Override public void onStop() {
    super.onStop();
    BusProvider.getInstance().unregister(this);
  }

  public void setVenueTags(VenueTagCombined venueTagCombined) {
    venueList = venueTagCombined.venueList;
    tagList = venueTagCombined.tagList;
    TagSuggestionAdapter tagSuggestionAdapter = new TagSuggestionAdapter(tagList);
    tagRecyclerView.setAdapter(tagSuggestionAdapter);

    
  }

  private void setupViews() {
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AppController.context);
    tagRecyclerView = (RecyclerView) getView().findViewWithTag(R.id.tagSuggestionFramelayout);
    tagRecyclerView.setLayoutManager(layoutManager);
    tagRecyclerView.setHasFixedSize(true);
    tagRecyclerView.setNestedScrollingEnabled(false);
    venueRecyclerView = (RecyclerView) getView().findViewWithTag(R.id.venueSuggestion);
    venueRecyclerView.setLayoutManager(layoutManager);
    venueRecyclerView.setHasFixedSize(true);
    venueRecyclerView.setNestedScrollingEnabled(false);
  }
}
