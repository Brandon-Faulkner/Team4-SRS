package com.team4.srs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.team4.srs.databinding.ActivityMainBinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

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
        navController = Navigation.findNavController(this, R.id.nav_frag_view);
        navBuilder = new NavOptions.Builder().setEnterAnim(R.anim.slide_left_enter).setExitAnim(R.anim.slide_left_exit).setPopEnterAnim(R.anim.slide_right_enter).setPopExitAnim(R.anim.slide_right_exit);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    public void showHideNav(int viewStatus) { navView.setVisibility(viewStatus); }

    public void switchFragment(int fragmentID, Bundle args, boolean showNavBar) {
        navController.navigate(fragmentID, args, navBuilder.build());
        if (showNavBar) showHideNav(View.VISIBLE);
        else showHideNav(View.GONE);
    }

    public void popFragmentStack(boolean showNavBar) {
        navController.popBackStack();
        if (showNavBar) showHideNav(View.VISIBLE);
        else showHideNav(View.GONE);
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
}