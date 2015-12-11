package com.ashojash.android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ashojash.android.R;

import java.util.List;


public class LoginFragment extends Fragment {

    private boolean isFirstInstantiation = true;

    private EmailLoginFragment signInFragment;
    private GoogleOauthFragment googleOauthFragment;

    public LoginFragment() {
        // Required empty public constructor
        setRetainInstance(true);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        if (isFirstInstantiation) {
            signInFragment = new EmailLoginFragment();
            googleOauthFragment = new GoogleOauthFragment();
            Bundle bundle = new Bundle();
            bundle.putString("calling_activity", getArguments().getString("calling_activity"));
            signInFragment.setArguments(getArguments());
            googleOauthFragment.setArguments(getArguments());
            fragmentTransaction.add(R.id.googleOauthFrameLayout, googleOauthFragment);
            fragmentTransaction.add(R.id.emailLoginFramelayout, signInFragment);
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
