package com.ashojash.android.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "venues", id = "id")
public class Venue extends Model {

    @Column(name = "slug")
    public String slug;

    @Column(name = "name")
    public String name;

    @Column(name = "cost")
    public int cost;

    @Column(name = "score")
    public float score;

    @Column(name = "address")
    public String address;


    @Column(name = "image_url")
    public String imageUrl;

    @Column(name = "lat")
    public double lat;

    @Column(name = "lng")
    public double lng;

    @Column(name = "instagram")
    public String instagram;

    @Column(name = "url")
    public String url;

    @Column(name = "phone")
    public String phone;

    @Column(name = "mobile")
    public String mobile;

    public Venue() {
        super();
    }

}
