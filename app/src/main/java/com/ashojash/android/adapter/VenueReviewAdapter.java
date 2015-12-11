package com.ashojash.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;
import com.ashojash.android.R;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.struct.StructReview;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.util.List;

public class VenueReviewAdapter extends RecyclerView.Adapter<VenueReviewAdapter.ViewHolder> {
    private ImageLoader imageLoader;
    private Context context;
    List<StructReview> structReviewList;
    private Intent intent;

    public VenueReviewAdapter(List<StructReview> structReviewList, Context context, Intent intent) {
        super();
        //Getting all the cities
        this.structReviewList = structReviewList;
        this.context = context;
        this.intent = intent;
    }

    String TAG = AppController.TAG;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_review, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: created");
        final StructReview structReview = structReviewList.get(position);
        Glide.with(context).load(structReview.getUserImageUrl()).asBitmap().centerCrop().diskCacheStrategy(DiskCacheStrategy.RESULT).into(new BitmapImageViewTarget(holder.userImage) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCornerRadius(3);
                holder.userImage.setImageDrawable(circularBitmapDrawable);
            }
        });

        holder.txtComment.setText(structReview.getComment());
        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (intent != null)
                    AppController.currentActivity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: count: " + structReviewList.size());
        return structReviewList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView userImage;
        public TextView txtComment;
        public ViewGroup rootLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            userImage = (ImageView) itemView.findViewById(R.id.imgUserImgReviewBasicCard);
            txtComment = (TextView) itemView.findViewById(R.id.txtCommentReviewBasicCard);
            rootLayout = (ViewGroup) itemView.findViewById(R.id.cardBasicReview);
        }
    }
}
