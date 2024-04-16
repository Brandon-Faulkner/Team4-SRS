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

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentOrdersBinding.inflate(inflater, container, false);

        mainActivity = ((MainActivity) requireActivity());

        Bundle args = getArguments();
        binding.ordersPageTitle.setText(args.getString("title"));
        currentUserID = args.getString("userID");
        userType = args.getString("userType");

        //Show orders in recycler view based on userType
        List<String[]> data;

        if (userType.equals("vendor")) {
            data = mainActivity.sqLiteHandler.getVendorRequests(currentUserID, false);
        } else {
            data = mainActivity.sqLiteHandler.getCustomerOrders(currentUserID, false);
        }

        if (!data.isEmpty()) {
            binding.ordersNoResultTitle.setVisibility(View.GONE);
            binding.ordersRecyclerView.setVisibility(View.VISIBLE);
            binding.ordersRecyclerView.setAdapter(new OrderAdapter(data));
            binding.ordersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        } else {
            binding.ordersNoResultTitle.setVisibility(View.VISIBLE);
            binding.ordersRecyclerView.setVisibility(View.GONE);
        }

        binding.ordersBackBtn.setOnClickListener(v -> {
            mainActivity.popFragmentStack();
        });

        return binding.getRoot();
    }

}