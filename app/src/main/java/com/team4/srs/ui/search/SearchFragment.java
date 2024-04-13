package com.team4.srs.ui.search;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team4.srs.R;

public class SearchFragment extends Fragment
{

    private SearchViewModel mViewModel;

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
        // TODO: Use the ViewModel

        //Get vendors of the specified service and setup recycler view
        //vendorsListView.setAdapter(new ServiceAdapter(mainActivity.sqLiteHandler.getVendorsByService(service), getServiceIcon(Objects.requireNonNull(service))));
        //vendorsListView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

}