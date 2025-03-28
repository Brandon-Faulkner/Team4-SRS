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

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private MainActivity mainActivity;

    private String currentUserID;

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

        //Find out if someone is logged in or just a guest
        if (mainActivity.loggedInUser.isEmpty()) currentUserID = MainActivity.GUEST_ID;
        else currentUserID = mainActivity.loggedInUser;

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
            mainActivity.switchFragment(R.id.navigation_search, null);
        });

        //Only allow to request service if user is not a vendor
        if (!mainActivity.sqLiteHandler.isCustomerOrVendor(currentUserID).equals("vendor")) {
            applianceBtn.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putString("service", "Appliances");
                mainActivity.switchFragment(R.id.navigation_services, args);
            });

            electricalBtn.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putString("service", "Electrical");
                mainActivity.switchFragment(R.id.navigation_services, args);
            });

            plumbingBtn.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putString("service", "Plumbing");
                mainActivity.switchFragment(R.id.navigation_services, args);
            });

            homeCleanBtn.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putString("service", "Home Cleaning");
                mainActivity.switchFragment(R.id.navigation_services, args);
            });

            tutoringBtn.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putString("service", "Tutoring");
                mainActivity.switchFragment(R.id.navigation_services, args);
            });

            movingBtn.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putString("service", "Packaging & Moving");
                mainActivity.switchFragment(R.id.navigation_services, args);
            });

            compRepairBtn.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putString("service", "Computer Repair");
                mainActivity.switchFragment(R.id.navigation_services, args);
            });

            homeRepairBtn.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putString("service", "Home Repair & Painting");
                mainActivity.switchFragment(R.id.navigation_services, args);
            });

            pestControlBtn.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putString("service", "Pest Control");
                mainActivity.switchFragment(R.id.navigation_services, args);
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}