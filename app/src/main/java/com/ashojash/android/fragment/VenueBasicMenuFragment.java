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
import android.widget.*;
import com.ashojash.android.R;
import com.ashojash.android.activity.VenueMenusActivity;
import com.ashojash.android.adapter.VenueBasicMenuAdapter;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.struct.StructMenu;
import com.ashojash.android.ui.UiUtils;
import com.ashojash.android.webserver.JsonParser;
import org.json.JSONArray;
import org.json.JSONException;

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
//        do stuff
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
        intent.putExtra("slug", slug);
        startActivity(intent);
    }

    public void onDataReceived(JSONArray response, int menusCount) {
        try {
            if (response == null)
                throw new JSONException("json parsing exception");
            List<StructMenu> menuList = JsonParser.parseVenueMenus(response);
            if (menusCount == 0)
                throw new IllegalArgumentException("no menus added yet :(");

            btnSeeAllMenus.setText(UiUtils.toPersianNumber(getString(R.string.venue_see_all_menus).replace("{{venueMenusCount}}", String.valueOf(menusCount))));
            String TAG = AppController.TAG;
            Log.d(TAG, "onDataReceived: height " + recyclerView.getLayoutParams().height);
            Intent intent = new Intent(getActivity(), VenueMenusActivity.class);
            RecyclerView.Adapter adapter = new VenueBasicMenuAdapter(menuList, AppController.context, intent);
            recyclerView.setAdapter(adapter);
            rootLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) UiUtils.convertDpToPixel(menuList.size() * 65 + 90)));
            btnSeeAllMenus.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            hideErrorViews();
        } catch (JSONException e) {
            showErrorViews();
            txtError.setText(R.string.error_retrieving_data);

        } catch (IllegalArgumentException e) {
            txtError.setText(R.string.no_menu_item_yet);
            showErrorViews();
        } finally {
            progressbar.setVisibility(View.GONE);
        }
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
