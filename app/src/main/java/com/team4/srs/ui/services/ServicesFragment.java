package com.team4.srs.ui.services;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.team4.srs.MainActivity;
import com.team4.srs.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ServicesFragment extends Fragment
{
    private ServicesViewModel mViewModel;
    MainActivity mainActivity;

    private String service;

    private TextView vendorsTitle, serviceDesc, serviceEstimate, userServiceDesc, serviceTime, serviceDate, userExtraInfo;
    private ImageView serviceIcon;
    private Button serviceCancelBtn, serviceSubmitBtn;

    public static ServicesFragment newInstance()
    {
        return new ServicesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_services, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ServicesViewModel.class);
        Bundle args = getArguments();
        service = args != null ? args.getString("service") : "";
        String serviceType = service + " Vendors";

        mainActivity = ((MainActivity) requireActivity());

        //Get inputs and buttons
        vendorsTitle = requireView().findViewById(R.id.vendors_list_title);
        serviceIcon = requireView().findViewById(R.id.service_icon);
        serviceDesc = requireView().findViewById(R.id.service_desc);
        serviceEstimate = requireView().findViewById(R.id.service_estimate_title);
        userServiceDesc = requireView().findViewById(R.id.service_request_input_desc_input);
        serviceTime = requireView().findViewById(R.id.service_request_input_time);
        serviceDate = requireView().findViewById(R.id.service_request_input_date);
        userExtraInfo = requireView().findViewById(R.id.service_request_input_other_input);
        serviceCancelBtn = requireView().findViewById(R.id.service_request_cancel_btn);
        serviceSubmitBtn = requireView().findViewById(R.id.service_request_submit_btn);

        //Set title of page and estimate
        vendorsTitle.setText(serviceType);
        setupServiceEstimate();

        //Change icon and description
        serviceIcon.setImageDrawable(getServiceIcon(Objects.requireNonNull(service)));
        serviceDesc.setText(getServiceDescription(service));

        setupListeners();
    }

    private void setupListeners() {
        serviceTime.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), (view, hourOfDay, minuteTime) ->
            {
                String format;
                if (hourOfDay == 0) { hourOfDay += 12; format = "AM";
                } else if (hourOfDay == 12) {format = "PM";
                } else if (hourOfDay > 12) { hourOfDay -= 12; format = "PM";
                } else { format = "AM"; }

                String time = String.format(Locale.US,"%2d:%02d %s", hourOfDay, minuteTime, format);
                serviceTime.setText(time);
            }, hour, minute, false);

            timePickerDialog.show();
        });

        serviceDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, yearInput, monthOfYear, dayOfMonth) ->
            {
                String date = (monthOfYear + 1) + "/" + dayOfMonth + "/" + yearInput;
                serviceDate.setText(date);
            }, year, month, day);

            datePickerDialog.show();
        });

        serviceCancelBtn.setOnClickListener(v -> {
            mainActivity.popFragmentStack();
        });

        serviceSubmitBtn.setOnClickListener(v -> {
            //Reset error states on inputs first
            userServiceDesc.setError(null); serviceTime.setError(null);
            serviceDate.setError(null);

            //Check if input is empty
            if (!mainActivity.isInputEmpty(userServiceDesc, "Please describe the service you need")) return;
            if (!mainActivity.isInputEmpty(serviceTime, "Please select desired time")) return;
            if (!mainActivity.isInputEmpty(serviceDate, "Please select desired date")) return;

            //Check if user is logged in, if not use guest id
            String idToUse = mainActivity.loggedInUser == null ? MainActivity.GUEST_ID : mainActivity.loggedInUser;

            //Submit service request
            if (mainActivity.sqLiteHandler.insertRequests(idToUse, service, userServiceDesc.getText().toString().trim(), serviceTime.getText().toString(), serviceDate.getText().toString(), userExtraInfo.getText().toString(), "N/A", "Waiting for Bid")) {
                Toast.makeText(getContext(), "Service Request Complete! Vendors will give you offers shortly.", Toast.LENGTH_LONG).show();
                mainActivity.popFragmentStack();
            } else {
                Toast.makeText(getContext(), "Unable to process your request. Please try again.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupServiceEstimate() {
        List<String[]> vendors = mainActivity.sqLiteHandler.getVendorsByService(service);
        int totalOfRates = 0;

        for (int i = 0; i < vendors.size(); i++) {
            totalOfRates += Integer.parseInt(vendors.get(i)[5]);
        }

        int totalAvg = totalOfRates / vendors.size();

        String estimate = "Estimated Cost Avg: $" + totalAvg;
        serviceEstimate.setText(estimate);
    }

    private Drawable getServiceIcon(String service) {
        Drawable drawable = null;
        switch (service)
        {
            case "Appliance":
                drawable = AppCompatResources.getDrawable(requireContext(), R.drawable.appliance_icon);
                break;
            case "Electrical":
                drawable = AppCompatResources.getDrawable(requireContext(), R.drawable.electrical_icon);
                break;
            case "Plumbing":
                drawable = AppCompatResources.getDrawable(requireContext(), R.drawable.plumbing_icon);
                break;
            case "Home Cleaning":
                drawable = AppCompatResources.getDrawable(requireContext(), R.drawable.home_cleaning_icon);
                break;
            case "Tutoring":
                drawable = AppCompatResources.getDrawable(requireContext(), R.drawable.tutoring_icon);
                break;
            case "Packaging & Moving":
                drawable = AppCompatResources.getDrawable(requireContext(), R.drawable.moving_icon);
                break;
            case "Computer Repair":
                drawable = AppCompatResources.getDrawable(requireContext(), R.drawable.computer_repair_icon);
                break;
            case "Home Repair & Painting":
                drawable = AppCompatResources.getDrawable(requireContext(), R.drawable.home_repair_icon);
                break;
            case "Pest Control":
                drawable = AppCompatResources.getDrawable(requireContext(), R.drawable.pest_control_icon);
                break;
        }
        return drawable;
    }

    private String getServiceDescription(String service) {
        String desc = null;
        switch (service)
        {
            case "Appliance":
                desc = "Microwave not cooking properly or fridge not staying cold? Submit a request below and let a vendor take care of it!";
                break;
            case "Electrical":
                desc = "Having electrical issues or need some wiring done? Submit a request below and let a vendor take care of it!";
                break;
            case "Plumbing":
                desc = "Toilets backed up or kitchen sink clogged again? Submit a request below and let a vendor take care of it!";
                break;
            case "Home Cleaning":
                desc = "Been a while since your house was cleaned? Submit a request below and let a vendor take care of it!";
                break;
            case "Tutoring":
                desc = "Need to pass a test and get a good grade for a specific subject? Submit a request below and let a vendor take care of it!";
                break;
            case "Packaging & Moving":
                desc = "Need to pack a bunch of things or move somewhere else? Submit a request below and let a vendor take care of it!";
                break;
            case "Computer Repair":
                desc = "Computer giving you a headache or performing poorly? Submit a request below and let a vendor take care of it!";
                break;
            case "Home Repair & Painting":
                desc = "Want to paint a room or fix something wrong with the house? Submit a request below and let a vendor take care of it!";
                break;
            case "Pest Control":
                desc = "Are bugs infesting important locations in your life? Submit a request below and let a vendor take care of it!";
                break;
        }
        return desc;
    }
}