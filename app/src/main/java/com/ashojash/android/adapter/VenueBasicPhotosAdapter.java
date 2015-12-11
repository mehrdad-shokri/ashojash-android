package com.ashojash.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.ashojash.android.R;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.struct.StructPhoto;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class VenueBasicPhotosAdapter extends RecyclerView.Adapter<VenueBasicPhotosAdapter.ViewHolder> {
    private Context context;
    List<StructPhoto> structPhotoList;
    private Intent intent;
    String TAG = AppController.TAG;

    public VenueBasicPhotosAdapter(List<StructPhoto> picsList, Context context, Intent intent) {
        super();
        this.structPhotoList = picsList;
        this.context = context;
        this.intent = intent;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_photo_basic, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final StructPhoto structPhoto = structPhotoList.get(position);
        Glide.with(context).load(structPhoto.getPhotoUrl()).centerCrop().diskCacheStrategy(DiskCacheStrategy.RESULT).into(holder.imgVenuePhoto);
    }

    @Override
    public int getItemCount() {
        return structPhotoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgVenuePhoto;

        public ViewHolder(View itemView) {
            super(itemView);
            imgVenuePhoto = (ImageView) itemView.findViewById(R.id.imgVenuePhotoVenuePhotoCard);
            ViewGroup viewGroup = (ViewGroup) itemView.findViewById(R.id.imgVenuePhotoContainerVenuePhotoCard);
            ViewGroup.LayoutParams layoutParams = viewGroup.getLayoutParams();
            layoutParams.width = AppController.widthPx / getItemCount();
            viewGroup.setLayoutParams(layoutParams);
        }
    }
}
