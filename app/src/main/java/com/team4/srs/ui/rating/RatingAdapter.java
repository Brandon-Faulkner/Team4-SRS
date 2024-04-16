package com.team4.srs.ui.rating;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.team4.srs.R;

import java.util.List;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.RatingViewHolder> {
    public static class RatingViewHolder extends RecyclerView.ViewHolder
    {
        RatingBar ratingbar;
        TextView customerID, comment;
        View view;
        public RatingViewHolder(@NonNull View itemView)
        {
            super(itemView);
            ratingbar = itemView.findViewById(R.id.rating_card_rating);
            customerID = itemView.findViewById(R.id.rating_card_customer);
            comment = itemView.findViewById(R.id.rating_card_comment);
            view = itemView;
        }
    }

    private final List<String[]> data;

    public RatingAdapter(List<String[]> list) {
        data = list;
    }

    @NonNull
    @Override
    public RatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        //Create a new view, which defines the ui of the list item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rating_cards, parent,false);

        return new RatingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingViewHolder holder, int position)
    {
        //Setup data on the cards
        holder.ratingbar.setRating(Float.parseFloat(data.get(position)[3]));
        holder.customerID.setText(String.format("From @%s", data.get(position)[2]));

        String comment = data.get(position)[4];
        if (!comment.isEmpty()) {
            holder.comment.setText(String.format("\"%s\"", data.get(position)[4]));
        } else {
            holder.comment.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount()
    {
        return data.size();
    }
}

