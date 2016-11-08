package com.ashojash.android.event;

import com.ashojash.android.model.Street;
import com.ashojash.android.model.Venue;
import com.ashojash.android.model.VenueTagCombined;
import java.util.List;

public class SearchApiEvents {
  private SearchApiEvents() {
  }

  public static class OnSuggestResultsReady {
    public VenueTagCombined venueTagCombined;

    public OnSuggestResultsReady(VenueTagCombined venueTagCombined) {
      this.venueTagCombined = venueTagCombined;
    }
  }

  public static class OnStreetSuggestResultsReady {
    public List<Street> streets;

    public OnStreetSuggestResultsReady(List<Street> streets) {
      this.streets = streets;
    }
  }

  public static class OnNearbyStreetsResultsReady {
    public List<Street> streets;

    public OnNearbyStreetsResultsReady(List<Street> streets) {
      this.streets = streets;
    }
  }

  public static class onSearchResultsReady {
    public List<Venue> venues;

    public onSearchResultsReady(List<Venue> venues) {
      this.venues = venues;
    }
  }
}
