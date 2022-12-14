package com.ashojash.android.util;

import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.Token;
import com.ashojash.android.model.User;
import org.json.JSONException;

public final class AuthUtil {


    public static boolean isUserLoggedIn() {
        long currentTime = System.currentTimeMillis() / 1000L;
        long exp = AppController.obsPref.getLong("exp", 0);
        if (exp > currentTime) return true;
        return false;
    }

    public static User getAuthUser() {
        if (!isUserLoggedIn()) return null;
        User user = new User();
        user.username = AppController.defaultPref.getString("username", "");
        user.name = AppController.defaultPref.getString("name", "");
        user.email = AppController.defaultPref.getString("email", "");
        return user;
    }


    public static void EmailLogin(User user) {
        AppController.editor.putString("username", user.username);
        AppController.editor.putString("email", user.email);
        AppController.editor.putString("name", user.name);
        AppController.editor.commit();
        updateTokenPayload(user.token);
    }


    public static boolean GoogleLogIn(User user) throws JSONException {
        boolean isNewUser = user.googleOAuth.isNewUser;
        AppController.editor.putString("username", user.username);
        AppController.editor.putString("email", user.email);
        AppController.editor.putString("name", user.name);
        AppController.editor.commit();
        updateTokenPayload(user.token);
        return isNewUser;
    }


    public static void updateTokenPayload(Token token) {
        AppController.obsEditor.putString("token", token.token);
        if (token.ttlRefresh != 0) {
            AppController.obsEditor.putLong("ttlRefresh", token.ttlRefresh);
        }
        AppController.obsEditor.putLong("ttl", token.ttl);
        AppController.obsEditor.putLong("exp", token.exp);
        AppController.obsEditor.commit();
    }

    public static void logoutUser() {
        AppController.obsEditor.remove("token");
        AppController.obsEditor.remove("ttl_refresh");
        AppController.obsEditor.remove("ttl");
        AppController.obsEditor.remove("exp");
        AppController.obsEditor.commit();
    }

    public static String getToken() {
        if (!isUserLoggedIn()) return null;
        return AppController.obsPref.getString("token", null);
    }
}
