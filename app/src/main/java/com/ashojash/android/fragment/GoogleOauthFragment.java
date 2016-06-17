package com.ashojash.android.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import com.ashojash.android.R;
import com.ashojash.android.main.MainActivity;
import com.ashojash.android.event.OnApiRequestErrorEvent;
import com.ashojash.android.event.OnApiResponseErrorEvent;
import com.ashojash.android.event.UserApiEvents;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.User;
import com.ashojash.android.ui.AshojashSnackbar;
import com.ashojash.android.utils.AuthUtils;
import com.ashojash.android.utils.BusProvider;
import com.ashojash.android.webserver.UserApi;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import org.greenrobot.eventbus.Subscribe;


public class GoogleOauthFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_GET_TOKEN = 9002;
    public static final String GET_ACCOUNTS_PERMISSION = Manifest.permission.GET_ACCOUNTS;
    private GoogleApiClient mGoogleApiClient;
    private SignInButton signInButton;
    private ProgressDialog progressDialog;
    private ViewGroup rootLayout;
    private GoogleSignInOptions gso;
    public static final int GET_ACCOUNT_PERMISSION_REQUEST_CODE = 79;
    private static boolean hasFinished = false;
    private AshojashSnackbar.AshojashSnackbarBuilder builder;

    public GoogleOauthFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_google_register, container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        String serverClientID = getString(R.string.server_client_id);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestScopes(new Scope(Scopes.EMAIL))
                .requestScopes(new Scope(Scopes.PROFILE))
                .requestServerAuthCode(serverClientID, false)
                .requestEmail()
                .build();
        builder = new AshojashSnackbar.AshojashSnackbarBuilder(getActivity().findViewById(R.id.rootView));
    }


    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        rootLayout = (ViewGroup) getView().findViewById(R.id.fragmentGoogleRegisterRootLayout);
        showGoogleOauth();
        checkForPermission();
    }

    private void checkForPermission() {
        if (AppController.ANDROID_VERSION >= Build.VERSION_CODES.M) {
            int hasGetAccountsPermission = ContextCompat.checkSelfPermission(getActivity(), GET_ACCOUNTS_PERMISSION);
            final Activity currentActivity = getActivity();
            // Should we show an explanation?
            if (hasGetAccountsPermission != PackageManager.PERMISSION_GRANTED) {
                showRationale();
            } else {
                showGoogleOauth();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0, len = permissions.length; i < len; i++) {
            String permission = permissions[i];
            if (permission.equals(GET_ACCOUNTS_PERMISSION) && requestCode == GET_ACCOUNT_PERMISSION_REQUEST_CODE) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    boolean showRationale = shouldShowRequestPermissionRationale(permission);
                    showRationale();
                    if (!showRationale) {
                        Button btnAskForPermission = (Button) getView().findViewById(R.id.btnGetUserAccountsPermission);
                        btnAskForPermission.setVisibility(View.GONE);
                    }
                } else {
                    showGoogleOauth();
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
        BusProvider.getInstance().register(this);
    }


    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GET_TOKEN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                String authCode = acct.getServerAuthCode();
                uiShowRegisteringUserProgressDialog();
                UserApi.google(authCode);
            } else {
                uiShowErrorConnectingToGoogleSnackbar();
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        uiShowErrorConnectingToGoogleSnackbar();
    }


    private void showGoogleOauth() {
        rootLayout.removeAllViews();
        rootLayout.addView(View.inflate(getContext(), R.layout.fragment_google_register, null));
        signInButton = (SignInButton) getView().findViewById(R.id.btnRegisterGoogle);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAuthCode();
            }
        });
    }


    private void showRationale() {
        LinearLayout rootLayout = (LinearLayout) getView().findViewById(R.id.fragmentGoogleRegisterRootLayout);
        rootLayout.removeAllViews();
        rootLayout.addView(View.inflate(getContext(), R.layout.fragment_get_accounts_rational, null));
        Button btnAskForPermission = (Button) getView().findViewById(R.id.btnGetUserAccountsPermission);
        btnAskForPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{GET_ACCOUNTS_PERMISSION}, GET_ACCOUNT_PERMISSION_REQUEST_CODE);
            }
        });
    }

    private void getAuthCode() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        Fragment parentFragment = getParentFragment();
        if (parentFragment != null)
            parentFragment.startActivityForResult(signInIntent, RC_GET_TOKEN);
        else
            startActivityForResult(signInIntent, RC_GET_TOKEN);

    }

    @Subscribe
    public void onEvent(UserApiEvents.OnUserGoogleHandled event) {
        if (hasFinished) return;
        User user = event.user;
        boolean isNewUser = user.googleOAuth.isNewUser;
        AuthUtils.updateTokenPayload(user.token);
        uiDismissProgressDialog();
        if (isNewUser) uiShowNewUserRegisteredDialog();
        else {
            AppController.currentActivity.finish();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
        hasFinished = true;
    }

    @Subscribe
    public void onEvent(OnApiResponseErrorEvent event) {
        if (event.object instanceof UserApiEvents.OnUserGoogleHandled) {
            builder.message(R.string.error_retrieving_data).duration(Snackbar.LENGTH_LONG).build().show();
            uiDismissProgressDialog();
        }
    }

    @Subscribe
    public void onEvent(OnApiRequestErrorEvent event) {
        builder = new AshojashSnackbar.AshojashSnackbarBuilder(getActivity().findViewById(R.id.rootView));
        builder.message(R.string.error_connecting_to_server).duration(Snackbar.LENGTH_LONG).build().show();
        uiDismissProgressDialog();
    }

    private void uiDismissProgressDialog() {
        if (progressDialog != null) progressDialog.dismiss();
    }

    private void uiShowRegisteringUserProgressDialog() {
        progressDialog = ProgressDialog.show(getActivity(), null, getResources().getString(R.string.logging_in), true, false);
    }

    private void uiShowNewUserRegisteredDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        LayoutInflater inflater = AppController.layoutInflater;
        View dialogView = inflater.inflate(R.layout.dialog_google_register_finished, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog registerCompleteDialog = dialogBuilder.create();
        dialogView.findViewById(R.id.btnVerifyEmail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerCompleteDialog.dismiss();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                AppController.currentActivity.finish();
                getActivity().finish();
                startActivity(intent);
            }
        });
        registerCompleteDialog.show();
    }


    private void uiShowErrorConnectingToGoogleSnackbar() {
        builder.message(R.string.error_connecting_to_google).duration(Snackbar.LENGTH_LONG).build().show();
    }
}
