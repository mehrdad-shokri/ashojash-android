package com.ashojash.android.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.ashojash.android.R;
import com.ashojash.android.activity.CollectionActivity;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.VenueCollection;
import com.ashojash.android.ui.UiUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.List;

public class CollectionVerticalAdapter extends RecyclerView.Adapter<CollectionVerticalAdapter.ViewHolder> {
    private List<VenueCollection> collectionList;

    public CollectionVerticalAdapter(List<VenueCollection> collectionList) {
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
        if (collectionList.size() - 1 == position)
            holder.root.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) UiUtils.dp2px(200)));
        final VenueCollection collection = collectionList.get(position);
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AppController.currentActivity, CollectionActivity.class);
                intent.putExtra("slug", collection.slug);
                intent.putExtra("collection", AppController.gson.toJson(collection));
                AppController.currentActivity.startActivity(intent);
            }
        });
        Glide.with(AppController.context).load(collection.photo.url).centerCrop().placeholder(R.drawable.city_list_loader).error(new IconicsDrawable(AppController.context).icon(GoogleMaterial.Icon.gmd_error).sizeDp(12).color(AppController.context.getResources().getColor(R.color.text_primary))).diskCacheStrategy(DiskCacheStrategy.RESULT).into(holder.imageView);
        if (collection.shouldShowContent) {
            holder.txtCollectionName.setText(collection.name);
            holder.txtCollectionDescription.setText(collection.description);
            holder.imageView.setColorFilter(Color.rgb(190, 190, 190), android.graphics.PorterDuff.Mode.MULTIPLY);

        } else {
            holder.descriptionContainer.setVisibility(View.GONE);
            FrameLayout.LayoutParams layoutParams = new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) UiUtils.dp2px(200));
            layoutParams.setMargins(0, 0, 0, (int) UiUtils.dp2px(8));
            holder.root.setLayoutParams(layoutParams);
        }
    }

    @Override
    public int getItemCount() {
        return collectionList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView txtCollectionName;
        public TextView txtCollectionDescription;
        public ViewGroup descriptionContainer;
        public CardView root;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imgCollection);
            txtCollectionName = (TextView) itemView.findViewById(R.id.txtCollectionName);
            txtCollectionDescription = (TextView) itemView.findViewById(R.id.txtCollectionDescription);
            descriptionContainer = (ViewGroup) itemView.findViewById(R.id.linearLayout);
            root = (CardView) itemView.findViewById(R.id.rootView);
        }
    }
}
