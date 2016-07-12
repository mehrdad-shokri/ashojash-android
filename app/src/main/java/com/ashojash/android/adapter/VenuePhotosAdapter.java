package com.ashojash.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.ashojash.android.R;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.Photo;
import com.ashojash.android.util.UiUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class VenuePhotosAdapter extends RecyclerView.Adapter<VenuePhotosAdapter.ViewHolder> {
    private static final Context CONTEXT = AppController.context;
    List<Photo> photoList;
    private OnCardClickListener onCardClickListener;

    public void setOnItemClickListener(OnCardClickListener onCardClickListener) {
        this.onCardClickListener = onCardClickListener;
    }

    public VenuePhotosAdapter(List<Photo> photoList) {
        super();
        this.photoList = photoList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_photo_basic, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    String TAG = AppController.TAG;

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Photo photo = photoList.get(position);
        Glide.with(CONTEXT).load(UiUtil.setUrlWidth(photo.url, 240)).centerCrop().diskCacheStrategy(DiskCacheStrategy.RESULT).into(holder.imgVenuePhoto);
        if (onCardClickListener != null)
            holder.imgVenuePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onCardClickListener.onClick(photo);
                }
            });
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgVenuePhoto;

        public ViewHolder(View itemView) {
            super(itemView);
            imgVenuePhoto = (ImageView) itemView.findViewById(R.id.imgVenuePhotoVenuePhotoCard);
//            imgVenuePhoto.setLayoutParams(new RelativeLayout.LayoutParams(AppController.widthPx / 2, AppController.widthPx / 2));
            /*ViewGroup viewGroup = (ViewGroup) itemView.findViewById(R.id.imgVenuePhotoContainerVenuePhotoCard);
            ViewGroup.LayoutParams layoutParams = viewGroup.getLayoutParams();
            layoutParams.width = AppController.widthPx / getItemCount();
            viewGroup.setLayoutParams(layoutParams);*/
        }
    }
}
