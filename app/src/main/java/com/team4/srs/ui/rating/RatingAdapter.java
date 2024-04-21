package com.team4.srs.ui.rating;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.team4.srs.MainActivity;
import com.team4.srs.R;

import java.util.List;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.RatingViewHolder> {
    public static class RatingViewHolder extends RecyclerView.ViewHolder
    {
        LinearLayout vendorRatingLayout, customerRatingLayout;
        TextInputLayout customerCommentInputLayout;
        RatingBar vendorRatingbar, customerRatingBar;
        TextView vendorCustomerID, vendorCustomerComment, customerRequestID, customerVendor, customerService, customerCost;
        EditText customerCommentInput;
        Button customerSubmitRating;
        View view;
        public RatingViewHolder(@NonNull View itemView)
        {
            super(itemView);
            //Vendor rating layout bindings
            vendorRatingLayout = itemView.findViewById(R.id.rating_card_info_layout);
            vendorRatingbar = itemView.findViewById(R.id.rating_card_rating);
            vendorCustomerID = itemView.findViewById(R.id.rating_card_customer);
            vendorCustomerComment = itemView.findViewById(R.id.rating_card_comment);

            //Customer rating layout bindings
            customerRatingLayout = itemView.findViewById(R.id.rating_card_submit_layout);
            customerRequestID = itemView.findViewById(R.id.rating_card_order_id);
            customerVendor = itemView.findViewById(R.id.rating_card_vendor);
            customerService = itemView.findViewById(R.id.rating_card_service);
            customerCost = itemView.findViewById(R.id.rating_card_cost);
            customerRatingBar = itemView.findViewById(R.id.rating_card_input_rating);
            customerCommentInputLayout = itemView.findViewById(R.id.rating_card_comment_input_layout);
            customerCommentInput = itemView.findViewById(R.id.rating_card_comment_input);
            customerSubmitRating = itemView.findViewById(R.id.rating_submit_btn);

            //Shared binding
            view = itemView;
        }
    }

    private final MainActivity mainActivity;
    private final List<String[]> data;
    private final String currentUserID;
    private final String userType;
    private final boolean isPastCustomerReviews;

    public RatingAdapter(List<String[]> list, String currentUserID, String userType, boolean isPastCustomerReviews, Context context) {
        mainActivity = ((MainActivity) context);
        data = list;
        this.currentUserID = currentUserID;
        this.userType = userType;
        this.isPastCustomerReviews = isPastCustomerReviews;
    }

    @NonNull
    @Override
    public RatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        //Create a new view, which defines the ui of the list item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rating_cards, parent,false);

        return new RatingViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull RatingViewHolder holder, int position)
    {
        //Change view based on user type
        if (userType.equals("vendor")) {
            //Setup data on the cards
            holder.vendorRatingLayout.setVisibility(View.VISIBLE);
            holder.customerRatingLayout.setVisibility(View.GONE);
            holder.vendorRatingbar.setRating(Float.parseFloat(data.get(position)[4]));
            holder.vendorCustomerID.setText(String.format("From %s", data.get(position)[6]));

            String comment = data.get(position)[5];
            if (!comment.isEmpty()) {
                holder.vendorCustomerComment.setText(String.format("\"%s\"", data.get(position)[5]));
            } else {
                holder.vendorCustomerComment.setVisibility(View.GONE);
            }
        } else {
            //Setup data on the cards
            holder.vendorRatingLayout.setVisibility(View.GONE);
            holder.customerRatingLayout.setVisibility(View.VISIBLE);

            if (isPastCustomerReviews) {
                holder.customerRequestID.setText(String.format("Request ID: %s", data.get(position)[1]));
                holder.customerVendor.setText(String.format("Vendor: %s", data.get(position)[6]));
                holder.customerService.setVisibility(View.GONE);
                holder.customerCost.setVisibility(View.GONE);

                holder.customerSubmitRating.setVisibility(View.GONE);
                holder.customerRatingBar.setIsIndicator(true);
                holder.customerRatingBar.setRating(Float.parseFloat(data.get(position)[4]));
                holder.customerCommentInputLayout.setHint(String.format("%s", "Your Comment"));
                holder.customerCommentInput.setInputType(InputType.TYPE_NULL);
                holder.customerCommentInput.setClickable(false);
                holder.customerCommentInputLayout.setClickable(false);
                holder.customerCommentInput.setText(String.format("\"%s\"", data.get(position)[5]));
            } else {
                holder.customerRequestID.setText(String.format("Request ID: %s", data.get(position)[0]));
                holder.customerVendor.setText(String.format("Vendor: %s", data.get(position)[10]));
                holder.customerService.setText(String.format("Service: %s", data.get(position)[3]));
                holder.customerCost.setText(String.format("Cost: $%s", data.get(position)[8]));

                holder.customerSubmitRating.setOnClickListener(v -> {
                    if (holder.customerRatingBar.getRating() >= 0.5) {
                        mainActivity.sqLiteHandler.insertCustomerReview(data.get(position)[0], data.get(position)[1],
                                currentUserID, String.valueOf(holder.customerRatingBar.getRating()), holder.customerCommentInput.getText().toString().trim());
                        data.remove(holder.getAdapterPosition());
                        notifyDataSetChanged();
                        Toast.makeText(mainActivity, "Rating submitted. Thank you!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mainActivity, "Please select a rating before submitting", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }

    }

    @Override
    public int getItemCount()
    {
        return data.size();
    }
}

