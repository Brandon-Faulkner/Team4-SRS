package com.team4.srs.ui.orders;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.team4.srs.MainActivity;
import com.team4.srs.R;
import com.team4.srs.databinding.FragmentOrdersBinding;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment
{
    private FragmentOrdersBinding binding;

    MainActivity mainActivity;

    String currentUserID, userType;
    boolean isPaid;
    boolean isCurrentRequestsTabSelected = true;
    boolean showTabs;

    String noResultText;

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

        if (isPaid) noResultText = "No orders found";
        else noResultText = "No requests found";

        setupBindings();
        setupVendorTabs();

        binding.ordersBackBtn.setOnClickListener(v -> {
            mainActivity.popFragmentStack();
        });

        return binding.getRoot();
    }

    private void setupBindings() {
        //Show orders in recycler view based on userType
        List<String[]> data;

        if (userType.equals("vendor")) {
            if(showTabs) binding.openOrCurrentRequestsTabs.setVisibility(View.VISIBLE);
            else binding.openOrCurrentRequestsTabs.setVisibility(View.GONE);
            data = mainActivity.sqLiteHandler.getVendorRequests(currentUserID, !isPaid ? isCurrentRequestsTabSelected : isPaid);
        } else {
            binding.openOrCurrentRequestsTabs.setVisibility(View.GONE);
            data = mainActivity.sqLiteHandler.getCustomerOrders(currentUserID, isPaid);
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
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }
}