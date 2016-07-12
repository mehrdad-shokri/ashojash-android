package com.ashojash.android.fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.ashojash.android.R;
import com.ashojash.android.event.PermissionEvents;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.ui.AshojashSnackbar;
import com.ashojash.android.ui.PlayGifView;
import com.ashojash.android.util.BusProvider;
import com.ashojash.android.util.PermissionUtil;
import permissions.dispatcher.*;

@RuntimePermissions
public class LocationPermissionNotAvailableFragment extends Fragment {

    private Button button;

    public LocationPermissionNotAvailableFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_location_permission_not_available, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        button = (Button) getView().findViewById(R.id.button);
        final LocationPermissionNotAvailableFragment instance = this;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLocationPermission();
                LocationPermissionNotAvailableFragmentPermissionsDispatcher.checkLocationPermissionWithCheck(instance);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (PermissionUtil.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION))
            BusProvider.getInstance().post(new PermissionEvents.OnPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION));
        final PlayGifView pGif = (PlayGifView) getView().findViewById(R.id.imageView);
        pGif.setBackgroundResource(R.drawable.telescope_img);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                pGif.setImageResource(R.drawable.telescope_gif, getView());
            }
        });
    }

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void checkLocationPermission() {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LocationPermissionNotAvailableFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
        if (PermissionUtil.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION))
            BusProvider.getInstance().post(new PermissionEvents.OnPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION));
    }

    @OnShowRationale(Manifest.permission.ACCESS_FINE_LOCATION)
    void showRationaleForLocation(final PermissionRequest request) {
        new AlertDialog.Builder(getActivity())
                .setMessage(R.string.permission_location_rationale)
                .setPositiveButton(R.string.button_allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton(R.string.button_deny, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .show();
    }

    @OnPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION)
    void showDeniedForPhoneCall() {
        new AshojashSnackbar.AshojashSnackbarBuilder(getView().findViewById(R.id.rootView)).message(R.string.no_permission_granted).duration(Snackbar.LENGTH_SHORT).build().show();
    }

    @OnNeverAskAgain(Manifest.permission.ACCESS_FINE_LOCATION)
    void showNeverAskForPhoneCall() {
        final Snackbar snackbar = new AshojashSnackbar.AshojashSnackbarBuilder(getView().findViewById(R.id.rootView))
                .duration(Snackbar.LENGTH_INDEFINITE)
                .message(R.string.permission_location_settings)
                .build();
        snackbar.setAction(R.string.action_settings, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent i = new Intent();
                i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                i.addCategory(Intent.CATEGORY_DEFAULT);
                i.setData(Uri.fromParts("package", AppController.context.getPackageName(), null));
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                getActivity().startActivity(i);
            }
        });
        snackbar.show();
    }
}
