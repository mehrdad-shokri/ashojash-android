package com.ashojash.android.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private GoogleApiClient mGoogleApiClient;
    private SignInButton signInButton;
    private ProgressDialog progressDialog;
    private LinearLayout linearLayout;
    private GoogleSignInOptions gso;


    public GoogleOauthFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String callingActivity=getArguments().getString("calling_activity");
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
        // [END configure_signin]

        // Build GoogleAPIClient with the Google Sign-In API and the above options.
    }


    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupViews();
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
                sendGoogleAuthCode(authCode);
            } else {
                uiShowErrorConnectingToGoogleSnackbar();
            }
            // [END get_id_token]
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


    private void setupViews() {
        signInButton = (SignInButton) getView().findViewById(R.id.btnRegisterGoogle);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAuthCode();
            }
        });
        linearLayout = (LinearLayout) getView().findViewById(R.id.fragmentGoogleRegisterRootLayout);
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
        Snackbar snackbar = Snackbar.make(linearLayout, R.string.error_retrieving_data, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void uiShowErrorConnectingToServerErrorSnackbar() {
        Snackbar snackbar = Snackbar
                .make(linearLayout, R.string.error_connecting_to_server, Snackbar.LENGTH_LONG);
        snackbar.show();

    }


    private void uiShowErrorConnectingToGoogleSnackbar() {
        Snackbar snackbar = Snackbar
                .make(linearLayout, R.string.error_connecting_to_google, Snackbar.LENGTH_LONG);
        snackbar.show();


    }
}
