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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ashojash.android.R;
import com.ashojash.android.activity.MainActivity;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.utils.AuthUtils;
import com.ashojash.android.webserver.WebServer;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import org.json.JSONException;
import org.json.JSONObject;


public class GoogleOauthFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "IdTokenActivity";
    private static final int RC_GET_TOKEN = 9002;
    public static final String GET_ACCOUNTS_PERMISSION = Manifest.permission.GET_ACCOUNTS;
    private GoogleApiClient mGoogleApiClient;
    private SignInButton signInButton;
    private ProgressDialog progressDialog;
    private ViewGroup rootLayout;
    private GoogleSignInOptions gso;
    public static final int GET_ACCOUNT_PERMISSION_REQUEST_CODE = 79;


    public GoogleOauthFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String callingActivity = getArguments().getString("calling_activity");
        Log.d(TAG, "onCreateView: google " + callingActivity);
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
                Log.d(TAG, "checkForPermission: should show rationale: " + ActivityCompat.shouldShowRequestPermissionRationale(currentActivity, Manifest.permission.READ_CONTACTS));
                showRationale();
            } else {
                showGoogleOauth();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult");
        for (int i = 0, len = permissions.length; i < len; i++) {
            String permission = permissions[i];
            Log.d(TAG, "onRequestPermissionsResult");
            if (permission.equals(GET_ACCOUNTS_PERMISSION) && requestCode == GET_ACCOUNT_PERMISSION_REQUEST_CODE) {
                Log.d(TAG, "onRequestPermissionsResult: here");
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    boolean showRationale = shouldShowRequestPermissionRationale(permission);
                    Log.d(TAG, "onRequestPermissionsResult: " + showRationale);
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
    }


    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GET_TOKEN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.d(TAG, "onActivityResult: success" + result.isSuccess());
            Log.d(TAG, "onActivityResult: " + result.getStatus().getStatusCode());
            Log.d(TAG, "onActivityResult: " + result.getStatus().getStatusMessage());
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                Log.d(TAG, "onActivityResult: success" + acct.getServerAuthCode());
                String authCode = acct.getServerAuthCode();
                uiShowRegisteringUserProgressDialog();
                sendGoogleAuthCode(authCode);
            } else {
                uiShowErrorConnectingToGoogleSnackbar();
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        AppController.getInstance().cancelPendingRequests("REGISTER_USER_GOOGLE_OAUTH");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
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


    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Log.d(TAG, "signOut:onResult:" + status);
                    }
                });
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Log.d(TAG, "revokeAccess:onResult:" + status);
                    }
                });
    }

    private void sendGoogleAuthCode(String authCode) {

        Log.d(TAG, "sendGoogleAuthCode: AuthCode: " + authCode);
        JsonObjectRequest request = WebServer.postGoogleAuthCode(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                user , token , is_new_user
                try {
                    boolean isNewUser = AuthUtils.GoogleLogIn(response);
                    if (isNewUser) uiShowNewUserRegisteredDialog();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                } catch (JSONException | NullPointerException e) {
                    showRetrievingErrorSnackbar();
                } finally {
                    uiDismissProgressDialog();
                }
                Log.d(TAG, "onResponse:  onSuccess");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                uiShowErrorConnectingToServerErrorSnackbar();
                uiDismissProgressDialog();
            }
        }, authCode);

        AppController.getInstance().addToRequestQueue(request, "REGISTER_USER_GOOGLE_OAUTH");
    }

    private void uiDismissProgressDialog() {
        if (progressDialog != null) progressDialog.dismiss();
    }

    private void uiShowRegisteringUserProgressDialog() {
        progressDialog = ProgressDialog.show(getActivity(), null, getResources().getString(R.string.logging_in), true, false);
    }


    private void uiShowNewUserRegisteredDialog() {
        final AlertDialog.Builder dialogBuilder =
                new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        dialogBuilder.setView(R.layout.dialog_google_register_finished);
        final AlertDialog registerCompleteDialog = dialogBuilder.show();
        registerCompleteDialog.findViewById(R.id.btnVerifyEmail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerCompleteDialog.dismiss();
            }
        });
    }


    private void showRetrievingErrorSnackbar() {
        Snackbar snackbar = Snackbar.make(rootLayout, R.string.error_retrieving_data, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void uiShowErrorConnectingToServerErrorSnackbar() {
        Snackbar snackbar = Snackbar
                .make(rootLayout, R.string.error_connecting_to_server, Snackbar.LENGTH_LONG);
        snackbar.show();

    }


    private void uiShowErrorConnectingToGoogleSnackbar() {
        Snackbar snackbar = Snackbar
                .make(rootLayout, R.string.error_connecting_to_google, Snackbar.LENGTH_LONG);
        snackbar.show();


    }
}
