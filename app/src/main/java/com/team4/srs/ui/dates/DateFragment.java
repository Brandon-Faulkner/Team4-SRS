package com.team4.srs.ui.dates;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.team4.srs.MainActivity;
import com.team4.srs.R;
import com.team4.srs.databinding.FragmentDateBinding;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;

public class DateFragment extends Fragment
{

    private FragmentDateBinding binding;

    MainActivity mainActivity;

    String currentUserID;

    ArrayList<String> data;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDateBinding.inflate(inflater, container, false);

        mainActivity = ((MainActivity) requireActivity());

        Bundle args = getArguments();
        currentUserID = args.getString("userID");

        //Get date list and update adapter of the list view
        updateListData();

        binding.datesAddRemoveBtn.setOnClickListener(v -> {
            String date = binding.datesDateInput.getText().toString();

            if (!date.isEmpty()) {
                if (data.contains(date)) {
                    //Remove date from vendor
                    mainActivity.sqLiteHandler.removeVendorDate(currentUserID, date);
                } else {
                    //Add date to vendor
                    mainActivity.sqLiteHandler.insertVendorDate(currentUserID, date);
                }

                updateListData();
            }
        });

        binding.datesBackBtn.setOnClickListener(v -> {
            mainActivity.popFragmentStack();
        });

        setupDateFilter();

        return binding.getRoot();
    }

    private void updateListData() {
        data = mainActivity.sqLiteHandler.getVendorDates(currentUserID);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mainActivity.getApplicationContext(), android.R.layout.simple_list_item_1, data);
        binding.datesCurrentList.setAdapter(adapter);
    }

    private void setupDateFilter() {
        binding.datesDateInput.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, yearInput, monthOfYear, dayOfMonth) ->
            {
                String date = (monthOfYear + 1) + "/" + dayOfMonth + "/" + yearInput;
                binding.datesDateInput.setText(date);
            }, year, month, day);

            datePickerDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Clear", (dialog, which) ->
            {
                binding.datesDateInput.setText("");
            });

            datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
            datePickerDialog.show();
        });
    }
}