package com.ashojash.android.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.ashojash.android.R;
import com.ashojash.android.adapter.OnCardClickListener;
import com.ashojash.android.adapter.VenuePhotosAdapter;
import com.ashojash.android.event.OnApiResponseErrorEvent;
import com.ashojash.android.event.VenueApiEvents;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.Photo;
import com.ashojash.android.utils.BusProvider;
import com.ashojash.android.webserver.VenueApi;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class VenuePhotosFragment extends Fragment {
    private RecyclerView recyclerView;
    private String venueSlug;

    public VenuePhotosFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_venue_photos, container, false);
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
        VenueApi.photos(venueSlug);
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
        showErrorViews();
        txtError.setText(R.string.error_retrieving_data);
    }


    @Subscribe
    public void onEvent(VenueApiEvents.OnVenuePhotosResponse event) {
        progressbar.setVisibility(View.GONE);

        photoList = event.photoList;
        int photosCount = photoList.size();
        if (photosCount == 0) {
            txtError.setText(R.string.no_photo_item_yet);
            showErrorViews();
        }
        adapter = new VenuePhotosAdapter(photoList);
        adapter.setOnItemClickListener(new OnCardClickListener() {
            @Override
            public void onClick(Object model) {
//                    Intent intent = new Intent(getActivity(), VenueReviewsActivity.class);
//                    show image dmadet
            }
        });
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(AppController.context, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }


    private VenuePhotosAdapter adapter;
    private List<Photo> photoList;
    private TextView txtError;

    private void setupViews() {
        recyclerView = (RecyclerView) getView().findViewById(R.id.venueMenusRecyclerView);
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AppController.context);
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
