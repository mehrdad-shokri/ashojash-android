package com.ashojash.android.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ashojash.android.R;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.ui.UiUtils;
import com.ashojash.android.utils.AuthValidator;
import com.ashojash.android.webserver.JsonParser;
import com.ashojash.android.webserver.WebServer;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;


public class EmailRegisterFragment extends Fragment {
    private String name;
    private String username;
    private String password;
    private String email;
    private LinearLayout coordinatorLayout;
    private TextInputLayout usernameWrapper;
    private TextInputLayout passwordWrapper;
    private TextInputLayout nameWrapper;
    private TextInputLayout emailWrapper;
    private Button btnRegister;
    private ProgressDialog progressDialog;


    public EmailRegisterFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_email_register, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupViews();
    }

    @Override
    public void onPause() {
        super.onPause();
        AppController.getInstance().cancelPendingRequests("REGISTER_USER_EMAIL");
    }

    private boolean validateInputs() {
        boolean canSendDataToServer = true;
        usernameWrapper.setErrorEnabled(false);
        passwordWrapper.setErrorEnabled(false);
        nameWrapper.setErrorEnabled(false);
        emailWrapper.setErrorEnabled(false);
        if (AuthValidator.validateEmail(email) < 0) {
            canSendDataToServer = false;
            final int ERROR_CODE = AuthValidator.validateEmail(email);
            if (ERROR_CODE == AuthValidator.FIELD_REQUIRED)
                emailWrapper.setError(getResources().getString(R.string.email_field_required));
            else if (ERROR_CODE == AuthValidator.REG_NOT_MATCH)
                emailWrapper.setError(getResources().getString(R.string.email_not_valid));
        }
        if (AuthValidator.validateName(name) < 0) {
            final int ERROR_CODE = AuthValidator.validateName(name);
            canSendDataToServer = false;
            if (ERROR_CODE == AuthValidator.REG_NOT_MATCH)
                nameWrapper.setError(getResources().getString(R.string.name_reg_not_match));
            else if (ERROR_CODE == AuthValidator.FIELD_REQUIRED)
                nameWrapper.setError(getResources().getString(R.string.name_field_required));
            else if (ERROR_CODE == AuthValidator.FIELD_UNDER_LIMIT)
                nameWrapper.setError(getResources().getString(R.string.name_under_limit));
            else if (ERROR_CODE == AuthValidator.FIELD_EXCEEDS_LIMIT)
                nameWrapper.setError(getResources().getString(R.string.name_under_limit));
        }
        if (AuthValidator.validatePassword(password) < 0) {
            canSendDataToServer = false;
            final int ERROR_CODE = AuthValidator.validatePassword(password);
            if (ERROR_CODE == AuthValidator.FIELD_REQUIRED)
                passwordWrapper.setError(getResources().getString(R.string.password_field_required));
            else if (ERROR_CODE == AuthValidator.FIELD_UNDER_LIMIT)
                passwordWrapper.setError(getResources().getString(R.string.password_under_limit));
            else if (ERROR_CODE == AuthValidator.FIELD_EXCEEDS_LIMIT)
                passwordWrapper.setError(getResources().getString(R.string.password_under_limit));

        }
        if (AuthValidator.validateUsername(username) < 0) {
            canSendDataToServer = false;
            final int ERROR_CODE = AuthValidator.validateUsername(username);
            if (ERROR_CODE == AuthValidator.REG_NOT_MATCH)
                usernameWrapper.setError(getResources().getString(R.string.username_reg_not_match));
            if (ERROR_CODE == AuthValidator.FIELD_REQUIRED)
                usernameWrapper.setError(getResources().getString(R.string.username_field_required));
            else if (ERROR_CODE == AuthValidator.FIELD_UNDER_LIMIT)
                usernameWrapper.setError(getResources().getString(R.string.username_under_limit));
            else if (ERROR_CODE == AuthValidator.FIELD_EXCEEDS_LIMIT)
                usernameWrapper.setError(getResources().getString(R.string.username_under_limit));
        }
        return canSendDataToServer;
    }

    String TAG = AppController.TAG;

    private void registerUser() {
        onRegisterPendingUpdateView();
        JsonObjectRequest request = WebServer.postRegisterUser(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                TODO:// user has registered
//                JsonParser.parseUserRegistrationJsonObject(response);
                boolean isGoogleUser = false;
                try {
                    JSONObject data = response.getJSONObject("data");
                    if (data.getBoolean("email_sent"))
                        isGoogleUser = JsonParser.parseUserRegistrationIsGoogleEmail(data);
                    onResponseUpdateView(isGoogleUser);
                } catch (JSONException e) {
                    showRetrievingErrorSnackbar();
                }
            }
        }, new Response.ErrorListener() {
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
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    showRetrievingErrorSnackbar();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    showRetrievingErrorSnackbar();
                } catch (JSONException e) {
                    e.printStackTrace();
                    showRetrievingErrorSnackbar();
                } finally {
                    dismissProgressDialog();
                }
            }
        }, name, username, password, email);
        AppController.getInstance().addToRequestQueue(request, "REGISTER_USER_EMAIL");
    }

    private void validatingInputError(JSONObject messageJsonObject) {
        nameWrapper.setError(JsonParser.convertJsonArrayToString(messageJsonObject.optJSONArray("name")));
        emailWrapper.setError(JsonParser.convertJsonArrayToString(messageJsonObject.optJSONArray("email")));
        usernameWrapper.setError(JsonParser.convertJsonArrayToString(messageJsonObject.optJSONArray("username")));
        passwordWrapper.setError(JsonParser.convertJsonArrayToString(messageJsonObject.optJSONArray("password")));
    }

    private void dismissProgressDialog() {
        if (progressDialog != null) progressDialog.dismiss();
    }

    private void onRegisterPendingUpdateView() {
        progressDialog = ProgressDialog.show(getActivity(), null, getResources().getString(R.string.registering), true, false);
    }

    private void onResponseUpdateView(boolean isGoogleEmail) {
        dismissProgressDialog();
        final AlertDialog verifyEmailDialog;
        final AlertDialog.Builder dialogBuilder =
                new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        if (isGoogleEmail) {
            dialogBuilder.setView(R.layout.dialog_email_register_finsished_google);
            verifyEmailDialog = dialogBuilder.show();
            LinearLayout btnContinueInBrowser = (LinearLayout) verifyEmailDialog.findViewById(R.id.btnContinueInBrowser);
            btnContinueInBrowser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://accounts.google.com/"));
                    startActivity(browserIntent);
                    ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.viewpagerProfileActivity);
                    viewPager.setCurrentItem(1);
                    verifyEmailDialog.dismiss();
                }
            });
        } else {
            dialogBuilder.setView(R.layout.fragment_email_register_finished);
            verifyEmailDialog = dialogBuilder.show();
        }
        verifyEmailDialog.findViewById(R.id.btnVerifyEmail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyEmailDialog.dismiss();
                /*Intent intent = new Intent(AppController.currentActivity, MainActivity.class);
                AppController.currentActivity.startActivity(intent);
                AppController.currentActivity.finish();*/
                ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.viewpagerProfileActivity);
                viewPager.setCurrentItem(1);
            }
        });
    }


    private void showRetrievingErrorSnackbar() {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, R.string.error_retrieving_data, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.try_again, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        registerUser();
                    }
                });
        snackbar.show();
    }


    private void setupViews() {
        usernameWrapper = (TextInputLayout) getView().findViewById(R.id.wrapperUsernameFragmentEmailRegister);
        passwordWrapper = (TextInputLayout) getView().findViewById(R.id.wrapperPasswordFragmentEmailRegister);
        nameWrapper = (TextInputLayout) getView().findViewById(R.id.wrapperNameFragmentEmailRegister);
        emailWrapper = (TextInputLayout) getView().findViewById(R.id.wrapperEmailFragmentEmailRegister);
        coordinatorLayout = (LinearLayout) getView().findViewById(R.id.LayoutFragmentEmailRegister);
        btnRegister = (Button) getView().findViewById(R.id.btnFragmentEmailRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UiUtils.hideKeyboard();
                username = usernameWrapper.getEditText().getText().toString();
                password = passwordWrapper.getEditText().getText().toString();
                email = emailWrapper.getEditText().getText().toString();
                name = nameWrapper.getEditText().getText().toString();
                if (validateInputs()) {
                    registerUser();
                }
            }
        });
    }

}