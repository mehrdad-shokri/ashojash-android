package com.ashojash.android.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import com.ashojash.android.R;
import com.ashojash.android.adapter.VenueVerticalAdapter;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.VenueCollection;
import com.google.gson.Gson;

public class VenueVerticalFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView txtCollectionName;
    private View hr;
    //    private ArrayList<Venue> venues;
    private VenueCollection collection;
    private Gson gson = new Gson();
    private VenueVerticalAdapter adapter;

    public VenueVerticalFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_venue_vertical, container, false);
    }

    private static final String TAG = "VenueVerticalFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        collection = gson.fromJson(bundle.getString("collection"), VenueCollection.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        setupViews();
    }


    private void setupViews() {
        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
        txtCollectionName = (TextView) getView().findViewById(R.id.txtCollectionName);
        hr = getView().findViewById(R.id.hrTxtCollectionName);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AppController.context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        txtCollectionName.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewGroup.LayoutParams params = hr.getLayoutParams();
                params.width = (int) (.62 * txtCollectionName.getWidth());
                hr.setLayoutParams(params);
                 ViewTreeObserver obs = txtCollectionName.getViewTreeObserver();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    obs.removeOnGlobalLayoutListener(this);
                } else {
                    obs.removeGlobalOnLayoutListener(this);
                }
            }
        });
        adapter = new VenueVerticalAdapter(collection.venues);
        txtCollectionName.setText(collection.name);
        recyclerView.setAdapter(adapter);
    }
}
