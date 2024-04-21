package com.team4.srs.ui.map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.team4.srs.MainActivity;
import com.team4.srs.databinding.FragmentMapBinding;

public class MapFragment extends Fragment {

    private FragmentMapBinding binding;
    private MainActivity mainActivity;
    private String customerAddress, vendorAddress, mapsUrl;
    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);

        mainActivity = ((MainActivity) requireActivity());

        Bundle args = getArguments();
        customerAddress = args.getString("customerAddress");
        vendorAddress = args.getString("vendorAddress");

        if (customerAddress == null && mainActivity.locationOkayToUse) {
            customerAddress = mainActivity.locationAddress;
        }

        if (customerAddress != null && vendorAddress != null)
        {
            //Setup the WebView to show directions between user address and vendor address
            mapsUrl = "https://www.google.com/maps/dir/" + customerAddress.replaceAll(" ", "+") + "/" + vendorAddress.replaceAll(" ", "+") + ",+USA";
            binding.mapMapVisualizer.setWebViewClient(new WebViewClient());
            binding.mapMapVisualizer.loadUrl(mapsUrl);
            WebSettings webSettings = binding.mapMapVisualizer.getSettings();
            webSettings.setJavaScriptEnabled(true);

            //Disable all clicks on the WebView
            binding.mapMapVisualizer.setOnTouchListener((v, event) -> true);
        } else {
            binding.mapFailureText.setVisibility(View.VISIBLE);
            binding.mapMapVisualizer.setVisibility(View.GONE);
            binding.mapOpenBtn.setVisibility(View.GONE);
        }

        binding.mapBackBtn.setOnClickListener(v -> {
            mainActivity.popFragmentStack();
        });

        binding.mapOpenBtn.setOnClickListener(v -> {
            Uri uri = Uri.parse(mapsUrl);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.apps.maps");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
