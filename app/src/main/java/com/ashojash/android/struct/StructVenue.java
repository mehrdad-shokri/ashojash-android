package com.ashojash.android.struct;

public class StructVenue {
    private String name;
    private String slug;
    private float score;
    private int cost;
    private String instagram;
    private String url;
    private String phone;
    private String mobile;
    private String address;
    private double lat;
    private double lng;
    private String imageUrl;

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMobile() {

        return mobile;
    }

    public String getPhone() {
        return phone;
    }

    public String getInstagram() {
        return instagram;
    }


    public String getAddress() {
        return address;
    }

    public double getLng() {
        return lng;
    }

    public double getLat() {
        return lat;
    }

    public String getSlug() {
        return slug;
    }

    public float getScore() {
        return score;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }


    public void setScore(float score) {
        this.score = score;
    }


    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
