package com.team4.srs.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.team4.srs.R;
import com.team4.srs.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);

        boolean isServiceProvider = dashboardViewModel.isServiceProvider();
        View root = binding.getRoot();

        if (isServiceProvider) {
            // Set up the dashboard for the service provider
            binding.userFullName.setText("Service Provider Name");
            binding.btnMakePayment.setText(R.string.make_payment);
            binding.btnCancelRequest.setText(R.string.view_service_requests);

            // Hide buttons that are not relevant for service providers
            binding.btnChangeRequest.setVisibility(View.GONE);
            binding.btnOrderHistory.setVisibility(View.GONE);
            binding.btnMapView.setVisibility(View.GONE);
        } else {
            // Set up the dashboard for the regular user
            binding.userFullName.setText("Firstname Lastname");
            binding.btnMakePayment.setText(R.string.make_payment);
            binding.btnCancelRequest.setText(R.string.cancel_request);

            // Show all buttons for regular users
            binding.btnChangeRequest.setVisibility(View.VISIBLE);
            binding.btnOrderHistory.setVisibility(View.VISIBLE);
            binding.btnMapView.setVisibility(View.VISIBLE);
        }

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}