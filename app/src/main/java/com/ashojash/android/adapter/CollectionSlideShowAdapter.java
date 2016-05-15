package com.ashojash.android.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
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
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.List;

public class CollectionSlideShowAdapter extends PagerAdapter {
    private List<VenueCollection> collectionList;

    public CollectionSlideShowAdapter(List<VenueCollection> collectionList) {
        this.collectionList = collectionList;
    }

    @Override
    public int getCount() {
        return collectionList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = AppController.layoutInflater.inflate(R.layout.card_collection_slideshow, null);
        final VenueCollection collection = collectionList.get(position);
        ImageView imageView = (ImageView) view.findViewById(R.id.imgCollection);
        TextView txtCollectionName = (TextView) view.findViewById(R.id.txtCollectionName);
        TextView txtCollectionDescription = (TextView) view.findViewById(R.id.txtCollectionDescription);
        if (collection.shouldShowContent) {
            txtCollectionName.setText(collection.name);
            txtCollectionDescription.setText(collection.description);
            imageView.setColorFilter(Color.rgb(190, 190, 190), android.graphics.PorterDuff.Mode.MULTIPLY);
        }
        CardView rootView = (CardView) view.findViewById(R.id.rootView);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AppController.currentActivity, CollectionActivity.class);
                intent.putExtra("slug", collection.slug);
                intent.putExtra("collection", AppController.gson.toJson(collection));
                AppController.currentActivity.startActivity(intent);
            }
        });
        Glide.with(AppController.context).load(collection.photo.url).centerCrop().placeholder(R.drawable.city_list_loader).error(new IconicsDrawable(AppController.context).icon(GoogleMaterial.Icon.gmd_error).sizeDp(12).color(AppController.context.getResources().getColor(R.color.text_primary))).diskCacheStrategy(DiskCacheStrategy.RESULT).into(imageView);
        container.addView(view);
        return view;
    }
}
