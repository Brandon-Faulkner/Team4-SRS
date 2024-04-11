package com.team4.srs.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.team4.srs.MainActivity;
import com.team4.srs.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginFragment extends Fragment
{
    private LoginViewModel mViewModel;

    private MainActivity mainActivity;

    //Main login/registration page buttons
    private LinearLayout logRegBtnLayout;
    private Button userLoginBtn, userRegBtn, userGuestBtn;

    //Login steps page buttons and input
    private LinearLayout loginStepsLayout;
    private EditText loginIDText, loginPassText;
    private Button loginSubmitBtn, loginCancelBtn, loginForgPassBtn;

    //Forgot password page buttons and input
    private LinearLayout forgPassLayout;
    private EditText forgPassIDText, forgPassEmailText, forgPassPhoneText, forgPassPassText;
    private Button forgPassSubmitBtn, forgPassCancelBtn;

    public static LoginFragment newInstance()
    {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        mainActivity = ((MainActivity)requireActivity());

        //Connect variables to appropriate view elements
        logRegBtnLayout = requireView().findViewById(R.id.login_and_registration_buttons);
        userLoginBtn = requireView().findViewById(R.id.user_login);
        userRegBtn = requireView().findViewById(R.id.user_registration);
        userGuestBtn = requireView().findViewById(R.id.continue_guest);

        //Login steps buttons and inputs
        loginStepsLayout = requireView().findViewById(R.id.login_steps);
        loginIDText = requireView().findViewById(R.id.login_id_input);
        loginPassText = requireView().findViewById(R.id.login_password_input);
        loginSubmitBtn = requireView().findViewById(R.id.login_steps_login_btn);
        loginCancelBtn = requireView().findViewById(R.id.login_steps_cancel_btn);
        loginForgPassBtn = requireView().findViewById(R.id.forgot_password);

        //Forgot password buttons and inputs
        forgPassLayout = requireView().findViewById(R.id.login_forgot_pass_steps);
        forgPassIDText = requireView().findViewById(R.id.login_forgot_pass_id_input);
        forgPassEmailText = requireView().findViewById(R.id.login_forgot_pass_email_input);
        forgPassPhoneText = requireView().findViewById(R.id.login_forgot_pass_phone_input);
        forgPassPassText = requireView().findViewById(R.id.login_forgot_pass_password_input);
        forgPassSubmitBtn = requireView().findViewById(R.id.login_forgot_pass_submit_btn);
        forgPassCancelBtn = requireView().findViewById(R.id.login_forgot_pass_cancel_btn);

        //Make sure inputs are not showing errors
        loginIDText.setError(null);
        loginPassText.setError(null);

        //Setup listeners for button clicks
        setupLoginListeners();
    }

    private void setupLoginListeners() {
        userLoginBtn.setOnClickListener(v ->
        {
            //Switch to login steps layout and reset inputs
            loginIDText.setText(""); loginPassText.setText("");
            loginIDText.setError(null); loginPassText.setError(null);
            mainActivity.crossfadeViews(loginStepsLayout, logRegBtnLayout, 250);
        });

        userRegBtn.setOnClickListener(v ->
        {
            //Head to RegistrationFragment
            mainActivity.switchFragment(R.id.navigation_registration, null);
        });

        userGuestBtn.setOnClickListener(v ->
        {
           //Head to home page and clear back stack
            mainActivity.switchFragment(R.id.navigation_home, null);
        });

        loginSubmitBtn.setOnClickListener(v ->
        {
            //Reset error state first
            loginIDText.setError(null);
            loginPassText.setError(null);

            if (loginIDText.length() == 0) { loginIDText.setError("Please enter your user ID"); return; }
            if (loginPassText.length() == 0) { loginPassText.setError("Please enter your password"); return; }

            //Attempt to login
            if (mainActivity.sqLiteHandler.checkLoginUser(loginIDText.getText().toString(), loginPassText.getText().toString())) {
                //Successful login, head to home page
                mainActivity.switchFragment(R.id.navigation_home, null);
                mainActivity.isLoggedIn = true;
            } else {
                //Invalid login combo, show both errors
                loginIDText.setError("User ID or password is incorrect");
                loginPassText.setError("User ID or password is incorrect");
            }
        });

        loginCancelBtn.setOnClickListener(v ->
        {
            //Switch to login/registration layout
            mainActivity.crossfadeViews(logRegBtnLayout, loginStepsLayout, 250);
        });

        loginForgPassBtn.setOnClickListener(v ->
        {
            //Switch to forgot password layout and reset inputs
            forgPassIDText.setText(""); forgPassEmailText.setText("");
            forgPassPhoneText.setText(""); forgPassPassText.setText("");
            forgPassIDText.setError(null); forgPassEmailText.setError(null);
            forgPassPhoneText.setError(null); forgPassPassText.setError(null);
            mainActivity.crossfadeViews(forgPassLayout, loginStepsLayout, 250);
        });

        forgPassSubmitBtn.setOnClickListener(v ->
        {
            //First make sure all inputs are entered and reset error states
            forgPassIDText.setError(null); forgPassEmailText.setError(null);
            forgPassPhoneText.setError(null); forgPassPassText.setError(null);

            if (forgPassIDText.length() == 0) { forgPassIDText.setError("Please enter userID"); return; }

            if (forgPassEmailText.length() == 0) { forgPassEmailText.setError("Please enter email"); return; }

            if (forgPassPhoneText.length() == 0) { forgPassPhoneText.setError("Please enter phone number"); return; }

            if (forgPassPassText.length() == 0) { forgPassPassText.setError("Please enter new password"); return; }

            // First verify account information
            if (mainActivity.sqLiteHandler.checkForgotPassUser(forgPassIDText.getText().toString(), forgPassEmailText.getText().toString(), forgPassPhoneText.getText().toString())) {
                // User verified, reset password then head to login screen
                mainActivity.sqLiteHandler.changePasswordUser(forgPassIDText.getText().toString(), forgPassPassText.getText().toString());
                mainActivity.crossfadeViews(loginStepsLayout, forgPassLayout, 250);
                Toast.makeText(getContext(), "Password successfully updated!", Toast.LENGTH_LONG).show();
            } else {
                // User not verified, show toast
                Toast.makeText(getContext(), "Unable to find account. Please verify your info and try again.", Toast.LENGTH_LONG).show();
            }
        });

        forgPassCancelBtn.setOnClickListener(v ->
        {
            //Switch to login steps layout
            mainActivity.crossfadeViews(loginStepsLayout, forgPassLayout, 250);
        });
    }
}