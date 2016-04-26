package com.ashojash.android.orm;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "users", id = "id")
public class UserOrm extends Model {

    @Column(name = "username")
    public String username;

    @Column(name = "name")
    public String name;

    @Column(name = "email")
    public String email;

    @Column(name = "image_url")
    public String imageUrl;
}
