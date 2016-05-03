package com.ashojash.android.orm;

import com.activeandroid.query.Select;
import com.ashojash.android.event.VenueApiEvents;
import com.ashojash.android.model.Venue;
import com.ashojash.android.utils.BusProvider;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class OrmListener {
    public OrmListener() {
        BusProvider.getInstance().register(this);
    }


    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onVenueIndexReady(VenueApiEvents.OnVenueIndexResultsReady event) {
        createOrUpdate(event.venue);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onNearbyVenues(VenueApiEvents.OnNearbyVenuesResult event) {
        createOrUpdate(event.venueList);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onSelectedVenues(VenueApiEvents.OnSelectedVenuesResult event) {
        createOrUpdate(event.venueList);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onSearchResult(VenueApiEvents.OnSearchResultsReady event) {
        createOrUpdate(event.venuePaginated.data);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onTopVenues(VenueApiEvents.OnTopVenuesResult event) {
        createOrUpdate(event.venueList);
    }

    public static void createOrUpdate(List<Venue> venues) {
        try {
//            ActiveAndroid.beginTransaction();
            for (Venue Venue : venues) {
                createOrUpdate(Venue);
            }
        } finally {
//            ActiveAndroid.endTransaction();
        }
    }

    public static void createOrUpdate(Venue venue) {
        VenueOrm venueOrm = new Select()
                .from(VenueOrm.class)
                .where("slug = ?", venue.slug)
                .executeSingle();
        if (venueOrm == null) {
            venueOrm = new VenueOrm();
            venueOrm.slug = venue.slug;
        }
        try {
            venueOrm.address = venue.location.address;
            venueOrm.cost = venue.cost;
            venueOrm.imageUrl = venue.photo.url;
            venueOrm.lat = venue.location.lat;
            venueOrm.lng = venue.location.lng;
            venueOrm.name = venue.name;
            venueOrm.score = venue.score;
            venueOrm.phone = venue.phone;
            venueOrm.save();
        } catch (NullPointerException e) {
        } catch (Exception e) {
        }
    }
}
