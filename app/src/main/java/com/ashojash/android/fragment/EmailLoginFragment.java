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
import com.ashojash.android.R;
import com.ashojash.android.activity.MainActivity;
import com.ashojash.android.event.ErrorEvents;
import com.ashojash.android.event.OnApiResponseErrorEvent;
import com.ashojash.android.event.UserApiEvents;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.ui.AshojashSnackbar;
import com.ashojash.android.util.UiUtil;
import com.ashojash.android.util.AuthUtil;
import com.ashojash.android.util.BusUtil;
import com.ashojash.android.util.ValidatorUtil;
import com.ashojash.android.webserver.UserApi;
import org.greenrobot.eventbus.Subscribe;

public class EmailLoginFragment extends Fragment {
    private String login;
    private String password;
    private TextInputLayout loginWrapper;
    private TextInputLayout passwordWrapper;
    private Button btnLogin;
    private ProgressDialog progressDialog;
    private AshojashSnackbar.AshojashSnackbarBuilder builder;


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
        super.onActivityCreated(savedInstanceState);
        setupViews();
    }

    private boolean validateInputs() {
        boolean canSendDataToServer = true;
        int passwordValidationCode = ValidatorUtil.validateLoginPassword(password);
        if (passwordValidationCode < 0) {
            canSendDataToServer = false;
            final int ERROR_CODE = ValidatorUtil.validatePassword(password);
            if (ERROR_CODE == ValidatorUtil.FIELD_REQUIRED)
                passwordWrapper.setError(getResources().getString(R.string.password_field_required));
        } else {
            passwordWrapper.setError("");
        }
        int loginValidationCode = ValidatorUtil.validateLogin(login);
        if (loginValidationCode < 0) {
            canSendDataToServer = false;
            final int ERROR_CODE = ValidatorUtil.validateUsername(login);
            if (ERROR_CODE == ValidatorUtil.FIELD_REQUIRED)
                loginWrapper.setError(getResources().getString(R.string.login_field_required));
        } else {
            loginWrapper.setError("");
        }
        return canSendDataToServer;
    }

    @Subscribe
    public void onEvent(UserApiEvents.OnUserLoggedIn event) {
        AuthUtil.EmailLogin(event.user);
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
        dismissProgressDialog();
    }

    @Subscribe
    public void onEvent(ErrorEvents.OnUserLoginFailed event) {
        builder.duration(Snackbar.LENGTH_LONG).message(event.error.message).build().show();
        dismissProgressDialog();
    }

    @Subscribe
    public void onEvent(OnApiResponseErrorEvent event) {
        if (event.object instanceof UserApiEvents.OnUserLoggedIn) {
            Log.d(TAG, "onEvent: email login");
            builder.duration(Snackbar.LENGTH_INDEFINITE).message(R.string.error_retrieving_data).build().setAction(R.string.try_again, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (validateInputs()) signInUser();
                }
            }).show();
            dismissProgressDialog();
        }
    }


    private void signInUser() {
        onLoginPendingUpdateView();
        UserApi.login(login, password);
    }

    @Override
    public void onStart() {
        super.onStart();
        BusUtil.getInstance().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        BusUtil.getInstance().unregister(this);
    }

    private void dismissProgressDialog() {
        if (progressDialog != null) progressDialog.dismiss();
    }

    private void onLoginPendingUpdateView() {
        progressDialog = ProgressDialog.show(getActivity(), null, getResources().getString(R.string.logging_in), true, false);
    }

    private void setupViews() {
        builder = new AshojashSnackbar.AshojashSnackbarBuilder(getActivity().findViewById(R.id.rootView));
        loginWrapper = (TextInputLayout) getView().findViewById(R.id.wrapperLoginFragmentEmailLogin);
        passwordWrapper = (TextInputLayout) getView().findViewById(R.id.wrapperPasswordFragmentEmailLogin);
        btnLogin = (Button) getView().findViewById(R.id.btnFragmentEmailLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UiUtil.hideKeyboard();
                login = loginWrapper.getEditText().getText().toString();
                password = passwordWrapper.getEditText().getText().toString();
                if (validateInputs()) signInUser();
            }
        });
    }
}
