package com.ashojash.android.model;

import java.util.List;

public class VenueTagCombined {
  public List<Venue> venueList;
  public List<Tag> tagList;

  public VenueTagCombined(List<Venue> venueList, List<Tag> tagList) {
    this.venueList = venueList;
    this.tagList = tagList;
  }
}
