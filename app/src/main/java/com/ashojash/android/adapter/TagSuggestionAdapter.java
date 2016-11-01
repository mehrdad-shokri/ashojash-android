package com.ashojash.android.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ashojash.android.R;
import com.ashojash.android.model.Tag;
import com.ashojash.android.util.UiUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import java.util.List;

import static com.ashojash.android.helper.AppController.context;

public class TagSuggestionAdapter extends RecyclerView.Adapter<TagSuggestionAdapter.ViewHolder> {

  List<Tag> tagList;

  public TagSuggestionAdapter(List<Tag> tagList) {
    this.tagList = tagList;
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.card_tag_suggestion, parent, false);
    TagSuggestionAdapter.ViewHolder viewHolder = new TagSuggestionAdapter.ViewHolder(v);
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(final TagSuggestionAdapter.ViewHolder holder, int position) {
    final Tag tag = tagList.get(position);
    holder.txtTagName.setText(tag.name);
    if (tag.photo != null) {
      Glide.with(context)
          .load(UiUtil.setUrlWidth(tag.photo.url, 60))
          .centerCrop()
          .diskCacheStrategy(DiskCacheStrategy.RESULT)
          .into(holder.imgTagPhoto);
    } else {
      holder.imgTagPhoto.setVisibility(View.GONE);
      RelativeLayout.LayoutParams params =
          (RelativeLayout.LayoutParams) holder.txtTagName.getLayoutParams();
      params.addRule(RelativeLayout.CENTER_IN_PARENT);
      holder.txtTagName.setLayoutParams(params);
    }
  }

  class ViewHolder extends RecyclerView.ViewHolder {
    public TextView txtTagName;
    public ImageView imgTagPhoto;

    public ViewHolder(View itemView) {
      super(itemView);
      txtTagName = (TextView) itemView.findViewById(R.id.txtTagName);
      imgTagPhoto = (ImageView) itemView.findViewById(R.id.imgTagPhoto);
    }
  }

  @Override public int getItemCount() {
    return tagList.size();
  }
}

