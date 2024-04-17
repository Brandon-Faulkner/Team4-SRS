package com.team4.srs.ui.map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.team4.srs.MainActivity;
import com.team4.srs.databinding.FragmentDashboardBinding;
import com.team4.srs.databinding.FragmentMapBinding;
import com.team4.srs.ui.dashboard.DashboardViewModel;

public class MapFragment extends Fragment {

    private FragmentMapBinding binding;

    private MainActivity mainActivity;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MapViewModel mapViewModel =
                new ViewModelProvider(this).get(MapViewModel.class);

        binding = FragmentMapBinding.inflate(inflater, container, false);

        mainActivity = ((MainActivity) requireActivity());

        Bundle args = getArguments();
        //binding.textTripInfo.setText(args.getString("userAddress"));

        binding.mapBackBtn.setOnClickListener(v -> {
            mainActivity.popFragmentStack();
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
