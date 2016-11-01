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

public class NearbyVenuesAdapter extends RecyclerView.Adapter<NearbyVenuesAdapter.ViewHolder> {

  List<Venue> venueList;

  public NearbyVenuesAdapter(List<Venue> venueList) {
    this.venueList = venueList;
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.card_venue_nearby, parent, false);
    NearbyVenuesAdapter.ViewHolder viewHolder = new NearbyVenuesAdapter.ViewHolder(v);
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(final NearbyVenuesAdapter.ViewHolder holder, int position) {
    final Venue venue = venueList.get(position);
    holder.txtVenueName.setText(venue.name);
    holder.imgVenuePhoto.post(new Runnable() {
      @Override public void run() {
        int itemHeight = holder.imgVenuePhoto.getHeight();
        Glide.with(context)
            .load(UiUtil.setUrlWidth(venue.photo.url, itemHeight + 30))
            .bitmapTransform(new CropSquareTransformation(context),
                new RoundedCornersTransformation(context, 4, 0,
                    RoundedCornersTransformation.CornerType.ALL))
            .diskCacheStrategy(DiskCacheStrategy.RESULT)
            .into(holder.imgVenuePhoto);
      }
    });
  }

  class ViewHolder extends RecyclerView.ViewHolder {
    public TextView txtVenueName;
    public ImageView imgVenuePhoto;

    public ViewHolder(View itemView) {
      super(itemView);
      txtVenueName = (TextView) itemView.findViewById(R.id.txtVenueName);
      imgVenuePhoto = (ImageView) itemView.findViewById(R.id.imgVenuePhoto);
    }
  }

  @Override public int getItemCount() {
    return venueList.size();
  }
}

