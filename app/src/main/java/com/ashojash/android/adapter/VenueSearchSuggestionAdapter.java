package com.ashojash.android.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.ashojash.android.R;
import com.ashojash.android.fragment.VenueTagFragment;
import com.ashojash.android.model.Venue;
import com.ashojash.android.util.UiUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import java.util.List;
import jp.wasabeef.glide.transformations.CropSquareTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.ashojash.android.helper.AppController.context;

public class VenueSearchSuggestionAdapter
    extends RecyclerView.Adapter<VenueSearchSuggestionAdapter.ViewHolder> {

  List<Venue> venueList;
  private VenueTagFragment.OnItemClickListener onItemClickListener;

  public VenueSearchSuggestionAdapter(List<Venue> venueList) {
    this.venueList = venueList;
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.card_venue_search_suggestion, parent, false);
    VenueSearchSuggestionAdapter.ViewHolder viewHolder =
        new VenueSearchSuggestionAdapter.ViewHolder(v);
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(final VenueSearchSuggestionAdapter.ViewHolder holder, int position) {
    final Venue venue = venueList.get(position);
    holder.txtVenueName.setText(venue.name);
    if (onItemClickListener != null) {
      holder.rootView.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          onItemClickListener.onVenueItemClickListener(venue);
        }
      });
    }
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

  public void setOnCardClickListener(VenueTagFragment.OnItemClickListener onItemClickListener) {
    this.onItemClickListener = onItemClickListener;
  }

  class ViewHolder extends RecyclerView.ViewHolder {
    public TextView txtVenueName;
    public ImageView imgVenuePhoto;
    public ViewGroup rootView;

    public ViewHolder(View itemView) {
      super(itemView);
      txtVenueName = (TextView) itemView.findViewById(R.id.txtVenueName);
      imgVenuePhoto = (ImageView) itemView.findViewById(R.id.imgVenuePhoto);
      rootView = (ViewGroup) itemView.findViewById(R.id.rootView);
    }
  }

  @Override public int getItemCount() {
    return venueList.size();
  }
}

