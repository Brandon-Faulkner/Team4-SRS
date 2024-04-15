package com.team4.srs.ui.search;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.team4.srs.R;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {
    public static class SearchViewHolder extends RecyclerView.ViewHolder
    {
        TextView vendorName, vendorPhone, vendorServices, vendorRate, vendorAddress;
        ImageView vendorIcon;
        View view;
        public SearchViewHolder(@NonNull View itemView)
        {
            super(itemView);
            vendorName = itemView.findViewById(R.id.service_card_vendor);
            vendorPhone = itemView.findViewById(R.id.service_card_vendor_phone);
            vendorServices = itemView.findViewById(R.id.service_card_vendor_services);
            vendorRate = itemView.findViewById(R.id.service_card_vendor_rate);
            vendorAddress = itemView.findViewById(R.id.service_card_vendor_address);
            vendorIcon = itemView.findViewById(R.id.service_card_icon);
            view = itemView;
        }
    }

    private final List<String[]> data;

    public SearchAdapter(List<String[]> list) {
        data = list;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        //Create a new view, which defines the ui of the list item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_cards, parent,false);

        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position)
    {
        //Setup data on the cards
        holder.vendorName.setText(data.get(position)[0]);
        holder.vendorPhone.setText(String.format("PHONE: (%s) %s-%s", data.get(position)[2].substring(0,3), data.get(position)[2].substring(3,6),data.get(position)[2].substring(6)));
        holder.vendorServices.setText(String.format("SERVICE: %s", data.get(position)[4]));
        holder.vendorRate.setText(String.format("RATE/HOUR: $%s", data.get(position)[5]));
        holder.vendorAddress.setText(String.format("ADDRESS: %s", data.get(position)[3]));
        holder.vendorIcon.setImageDrawable(getServiceIcon(holder.view, data.get(position)[4]));

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

    private Drawable getServiceIcon(View view, String service) {
        Drawable drawable = null;
        switch (service)
        {
            case "Appliances":
                drawable = AppCompatResources.getDrawable(view.getContext(), R.drawable.appliance_icon);
                break;
            case "Electrical":
                drawable = AppCompatResources.getDrawable(view.getContext(), R.drawable.electrical_icon);
                break;
            case "Plumbing":
                drawable = AppCompatResources.getDrawable(view.getContext(), R.drawable.plumbing_icon);
                break;
            case "Home Cleaning":
                drawable = AppCompatResources.getDrawable(view.getContext(), R.drawable.home_cleaning_icon);
                break;
            case "Tutoring":
                drawable = AppCompatResources.getDrawable(view.getContext(), R.drawable.tutoring_icon);
                break;
            case "Packaging & Moving":
                drawable = AppCompatResources.getDrawable(view.getContext(), R.drawable.moving_icon);
                break;
            case "Computer Repair":
                drawable = AppCompatResources.getDrawable(view.getContext(), R.drawable.computer_repair_icon);
                break;
            case "Home Repair & Painting":
                drawable = AppCompatResources.getDrawable(view.getContext(), R.drawable.home_repair_icon);
                break;
            case "Pest Control":
                drawable = AppCompatResources.getDrawable(view.getContext(), R.drawable.pest_control_icon);
                break;
            default:
                drawable = AppCompatResources.getDrawable(view.getContext(), R.drawable.general_service_icon);
        }
        return drawable;
    }
}

