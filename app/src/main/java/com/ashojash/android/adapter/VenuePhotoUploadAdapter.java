package com.ashojash.android.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import com.ashojash.android.R;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.ui.UiUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class VenuePhotoUploadAdapter extends RecyclerView.Adapter<VenuePhotoUploadAdapter.ViewHolder> {


    private Context context;
    private Map<String, Boolean> filesPathMap;
    private ArrayList<String> filesPath;

    public VenuePhotoUploadAdapter(ArrayList<String> filesPath, Context context) {
        super();
        this.filesPath = filesPath;
        initializeFilesPath(filesPath);
        Log.d(TAG, "VenuePhotoUploadAdapter: constructor" + filesPathMap.size());
        this.context = context;
    }

    private void initializeFilesPath(ArrayList<String> filesPath) {
        filesPathMap = new LinkedHashMap<>();
        for (String filePath : filesPath)
            filesPathMap.put(filePath, true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_venue_photo_upload, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final String filePath = new ArrayList<>(filesPathMap.keySet()).get(position);
        Log.d(TAG, "onBindViewHolder: current File path " + filePath);
        boolean isChecked = new ArrayList<>(filesPathMap.values()).get(position);
        Log.d(TAG, "onBindViewHolder: is Checked " + isChecked);
        holder.checkBox.setChecked(isChecked);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                setPhoto(filePath, holder.imageView);
            }
        });
        thread.run();
        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean updatedChecked = !new ArrayList<>(filesPathMap.values()).get(position);
                holder.checkBox.setChecked(updatedChecked);
                setFileChecked(filePath, updatedChecked);
            }
        });
    }

    private void setFileChecked(String filePath, boolean updatedChecked) {
        filesPathMap.put(filePath, updatedChecked);
    }

    public ArrayList<String> getCheckedFilesPath() {
        ArrayList<String> strings = new ArrayList<>();
        if (filesPathMap.containsValue(true))
            for (String entry : filesPathMap.keySet())
                if (filesPathMap.get(entry))
                    strings.add(entry);
        return strings;
    }


    String TAG = AppController.TAG;

    private void setPhoto(String mCurrentPhotoPath, ImageView mImageView) {
        int targetW = (int) UiUtils.convertDpToPixel(72);
        int targetH = (int) UiUtils.convertDpToPixel(72);
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmScaledOptions = new BitmapFactory.Options();
        bmScaledOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmScaledOptions);
        int photoW = bmScaledOptions.outWidth;
        int photoH = bmScaledOptions.outHeight;
        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        // Decode the image file into a Bitmap sized to fill the View
        bmScaledOptions.inJustDecodeBounds = false;
        bmScaledOptions.inSampleSize = scaleFactor;
        bmScaledOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmScaledOptions);
        mImageView.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        updateFilesPath(filesPath);
        Log.d(TAG, "getItemCount: " + filesPathMap.size());
        return filesPathMap.size();
    }

    private void updateFilesPath(ArrayList<String> filesPath) {
        for (String filePath : filesPath)
            if (filesPathMap.get(filePath) == null)
                filesPathMap.put(filePath, true);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public CheckBox checkBox;
        public ViewGroup rootLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imgVenueCapturedPhoto);
            rootLayout = (ViewGroup) itemView.findViewById(R.id.cardVenueCapturedPhoto);
            checkBox = (CheckBox) itemView.findViewById(R.id.chkSelected);
            checkBox.setClickable(false);
        }
    }
}
