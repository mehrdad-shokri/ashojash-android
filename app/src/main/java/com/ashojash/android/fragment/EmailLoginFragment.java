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
import com.ashojash.android.R;
import com.ashojash.android.activity.MainActivity;
import com.ashojash.android.event.ErrorEvents;
import com.ashojash.android.event.OnApiResponseErrorEvent;
import com.ashojash.android.event.UserApiEvents;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.ui.AshojashSnackbar;
import com.ashojash.android.ui.UiUtils;
import com.ashojash.android.utils.AuthUtils;
import com.ashojash.android.utils.AuthValidator;
import com.ashojash.android.utils.BusProvider;
import com.ashojash.android.webserver.UserApi;
import org.greenrobot.eventbus.Subscribe;

public class EmailLoginFragment extends Fragment {
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
        int passwordValidationCode = AuthValidator.validateLoginPassword(password);
        if (passwordValidationCode < 0) {
            canSendDataToServer = false;
            final int ERROR_CODE = AuthValidator.validatePassword(password);
            if (ERROR_CODE == AuthValidator.FIELD_REQUIRED)
                passwordWrapper.setError(getResources().getString(R.string.password_field_required));
        } else {
            passwordWrapper.setError("");
        }
        int loginValidationCode = AuthValidator.validateLogin(login);
        if (loginValidationCode < 0) {
            canSendDataToServer = false;
            final int ERROR_CODE = AuthValidator.validateUsername(login);
            if (ERROR_CODE == AuthValidator.FIELD_REQUIRED)
                loginWrapper.setError(getResources().getString(R.string.login_field_required));
        } else {
            loginWrapper.setError("");
        }
        return canSendDataToServer;
    }

    @Subscribe
    public void onEvent(UserApiEvents.OnUserLoggedIn event) {
        AuthUtils.EmailLogin(event.user);
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
        dismissProgressDialog();
    }

    @Subscribe
    public void onEvent(ErrorEvents.OnUserLoginFailed event) {
        AshojashSnackbar.make(getActivity(), event.error.message, Snackbar.LENGTH_LONG).show();
        dismissProgressDialog();
    }

    @Subscribe
    public void onEvent(OnApiResponseErrorEvent event) {
        showRetrievingErrorSnackbar();
        dismissProgressDialog();
    }

    private void signInUser() {
        onLoginPendingUpdateView();
        UserApi.login(login, password);
    }

    @Override
    public void onStart() {
        super.onStart();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onPause() {
        super.onPause();
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
