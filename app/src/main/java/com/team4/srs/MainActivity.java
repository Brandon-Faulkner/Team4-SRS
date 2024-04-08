package com.team4.srs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.team4.srs.databinding.ActivityMainBinding;
import com.team4.srs.ui.login.LoginFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private BottomNavigationView navView;

    private NavController navController;
    private NavOptions.Builder navBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Bind the bottom nav view and then hide it for login
        navView = findViewById(R.id.nav_view);
        showHideNav(View.GONE);

        //Bind nav controller for setup and switching fragments
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        navBuilder = new NavOptions.Builder().setEnterAnim(R.anim.slide_left_enter).setExitAnim(R.anim.slide_left_exit).setPopEnterAnim(R.anim.slide_right_enter).setPopExitAnim(R.anim.slide_right_exit);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    public void showHideNav(int viewStatus) { navView.setVisibility(viewStatus); }

    public void switchFragment(int fragmentID) { navController.navigate(fragmentID, null, navBuilder.build()); }

    public void popFragmentStack() { navController.popBackStack(); }

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
}