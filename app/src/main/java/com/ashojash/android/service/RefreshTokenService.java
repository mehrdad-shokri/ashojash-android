package com.ashojash.android.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.utils.AuthUtils;
import com.ashojash.android.webserver.WebServer;
import org.json.JSONException;
import org.json.JSONObject;

public class RefreshTokenService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String token = AppController.obsPref.getString("token", null);
        if (token != null) {
            JsonObjectRequest request = WebServer.refreshToken(new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        AuthUtils.updateTokenPayload(response.getJSONObject("data").getJSONObject("token_payload"));
                    } catch (JSONException e) {
                        AuthUtils.logoutUser();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse response = error.networkResponse;
                    if (response != null) {
                        if (response.statusCode == 400 || response.statusCode == 401) {
                            AuthUtils.logoutUser();
                        }
                    } else {
                        if (!AuthUtils.isUserLoggedIn())
                            AuthUtils.logoutUser();
                    }
                }
            });
            AppController.getInstance().addToRequestQueue(request, "REFRESH_TOKEN");
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
