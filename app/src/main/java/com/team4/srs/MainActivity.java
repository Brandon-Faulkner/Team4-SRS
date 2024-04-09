package com.team4.srs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.team4.srs.databinding.ActivityMainBinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "srs_settings";

    private ActivityMainBinding binding;
    private BottomNavigationView navView;

    private NavController navController;
    private NavOptions.Builder navBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Bind the bottom nav view
        navView = findViewById(R.id.nav_view);

        //Bind nav controller for setup and switching fragments
        navController = Navigation.findNavController(this, R.id.nav_frag_view);
        navBuilder = new NavOptions.Builder().setEnterAnim(R.anim.slide_left_enter).setExitAnim(R.anim.slide_left_exit).setPopEnterAnim(R.anim.slide_right_enter).setPopExitAnim(R.anim.slide_right_exit);
        NavigationUI.setupWithNavController(binding.navView, navController);

        //Listener to show/hide nav bar based on current fragment
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.navigation_home || destination.getId() == R.id.navigation_dashboard ||
            destination.getId() == R.id.navigation_settings) {
                navView.setVisibility(View.VISIBLE);
            } else {
                navView.setVisibility(View.GONE);
            }
        });

        //Restore preferences for settings
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean isSettingsSetup = settings.getBoolean("isSetup", false);

        if (isSettingsSetup) {
            loadAppSettings();
        } else {
            setDefaultSettings();
        }
    }

    public void switchFragment(int fragmentID, Bundle args) {
        navController.navigate(fragmentID, args, navBuilder.build());
    }

    public void popFragmentStack(boolean showNavBar) {
        navController.popBackStack();
    }

    public void crossfadeViews(View targView, View currView, int duration) {
        targView.setAlpha(0f);
        targView.setVisibility(View.VISIBLE);
        targView.animate().alpha(1f).setDuration(duration).setListener(null);

        currView.animate().alpha(0f).setDuration(duration).setListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                currView.setVisibility(View.GONE);
                currView.animate().setListener(null);
            }
        });
    }

    public void setDefaultSettings() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("isSetup", true);
        editor.putBoolean("sound", true);
        editor.putBoolean("notifications", true);
        editor.putBoolean("location", true);
        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                editor.putBoolean("dark_mode", true);
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                editor.putBoolean("dark_mode", false);
        }
        editor.putBoolean("texts_emails", true);
        editor.putBoolean("redeem_points", false);
        editor.apply();
    }

    public void loadAppSettings() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean sound = settings.getBoolean("sound", true);
        boolean notifications = settings.getBoolean("notifications", true);
        boolean location = settings.getBoolean("location", true);
        boolean darkMode = settings.getBoolean("dark_mode", false);
        boolean textsEmails = settings.getBoolean("texts_emails", true);
        boolean redeemPoints = settings.getBoolean("redeem_points", false);

        //Handle sound

        //Handle notifications

        //Handle location

        //Handle darkMode
        if (darkMode) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        //Handle texts and emails

        //Handle redeem points
    }
}