package com.ashojash.android.event;

import com.ashojash.android.model.VenueTagCombined;

public class SearchApiEvents {
  private SearchApiEvents() {
  }

  public static class OnSuggestResultsReady {
    public VenueTagCombined venueTagCombined;

    public OnSuggestResultsReady(VenueTagCombined venueTagCombined) {
      this.venueTagCombined = venueTagCombined;
    }
  }
}
