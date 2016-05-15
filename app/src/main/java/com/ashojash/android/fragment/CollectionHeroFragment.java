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
import com.ashojash.android.activity.CollectionActivity;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.VenueCollection;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

public class CollectionHeroFragment extends Fragment {

    private ImageView imgCollection;
    private TextView txtCollectionName;
    private ViewGroup rootView;
    private Gson gson;


    public CollectionHeroFragment() {
        gson = new Gson();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_collection_hero, container, false);
    }

    private VenueCollection collection;

    @Override
    public void onResume() {
        super.onResume();
        collection = gson.fromJson(getArguments().getString("collection"), VenueCollection.class);
        if (collection == null) {
//            getActivity().getFragmentManager().popBackStack();
        }
        setupViews();
    }

    private void setupViews() {
        View view = getView();
        rootView = (ViewGroup) view.findViewById(R.id.rootView);
        imgCollection = (ImageView) view.findViewById(R.id.imgCollection);
        txtCollectionName = (TextView) view.findViewById(R.id.txtCollectionName);
        if (collection.shouldShowContent) {
            imgCollection.setColorFilter(Color.rgb(190, 190, 190), android.graphics.PorterDuff.Mode.MULTIPLY);
            txtCollectionName.setText(collection.name);
        }
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AppController.currentActivity, CollectionActivity.class);
                intent.putExtra("slug", collection.slug);
                intent.putExtra("collection", gson.toJson(collection));
                startActivity(intent);
            }
        });
        IconicsDrawable errorIcon = new IconicsDrawable(AppController.context).icon(GoogleMaterial.Icon.gmd_error).sizeDp(12).color(AppController.context.getResources().getColor(R.color.text_primary));
        Glide.with(AppController.context).load(collection.photo.url).centerCrop().placeholder(R.drawable.city_list_loader).error(errorIcon).diskCacheStrategy(DiskCacheStrategy.RESULT).into(imgCollection);
    }
}
