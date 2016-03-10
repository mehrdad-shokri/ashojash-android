package com.ashojash.android.db;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.ashojash.android.model.Venue;
import com.ashojash.android.struct.StructVenue;

import java.util.List;

public class VenueDb {

    public static void createOrUpdate(List<StructVenue> venues) {
        try {
            ActiveAndroid.beginTransaction();

            for (StructVenue structVenue : venues) {
                createOrUpdate(structVenue);
            }
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    public static void createOrUpdate(StructVenue structVenue) {
        Venue venue = new Select()
                .from(Venue.class)
                .where("slug=?", structVenue.getSlug())
                .executeSingle();
        if (venue == null) {
            venue = new Venue();
            venue.slug = structVenue.getSlug();
        }
        venue.address = structVenue.getAddress();
        venue.cost = structVenue.getCost();
        venue.imageUrl = structVenue.getImageUrl();
        venue.instagram = structVenue.getInstagram();
        venue.url = structVenue.getUrl();
        venue.lat = structVenue.getLat();
        venue.lng = structVenue.getLng();
        venue.mobile = structVenue.getMobile();
        venue.phone = structVenue.getPhone();
        venue.name = structVenue.getName();
        venue.score = structVenue.getScore();
        venue.save();
    }
}
