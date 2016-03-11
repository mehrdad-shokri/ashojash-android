package com.ashojash.android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ashojash.android.R;
import com.ashojash.android.activity.MainActivity;
import com.ashojash.android.adapter.CityAdapter;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.struct.StructCity;
import com.ashojash.android.webserver.JsonParser;
import com.ashojash.android.webserver.WebServer;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class CityListFragment extends Fragment {
    private List<StructCity> listCityStruct;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private ProgressBar progressBar;

    public CityListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_city_list, container, false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupViews();
        getCityListJsonObject();
    }

    private void getCityListJsonObject() {
        final JsonObjectRequest jsonObjectRequest = WebServer.getCityList(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    listCityStruct = JsonParser.parseCityJsonObject(response);
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    adapter = new CityAdapter(listCityStruct, AppController.context, intent);
                    recyclerView.setAdapter(adapter);
                } catch (JSONException e) {
                } finally {
                    progressBar.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private void setupViews() {
        progressBar = (ProgressBar) getView().findViewById(R.id.prgCityList);
        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerViewCityList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(AppController.context);
        recyclerView.setLayoutManager(layoutManager);
    }
}
