package com.ashojash.android.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.ashojash.android.R;
import com.ashojash.android.activity.VenueActivity;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.Venue;
import com.ashojash.android.model.VenueCollection;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

public class VenueHeroNormalFragment extends Fragment {

    private ImageView imgVenue;
    private TextView txtVenueName;
    private ViewGroup rootView;
    private Gson gson;


    public VenueHeroNormalFragment() {
        gson = new Gson();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_venue_hero_normal, container, false);
    }

    private VenueCollection collection;
    private Venue venue;

    @Override
    public void onResume() {
        super.onResume();
        collection = gson.fromJson(getArguments().getString("collection"), VenueCollection.class);
        venue = collection.venues.get(0);
        setupViews();

//        txtVenueDescription.setText();        set from description

    }

    private void setupViews() {
        View view = getView();
        txtVenueName = (TextView) view.findViewById(R.id.txtVenueName);
        imgVenue = (ImageView) view.findViewById(R.id.imgVenue);
        imgVenue.setColorFilter(Color.rgb(130, 130, 130), android.graphics.PorterDuff.Mode.MULTIPLY);
        rootView = (ViewGroup) view.findViewById(R.id.rootView);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AppController.currentActivity, VenueActivity.class);
                intent.putExtra("slug", venue.slug);
                intent.putExtra("venue", gson.toJson(venue));
                startActivity(intent);
            }
        });
        txtVenueName.setText(venue.name);
        IconicsDrawable errorIcon = new IconicsDrawable(AppController.context).icon(GoogleMaterial.Icon.gmd_error).sizeDp(12).color(AppController.context.getResources().getColor(R.color.text_primary));
        Glide.with(AppController.context).load(venue.photo.url).centerCrop().placeholder(R.drawable.city_list_loader).error(errorIcon).diskCacheStrategy(DiskCacheStrategy.RESULT).into(imgVenue);
    }

}
