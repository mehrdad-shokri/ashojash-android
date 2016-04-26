package com.ashojash.android.adapter;

import android.content.Context;
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
import com.ashojash.android.R;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.Review;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.util.List;

public class VenueReviewsAdapter extends RecyclerView.Adapter<VenueReviewsAdapter.ViewHolder> {
    private static final Context CONTEXT = AppController.context;
    List<Review> reviewList;
    private OnCardClickListener onCardClickListener;

    public void setOnItemClickListener(OnCardClickListener onCardClickListener) {
        this.onCardClickListener = onCardClickListener;
    }

    public VenueReviewsAdapter(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

    String TAG = AppController.TAG;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_review_basic, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: created");
        final Review review = reviewList.get(position);
        Glide.with(CONTEXT).load(review.user.photo.url).asBitmap().centerCrop().diskCacheStrategy(DiskCacheStrategy.RESULT).into(new BitmapImageViewTarget(holder.userImage) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(CONTEXT.getResources(), resource);
                circularBitmapDrawable.setCornerRadius(3);
                holder.userImage.setImageDrawable(circularBitmapDrawable);
            }
        });

        holder.txtComment.setText(review.comment);
        if (onCardClickListener != null)
            holder.rootLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    onCardClickListener.onClick(review);
                }
            });
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: count: " + reviewList.size());
        return reviewList.size();
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
