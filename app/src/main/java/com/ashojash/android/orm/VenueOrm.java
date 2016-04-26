package com.ashojash.android.orm;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "venues", id = "id")
public class VenueOrm extends Model {

    @Column(name = "slug")
    public String slug;

    @Column(name = "name")
    public String name;

    @Column(name = "cost")
    public int cost;

    @Column(name = "image_url")
    public String imageUrl;

    @Column(name = "phone")
    public String phone;

    @Column(name = "score")
    public float score;

    @Column(name = "address")
    public String address;

    @Column(name = "lat")
    public double lat;

    @Column(name = "lng")
    public double lng;

    public VenueOrm() {
        super();
    }

}
