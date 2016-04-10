package com.ashojash.android.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.ashojash.android.R;
import com.ashojash.android.adapter.VenuePhotoUploadAdapter;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.Venue;
import com.ashojash.android.task.UploadVenuePhotosTask;
import com.ashojash.android.utils.ContentResolverUtil;
import com.ashojash.android.webserver.WebServer;
import com.loopj.android.http.AsyncHttpResponseHandler;
import cz.msebera.android.httpclient.Header;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class VenueUploadPhotoDialogFragment extends DialogFragment {

    private PackageManager packageManager;
    static final int REQUEST_IMAGE_CAPTURE = 8088;
    static final int REQUEST_IMAGE_GALLERY = 8089;
    private ArrayList<String> files;
    private VenuePhotoUploadAdapter adapter;
    @NonNull
    public Venue venue;
    private Button btnAddVenuePhotoCamera;
    private AlertDialog dialog;
    private Button btnAddVenuePhotoGallery;

    public static VenueUploadPhotoDialogFragment newInstance() {
        Bundle args = new Bundle();
        VenueUploadPhotoDialogFragment fragment = new VenueUploadPhotoDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        packageManager = activity.getPackageManager();
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder dialogBuilder =
                new AlertDialog.Builder(AppController.currentActivity, R.style.AppCompatAlertDialogStyle);
        dialogBuilder.setView(R.layout.dialog_add_photo);
        dialog = dialogBuilder.create();
        dialog.show();
        if (files == null)
            files = new ArrayList<>();
        if (adapter == null) {
            adapter = new VenuePhotoUploadAdapter(files, AppController.context);
        }
        setupViews(dialog);
        return dialog;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    private Button btnUploadVenuePhotos;
    private RecyclerView venuePhotosRecyclerView;

    private void setupViews(AlertDialog dialog) {
        TextView txtVenueTitle = (TextView) dialog.findViewById(R.id.txtAddPhotoTitle);
        txtVenueTitle.setText(getString(R.string.add_venue_photo_title).replace("{{venueName}}", venue.name));
        btnUploadVenuePhotos = (Button) dialog.findViewById(R.id.btnUploadVenuePhotos);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        venuePhotosRecyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerViewPhotosList);
        venuePhotosRecyclerView.setLayoutManager(layoutManager);
        venuePhotosRecyclerView.setAdapter(adapter);
        btnAddVenuePhotoCamera = (Button) dialog.findViewById(R.id.btnAddVenuePhotoCamera);
        btnAddVenuePhotoGallery = (Button) dialog.findViewById(R.id.btnAddVenuePhotoGallery);
        btnAddVenuePhotoCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                packageManager = getActivity().getPackageManager();
                if (!(packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA) || packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT))) {
                    Snackbar.make(getActivity().findViewById(R.id.venueActivityRootLayout), R.string.camera_not_supported, Snackbar.LENGTH_LONG).show();
                    return;
                }
                dispatchTakePictureIntent();
            }
//            }
        });
        btnAddVenuePhotoGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  Intent intent = new Intent();
                intent.setType("image*//*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1);*/
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_GALLERY);
            }
        });
        btnUploadVenuePhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ArrayList<String> checkedItems = adapter.getCheckedFilesPath();
                if (checkedItems.size() != 0) {
                    UploadVenuePhotosTask task = new UploadVenuePhotosTask();
                    task.execute(new UploadVenuePhotosTask.VenueAddPhotoRequest(venue.slug, checkedItems));
                    dismiss();
                } else {
                    Snackbar.make(getActivity().findViewById(R.id.venueActivityRootLayout), R.string.select_some_files_to_upload, Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void galleryAddPic(String path) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
//        files.add(f);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    /* private void setPic() {

         // Get the dimensions of the View
         int targetW = mImageView.getWidth();
         int targetH = mImageView.getHeight();
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
     }*/
    private String TAG = "VENUEUPLOADPHOTODIALOGFRAGMENT";

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            handleBigCameraPhoto();
        } else if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            String path = ContentResolverUtil.getPath(AppController.context, uri);
            addToAdapter(path);
        }
    }


    /*public String getRealPathFromURI( Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = AppController.context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
*/

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param Context The context.
     * @param Uri     The Uri to query.
     * @author paulburke
     */


    private void handleBigCameraPhoto() {

        if (mCurrentPhotoPath != null) {
//            setPic();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    galleryAddPic(mCurrentPhotoPath);
                }
            });
            thread.run();
            Thread thread2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    addToAdapter(mCurrentPhotoPath);
                }
            });
            thread2.run();
            mCurrentPhotoPath = null;
        }
    }

    private void addToAdapter(String path) {
        if (files.contains(path)) {
            Snackbar.make(getActivity().findViewById(R.id.venueActivityRootLayout), R.string.photo_added_already, Snackbar.LENGTH_LONG).show();
            return;
        }
        files.add(path);
        btnUploadVenuePhotos.setVisibility(View.VISIBLE);
        venuePhotosRecyclerView.setVisibility(View.VISIBLE);
        adapter.notifyItemInserted(files.size() - 1);
    }
}
