package com.ashojash.android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import com.ashojash.android.R;
import com.ashojash.android.activity.MainActivity;
import com.ashojash.android.adapter.CityAdapter;
import com.ashojash.android.event.CityApiEvents;
import com.ashojash.android.event.OnApiResponseErrorEvent;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.City;
import com.ashojash.android.ui.AshojashSnackbar;
import com.ashojash.android.utils.BusProvider;
import com.ashojash.android.webserver.CityApi;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;


public class CityListFragment extends Fragment {
    private List<City> cityList;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private CityAdapter adapter;
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
        progressBar = (ProgressBar) getView().findViewById(R.id.prg);
        CityApi.getAllCities();
    }

    @Override
    public void onStart() {
        super.onStart();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onStop() {
        BusProvider.getInstance().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onEvent(CityApiEvents.OnAllCitiesAvailable event) {
        progressBar.setVisibility(View.GONE);
        cityList = event.cityList;
        adapter = new CityAdapter(cityList);
        adapter.setOnItemClickListener(new CityAdapter.OnItemClickListener() {
            @Override
            public void onClick(City city) {

                AppController.editor.putString("current_city_slug", city.slug);
                AppController.editor.commit();
                AppController.citySlug = city.slug;
                Intent intent = new Intent(getActivity(), MainActivity.class);
                AppController.currentActivity.startActivity(intent);
                AppController.currentActivity.finish();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Subscribe
    public void onEvent(OnApiResponseErrorEvent error) {
        progressBar.setVisibility(View.GONE);
        new AshojashSnackbar.AshojashSnackbarBuilder(getActivity()).message(R.string.error_retrieving_data).duration(Snackbar.LENGTH_INDEFINITE).build().setAction(R.string.try_again, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CityApi.getAllCities();
                progressBar.setVisibility(View.VISIBLE);
            }
        }).show();
    }

    private void setupViews() {
        progressBar = (ProgressBar) getView().findViewById(R.id.prg);
        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerViewCityList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(AppController.context);
        recyclerView.setLayoutManager(layoutManager);
    }
}
