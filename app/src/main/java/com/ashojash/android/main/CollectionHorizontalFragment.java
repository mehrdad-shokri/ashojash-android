package com.ashojash.android.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ashojash.android.R;
import com.ashojash.android.adapter.CollectionVerticalAdapter;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.VenueCollection;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class CollectionHorizontalFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<VenueCollection> venueCollectionList;
    private Gson gson = new Gson();
    private CollectionVerticalAdapter adapter;

    public CollectionHorizontalFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_venue_vertical, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        setupViews();
        Bundle bundle = getArguments();
        venueCollectionList = gson.fromJson(bundle.getString("collection"), new TypeToken<ArrayList<VenueCollection>>() {
        }.getType());
        adapter = new CollectionVerticalAdapter(venueCollectionList);
        recyclerView.setAdapter(adapter);
    }


    private void setupViews() {
        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AppController.context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
    }

}
