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
import android.widget.ProgressBar;
import android.widget.TextView;
import com.ashojash.android.R;
import com.ashojash.android.activity.VenueMenusActivity;
import com.ashojash.android.activity.VenuePhotosActivity;
import com.ashojash.android.adapter.VenueBasicPhotosAdapter;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.struct.StructPhoto;
import com.ashojash.android.webserver.JsonParser;
import org.json.JSONArray;
import org.json.JSONException;

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
        rootLayout = (ViewGroup) getView().findViewById(R.id.rootLayoutVenueBasicPhotosFragment);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        errorRootLayout = (ViewGroup) getView().findViewById(R.id.errorViewVenueBasicPicsFragment);
        txtError = (TextView) getView().findViewById(R.id.txtErrorVenueBasicPicsFragment);
        progressbar = (ProgressBar) getView().findViewById(R.id.progressbar);
        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerViewVenueBasicPicsFragment);
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
        startActivity(intent);
    }

    public void onDataReceived(JSONArray response) {
        try {
            if (response == null)
                throw new JSONException("json parsing exception");
            List<StructPhoto> structPhotoList = JsonParser.parseVenuePhotos(response);
            if (structPhotoList.size() == 0)
                throw new IllegalArgumentException("no photos added yet :(");
//            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (AppController.widthPx / structPhotoList.size() + UiUtils.convertDpToPixel(15 + 15 + 1 + 23)));
//            rootLayout.setLayoutParams(layoutParams);
            Intent intent = new Intent(getActivity(), VenueMenusActivity.class);
            RecyclerView.Adapter adapter = new VenueBasicPhotosAdapter(structPhotoList, AppController.context, intent);
            recyclerView.setAdapter(adapter);
            btnSeeMore.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            hideErrorViews();
        } catch (JSONException e) {
            showErrorViews();
            txtError.setText(R.string.error_retrieving_data);

        } catch (IllegalArgumentException e) {
            txtError.setText(R.string.no_photo_item_yet);
            showErrorViews();
        } finally {
            progressbar.setVisibility(View.GONE);
        }
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

