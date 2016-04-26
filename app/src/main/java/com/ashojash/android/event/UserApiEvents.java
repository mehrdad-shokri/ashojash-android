package com.ashojash.android.event;

import com.ashojash.android.model.*;

public final class UserApiEvents {
    private UserApiEvents() {
    }


    public static class OnUserLoggedIn {
        public User user;

        public OnUserLoggedIn(User user) {
            this.user = user;
        }
    }

    public static class onUserRegistered {
        public UserRegistered userRegistered;

        public onUserRegistered(UserRegistered userRegistered) {
            this.userRegistered = userRegistered;
        }
    }

    public static class OnUserGoogleHandled {
        public User user;

        public OnUserGoogleHandled(User user) {
            this.user = user;
        }
    }


    public static class OnTokenRefreshed {
        public Token token;

        public OnTokenRefreshed(Token token) {
            this.token = token;
        }
    }
}
