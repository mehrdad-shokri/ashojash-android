package com.ashojash.android.struct;
@Deprecated
public class StructUser {
    private String email;
    private String name;
    private String username;
    private String userImageUrl;

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }
}
