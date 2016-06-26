package com.ashojash.android.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.ashojash.android.R;
import com.ashojash.android.adapter.VenueMenusAdapter;
import com.ashojash.android.event.OnApiResponseErrorEvent;
import com.ashojash.android.event.VenueApiEvents;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.Menu;
import com.ashojash.android.util.BusUtil;
import com.ashojash.android.webserver.VenueApi;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class VenueMenusFragment extends Fragment {
    private RecyclerView recyclerView;
    private String venueSlug;

    public VenueMenusFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_venue_menus, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        venueSlug = getActivity().getIntent().getStringExtra("slug");
        if (venueSlug == null)
            getActivity().getFragmentManager().popBackStack();
    }

    @Override
    public void onResume() {
        super.onResume();
        setupViews();
        VenueApi.menus(venueSlug);
    }


    @Override
    public void onStart() {
        super.onStart();
        BusUtil.getInstance().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        BusUtil.getInstance().unregister(this);
    }

    @Subscribe
    public void onEvent(VenueApiEvents.OnVenueMenusResponse event) {
        menuList = event.menuList;
        int menusCount = event.menuList.size();
        progressbar.setVisibility(View.GONE);

        adapter = new VenueMenusAdapter(menuList);
        recyclerView.setAdapter(adapter);
        if (menusCount == 0) {
            txtError.setText(R.string.no_menu_item_yet);
            showErrorViews();
        }
    }

    @Subscribe
    public void onEvent(OnApiResponseErrorEvent event) {
        showErrorViews();
        txtError.setText(R.string.error_retrieving_data);
    }



    private VenueMenusAdapter adapter;
    private List<Menu> menuList;
    private TextView txtError;

    private void setupViews() {
        recyclerView = (RecyclerView) getView().findViewById(R.id.venueMenusRecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AppController.context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        progressbar = (ProgressBar) getView().findViewById(R.id.prgNearbyFragment);
        errorTxtContainer = (LinearLayout) getView().findViewById(R.id.errorViewVenue);
        txtError = (TextView) getView().findViewById(R.id.txtErrorVenue);
    }

    private void showErrorViews() {
        progressbar.setVisibility(View.GONE);
        errorTxtContainer.setVisibility(View.VISIBLE);
    }

    private LinearLayout errorTxtContainer;
    private ProgressBar progressbar;

    private void hideErrorViews() {
        progressbar.setVisibility(View.GONE);
        errorTxtContainer.setVisibility(View.GONE);
    }
}
