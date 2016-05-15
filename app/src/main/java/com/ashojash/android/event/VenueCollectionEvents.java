package com.ashojash.android.event;

import com.ashojash.android.model.VenueCollection;

import java.util.List;

public class VenueCollectionEvents {
    private VenueCollectionEvents() {
    }

    public static class OnVenueCollectionsResponse {
        public List<VenueCollection> venueCollections;

        public OnVenueCollectionsResponse(List<VenueCollection> venueCollections) {
            this.venueCollections = venueCollections;
        }
    }

    public static class OnVenueCollectionVenuesResponse {
        public VenueCollection collection;

        public OnVenueCollectionVenuesResponse(VenueCollection collection) {
            this.collection = collection;
        }
    }
}
