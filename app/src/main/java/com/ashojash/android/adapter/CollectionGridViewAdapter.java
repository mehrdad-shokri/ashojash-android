package com.ashojash.android.adapter;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.ashojash.android.R;
import com.ashojash.android.activity.CollectionActivity;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.VenueCollection;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.List;

public class CollectionGridViewAdapter extends RecyclerView.Adapter<CollectionGridViewAdapter.ViewHolder> {
    private List<VenueCollection> collectionList;
    private Gson gson = new Gson();

    public CollectionGridViewAdapter(List<VenueCollection> collectionList) {
        this.collectionList = collectionList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_collection, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
//        final Venue venue = collectionList.getBottombar(position);
        final VenueCollection collection = collectionList.get(position);
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AppController.currentActivity, CollectionActivity.class);
                intent.putExtra("slug", collection.slug);
//                intent.putExtra("collection", gson.toJson(venue));
            }
        });
        Glide.with(AppController.context).load(collection.photo.url).centerCrop().placeholder(R.drawable.city_list_loader).error(new IconicsDrawable(AppController.context).icon(GoogleMaterial.Icon.gmd_error).sizeDp(12).color(AppController.context.getResources().getColor(R.color.text_primary))).diskCacheStrategy(DiskCacheStrategy.RESULT).into(holder.imageView);
    }

    @Override
    public int  getItemCount() {
        return collectionList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public CardView root;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imgCollection);
            root = (CardView) itemView.findViewById(R.id.rootView);
        }
    }
}
