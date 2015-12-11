package com.ashojash.android.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

@Table(name = "cities", id = "id")
public class City extends Model {

    @Column(name = "name")
    public String name;

    public List<Venue> venues() {

        return getMany(Venue.class, "StructCity");
    }
}
