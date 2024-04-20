package com.team4.srs.ui.registration;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.tabs.TabLayout;
import com.team4.srs.MainActivity;
import com.team4.srs.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationFragment extends Fragment
{
    private MainActivity mainActivity;

    //Main layouts and tabs
    private TabLayout userVendorRegTabs;
    private CardView userProfileCard, userLoginCard, userVendorCard;

    //Main inputs and buttons
    private EditText userName, userEmail, userPhone, userAddress, userCity, userState, userZip, userID, userPassword;
    private EditText vendorCompName, vendorCompEmail, vendorCompPhone, vendorCompAddress, vendorCompState, vendorCompCity, vendorCompZip, vendorChargeAmount;
    private CheckBox vendorFee;
    private Button userCancel, userSubmit;
    private TextView vendorServices;
    private boolean[] selectedServices;
    private final ArrayList<Integer> serviceList = new ArrayList<>();
    private final String[] serviceArray = {"Appliances", "Electrical", "Plumbing", "Home Cleaning", "Tutoring", "Packaging & Moving", "Computer Repair", "Home Repair & Painting", "Pest Control"};

    //Regex strings for testing input
    private static final String userIDRegex = "^[a-zA-Z0-9]{8,}$";
    private static final String userPassRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!])(?!.*\\s).{6,}$";
    private static final String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    private static final String phoneRegex = "^[0-9]{10}$";
    private static final String stateRegex = "\\b(?!HI|AK)(AL|AZ|AR|CA|CO|CT|DE|FL|GA|ID|IL|IN|IA|KS|KY|LA|ME|MD|MA|MI|MN|MS|MO|MT|NE|NV|NH|NJ|NM|NY|NC|ND|OH|OK|OR|PA|RI|SC|SD|TN|TX|UT|VT|VA|WA|WV|WI|WY)\\b";

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

    /** @noinspection deprecation*/
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        RegistrationViewModel mViewModel = new ViewModelProvider(this).get(RegistrationViewModel.class);

        mainActivity = ((MainActivity)requireActivity());

        //Connect variables to appropriate view elements
        userVendorRegTabs = requireView().findViewById(R.id.user_or_vendor_reg_tabs);
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
        vendorServices = requireView().findViewById(R.id.search_filter_service);
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

    /** @noinspection BooleanMethodIsAlwaysInverted*/
    private boolean testInputWithRegex(TextView input, String regex, String errMsg) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input.getText().toString().trim());
        if (matcher.matches()) return true;
        else {
            Toast.makeText(getContext(), errMsg, Toast.LENGTH_LONG).show();
            return false;
        }
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
            mainActivity.popFragmentStack();
        });

        userSubmit.setOnClickListener(v ->
        {
            //Check which tab we are on. 0 = User, 1 = Vendor
            int selectedTab = userVendorRegTabs.getSelectedTabPosition();

            if (selectedTab == 0 && checkUserInfo()) {
                //Proceed with registration
                if (submitUserInfo()) {
                    //Customer registration complete, head to home page with user ID
                    mainActivity.updateLoggedInUserInPrefSettings(userID.getText().toString().trim());
                    mainActivity.switchFragment(R.id.navigation_home, null);
                    Toast.makeText(getContext(), "Registration complete! Welcome to Service Request System!", Toast.LENGTH_LONG).show();
                }
            }
            else if (selectedTab == 1 && checkUserInfo()) {
                //Safe to move on to checking vendor input
                if (checkVendorInfo()) {
                    //Proceed with registration
                    if (submitVendorInfo()) {
                        //Vendor registration complete, head to home page with user ID
                        mainActivity.updateLoggedInUserInPrefSettings(userID.getText().toString().trim());
                        mainActivity.switchFragment(R.id.navigation_home, null);
                        Toast.makeText(getContext(), "Registration complete! Welcome to Service Request System!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private boolean checkUserInfo()
    {
        //Reset error states on inputs first
        userName.setError(null); userEmail.setError(null); userPhone.setError(null);
        userAddress.setError(null); userCity.setError(null); userState.setError(null);
        userZip.setError(null); userID.setError(null); userPassword.setError(null);

        //Start checking if input is empty
        if (!mainActivity.isInputEmpty(userName, "Please enter your name")) return false;
        if (!mainActivity.isInputEmpty(userEmail, "Please enter your email")) return false;
        if (!mainActivity.isInputEmpty(userPhone, "Please enter your phone number")) return false;
        if (!mainActivity.isInputEmpty(userAddress, "Please enter your address")) return false;
        if (!mainActivity.isInputEmpty(userCity, "Please enter your city")) return false;
        if (!mainActivity.isInputEmpty(userState, "Please enter your state")) return false;
        if (!mainActivity.isInputEmpty(userZip, "Please enter your zip code")) return false;
        if (!mainActivity.isInputEmpty(userID, "Please enter your user ID")) return false;
        if (!mainActivity.isInputEmpty(userPassword, "Please enter your password")) return false;

        //Check if input is valid information for certain inputs
        if (!testInputWithRegex(userEmail, emailRegex, "Invalid email")) return false;
        if (!testInputWithRegex(userPhone, phoneRegex, "Invalid phone number")) return false;
        if (!testInputWithRegex(userState, stateRegex, "Invalid U.S. State (excluding AK and HI")) return false;
        if (!testInputWithRegex(userID, userIDRegex, "Invalid user ID")) return false;
        if (!testInputWithRegex(userPassword, userPassRegex, "Invalid password")) return false;

        //Finally, check if user ID already exists
        if (mainActivity.sqLiteHandler.checkUserIDExists(userID.getText().toString().trim())) {
            userID.setError("User ID already exists");
            Toast.makeText(getContext(), "User ID already exists", Toast.LENGTH_LONG).show();
            return false;
        }

        //Input is verified
        return true;
    }

    private boolean submitUserInfo()
    {
        //Get all text from inputs
        String userNameText = userName.getText().toString().trim();
        String userEmailText = userEmail.getText().toString().trim();
        String userPhoneText = userPhone.getText().toString().trim();
        String userAddressText = userAddress.getText().toString().trim();
        String userCityText = userCity.getText().toString().trim();
        String userStateText = userState.getText().toString().trim().toUpperCase();
        String userZipText = userZip.getText().toString().trim();
        String userIDText = userID.getText().toString().trim();
        String userPasswordText = userPassword.getText().toString().trim();

        //All is well, complete registration
        String fullAddress = userAddressText + ", " + userCityText + ", " + userStateText + ", " + userZipText;
        if(mainActivity.sqLiteHandler.insertUsers(userIDText, userPasswordText, userNameText, userEmailText, userPhoneText, fullAddress)) {
            if (mainActivity.sqLiteHandler.insertCustomers(userIDText, "0", "0")) {
                //Successfully inserted user and customer
                return true;
            } else {
                //Need to remove user from users table to avoid conflicts when trying again
                boolean deleteSuccess = mainActivity.sqLiteHandler.deleteUser(userIDText, false, false);
                Toast.makeText(getContext(), "Error completing registration. Please try again.", Toast.LENGTH_LONG).show();
                return false;
            }
        } else {
            Toast.makeText(getContext(), "Error completing registration. Please try again.", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private boolean checkVendorInfo()
    {
        //Reset error states on inputs first
        vendorCompName.setError(null); vendorCompEmail.setError(null); vendorCompPhone.setError(null);
        vendorCompAddress.setError(null); vendorCompState.setError(null); vendorCompCity.setError(null);
        vendorCompZip.setError(null); vendorChargeAmount.setError(null); vendorFee.setError(null);
        vendorServices.setError(null); vendorFee.setError(null);

        //Start checking if input is empty
        if (!mainActivity.isInputEmpty(vendorCompName, "Please enter companies name")) return false;
        if (!mainActivity.isInputEmpty(vendorCompEmail, "Please enter companies email")) return false;
        if (!mainActivity.isInputEmpty(vendorCompPhone, "Please enter companies phone number")) return false;
        if (!mainActivity.isInputEmpty(vendorCompAddress, "Please enter companies address")) return false;
        if (!mainActivity.isInputEmpty(vendorCompCity, "Please enter companies city")) return false;
        if (!mainActivity.isInputEmpty(vendorCompState, "Please enter companies state")) return false;
        if (!mainActivity.isInputEmpty(vendorCompZip, "Please enter companies zip code")) return false;
        if (!mainActivity.isInputEmpty(vendorServices, "Please select companies services")) return false;
        if (!mainActivity.isInputEmpty(vendorChargeAmount, "Please enter hourly rates for companies services")) return false;

        //Check if input is valid information for certain inputs
        if (!testInputWithRegex(vendorCompEmail, emailRegex, "Invalid email")) return false;
        if (!testInputWithRegex(vendorCompPhone, phoneRegex, "Invalid phone number")) return false;
        if (!testInputWithRegex(vendorCompState, stateRegex, "Invalid U.S. State (excluding AK and HI)")) return false;
        if (!vendorFee.isChecked()) {
            vendorFee.setError("You must agree to this in order to register");
            Toast.makeText(getContext(), "You must agree to pay a fee in order to register", Toast.LENGTH_LONG).show();
            return false;
        }

        //Input is verified
        return true;
    }

    private boolean submitVendorInfo()
    {
        //Get all text from inputs
        String userNameText = userName.getText().toString().trim();
        String userEmailText = userEmail.getText().toString().trim();
        String userPhoneText = userPhone.getText().toString().trim();
        String userAddressText = userAddress.getText().toString().trim();
        String userCityText = userCity.getText().toString().trim();
        String userStateText = userState.getText().toString().trim().toUpperCase();
        String userZipText = userZip.getText().toString().trim();

        String userIDText = userID.getText().toString().trim();
        String userPasswordText = userPassword.getText().toString().trim();

        String nameText = vendorCompName.getText().toString().trim();
        String emailText = vendorCompEmail.getText().toString().trim();
        String phoneText = vendorCompPhone.getText().toString().trim();
        String addressText = vendorCompAddress.getText().toString().trim();
        String cityText = vendorCompCity.getText().toString().trim();
        String stateText = vendorCompState.getText().toString().trim().toUpperCase();
        String zipText = vendorCompZip.getText().toString().trim();
        String chargeText = vendorChargeAmount.getText().toString().trim();
        String servicesText = vendorServices.getText().toString().trim();

        //All is well, complete registration
        String fullUserAddress = userAddressText + ", " + userCityText + ", " + userStateText + ", " + userZipText;
        String fullVendorAddress = addressText + ", " + cityText + ", " + stateText + ", " + zipText;
        if(mainActivity.sqLiteHandler.insertUsers(userIDText, userPasswordText, userNameText, userEmailText, userPhoneText, fullUserAddress)) {
            if (mainActivity.sqLiteHandler.insertVendors(userIDText, nameText, emailText, phoneText, fullVendorAddress, servicesText, chargeText)) {
                //Successfully inserted user and vendor
                return true;
            } else {
                //Need to remove user from users table to avoid conflicts when trying again
                boolean deleteSuccess = mainActivity.sqLiteHandler.deleteUser(userIDText, false, false);
                Toast.makeText(getContext(), "Error completing registration. Please try again.", Toast.LENGTH_LONG).show();
                return false;
            }
        } else {
            Toast.makeText(getContext(), "Error completing registration. Please try again.", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private void setupServicesDropdown()
    {
        selectedServices = new boolean[serviceArray.length];

        vendorServices.setOnClickListener(v ->
        {
            //Initialize alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomDialogTheme);

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
                    dialog.dismiss());

            builder.setNeutralButton("Clear All", (dialog, which) ->
            {
                for (int i = 0; i < selectedServices.length; i++)
                {
                    //Remove all selections, clear list, update textview
                    selectedServices[i] = false;
                    serviceList.clear();
                    vendorServices.setText("");
                }
            });

            builder.show();
        });
    }
}