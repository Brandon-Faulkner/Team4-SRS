package com.team4.srs.ui.login;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.team4.srs.MainActivity;
import com.team4.srs.R;
import com.team4.srs.ui.home.HomeFragment;
import com.team4.srs.ui.registration.RegistrationFragment;

import java.util.Objects;

public class LoginFragment extends Fragment
{
    private LoginViewModel mViewModel;

    private MainActivity mainActivity;

    //Main login/registration page buttons
    private LinearLayout logRegBtnLayout;
    private Button userLoginBtn, userRegBtn, userGuestBtn;

    //Login steps page buttons
    private LinearLayout loginStepsLayout;
    private EditText loginIDText, loginPassText;
    private Button loginSubmitBtn, loginCancelBtn, loginForgPassBtn;

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

        loginStepsLayout = requireView().findViewById(R.id.login_steps);
        loginIDText = requireView().findViewById(R.id.login_id_input);
        loginPassText = requireView().findViewById(R.id.login_password_input);
        loginSubmitBtn = requireView().findViewById(R.id.login_steps_login_btn);
        loginCancelBtn = requireView().findViewById(R.id.login_steps_cancel_btn);
        loginForgPassBtn = requireView().findViewById(R.id.forgot_password);

        //Setup listeners for button clicks
        setupLoginListeners();
    }

    private void setupLoginListeners() {
        userLoginBtn.setOnClickListener(v ->
        {
            loginIDText.setText(""); loginPassText.setText("");
            mainActivity.crossfadeViews(loginStepsLayout, logRegBtnLayout, 250);
        });

        userRegBtn.setOnClickListener(v ->
        {
            //Head to RegistrationFragment
            mainActivity.switchFragment(R.id.navigation_registration, null);
        });

        userGuestBtn.setOnClickListener(v ->
        {
           //Head to home page
            mainActivity.switchFragment(R.id.navigation_home, null);
        });

        loginSubmitBtn.setOnClickListener(v ->
        {
            //Head to home page
            mainActivity.switchFragment(R.id.navigation_home,null);
        });

        loginCancelBtn.setOnClickListener(v ->
        {
            mainActivity.crossfadeViews(logRegBtnLayout, loginStepsLayout, 250);
        });

        loginForgPassBtn.setOnClickListener(v ->
        {

        });
    }
}