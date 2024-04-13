package com.team4.srs.ui.services;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.team4.srs.R;

import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {
    public static class ServiceViewHolder extends RecyclerView.ViewHolder
    {
        TextView vendorName, vendorRate;
        ImageView vendorIcon;
        View view;
        public ServiceViewHolder(@NonNull View itemView)
        {
            super(itemView);
            vendorName = itemView.findViewById(R.id.service_card_vendor);
            vendorRate = itemView.findViewById(R.id.service_card_vendor_rate);
            vendorIcon = itemView.findViewById(R.id.service_card_icon);
            view = itemView;
        }
    }

    private final List<String[]> data;
    private final Drawable serviceIcon;

    public ServiceAdapter(List<String[]> list, Drawable icon) {
        data = list;
        serviceIcon = icon;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        //Create a new view, which defines the ui of the list item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_cards, parent,false);

        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position)
    {
        //Setup data on the cards
        holder.vendorName.setText(data.get(position)[0]);
        String chargeText = "Charge Per Hour: $" + data.get(position)[5];
        holder.vendorRate.setText(chargeText);
        holder.vendorIcon.setImageDrawable(serviceIcon);

        //Setup click listener
        holder.view.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Pressed: " + data.get(position)[0], Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount()
    {
        return data.size();
    }
}

