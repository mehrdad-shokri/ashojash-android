package com.ashojash.android.task;

import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.NotificationCompat;
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
    private final int venuePhotoNotificationId = 1001;
    private NotificationManager notificationManager = (NotificationManager) AppController.context.getSystemService(Context.NOTIFICATION_SERVICE);
    private NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(AppController.context);
    private int filesCount;

    @Override
    protected Void doInBackground(VenueAddPhotoRequest... venueAddPhotoRequests) {
        try {
            VenueAddPhotoRequest request = venueAddPhotoRequests[0];
            ArrayList<String> checkedItems = request.checkedItems;
            String venueSlug = request.venueSlug;
            File[] files = getFilesFromPath(checkedItems);
            filesCount = files.length;

            for (int i = 0; i < files.length; i++) {
                WebServer.UploadVenuePhoto(venueSlug, files[i], new AsyncHttpResponseHandler() {


                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                });
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            return null;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        notificationBuilder.setContentTitle(AppController.context.getResources().getString(R.string.photo_upload))
                .setSmallIcon(R.drawable.ic_stat_ashojash_logo_final)// change icon to notification icon
                .setAutoCancel(false)
                .setOngoing(true)
                .setContentText(AppController.context.getResources().getString(R.string.uploading_photos));
        notificationBuilder.setProgress(100, 0, true);
        notificationManager.notify(venuePhotoNotificationId, notificationBuilder.build());
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        notificationBuilder.setProgress(0, 0, true);
        notificationManager.notify(venuePhotoNotificationId, notificationBuilder.build());
        /*NotificationCompat.Builder builder = new NotificationCompat.Builder(AppController.context);*/
        notificationBuilder.setContentTitle(AppController.context.getResources().getString(R.string.finished))
                .setSmallIcon(R.drawable.ic_stat_ashojash_logo_final)// change icon to notification icon
                .setOngoing(false)
                .setProgress(0,0,false)
                .setSmallIcon(R.drawable.ic_stat_ashojash_logo_final)// change icon to notification icon
                .setContentText(AppController.context.getResources().getString(R.string.uploading_photos_finished));
        if (filesCount == 1)
            notificationBuilder.setContentText(AppController.context.getResources().getString(R.string.uploading_photo_finished));
        notificationManager.notify(venuePhotoNotificationId, notificationBuilder.build());
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
