package com.team4.srs.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.team4.srs.MainActivity;
import com.team4.srs.R;
import com.team4.srs.databinding.FragmentDashboardBinding;

import java.util.Objects;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    MainActivity mainActivity;

    String currentUserID;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);

        mainActivity = ((MainActivity) requireActivity());

        //Find out if someone is logged in or just a guest
        if (mainActivity.loggedInUser.isEmpty()) currentUserID = MainActivity.GUEST_ID;
        else currentUserID = mainActivity.loggedInUser;

        //Set the name of user
        String userNameText = "Hello, " + mainActivity.sqLiteHandler.getUsersName(currentUserID) + "!";
        binding.userFullName.setText(userNameText);

        //Find out if user is a customer or vendor
        String customerOrVendor = mainActivity.sqLiteHandler.isCustomerOrVendor(currentUserID);

        if (Objects.equals(customerOrVendor, "customer")) {
            binding.userDashboardGrid.setVisibility(View.VISIBLE);
            binding.serviceProviderDashboardGrid.setVisibility(View.GONE);
            binding.dashboardRateServices.setVisibility(View.VISIBLE);
            binding.dashboardLogout.setText(R.string.dashboard_user_logout_title);

            //Setup rewards for customer
            binding.userRewards.setVisibility(View.VISIBLE);
            String[] customerRewards = mainActivity.sqLiteHandler.getCustomerRewards(currentUserID);
            String rewardsText = String.format("Discounts: %s | Points: %s", customerRewards[1], customerRewards[0]);
            binding.userRewards.setText(rewardsText);
        } else if (Objects.equals(customerOrVendor, "vendor")) {
            binding.userDashboardGrid.setVisibility(View.GONE);
            binding.serviceProviderDashboardGrid.setVisibility(View.VISIBLE);
            binding.dashboardLogout.setText(R.string.dashboard_user_logout_title);
            binding.userRewards.setVisibility(View.GONE);
        } else if (Objects.equals(customerOrVendor, "guest")){
            binding.userDashboardGrid.setVisibility(View.VISIBLE);
            binding.serviceProviderDashboardGrid.setVisibility(View.GONE);
            binding.dashboardRateServices.setVisibility(View.GONE);
            binding.dashboardLogout.setText(R.string.dashboard_login_register);
            binding.userRewards.setVisibility(View.GONE);
        }

        setupButtonListeners();

        return binding.getRoot();
    }

    private void setupButtonListeners() {
        //Customer and Guest listeners
        binding.dashboardCurrentRequestsCustomer.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("title", "Current Service Requests");
            args.putString("userID", currentUserID);
            args.putString("userType", "customer");
            mainActivity.switchFragment(R.id.navigation_orders, args);
        });

        binding.dashboardPayment.setOnClickListener(v -> {

        });

        binding.dashboardOrderHistoryCustomer.setOnClickListener(v -> {

        });

        binding.dashboardRateServices.setOnClickListener(v -> {

        });

        //Vendor Listeners
        binding.dashboardCurrentRequestsVendor.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("title", "Open Service Requests");
            args.putString("userID", currentUserID);
            args.putString("userType", "vendor");
            mainActivity.switchFragment(R.id.navigation_orders, args);
        });

        binding.dashboardAddDates.setOnClickListener(v -> {

        });

        binding.dashboardOrderHistoryVendor.setOnClickListener(v -> {

        });

        binding.dashboardViewRatings.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("title", "View Your Ratings");
            args.putString("userID", currentUserID);
            args.putString("userType", "vendor");
            mainActivity.switchFragment(R.id.navigation_rating, args);
        });

        //Mutual listeners
        binding.dashboardLogout.setOnClickListener(v -> {
            if (binding.dashboardLogout.getText().toString().equals("Logout")) {
                mainActivity.updateLoggedInUserInPrefSettings("");
                mainActivity.switchFragment(R.id.navigation_login, null);
            } else {
                mainActivity.switchFragment(R.id.navigation_login, null);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}