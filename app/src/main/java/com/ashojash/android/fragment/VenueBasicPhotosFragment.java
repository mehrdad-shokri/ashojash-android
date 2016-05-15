package com.ashojash.android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.ashojash.android.R;
import com.ashojash.android.activity.VenuePhotosActivity;
import com.ashojash.android.adapter.OnCardClickListener;
import com.ashojash.android.adapter.VenuePhotosAdapter;
import com.ashojash.android.event.OnApiResponseErrorEvent;
import com.ashojash.android.event.VenueApiEvents;
import com.ashojash.android.model.Photo;
import com.ashojash.android.ui.UiUtils;
import com.ashojash.android.utils.BusProvider;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class VenueBasicPhotosFragment extends Fragment {
    private ViewGroup rootLayout;
    private RecyclerView recyclerView;
    private ViewGroup errorRootLayout;
    private ProgressBar progressbar;
    private TextView txtError;
    private Button btnSeeMore;
    private String slug;

    public VenueBasicPhotosFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_venue_basic_photos, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        slug = getActivity().getIntent().getStringExtra("slug");
        if (slug == null)
            getActivity().getFragmentManager().popBackStack();
        setupViews();
    }

    private void setupViews() {
        rootLayout = (ViewGroup) getView().findViewById(R.id.rootView);
        errorRootLayout = (ViewGroup) getView().findViewById(R.id.errorViewVenueBasicPicsFragment);
        txtError = (TextView) getView().findViewById(R.id.txtErrorVenueBasicPicsFragment);
        progressbar = (ProgressBar) getView().findViewById(R.id.progressbar);
        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerViewVenueBasicPicsFragment);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        btnSeeMore = (Button) getView().findViewById(R.id.btnSeeMorePicsVenueBasicsPicsFragment);
        btnSeeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startVenuePhotosActivity();
            }
        });
    }

    private void startVenuePhotosActivity() {
        Intent intent = new Intent(getActivity(), VenuePhotosActivity.class);
        intent.putExtra("slug", slug);
        intent.putExtra("venue", getActivity().getIntent().getStringExtra("venue"));
        startActivity(intent);
    }

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
    public void onEvent(OnApiResponseErrorEvent event) {
        btnSeeMore.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        getView().findViewById(R.id.rootView).setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) UiUtils.dp2px(80)));
        showErrorViews();
        txtError.setText(R.string.error_retrieving_data);
    }

    @Subscribe
    public void onEvent(VenueApiEvents.OnVenueIndexResultsReady event) {
        progressbar.setVisibility(View.GONE);

        List<Photo> photoList = event.venue.photos;
        int photosCount = photoList.size();
        if (photosCount == 0) {
            txtError.setText(R.string.no_photo_item_yet);
            btnSeeMore.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            showErrorViews();
            getView().findViewById(R.id.rootView).setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) UiUtils.dp2px(80)));
            return;
        }
//            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (AppController.widthPx / photoList.size() + UiUtils.dp2px(200)));
//            rootLayout.setLayoutParams(layoutParams);
        VenuePhotosAdapter adapter = new VenuePhotosAdapter(photoList);
        adapter.setOnItemClickListener(new OnCardClickListener() {
            @Override
            public void onClick(Object model) {
                startVenuePhotosActivity();
            }
        });
        recyclerView.setAdapter(adapter);
        btnSeeMore.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        hideErrorViews();
    }

    private void showErrorViews() {
        progressbar.setVisibility(View.GONE);
        errorRootLayout.setVisibility(View.VISIBLE);
    }

    private void hideErrorViews() {
        progressbar.setVisibility(View.GONE);
        errorRootLayout.setVisibility(View.GONE);
    }
}

