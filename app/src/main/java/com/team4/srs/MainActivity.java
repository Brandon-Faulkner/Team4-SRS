package com.team4.srs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.team4.srs.databinding.ActivityMainBinding;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private LocationManager locationManager;
    public String locationAddress;
    public boolean locationOkayToUse;
    public static final String PREFS_NAME = "srs_settings";
    public String loggedInUser = "";
    public static final String GUEST_ID = "guestUserID";
    public Bundle passThroughArgs;

    public SQLiteHandler sqLiteHandler;
    private ActivityMainBinding binding;
    private BottomNavigationView navView;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //Get our database setup
        sqLiteHandler = new SQLiteHandler(MainActivity.this);
        sqLiteHandler.initializeDB();

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
                if ((destination.getId() == R.id.navigation_login || destination.getId() == R.id.navigation_registration) && !loggedInUser.isEmpty()) {
                    //Don't let user go to login/registration page if they are logged in
                    popFragmentStack();
                } else navView.setVisibility(View.GONE);
            }
        });

        //Restore preferences for settings and isLoggedIn
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean isSettingsSetup = settings.getBoolean("isSetup", false);
        loggedInUser = settings.getString("loggedInUser", "");

        if (isSettingsSetup) loadAppSettings(); else setDefaultSettings();

        //Check if user is coming from switching themes
        //If so, head to settings, otherwise proceed normally
        if (settings.getBoolean("isChangingTheme", false)) {
            SharedPreferences.Editor editor = settings.edit();
            editor.remove("isChangingTheme"); editor.apply();
            switchFragment(R.id.navigation_settings, null);
        } else if (loggedInUser.isEmpty()) {
            //Head to Login fragment on startup if not logged in
            switchFragment(R.id.navigation_login, null);
        }
    }

    public void switchFragment(int fragmentID, Bundle args) {
        navController.navigate(fragmentID, args);
    }

    public void popFragmentStack() {
        navController.popBackStack();
    }

    public boolean isInputEmpty(TextView input, String errorMsg) {
        if (input.getText().toString().trim().isEmpty()) {
            input.setError(errorMsg);
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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

    public void updateLoggedInUserInPrefSettings(String userID) {
        loggedInUser = userID;
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("loggedInUser", userID);
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
        toggleAppSounds(sound);

        //Handle notifications
        //**Not being used do to it being a local app with no online database**

        //Handle location
        setupLocationRequest(location);

        //Handle darkMode
        if (darkMode) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        //Handle texts and emails
        //**Not being used do to it being a local app with no online database**
        //**Sending texts and emails would cost money as well**

        //Handle redeem points
        //**This is handled during payment process**
    }

    public void toggleAppSounds(boolean muteState) {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, muteState);
    }

    public void setupLocationRequest(boolean locationAllowed) {
        if (locationAllowed) {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            boolean fineLoc = settings.getBoolean("fineLocationAllowed", false);
            boolean coarseLoc = settings.getBoolean("coarseLocationAllowed", false);
            locationOkayToUse = fineLoc || coarseLoc;

            if (!fineLoc && !coarseLoc) {
                ActivityResultLauncher<String[]> locationPermissionRequest = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result ->
                        {
                            Boolean fineLocationGranted = result.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = result.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false);
                            editor.putBoolean("fineLocationAllowed", fineLocationGranted != null && fineLocationGranted);
                            editor.putBoolean("coarseLocationAllowed", coarseLocationGranted != null && coarseLocationGranted);
                            editor.apply();
                            locationOkayToUse = (fineLocationGranted != null && fineLocationGranted) || (coarseLocationGranted != null && coarseLocationGranted);
                            startGettingLocation(locationOkayToUse);
                        }
                );

                locationPermissionRequest.launch(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION});
            } else {
                startGettingLocation(true);
            }
        }
    }

    private void startGettingLocation(boolean locationAllowed) {
        if (locationAllowed) {
            try {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
            } catch (SecurityException e){
                Log.e("Security Exception", "loadAppSettings: ", e);
            }
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location)
    {
        // Handle location changes
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        // Reverse geocoding to get address
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                locationAddress = address.getAddressLine(0);
                if (loggedInUser.isEmpty()) {
                    sqLiteHandler.updateGuestAddress(GUEST_ID, locationAddress);
                }
            }
        } catch (IOException e) {
            Log.e("Location Exception", "onLocationChanged: ", e);
        }
    }
}