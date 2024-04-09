package com.team4.srs.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.team4.srs.MainActivity;
import com.team4.srs.R;
import com.team4.srs.databinding.FragmentHomeBinding;
import com.team4.srs.ui.services.ServicesFragment;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private MainActivity mainActivity;

    //Main inputs and buttons
    private TextView serviceSearch;
    private Button applianceBtn, electricalBtn, plumbingBtn, homeCleanBtn, tutoringBtn, movingBtn, compRepairBtn, homeRepairBtn, pestControlBtn;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        mainActivity = ((MainActivity) requireActivity());

        //Connect variables to appropriate view elements
        serviceSearch = requireView().findViewById(R.id.search_for_service);

        applianceBtn = requireView().findViewById(R.id.appliance_button);
        electricalBtn = requireView().findViewById(R.id.electrical_button);
        plumbingBtn = requireView().findViewById(R.id.plumbing_button);
        homeCleanBtn = requireView().findViewById(R.id.home_cleaning_button);
        tutoringBtn = requireView().findViewById(R.id.tutoring_button);
        movingBtn = requireView().findViewById(R.id.moving_button);
        compRepairBtn = requireView().findViewById(R.id.computer_repair_button);
        homeRepairBtn = requireView().findViewById(R.id.home_repair_button);
        pestControlBtn = requireView().findViewById(R.id.pest_control_button);

        //Setup listeners for search bar and buttons
        setupHomeListeners();
    }

    private void setupHomeListeners()
    {
        serviceSearch.setOnClickListener(v -> {

        });

        applianceBtn.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("service", "appliances");
            mainActivity.switchFragment(R.id.navigation_services, args, false);
        });

        electricalBtn.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("service", "electrical");
            mainActivity.switchFragment(R.id.navigation_services, args, false);
        });

        plumbingBtn.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("service", "plumbing");
            mainActivity.switchFragment(R.id.navigation_services, args, false);
        });

        homeCleanBtn.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("service", "home_clean");
            mainActivity.switchFragment(R.id.navigation_services, args, false);
        });

        tutoringBtn.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("service", "tutoring");
            mainActivity.switchFragment(R.id.navigation_services, args, false);
        });

        movingBtn.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("service", "moving");
            mainActivity.switchFragment(R.id.navigation_services, args, false);
        });

        compRepairBtn.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("service", "computer_repair");
            mainActivity.switchFragment(R.id.navigation_services, args, false);
        });

        homeRepairBtn.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("service", "home_repair");
            mainActivity.switchFragment(R.id.navigation_services, args, false);
        });

        pestControlBtn.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("service", "pest_control");
            mainActivity.switchFragment(R.id.navigation_services, args, false);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}