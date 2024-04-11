package com.team4.srs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.team4.srs.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "srs_settings";
    public boolean isLoggedIn = false;

    public SQLiteHandler sqLiteHandler; //USE THIS HANDLER FOR ALL FRAGMENTS. DO NOT CREATE A NEW ONE!
    private ActivityMainBinding binding;
    private BottomNavigationView navView;

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Get our database setup
        sqLiteHandler = new SQLiteHandler(MainActivity.this);

        //Bind the bottom nav view
        navView = findViewById(R.id.nav_view);

        //Bind nav controller for setup and switching fragments
        navController = Navigation.findNavController(this, R.id.nav_frag_view);
        NavigationUI.setupWithNavController(binding.navView, navController);

        //Listener to show/hide nav bar based on current fragment
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.navigation_home || destination.getId() == R.id.navigation_dashboard || destination.getId() == R.id.navigation_settings) {
                navView.setVisibility(View.VISIBLE);
            } else {
                if (destination.getId() == R.id.navigation_login && isLoggedIn) {
                    //Don't let user go to login page if they are logged in
                    popFragmentStack();
                } else navView.setVisibility(View.GONE);
            }
        });

        //Restore preferences for settings and isLoggedIn
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean isSettingsSetup = settings.getBoolean("isSetup", false);
        boolean isLoggedIn = settings.getBoolean("isLoggedIn", false);

        if (isSettingsSetup) loadAppSettings(); else setDefaultSettings();

        //Head to Login fragment on startup if not logged in
        if (!isLoggedIn) switchFragment(R.id.navigation_login, null);
    }

    public void switchFragment(int fragmentID, Bundle args) {
        navController.navigate(fragmentID, args);
    }

    public void popFragmentStack() {
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