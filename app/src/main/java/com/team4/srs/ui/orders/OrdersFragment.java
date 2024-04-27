package com.team4.srs.ui.orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.tabs.TabLayout;
import com.team4.srs.MainActivity;
import com.team4.srs.databinding.FragmentOrdersBinding;

import java.util.List;
import java.util.Objects;

public class OrdersFragment extends Fragment
{
    private FragmentOrdersBinding binding;

    MainActivity mainActivity;

    String currentUserID, userType, statusToSearch, noResultText;
    boolean isPaid;
    boolean isCurrentRequestsTabSelected = true;
    boolean showTabs;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentOrdersBinding.inflate(inflater, container, false);

        mainActivity = ((MainActivity) requireActivity());

        Bundle args = getArguments();
        binding.ordersPageTitle.setText(args.getString("title"));
        currentUserID = args.getString("userID");
        userType = args.getString("userType");
        isPaid = args.getBoolean("isPaid");
        showTabs = args.getBoolean("showTabs");
        statusToSearch = args.getString("statusToSearchFor");

        if (isPaid) noResultText = "No orders found";
        else noResultText = "No requests found";

        setupBindings();
        setupVendorTabs();

        binding.ordersBackBtn.setOnClickListener(v -> mainActivity.popFragmentStack());

        return binding.getRoot();
    }

    private void setupBindings() {
        //Show orders in recycler view based on userType
        List<String[]> data;

        if (userType.equals("vendor")) {
            if(showTabs) {
                Objects.requireNonNull(binding.openOrCurrentRequestsTabs.getTabAt(0)).setText("Current Requests");
                Objects.requireNonNull(binding.openOrCurrentRequestsTabs.getTabAt(1)).setText("Available Requests");
                binding.openOrCurrentRequestsTabs.setVisibility(View.VISIBLE);
            } else binding.openOrCurrentRequestsTabs.setVisibility(View.GONE);
            data = mainActivity.sqLiteHandler.getVendorRequests(currentUserID, !isPaid ? isCurrentRequestsTabSelected : isPaid, isPaid != isCurrentRequestsTabSelected ? "AND status NOT LIKE 'Paid'" : statusToSearch);
        } else {
            if(showTabs) {
                Objects.requireNonNull(binding.openOrCurrentRequestsTabs.getTabAt(0)).setText("Accepted Requests");
                Objects.requireNonNull(binding.openOrCurrentRequestsTabs.getTabAt(1)).setText("Requests With Bids");
                binding.openOrCurrentRequestsTabs.setVisibility(View.VISIBLE);
            }
            else binding.openOrCurrentRequestsTabs.setVisibility(View.GONE);
            data = mainActivity.sqLiteHandler.getCustomerOrders(currentUserID, isPaid, isCurrentRequestsTabSelected, isCurrentRequestsTabSelected, false);
        }

        if (!data.isEmpty()) {
            binding.ordersNoResultTitle.setVisibility(View.GONE);
            binding.ordersRecyclerView.setVisibility(View.VISIBLE);
            binding.ordersRecyclerView.setAdapter(new OrderAdapter(data, currentUserID, userType, isPaid, isCurrentRequestsTabSelected));
            binding.ordersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        } else {
            binding.ordersNoResultTitle.setText(noResultText);
            binding.ordersNoResultTitle.setVisibility(View.VISIBLE);
            binding.ordersRecyclerView.setVisibility(View.GONE);
        }
    }

    private void setupVendorTabs() {
        binding.openOrCurrentRequestsTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                if (tab.getPosition() == 0) //Current Requests Tab
                {
                    isCurrentRequestsTabSelected = true;
                    noResultText = "No requests found";
                    setupBindings();
                } else {
                    isCurrentRequestsTabSelected = false;
                    noResultText = "No requests available";
                    setupBindings();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) //Current Requests Tab
                {
                    isCurrentRequestsTabSelected = true;
                    noResultText = "No requests found";
                    setupBindings();
                } else {
                    isCurrentRequestsTabSelected = false;
                    noResultText = "No requests available";
                    setupBindings();
                }
            }
        });
    }
}