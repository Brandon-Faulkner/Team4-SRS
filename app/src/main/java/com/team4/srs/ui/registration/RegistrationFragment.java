package com.team4.srs.ui.registration;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.team4.srs.MainActivity;
import com.team4.srs.R;
import com.team4.srs.ui.home.HomeFragment;

import java.util.ArrayList;
import java.util.Collections;

public class RegistrationFragment extends Fragment
{
    private RegistrationViewModel mViewModel;
    private MainActivity mainActivity;

    //Main layouts and tabs
    private TabLayout userVendorRegTabs;
    private ScrollView userRegScrollView, vendorRegScrollView;

    //Main inputs and buttons
    private EditText userFName, userLName, userEmail, userPhone, userAddress, userID, userPassword;
    private EditText vendorFName, vendorLName, vendorEmail, vendorPhone, vendorAddress, vendorID, vendorPassword, vendorCompName;
    private Button userCancel, userSubmit, vendorCancel, vendorSubmit;
    private TextView vendorServices;
    private boolean[] selectedServices;
    private ArrayList<Integer> serviceList = new ArrayList<>();
    private String[] serviceArray = {"Appliances", "Electrical", "Plumbing", "Home Cleaning", "Tutoring", "Packaging and Moving", "Computer Repair", "Home Repair and Painting", "Pest Control"};

    public static RegistrationFragment newInstance()
    {
        return new RegistrationFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_registration, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(RegistrationViewModel.class);

        mainActivity = ((MainActivity)requireActivity());

        //Connect variables to appropriate view elements
        userVendorRegTabs = requireView().findViewById(R.id.user_or_vender_reg_tabs);
        userRegScrollView = requireView().findViewById(R.id.user_reg_scrollview);
        vendorRegScrollView = requireView().findViewById(R.id.vendor_reg_scrollview);

        userFName = requireView().findViewById(R.id.user_reg_fname);
        userLName = requireView().findViewById(R.id.user_reg_lname);
        userEmail = requireView().findViewById(R.id.user_reg_email);
        userPhone = requireView().findViewById(R.id.user_reg_phone);
        userAddress = requireView().findViewById(R.id.user_reg_address);
        userID = requireView().findViewById(R.id.user_reg_id);
        userPassword = requireView().findViewById(R.id.user_reg_password);
        userCancel = requireView().findViewById(R.id.user_reg_cancel_btn);
        userSubmit = requireView().findViewById(R.id.user_reg_submit_btn);

        vendorFName = requireView().findViewById(R.id.vendor_reg_fname);
        vendorLName = requireView().findViewById(R.id.vendor_reg_lname);
        vendorEmail = requireView().findViewById(R.id.vendor_reg_email);
        vendorPhone = requireView().findViewById(R.id.vendor_reg_phone);
        vendorAddress = requireView().findViewById(R.id.vendor_reg_address);
        vendorID = requireView().findViewById(R.id.vendor_reg_id);
        vendorPassword = requireView().findViewById(R.id.vendor_reg_password);
        vendorCompName = requireView().findViewById(R.id.vendor_reg_comp_name);
        vendorServices = requireView().findViewById(R.id.vendor_reg_services);
        vendorCancel = requireView().findViewById(R.id.vendor_reg_cancel_btn);
        vendorSubmit = requireView().findViewById(R.id.vendor_reg_submit_btn);

        //Initially show user registration view
        userRegScrollView.setVisibility(View.VISIBLE);
        vendorRegScrollView.setVisibility(View.GONE);

        //Setup vendor services multi-value dropdown
        setupServicesDropdown();

        //Setup listeners for the rest
        setupRegistrationListeners();
    }

    private void setupRegistrationListeners()
    {
        userVendorRegTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                if (tab.getPosition() == 0) //User registration
                {
                    mainActivity.crossfadeViews(userRegScrollView, vendorRegScrollView, 250);
                } else {
                    mainActivity.crossfadeViews(vendorRegScrollView, userRegScrollView, 250);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        userCancel.setOnClickListener(v ->
        {
            //Head to back to previous fragment
            mainActivity.popFragmentStack();
        });

        userSubmit.setOnClickListener(v ->
        {
            //Head to home fragment
            mainActivity.switchFragment(R.id.navigation_home);
        });

        vendorCancel.setOnClickListener(v ->
        {
            //Head to back to previous fragment
            mainActivity.popFragmentStack();
        });

        vendorSubmit.setOnClickListener(v ->
        {
            //Head to home fragment
            mainActivity.switchFragment(R.id.navigation_home);
        });
    }

    private void setupServicesDropdown()
    {
        selectedServices = new boolean[serviceArray.length];

        vendorServices.setOnClickListener(v ->
        {
            //Initialize alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Select Services");
            builder.setCancelable(false);

            builder.setMultiChoiceItems(serviceArray, selectedServices, (dialog, which, isChecked) ->
            {
                if (isChecked) {
                    //When a service is selected, add its position to serviceList
                    serviceList.add(which);
                    Collections.sort(serviceList);
                } else {
                    serviceList.remove(Integer.valueOf(which));
                }
            });

            builder.setPositiveButton("OK", (dialog, which) ->
            {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < serviceList.size(); i++)
                {
                    stringBuilder.append(serviceArray[serviceList.get(i)]);
                    if (i != serviceList.size() - 1)
                    {
                        stringBuilder.append(", ");
                    }
                }
                vendorServices.setText(stringBuilder.toString());
            });

            builder.setNegativeButton("Cancel", (dialog, which) ->
            {
                dialog.dismiss();
            });

            builder.setNeutralButton("Clear All", (dialog, which) ->
            {
                for (int i = 0; i < selectedServices.length; i++)
                {
                    //Remove all selections, clear list, update textview
                    selectedServices[i] = false;
                    serviceList.clear();
                    vendorServices.setText(R.string.reg_services_title);
                }
            });

            builder.show();
        });
    }
}