package com.team4.srs.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.team4.srs.MainActivity;
import com.team4.srs.R;
import com.team4.srs.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    private MainActivity mainActivity;

    private SwitchCompat soundTog, notifsTog, locationTog, darkModeTog, textsEmailsTog, redeemTog;
    private Button goBack;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        mainActivity = ((MainActivity) requireActivity());

        soundTog = requireView().findViewById(R.id.settings_sound);
        notifsTog = requireView().findViewById(R.id.settings_notifs);
        locationTog = requireView().findViewById(R.id.settings_location);
        darkModeTog = requireView().findViewById(R.id.settings_dark_mode);
        textsEmailsTog = requireView().findViewById(R.id.settings_texts_emails);
        redeemTog = requireView().findViewById(R.id.settings_redeem);
        goBack = requireView().findViewById(R.id.settings_go_back);

        //Set listeners for the switches
        setSwitchListeners();

        //Load the stored state of toggles and settings
        loadSwitchStates();
    }

    private void setSwitchListeners() {
        soundTog.setOnClickListener(v -> {
            saveSettings("sound", soundTog.isChecked());
        });

        notifsTog.setOnClickListener(v -> {
            saveSettings("notifications", notifsTog.isChecked());
        });

        locationTog.setOnClickListener(v -> {
            saveSettings("location", locationTog.isChecked());
        });

        darkModeTog.setOnClickListener(v -> {
            saveSettings("dark_mode", darkModeTog.isChecked());
            saveSettings("isChangingTheme", true);
            if (darkModeTog.isChecked()) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        });

        textsEmailsTog.setOnClickListener(v -> {
            saveSettings("texts_emails", textsEmailsTog.isChecked());
        });

        redeemTog.setOnClickListener(v -> {
            saveSettings("redeem_points", redeemTog.isChecked());
        });

        goBack.setOnClickListener(v -> {
            mainActivity.switchFragment(R.id.navigation_home, null);
        });
    }

    public void loadSwitchStates() {
        SharedPreferences settings = getActivity().getSharedPreferences(MainActivity.PREFS_NAME, 0);
        soundTog.setChecked(settings.getBoolean("sound", true));
        notifsTog.setChecked(settings.getBoolean("notifications", true));
        locationTog.setChecked(settings.getBoolean("location", true));
        darkModeTog.setChecked(settings.getBoolean("dark_mode", true));
        textsEmailsTog.setChecked(settings.getBoolean("texts_emails", true));
        redeemTog.setChecked(settings.getBoolean("redeem_points", true));
    }

    public void saveSettings(String key, boolean value) {
        SharedPreferences settings = getActivity().getSharedPreferences(MainActivity.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}