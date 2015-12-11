package com.ashojash.android.task;

import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import com.ashojash.android.R;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.webserver.WebServer;
import com.loopj.android.http.AsyncHttpResponseHandler;
import cz.msebera.android.httpclient.Header;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class UploadVenuePhotosTask extends AsyncTask<UploadVenuePhotosTask.VenueAddPhotoRequest, Void, Void> {

    String TAG = AppController.TAG;


    @Override
    protected Void doInBackground(VenueAddPhotoRequest... venueAddPhotoRequests) {
        try {
            VenueAddPhotoRequest request = venueAddPhotoRequests[0];
            ArrayList<String> checkedItems = request.checkedItems;
            String venueSlug = request.venueSlug;
            File[] files = getFilesFromPath(checkedItems);
            for (int i = 0; i < files.length; i++) {
                final int finalI = i;
                WebServer.UploadVenuePhoto(venueSlug, files[i], new AsyncHttpResponseHandler() {
                    int id = finalI;

                    @Override
                    public void onStart() {
                        super.onStart();
                        notificationBuilder.setContentTitle("My app")
                                .setSmallIcon(R.drawable.ic_action_search)
                                .setContentText("Download in progress");
                        notificationBuilder.setProgress(100, 0, false);
                        notificationManager.notify(id, notificationBuilder.build());
                    }

                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        Log.d(TAG, "onSuccess:");
                        notificationManager.cancel(id);
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        Log.d(TAG, "onFailure: " + i + " " + throwable.getMessage());
                    }

                    @Override
                    public void onProgress(long bytesWritten, long totalSize) {
                        super.onProgress(bytesWritten, totalSize);
                        Log.d(TAG, "onProgress: " + (100 * (bytesWritten / totalSize)));
                        notificationBuilder.setProgress(100, (int) (100 * (bytesWritten / totalSize)), false);
                    }
                });
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d(TAG, "file not found: ");
        } finally {
            return null;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        showNotification();
    }

    private NotificationManager notificationManager = (NotificationManager) AppController.context.getSystemService(Context.NOTIFICATION_SERVICE);
    private NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
            AppController.context);

    private void showNotification() {

        // Issues the notification

    }


    public final static class VenueAddPhotoRequest {
        String venueSlug;
        ArrayList<String> checkedItems;

        public VenueAddPhotoRequest(String venueSlug, ArrayList<String> checkedItems) {
            this.venueSlug = venueSlug;
            this.checkedItems = checkedItems;
        }
    }


    private File[] getFilesFromPath(ArrayList<String> checkedItems) {
        ArrayList<File> files = new ArrayList<>();
        for (String filePath : checkedItems)
            files.add(new File(filePath));
        return files.toArray(new File[files.size()]);
    }
}
