package com.ashojash.android.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.ashojash.android.R;
import com.ashojash.android.activity.VenueActivity;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.Venue;
import com.ashojash.android.ui.VenueUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.List;

public class CollectionVenuesAdapter extends RecyclerView.Adapter<CollectionVenuesAdapter.ViewHolder> {
    private List<Venue> venueList;
    private Gson gson = new Gson();

    public CollectionVenuesAdapter(List<Venue> venueList) {
        this.venueList = venueList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_collection_venue, parent, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Venue venue = venueList.get(position);
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AppController.currentActivity, VenueActivity.class);
                intent.putExtra("slug", venue.slug);
                intent.putExtra("venue", gson.toJson(venue));
                AppController.currentActivity.startActivity(intent);
            }
        });
        Glide.with(AppController.context).load(venue.photo.url).centerCrop().placeholder(R.drawable.city_list_loader).error(new IconicsDrawable(AppController.context).icon(GoogleMaterial.Icon.gmd_error).sizeDp(12).color(AppController.context.getResources().getColor(R.color.text_primary))).diskCacheStrategy(DiskCacheStrategy.RESULT).into(holder.imageView);
        holder.txtVenueName.setText(venue.name);
        updateVenueScoreBackgroundDrawable(holder.txtVenueScore, venue.score);
        holder.txtVenueName.setText(venue.name);
        holder.txtVenueScore.setText(String.valueOf(venue.score));
        holder.txtVenueAddress.setText(venue.location.address);
    }

    private void updateVenueScoreBackgroundDrawable(TextView textView, float score) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            textView.setBackground(AppController.context.getResources().getDrawable(VenueUtils.getVenueScoreDrawableId(score)));
        } else {
            textView.setBackgroundDrawable(AppController.context.getResources().getDrawable(VenueUtils.getVenueScoreDrawableId(score)));
        }
    }

    @Override
    public int getItemCount() {
        return venueList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView txtVenueName;
        public TextView txtVenueAddress;
        public TextView txtVenueScore;
        public CardView root;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imgVenue);
            imageView.setColorFilter(Color.rgb(230, 230, 230), PorterDuff.Mode.MULTIPLY);
            txtVenueName = (TextView) itemView.findViewById(R.id.txtVenueName);
            txtVenueAddress = (TextView) itemView.findViewById(R.id.txtVenueAddress);
            txtVenueScore = (TextView) itemView.findViewById(R.id.txtVenueScore);
            root = (CardView) itemView.findViewById(R.id.rootView);
        }
    }
}
