package com.ashojash.android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ashojash.android.R;

import java.util.List;

public class RegisterFragment extends Fragment {

    private boolean isFirstInstantiation = true;

    private GoogleOauthFragment googleOauthFragment;
    private EmailRegisterFragment emailRegisterFragment;

    public RegisterFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        if (isFirstInstantiation) {
            Bundle bundle = new Bundle();
            bundle.putString("calling_activity", getArguments().getString("calling_activity"));
            emailRegisterFragment = new EmailRegisterFragment();
            googleOauthFragment = new GoogleOauthFragment();
            googleOauthFragment.setArguments(getArguments());
            emailRegisterFragment.setArguments(getArguments());
            fragmentTransaction.add(R.id.emailRegisterFramelayout, emailRegisterFragment);
            fragmentTransaction.add(R.id.googleOauthFrameLayout, googleOauthFragment);
            fragmentTransaction.commit();
            isFirstInstantiation = false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        List<Fragment> fragments = getChildFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

}
