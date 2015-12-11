package com.ashojash.android.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
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
import com.ashojash.android.ui.UiUtils;
import com.ashojash.android.utils.AuthUtils;
import com.ashojash.android.utils.AuthValidator;
import com.ashojash.android.webserver.JsonParser;
import com.ashojash.android.webserver.WebServer;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class EmailLoginFragment extends Fragment {
    private String callingActivity;
    private String login;
    private String password;
    private LinearLayout linearLayout;
    private TextInputLayout loginWrapper;
    private TextInputLayout passwordWrapper;
    private Button btnLogin;
    private ProgressDialog progressDialog;


    public EmailLoginFragment() {
    }


    String TAG = AppController.TAG;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_email_login, container, false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        String callingActivity = getArguments().getString("calling_activity");
        Log.d(TAG, "onActivityCreated: email login " + callingActivity);

        super.onActivityCreated(savedInstanceState);
        setupViews();
    }

    private boolean validateInputs() {
        boolean canSendDataToServer = true;
        loginWrapper.setErrorEnabled(false);
        passwordWrapper.setErrorEnabled(false);
        if (AuthValidator.validateLoginPassword(password) < 0) {
            canSendDataToServer = false;
            final int ERROR_CODE = AuthValidator.validatePassword(password);
            if (ERROR_CODE == AuthValidator.FIELD_REQUIRED)
                passwordWrapper.setError(getResources().getString(R.string.password_field_required));
        }
        if (AuthValidator.validateLogin(login) < 0) {
            canSendDataToServer = false;
            final int ERROR_CODE = AuthValidator.validateUsername(login);
            if (ERROR_CODE == AuthValidator.FIELD_REQUIRED)
                loginWrapper.setError(getResources().getString(R.string.login_field_required));
        }
        return canSendDataToServer;
    }

    private void signInUser() {
        onLoginPendingUpdateView();
        JsonObjectRequest request = WebServer.postSignInUser(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    AuthUtils.EmailLogin(response);
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();

                } catch (JSONException e) {
                    showRetrievingErrorSnackbar();
                } finally {
                    dismissProgressDialog();
                }
            }
        }

                , new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    if (error.networkResponse.data != null) {
                        String body = new String(error.networkResponse.data, "UTF-8");
                        int statusCode = error.networkResponse.statusCode;
                        if (statusCode == 422) {
                            JSONObject messageJsonObject = new JSONObject(body).getJSONObject("error").getJSONObject("message");
                            validatingInputError(messageJsonObject);
                        }
                        if (statusCode == 400) {
                            String message = new JSONObject(body).getJSONObject("error").getString("message");
                            showUserNotFoundSnackBar(message);
                        }
//                        validate for other stuff
                    }
                } catch (UnsupportedEncodingException | NullPointerException | JSONException e) {
                    e.printStackTrace();
                    showRetrievingErrorSnackbar();
                } finally {
                    dismissProgressDialog();
                }
            }
        }

                , login, password);
        AppController.getInstance().

                addToRequestQueue(request, "REGISTER_LOGIN_EMAIL");

    }

    @Override
    public void onPause() {
        super.onPause();
        AppController.getInstance().cancelPendingRequests("REGISTER_LOGIN_EMAIL");
    }

    private void validatingInputError(JSONObject messageJsonObject) {
        loginWrapper.setError(JsonParser.convertJsonArrayToString(messageJsonObject.optJSONArray("login")));
        passwordWrapper.setError(JsonParser.convertJsonArrayToString(messageJsonObject.optJSONArray("password")));
    }

    private void dismissProgressDialog() {
        if (progressDialog != null) progressDialog.dismiss();
    }

    private void onLoginPendingUpdateView() {
        progressDialog = ProgressDialog.show(getActivity(), null, getResources().getString(R.string.logging_in), true, false);
    }


    private void showRetrievingErrorSnackbar() {
        Snackbar snackbar = Snackbar
                .make(linearLayout, R.string.error_retrieving_data, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.try_again, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (validateInputs()) signInUser();
                    }
                });
        snackbar.show();
    }

    private void showUserNotFoundSnackBar(String message) {
        Snackbar snackbar = Snackbar
                .make(linearLayout, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void setupViews() {
        loginWrapper = (TextInputLayout) getView().findViewById(R.id.wrapperLoginFragmentEmailLogin);
        passwordWrapper = (TextInputLayout) getView().findViewById(R.id.wrapperPasswordFragmentEmailLogin);
        linearLayout = (LinearLayout) getView().findViewById(R.id.fragmentEmailSigninLayout);
        btnLogin = (Button) getView().findViewById(R.id.btnFragmentEmailLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UiUtils.hideKeyboard();
                login = loginWrapper.getEditText().getText().toString();
                password = passwordWrapper.getEditText().getText().toString();
                if (validateInputs()) signInUser();
            }
        });
    }
}
