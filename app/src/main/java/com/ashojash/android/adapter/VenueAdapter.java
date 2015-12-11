package com.ashojash.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.ashojash.android.R;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.struct.StructVenue;
import com.ashojash.android.ui.VenueUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.List;

public class VenueAdapter extends RecyclerView.Adapter<VenueAdapter.ViewHolder> {


    private Context context;
    List<StructVenue> structVenueList;
    private Intent intent;

    public VenueAdapter(List<StructVenue> structVenueList, Context context, Intent intent) {
        super();
        //Getting all the cities
        this.structVenueList = structVenueList;
        this.context = context;
        this.intent = intent;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_venue, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final StructVenue structVenue = structVenueList.get(position);
        IconicsDrawable errorIcon = new IconicsDrawable(AppController.context).icon(GoogleMaterial.Icon.gmd_error).sizeDp(12).color(AppController.context.getResources().getColor(R.color.text_primary));
        Glide.with(context).load(structVenue.getImageUrl()).centerCrop().placeholder(R.drawable.city_list_loader).error(errorIcon).diskCacheStrategy(DiskCacheStrategy.RESULT).into(holder.imgVenue);
        holder.txtVenueName.setText(structVenue.getName());
        setVenueScoreBackgroundDrawable(holder, structVenue);
        holder.txtVenueScore.setText(VenueUtils.getVenueScoreText(structVenue.getScore()));
        holder.txtVenueCost.setText(Html.fromHtml(VenueUtils.getCostSign(structVenue.getCost())));
        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("slug", structVenue.getSlug());
                AppController.currentActivity.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return structVenueList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgVenue;
        public TextView txtVenueName;
        public TextView txtVenueCost;
        public TextView txtVenueScore;
        public ViewGroup rootLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            imgVenue = (ImageView) itemView.findViewById(R.id.imgVenueImg);
            txtVenueName = (TextView) itemView.findViewById(R.id.txtVenueNameCardVenue);
            txtVenueCost = (TextView) itemView.findViewById(R.id.txtVenueCostCardVenue);
            txtVenueScore = (TextView) itemView.findViewById(R.id.txtVenueScoreCardVenue);
            rootLayout = (ViewGroup) itemView.findViewById(R.id.cardVenue);
        }
    }

    private void setVenueScoreBackgroundDrawable(ViewHolder holder, StructVenue structVenue) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            holder.txtVenueScore.setBackground(AppController.context.getResources().getDrawable(VenueUtils.getVenueScoreDrawableId(structVenue.getScore())));
        } else {
            holder.txtVenueScore.setBackgroundDrawable(AppController.context.getResources().getDrawable(VenueUtils.getVenueScoreDrawableId(structVenue.getScore())));
        }
    }
}
