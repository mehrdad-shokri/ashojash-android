package com.ashojash.android.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.ashojash.android.R;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.Venue;
import com.ashojash.android.ui.UiUtils;


public class VenueInfoFragment extends Fragment {
    private TextView txtVenueAddress;
    private TextView txtVenuePhone;
    private TextView txtVenuePhoneTitle;
    private Venue venue;

    public VenueInfoFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_venue_info, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String slug = getActivity().getIntent().getStringExtra("slug");
        if (slug == null)
            getActivity().getFragmentManager().popBackStack();
        venue = AppController.gson.fromJson(getActivity().getIntent().getStringExtra("venue"), Venue.class);
        setupViews();
    }

    private void setupViews() {
        txtVenueAddress = (TextView) getActivity().findViewById(R.id.txtVenueAddress);
        txtVenuePhone = (TextView) getActivity().findViewById(R.id.txtVenuePhone);
        txtVenuePhoneTitle = (TextView) getActivity().findViewById(R.id.txtVenuePhone);
        if (venue.phone.isEmpty()) {
            txtVenuePhone.setVisibility(View.GONE);
            txtVenuePhoneTitle.setVisibility(View.GONE);
        }
        txtVenuePhone.setText(UiUtils.toPersianNumber(venue.phone));
        txtVenueAddress.setText(venue.location.address);
    }
}
