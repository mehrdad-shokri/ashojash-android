package com.ashojash.android.orm;

import com.activeandroid.Model;
import com.activeandroid.annotation.Table;

@Deprecated
@Table(name = "reviews", id = "id")
public class ReviewObject extends Model {

    public String comment;
    public int score;
    public int cost;
}
