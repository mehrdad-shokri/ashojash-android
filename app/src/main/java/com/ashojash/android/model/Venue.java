package com.ashojash.android.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Venue {
  public String name;
  public String slug;
  public float score;
  public int cost;
  public String phone;
  @SerializedName("mainPhoto")
  public SimplePhoto photo;
  public Location location;
  public List<Menu> menus;
  public List<Review> reviews;
  public List<Photo> photos;
  public int reviewsCount;
  public int menusCount;
  public int photosCount;
  public double distance;
}
