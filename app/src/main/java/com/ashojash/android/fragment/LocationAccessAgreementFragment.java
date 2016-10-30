package com.ashojash.android.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.ashojash.android.R;
import com.ashojash.android.activity.SplashActivity;
import com.ashojash.android.ui.PlayGifView;

public class LocationAccessAgreementFragment extends Fragment {

  private Button button;

  public LocationAccessAgreementFragment() {
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_location_information, container, false);
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    button = (Button) getView().findViewById(R.id.button);
    button.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Intent i = new Intent(getContext(), SplashActivity.class);
        getActivity().startActivity(i);
      }
    });
  }

  @Override public void onResume() {
    super.onResume();
    final PlayGifView pGif = (PlayGifView) getView().findViewById(R.id.imageView);
    pGif.setBackgroundResource(R.drawable.telescope_img);
    AsyncTask.execute(new Runnable() {
      @Override public void run() {
        pGif.setImageResource(R.drawable.telescope_gif, getView());
      }
    });
  }
}