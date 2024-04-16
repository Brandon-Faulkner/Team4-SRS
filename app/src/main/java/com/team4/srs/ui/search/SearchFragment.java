package com.team4.srs.ui.search;

import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.team4.srs.MainActivity;
import com.team4.srs.R;

import java.util.Calendar;
import java.util.List;

public class SearchFragment extends Fragment
{

    MainActivity mainActivity;
    private SearchViewModel mViewModel;

    private TextView filterService, filterRating, filterDate, filterPrice, noResult;
    private Button backBtn, searchBtn;
    private RecyclerView searchRecycler;

    //Service filter
    private final int[] selectedService = {-1};
    private final String[] serviceArray = {"Appliances", "Electrical", "Plumbing", "Home Cleaning", "Tutoring", "Packaging & Moving", "Computer Repair", "Home Repair & Painting", "Pest Control"};

    //Rating filter
    private final int[] selectedRating = {-1};
    private final String[] ratingArray = {"1-2", "2-3", "3-4", "4-5"};

    //Price filter
    private final int[] selectedPrice = {-1};
    private final String[] priceArray = {"0-20", "20-40", "40-70", "70-100", "100+"};

    public static SearchFragment newInstance()
    {
        return new SearchFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        mainActivity = ((MainActivity) requireActivity());

        filterService = requireView().findViewById(R.id.search_filter_service);
        filterRating = requireView().findViewById(R.id.search_filter_rating);
        filterDate = requireView().findViewById(R.id.search_filter_date);
        filterPrice = requireView().findViewById(R.id.search_filter_price);
        backBtn = requireView().findViewById(R.id.search_back_btn);
        searchBtn = requireView().findViewById(R.id.search_search_btn);
        noResult = requireView().findViewById(R.id.orders_no_result_title);
        searchRecycler = requireView().findViewById(R.id.orders_recycler_view);

        //Initial query of all vendors to show in recycler view
        updateRecycler(new SearchAdapter(mainActivity.sqLiteHandler.getVendorsBySearch("", "", "", "")));

        setupFilterDialogs(filterService, serviceArray, selectedService, "Select Service", R.string.search_filter_service_title);
        setupFilterDialogs(filterRating, ratingArray, selectedRating, "Select Rating", R.string.search_filter_rating_title);
        setupFilterDialogs(filterPrice, priceArray, selectedPrice, "Select Price", R.string.search_filter_price_title);
        setupDateFilter();

        backBtn.setOnClickListener(v -> {
            mainActivity.popFragmentStack();
        });

        searchBtn.setOnClickListener(v -> {
            List<String[]> data = mainActivity.sqLiteHandler.getVendorsBySearch(filterService.getText().toString(), filterRating.getText().toString(), filterDate.getText().toString(), filterPrice.getText().toString());
            if (!data.isEmpty()) {
                noResult.setVisibility(View.GONE);
                searchRecycler.setVisibility(View.VISIBLE);
                updateRecycler(new SearchAdapter(data));
            }
            else {
                noResult.setVisibility(View.VISIBLE);
                searchRecycler.setVisibility(View.GONE);
            }
        });
    }

    private void updateRecycler(SearchAdapter adapter) {
        searchRecycler.setAdapter(adapter);
        searchRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void setupDateFilter() {
        filterDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, yearInput, monthOfYear, dayOfMonth) ->
            {
                String date = (monthOfYear + 1) + "/" + dayOfMonth + "/" + yearInput;
                filterDate.setText(date);
            }, year, month, day);

            datePickerDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Clear", (dialog, which) ->
            {
                filterDate.setText("");
            });

            datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
            datePickerDialog.show();
        });
    }

    private void setupFilterDialogs(TextView view, String[] data, int[] checkedItem, String dialogTitle, int textTitle) {
        view.setOnClickListener(v ->
        {
            //Initialize alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomDialogTheme);

            builder.setTitle(dialogTitle);
            builder.setCancelable(false);

            builder.setSingleChoiceItems(data, checkedItem[0], (dialog, which) -> {
                checkedItem[0] = which;
                view.setText(data[which]);
                dialog.dismiss();
            });

            builder.setNegativeButton("Cancel", (dialog, which) ->
                    dialog.dismiss());

            builder.setNeutralButton("Clear", (dialog, which) ->
            {
                checkedItem[0] = -1;
                view.setText("");
                dialog.dismiss();
            });

            builder.show();
        });
    }
}