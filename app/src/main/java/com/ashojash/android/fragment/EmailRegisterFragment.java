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
import com.ashojash.android.R;
import com.ashojash.android.event.ErrorEvents;
import com.ashojash.android.event.OnApiResponseErrorEvent;
import com.ashojash.android.event.UserApiEvents;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.UserRegistered;
import com.ashojash.android.model.ValidationError;
import com.ashojash.android.ui.AshojashSnackbar;
import com.ashojash.android.util.UiUtil;
import com.ashojash.android.util.BusUtil;
import com.ashojash.android.util.ValidatorUtil;
import com.ashojash.android.webserver.UserApi;
import org.greenrobot.eventbus.Subscribe;


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
        UserApi.cancelLogin();
    }

    private boolean validateInputs() {
        boolean canSendDataToServer = true;
        int emailValidationCode = ValidatorUtil.validateEmail(email);
        int nameValidationCode = ValidatorUtil.validateName(name);
        int usernameValidationCode = ValidatorUtil.validateUsername(username);
        int passwordValidationCode = ValidatorUtil.validatePassword(password);
        if (emailValidationCode < 0) {
            canSendDataToServer = false;
            final int ERROR_CODE = emailValidationCode;
            if (ERROR_CODE == ValidatorUtil.FIELD_REQUIRED)
                emailWrapper.setError(getResources().getString(R.string.email_field_required));
            else if (ERROR_CODE == ValidatorUtil.REG_NOT_MATCH)
                emailWrapper.setError(getResources().getString(R.string.email_not_valid));
        } else {
            emailWrapper.setError("");
        }
        if (nameValidationCode < 0) {
            canSendDataToServer = false;
            final int ERROR_CODE = nameValidationCode;
            if (ERROR_CODE == ValidatorUtil.REG_NOT_MATCH)
                nameWrapper.setError(getResources().getString(R.string.name_reg_not_match));
            else if (ERROR_CODE == ValidatorUtil.FIELD_REQUIRED)
                nameWrapper.setError(getResources().getString(R.string.name_field_required));
            else if (ERROR_CODE == ValidatorUtil.FIELD_UNDER_LIMIT)
                nameWrapper.setError(getResources().getString(R.string.name_under_limit));
            else if (ERROR_CODE == ValidatorUtil.FIELD_EXCEEDS_LIMIT)
                nameWrapper.setError(getResources().getString(R.string.name_under_limit));
        } else {
            nameWrapper.setError("");
        }
        if (passwordValidationCode < 0) {
            canSendDataToServer = false;
            final int ERROR_CODE = passwordValidationCode;
            if (ERROR_CODE == ValidatorUtil.FIELD_REQUIRED)
                passwordWrapper.setError(getResources().getString(R.string.password_field_required));
            else if (ERROR_CODE == ValidatorUtil.FIELD_UNDER_LIMIT)
                passwordWrapper.setError(getResources().getString(R.string.password_under_limit));
            else if (ERROR_CODE == ValidatorUtil.FIELD_EXCEEDS_LIMIT)
                passwordWrapper.setError(getResources().getString(R.string.password_under_limit));

        } else {
            passwordWrapper.setError("");
        }
        if (usernameValidationCode < 0) {
            canSendDataToServer = false;
            final int ERROR_CODE = usernameValidationCode;
            if (ERROR_CODE == ValidatorUtil.REG_NOT_MATCH)
                usernameWrapper.setError(getResources().getString(R.string.username_reg_not_match));
            if (ERROR_CODE == ValidatorUtil.FIELD_REQUIRED)
                usernameWrapper.setError(getResources().getString(R.string.username_field_required));
            else if (ERROR_CODE == ValidatorUtil.FIELD_UNDER_LIMIT)
                usernameWrapper.setError(getResources().getString(R.string.username_under_limit));
            else if (ERROR_CODE == ValidatorUtil.FIELD_EXCEEDS_LIMIT)
                usernameWrapper.setError(getResources().getString(R.string.username_under_limit));
        } else {
            usernameWrapper.setError("");
        }
        return canSendDataToServer;
    }

    String TAG = AppController.TAG;

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

    @Subscribe
    public void onEvent(UserApiEvents.onUserRegistered event) {
        UserRegistered user = event.userRegistered;
        boolean isGoogleUser = user.isGmailUser;
        dismissProgressDialog();
        if (user.emailSent)
            onResponseUpdateView(isGoogleUser);
    }

    @Subscribe
    public void onEvent(ErrorEvents.OnUserRegistrationFailed event) {
        ValidationError error = event.error;
        if (error.name != null) nameWrapper.setError(error.name.get(0));
        if (error.email != null) emailWrapper.setError(error.email.get(0));
        if (error.username != null) usernameWrapper.setError(error.username.get(0));
        if (error.password != null) passwordWrapper.setError(error.password.get(0));
        dismissProgressDialog();
//        showRetrievingErrorSnackbar();
    }

    @Subscribe
    public void onEvent(OnApiResponseErrorEvent error) {
        if (error.object instanceof UserApiEvents.onUserRegistered) {
            dismissProgressDialog();
            new AshojashSnackbar.AshojashSnackbarBuilder(getActivity().findViewById(R.id.rootView)).message(R.string.error_retrieving_data).duration(Snackbar.LENGTH_INDEFINITE).build().setAction(R.string.try_again, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    registerUser();
                }
            }).show();
        }
    }

    private void registerUser() {
        UiUtil.hideKeyboard();
        username = usernameWrapper.getEditText().getText().toString();
        password = passwordWrapper.getEditText().getText().toString();
        email = emailWrapper.getEditText().getText().toString();
        name = nameWrapper.getEditText().getText().toString();
        if (validateInputs()) {
            progressDialog = ProgressDialog.show(getActivity(), null, getResources().getString(R.string.registering), true, false);
            UserApi.register(name, username, password, email);
        }
    }


    private void dismissProgressDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }


    private void onResponseUpdateView(boolean isGoogleEmail) {
        dismissProgressDialog();
        final AlertDialog verifyEmailDialog;
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        verifyEmailDialog = dialogBuilder.show();
        if (isGoogleEmail) {
            dialogBuilder.setView(R.layout.dialog_email_register_finsished_google);
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
        }
        verifyEmailDialog.findViewById(R.id.btnVerifyEmail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyEmailDialog.dismiss();
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
                registerUser();
            }
        });
    }

}