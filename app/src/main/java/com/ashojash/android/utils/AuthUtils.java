package com.ashojash.android.utils;

import android.util.Log;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.struct.StructUser;
import com.ashojash.android.webserver.JsonParser;
import org.json.JSONException;
import org.json.JSONObject;

public final class AuthUtils {


    public static boolean isUserLoggedIn() {
        long currentTime = System.currentTimeMillis() / 1000L;
        long exp = AppController.obsPref.getLong("exp", 0);
        if (exp > currentTime) return true;
        return false;
    }

    public static StructUser getAuthUser() {
        if (!isUserLoggedIn()) return null;
        StructUser user = new StructUser();
        user.setUsername(AppController.defaultPref.getString("username", ""));
        user.setName(AppController.defaultPref.getString("name", ""));
        user.setEmail(AppController.defaultPref.getString("email", ""));
        return user;
    }


    public static void EmailLogin(JSONObject response) throws JSONException {
        JSONObject data = response.getJSONObject("data");
        StructUser user = JsonParser.parseUserJsonObject(data.getJSONObject("user"));
        AppController.editor.putString("username", user.getUsername());
        AppController.editor.putString("email", user.getEmail());
        AppController.editor.putString("name", user.getName());
        AppController.editor.commit();
        updateTokenPayload(data.getJSONObject("token_payload"));
    }


    public static boolean GoogleLogIn(JSONObject response) throws JSONException {
        JSONObject data = response.getJSONObject("data");
        StructUser user = JsonParser.parseUserJsonObject(data.getJSONObject("user"));
        boolean isNewUser = data.getBoolean("is_new_user");
        AppController.editor.putString("username", user.getUsername());
        AppController.editor.putString("email", user.getEmail());
        AppController.editor.putString("name", user.getName());
        updateTokenPayload(data.getJSONObject("token_payload"));
        AppController.editor.commit();
        AppController.obsEditor.commit();
        return isNewUser;
    }

    private static String TAG = AppController.TAG;

    public static void updateTokenPayload(JSONObject tokenPayload) throws JSONException {
        AppController.obsEditor.putString("token", tokenPayload.getString("token"));
        AppController.obsEditor.putLong("ttl_refresh", tokenPayload.getLong("ttl_refresh"));
        AppController.obsEditor.putLong("ttl", tokenPayload.getLong("ttl"));
        AppController.obsEditor.putLong("exp", tokenPayload.getLong("exp"));
        AppController.obsEditor.commit();
    }

    public static void logoutUser() {
        AppController.obsEditor.remove("token");
        AppController.obsEditor.remove("ttl_refresh");
        AppController.obsEditor.remove("ttl");
        AppController.obsEditor.remove("exp");
        AppController.obsEditor.commit();
        Log.d(TAG, "logoutUser: after login token: " + (AppController.obsPref.getString("token", "") == null));
    }

    public static String getToken() {
        if (!isUserLoggedIn()) return null;
        return AppController.obsPref.getString("token", null);
    }
}
