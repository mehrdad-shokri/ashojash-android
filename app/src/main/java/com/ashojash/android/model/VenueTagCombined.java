package com.ashojash.android.model;

import java.util.List;

public class VenueTagCombined {
  public List<Venue> venues;
  public List<Tag> tags;

  public VenueTagCombined(List<Venue> venueList, List<Tag> tagList) {
    this.venues = venueList;
    this.tags = tagList;
  }
}
