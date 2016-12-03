package com.ashojash.android.fragment;

import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.ashojash.android.R;
import com.ashojash.android.util.LocationRequestUtil;
import com.google.android.gms.common.api.Status;

public class LocationServiceNotAvailableFragment extends Fragment {

  private Button button;
  public static final String STATUS_KEY = "1";
  private Status status;

  public LocationServiceNotAvailableFragment() {
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_location_service_not_available, container, false);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    status = getArguments().getParcelable(STATUS_KEY);
    button = (Button) getView().findViewById(R.id.button);
    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        try {
          status.startResolutionForResult(getActivity(), LocationRequestUtil.REQUEST_CODE);
        } catch (IntentSender.SendIntentException e) {
          e.printStackTrace();
        }
      }
    });
  }
}
