package com.ashojash.android.orm;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

@Table(name = "cities", id = "id")
public class CityObject extends Model {

    @Column(name = "name")
    public String name;

    public List<VenueOrm> venues() {

        return getMany(VenueOrm.class, "StructCity");
    }
}
