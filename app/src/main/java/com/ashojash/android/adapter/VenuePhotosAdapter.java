package com.ashojash.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.ashojash.android.R;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.struct.StructPhoto;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class VenuePhotosAdapter extends BaseAdapter {
    private Context context;
    List<StructPhoto> structPhotoList;
    private Intent intent;

    public VenuePhotosAdapter(List<StructPhoto> picsList, Context context, Intent intent) {
        super();
        this.structPhotoList = picsList;
        this.context = context;
        this.intent = intent;
    }

    private String TAG = AppController.TAG;

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.card_photo_basic, viewGroup, false);
        } else {
        }
        final StructPhoto structPhoto = structPhotoList.get(i);
        ImageView imgVenuePhoto = (ImageView) view.findViewById(R.id.imgVenuePhotoVenuePhotoCard);
        Log.d(TAG, "getView: " + (imgVenuePhoto == null));
        Glide.with(context).load(structPhoto.getPhotoUrl()).centerCrop().diskCacheStrategy(DiskCacheStrategy.RESULT).into(imgVenuePhoto);
        return view;
    }

    @Override
    public int getCount() {
        return structPhotoList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}
