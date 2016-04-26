package com.ashojash.android.db;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.ashojash.android.model.Venue;
import com.ashojash.android.orm.VenueOrm;

import java.util.List;

public class VenueDb {
public static VenueOrm findBySlugOrFail(String slug)
{
    return new Select().from(VenueOrm.class).where("slug =?", slug).executeSingle();
}
    public static void createOrUpdate(List<Venue> venues) {
        try {
            ActiveAndroid.beginTransaction();

            for (Venue Venue : venues) {
                createOrUpdate(Venue);
            }
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    public static void createOrUpdate(Venue Venue) {
        VenueOrm venueOrm = new Select()
                .from(VenueOrm.class)
                .where("slug=?", Venue.slug)
                .executeSingle();
        if (venueOrm == null) {
            venueOrm = new VenueOrm();
            venueOrm.slug = Venue.slug;
        }
        venueOrm.address = Venue.location.address;
        venueOrm.cost = Venue.cost;
        venueOrm.imageUrl = Venue.photo.url;
        venueOrm.lat = Venue.location.lat;
        venueOrm.lng = Venue.location.lng;
        venueOrm.name = Venue.name;
        venueOrm.score = Venue.score;
        venueOrm.save();
    }
}
