package com.team4.srs.ui.rating;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team4.srs.MainActivity;
import com.team4.srs.R;
import com.team4.srs.databinding.FragmentOrdersBinding;
import com.team4.srs.databinding.FragmentRatingBinding;

import java.util.ArrayList;
import java.util.List;

public class RatingFragment extends Fragment
{
    private FragmentRatingBinding binding;

    MainActivity mainActivity;

    String currentUserID, userType;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentRatingBinding.inflate(inflater, container, false);
        mainActivity = ((MainActivity) requireActivity());

        Bundle args = getArguments();
        binding.ratingsPageTitle.setText(args.getString("title"));
        currentUserID = args.getString("userID");
        userType = args.getString("userType");

        //Show ratings either for customer to enter or vendor to view
        List<String[]> data;

        if (userType.equals("vendor")) {
            binding.ratingsPageTitle.setText(String.format("%s", "View Your Ratings"));
            binding.ratingsOverallTitle.setVisibility(View.VISIBLE);
            String result = mainActivity.sqLiteHandler.getVendorOverallRating(currentUserID);
            binding.ratingsOverallTitle.setText(String.format("Overall Avg: %s", result.isEmpty() ? "N/A" : result));
            data = mainActivity.sqLiteHandler.getVendorRatings(currentUserID);
        } else {
            binding.ratingsPageTitle.setText(String.format("%s", "Submit Vendor Ratings"));
            binding.ratingsOverallTitle.setVisibility(View.GONE);
            data = mainActivity.sqLiteHandler.getRequestsForReview(currentUserID);
        }

        if (!data.isEmpty()) {
            binding.ratingsNoResultTitle.setVisibility(View.GONE);
            binding.ratingsRecyclerView.setVisibility(View.VISIBLE);
            binding.ratingsRecyclerView.setAdapter(new RatingAdapter(data, currentUserID, userType, getContext()));
            binding.ratingsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        } else {
            binding.ratingsNoResultTitle.setVisibility(View.VISIBLE);
            binding.ratingsRecyclerView.setVisibility(View.GONE);
        }

        binding.ratingsBackBtn.setOnClickListener(v -> {
            mainActivity.popFragmentStack();
        });

        return binding.getRoot();
    }

}