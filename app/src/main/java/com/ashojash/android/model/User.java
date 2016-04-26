package com.ashojash.android.model;

import android.support.annotation.Nullable;

public class User {
    public String name;
    public String username;
    public String email;
    public String bio;
    public SimplePhoto photo;
    public Date createdAt;
    public Token token;


    @Nullable
    public GoogleOAuth googleOAuth;



    public class GoogleOAuth {
        public boolean isNewUser;
    }
}
