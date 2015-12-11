package com.ashojash.android.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Table;

@Deprecated
@Table(name = "reviews", id = "id")
public class Review extends Model {

    public String comment;
    public int score;
    public int cost;
}
