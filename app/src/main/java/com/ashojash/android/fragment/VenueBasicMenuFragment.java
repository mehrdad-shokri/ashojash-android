package com.ashojash.android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.ashojash.android.R;
import com.ashojash.android.activity.VenueMenusActivity;
import com.ashojash.android.adapter.OnCardClickListener;
import com.ashojash.android.adapter.VenueMenusAdapter;
import com.ashojash.android.event.OnApiResponseErrorEvent;
import com.ashojash.android.event.VenueApiEvents;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.Menu;
import com.ashojash.android.model.Venue;
import com.ashojash.android.ui.UiUtils;
import com.ashojash.android.utils.BusProvider;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class VenueBasicMenuFragment extends Fragment {
    private LinearLayout errorTxtContainer;
    private ProgressBar progressbar;
    private RecyclerView recyclerView;
    private TextView btnSeeAllMenus;
    private View rootLayout;
    private String slug;

    public VenueBasicMenuFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_venue_basic_menu, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        slug = getActivity().getIntent().getStringExtra("slug");
        if (slug == null)
            getActivity().getFragmentManager().popBackStack();
        setupViews();
    }

    private TextView txtError;

    private void setupViews() {
        rootLayout = getView().findViewById(R.id.menuRootLayoutVenueMenuFragment);
        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerViewMenusVenueMenuFragment);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AppController.context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        btnSeeAllMenus = (TextView) getView().findViewById(R.id.btnSellAllVenueMenuFragment);
        btnSeeAllMenus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startVenueMenusActivity();
            }
        });
        progressbar = (ProgressBar) getView().findViewById(R.id.prgNearbyFragment);
        txtError = (TextView) getView().findViewById(R.id.txtErrorVenueBasicMenuFragment);
        errorTxtContainer = (LinearLayout) getView().findViewById(R.id.errorViewVenueMenuFragment);
    }

    private void startVenueMenusActivity() {
        Intent intent = new Intent(getActivity(), VenueMenusActivity.class);
        Log.d(TAG, "startVenueMenusActivity: "+slug);
        intent.putExtra("slug", slug);
        startActivity(intent);
    }

    private static final String TAG = "VenueBasicMenuFragment";

    @Override
    public void onStart() {
        super.onStart();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    public void onEvent(VenueApiEvents.OnVenueIndexResultsReady event) {
        Log.d(TAG, "onVenueInfoReceived: received");
        Venue venue = event.venue;
        int menusCount = venue.menusCount;
        List<Menu> menuList = venue.menus;
        progressbar.setVisibility(View.GONE);
        if (menusCount == 0) {
            txtError.setText(R.string.no_menu_item_yet);
            showErrorViews();
            btnSeeAllMenus.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            getView().findViewById(R.id.menuRootLayoutVenueMenuFragment).setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) UiUtils.convertDpToPixel(80)));
            return;
        }
        btnSeeAllMenus.setText(UiUtils.toPersianNumber(getString(R.string.venue_see_all_menus).replace("{{venueMenusCount}}", String.valueOf(menusCount))));
        VenueMenusAdapter adapter = new VenueMenusAdapter(menuList);
        adapter.setOnItemClickLister(new OnCardClickListener() {
            @Override
            public void onClick(Object model) {
                startVenueMenusActivity();
            }
        });
        recyclerView.setAdapter(adapter);
        rootLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) UiUtils.convertDpToPixel(menuList.size() * 65 + 90)));
        btnSeeAllMenus.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        hideErrorViews();
    }

    @Subscribe
    public void onError(OnApiResponseErrorEvent event) {
        showErrorViews();
        txtError.setText(R.string.error_retrieving_data);
    }

    private void showErrorViews() {
        progressbar.setVisibility(View.GONE);
        errorTxtContainer.setVisibility(View.VISIBLE);
    }

    private void hideErrorViews() {
        progressbar.setVisibility(View.GONE);
        errorTxtContainer.setVisibility(View.GONE);
    }
}
