package com.ashojash.android.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.ashojash.android.R;
import com.ashojash.android.model.Venue;
import com.ashojash.android.util.UiUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import java.util.List;
import jp.wasabeef.glide.transformations.CropSquareTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.ashojash.android.helper.AppController.context;

public class VenueSearchResultAdapter
    extends RecyclerView.Adapter<VenueSearchResultAdapter.ViewHolder> {

  List<Venue> venueList;
  private OnCardClickListener onCardClickListener;

  public VenueSearchResultAdapter(List<Venue> venueList) {
    this.venueList = venueList;
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.card_venue_search_result, parent, false);
    VenueSearchResultAdapter.ViewHolder viewHolder = new VenueSearchResultAdapter.ViewHolder(v);
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(final VenueSearchResultAdapter.ViewHolder holder, int position) {
    final Venue venue = venueList.get(position);
    double distance = venue.distance;
    if (onCardClickListener != null) {
      holder.rootView.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          onCardClickListener.onClick(venue);
        }
      });
    }
    holder.txtVenueName.setText(venue.name);
    holder.txtVenueDistance.setText(String.valueOf(distance));
    holder.imgVenuePhoto.post(new Runnable() {
      @Override public void run() {
        int itemHeight = holder.imgVenuePhoto.getHeight();
        Glide.with(context)
            .load(UiUtil.setUrlWidth(venue.photo.url, itemHeight))
            .bitmapTransform(new CropSquareTransformation(context),
                new RoundedCornersTransformation(context, 4, 0))
            .diskCacheStrategy(DiskCacheStrategy.RESULT)
            .into(holder.imgVenuePhoto);
      }
    });
  }

  public void setOnCardClickListener(OnCardClickListener onCardClickListener) {
    this.onCardClickListener = onCardClickListener;
  }

  class ViewHolder extends RecyclerView.ViewHolder {
    public TextView txtVenueName;
    public TextView txtVenueDistance;
    public ImageView imgVenuePhoto;
    public ViewGroup rootView;

    public ViewHolder(View itemView) {
      super(itemView);
      txtVenueName = (TextView) itemView.findViewById(R.id.txtVenueName);
      txtVenueDistance = (TextView) itemView.findViewById(R.id.txtVenueDistance);
      imgVenuePhoto = (ImageView) itemView.findViewById(R.id.imgVenuePhoto);
      rootView = (ViewGroup) itemView.findViewById(R.id.rootView);
    }
  }

  @Override public int getItemCount() {
    return venueList.size();
  }
}

