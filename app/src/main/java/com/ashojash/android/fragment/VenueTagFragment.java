package com.ashojash.android.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ashojash.android.R;
import com.ashojash.android.event.SearchApiEvents;
import com.ashojash.android.model.Tag;
import com.ashojash.android.model.Venue;
import com.ashojash.android.model.VenueTagCombined;
import com.ashojash.android.util.BusProvider;
import java.util.List;
import org.greenrobot.eventbus.Subscribe;

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

  @Override public void onStart() {
    super.onStart();
    BusProvider.getInstance().register(this);
  }

  @Override public void onStop() {
    super.onStop();
    BusProvider.getInstance().unregister(this);
  }

  @Subscribe public void onEvent(SearchApiEvents.OnSuggestResultsReady e) {
    VenueTagCombined venueTagCombined = e.venueTagCombined;
    venueList = venueTagCombined.venueList;
    tagList = venueTagCombined.tagList;
  }

  private void setupViews() {
    tagRecyclerView = (RecyclerView) getView().findViewWithTag(R.id.tagSuggestion);
    venueRecyclerView = (RecyclerView) getView().findViewWithTag(R.id.venueSuggestion);
  }
}
