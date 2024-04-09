package com.team4.srs.ui.services;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.team4.srs.MainActivity;
import com.team4.srs.R;

public class ServicesFragment extends Fragment
{
    private ServicesViewModel mViewModel;
    MainActivity mainActivity;

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
        String serviceType = args != null ? args.getString("service") : null;

        mainActivity = ((MainActivity) requireActivity());
    }

}