package com.team4.srs.ui.orders;

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

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    public static class OrderViewHolder extends RecyclerView.ViewHolder
    {
        TextView requestID, service, description, dateTime, other, cost, status;
        View view;
        public OrderViewHolder(@NonNull View itemView)
        {
            super(itemView);
            requestID = itemView.findViewById(R.id.orders_card_id);
            service = itemView.findViewById(R.id.orders_card_service);
            description = itemView.findViewById(R.id.orders_card_desc);
            dateTime = itemView.findViewById(R.id.orders_card_datetime);
            other = itemView.findViewById(R.id.orders_card_other);
            cost = itemView.findViewById(R.id.orders_card_cost);
            status = itemView.findViewById(R.id.orders_card_status);
            view = itemView;
        }
    }

    private final List<String[]> data;

    public OrderAdapter(List<String[]> list) {
        data = list;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        //Create a new view, which defines the ui of the list item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_orders_cards, parent,false);

        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position)
    {
        //Setup data on the cards
        holder.requestID.setText(String.format("Request ID: %s", data.get(position)[0]));
        holder.service.setText(String.format("SERVICE: %s", data.get(position)[3]));
        holder.description.setText(String.format("DESCRIPTION: %s", data.get(position)[4]));
        holder.dateTime.setText(String.format("DATE & TIME: %s on %s", data.get(position)[5], data.get(position)[6]));
        holder.other.setText(String.format("OTHER INFO: %s", data.get(position)[7]));
        holder.cost.setText(String.format("COST: %s", data.get(position)[8]));
        holder.status.setText(String.format("STATUS: %s", data.get(position)[9]));

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

