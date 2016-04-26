package com.ashojash.android.event;

import com.ashojash.android.model.*;

import java.util.List;

public final class VenueApiEvents {
    private VenueApiEvents() {
    }


    public static class OnSearchResultsReady {
        public VenuePaginated venuePaginated;

        public OnSearchResultsReady(VenuePaginated venuePaginated) {
            this.venuePaginated = venuePaginated;
        }
    }

    public static class OnVenueIndexResultsReady {
        public Venue venue;

        public OnVenueIndexResultsReady(Venue venue) {
            this.venue = venue;
        }
    }

    public static class OnTopVenuesResult {
        public List<Venue> venueList;

        public OnTopVenuesResult(List<Venue> venueList) {
            this.venueList = venueList;
        }
    }

    public static class OnNearbyVenuesResult {
        public List<Venue> venueList;

        public OnNearbyVenuesResult(List<Venue> venueList) {

            this.venueList = venueList;
        }
    }

    public static class OnSelectedVenuesResult {
        public List<Venue> venueList;

        public OnSelectedVenuesResult(List<Venue> venueList) {
            this.venueList = venueList;
        }
    }

    public static class OnVenueMenusResponse {
        public List<Menu> menuList;

        public OnVenueMenusResponse(List<Menu> menuList) {
            this.menuList = menuList;
        }
    }

    public static class OnVenuePhotosResponse {
        public List<Photo> photoList;

        public OnVenuePhotosResponse(List<Photo> photoList) {
            this.photoList = photoList;
        }
    }

    public static class OnVenueReviewsResponse {
        public List<Review> reviewList;

        public OnVenueReviewsResponse(List<Review> reviewList) {
            this.reviewList = reviewList;
        }
    }

    public static class OnReviewAdded {
    }

    public static class OnPhotoAdded {
    }
}
