package com.team4.srs.ui.registration;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.team4.srs.MainActivity;
import com.team4.srs.R;

import java.util.ArrayList;
import java.util.Collections;

public class RegistrationFragment extends Fragment
{
    private RegistrationViewModel mViewModel;
    private MainActivity mainActivity;

    //Main layouts and tabs
    private TabLayout userVendorRegTabs;
    private ScrollView userRegScrollView;
    private CardView userProfileCard, userLoginCard, userVendorCard;

    //Main inputs and buttons
    private EditText userName, userEmail, userPhone, userAddress, userCity, userState, userZip, userID, userPassword, vendorCompName, vendorCompEmail, vendorCompPhone, vendorCompAddress, vendorCompState, vendorCompCity, vendorCompZip, vendorChargeAmount;
    private CheckBox vendorFee;
    private Button userCancel, userSubmit;
    private TextView vendorServices;
    private boolean[] selectedServices;
    private final ArrayList<Integer> serviceList = new ArrayList<>();
    private final String[] serviceArray = {"Appliances", "Electrical", "Plumbing", "Home Cleaning", "Tutoring", "Packaging and Moving", "Computer Repair", "Home Repair and Painting", "Pest Control"};

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
        userVendorRegTabs = requireView().findViewById(R.id.user_or_vendor_reg_tabs);
        userRegScrollView = requireView().findViewById(R.id.user_reg_scrollview);
        userProfileCard = requireView().findViewById(R.id.user_reg_profile_card_view);
        userLoginCard = requireView().findViewById(R.id.user_reg_login_card_view);
        userVendorCard = requireView().findViewById(R.id.user_reg_vendor_card_view);

        userName = requireView().findViewById(R.id.user_reg_name_input);
        userEmail = requireView().findViewById(R.id.user_reg_email_input);
        userPhone = requireView().findViewById(R.id.user_reg_phone_input);
        userAddress = requireView().findViewById(R.id.user_reg_address_input);
        userCity = requireView().findViewById(R.id.user_reg_city_input);
        userState = requireView().findViewById(R.id.user_reg_state_input);
        userZip = requireView().findViewById(R.id.user_reg_zipcode_input);
        userID = requireView().findViewById(R.id.user_reg_id_input);
        userPassword = requireView().findViewById(R.id.user_reg_password_input);
        vendorCompName = requireView().findViewById(R.id.vendor_reg_comp_name_input);
        vendorCompEmail = requireView().findViewById(R.id.vendor_reg_comp_email_input);
        vendorCompPhone = requireView().findViewById(R.id.vendor_reg_comp_phone_input);
        vendorCompAddress = requireView().findViewById(R.id.vendor_reg_comp_address_input);
        vendorCompCity = requireView().findViewById(R.id.vendor_reg_comp_city_input);
        vendorCompState = requireView().findViewById(R.id.vendor_reg_comp_state_input);
        vendorCompZip = requireView().findViewById(R.id.vendor_reg_comp_zipcode_input);
        vendorServices = requireView().findViewById(R.id.vendor_reg_services);
        vendorChargeAmount = requireView().findViewById(R.id.vendor_reg_comp_charge_input);
        vendorFee = requireView().findViewById(R.id.vendor_reg_fee_check);
        userCancel = requireView().findViewById(R.id.user_reg_cancel_btn);
        userSubmit = requireView().findViewById(R.id.user_reg_submit_btn);

        //Initially show Profile and Login cards, not vendor cards
        userProfileCard.setVisibility(View.VISIBLE);
        userLoginCard.setVisibility(View.VISIBLE);
        userVendorCard.setVisibility(View.GONE);

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
                    userVendorCard.setVisibility(View.GONE);
                } else {
                    userVendorCard.setVisibility(View.VISIBLE);
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
            mainActivity.popFragmentStack(false);
        });

        userSubmit.setOnClickListener(v ->
        {
            //Head to home fragment
            mainActivity.switchFragment(R.id.navigation_home,null);
        });
    }

    private void setupServicesDropdown()
    {
        selectedServices = new boolean[serviceArray.length];

        vendorServices.setOnClickListener(v ->
        {
            //Initialize alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.Theme_ServiceRequestSystem);

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