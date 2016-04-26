package com.ashojash.android.model;

import java.util.List;

public class ValidationError {
    public List<String> name;
    public List<String> username;
    public List<String> email;
    public List<String> password;

    public ValidationError(List<String> name, List<String> username, List<String> email, List<String> password) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
