package com.team4.srs.ui.payment;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.team4.srs.MainActivity;
import com.team4.srs.R;

import java.util.List;
import java.util.Locale;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder> {
    public static class PaymentViewHolder extends RecyclerView.ViewHolder
    {
        TextView requestID, vendor, service, description, dateTime, other, cost, status;
        Button leftBtn, rightBtn;
        View view;
        public PaymentViewHolder(@NonNull View itemView) {
            super(itemView);
            requestID = itemView.findViewById(R.id.orders_card_id);
            vendor = itemView.findViewById(R.id.orders_card_vendor);
            service = itemView.findViewById(R.id.orders_card_service);
            description = itemView.findViewById(R.id.orders_card_desc);
            dateTime = itemView.findViewById(R.id.orders_card_datetime);
            other = itemView.findViewById(R.id.orders_card_other);
            cost = itemView.findViewById(R.id.orders_card_cost);
            status = itemView.findViewById(R.id.orders_card_status);
            leftBtn = itemView.findViewById(R.id.orders_card_left_btn);
            rightBtn = itemView.findViewById(R.id.orders_card_right_btn);
            view = itemView;
        }
    }

    private final MainActivity mainActivity;
    private final List<String[]> data;
    private final String currentUserID;
    private final View scrollView, infoView, backBtn;
    private final TextView infoTextView;

    public PaymentAdapter(List<String[]> list, String currentUserID, Context context, View scrollView, View infoView, View backBtn, TextView infoTextView) {
        data = list;
        this.currentUserID = currentUserID;
        mainActivity = ((MainActivity) context);
        this.scrollView = scrollView;
        this.infoView = infoView;
        this.infoTextView = infoTextView;
        this.backBtn = backBtn;
    }

    @NonNull
    @Override
    public PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        //Create a new view, which defines the ui of the list item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_orders_cards, parent,false);

        return new PaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentViewHolder holder, int position)
    {
        //Setup data on the cards
        holder.requestID.setText(String.format("Request ID: %s", data.get(position)[0]));
        holder.vendor.setText(String.format("VENDOR: %s", data.get(position)[10]));
        holder.service.setText(String.format("SERVICE: %s", data.get(position)[3]));
        holder.description.setText(String.format("DESCRIPTION: %s", data.get(position)[4]));
        holder.dateTime.setText(String.format("DATE & TIME: %s on %s", data.get(position)[5], data.get(position)[6]));
        holder.other.setText(String.format("OTHER INFO: %s", data.get(position)[7]));
        holder.cost.setText(String.format("COST: $%s", data.get(position)[8]));
        holder.status.setText(String.format("STATUS: %s", data.get(position)[9]));

        holder.leftBtn.setVisibility(View.GONE);
        holder.rightBtn.setText(String.format("%s", "Make Payment"));

        holder.rightBtn.setOnClickListener(v -> {
            mainActivity.crossfadeViews(infoView, scrollView, 250);

            //Check if user wants to use points
            SharedPreferences settings = mainActivity.getSharedPreferences(MainActivity.PREFS_NAME, 0);
            boolean redeemPoints = settings.getBoolean("redeem_points", false);

            String[] currentPoints = currentUserID.equals("guestUserID") ? null : mainActivity.sqLiteHandler.getCustomerRewards(currentUserID);

            String discount = currentPoints != null ? currentPoints[1].equals(".05") ? "5%" : "0%" : "N/A";
            String discountApplied = currentPoints != null ? String.format(Locale.US, "-$%.2f (%s)", (Float.parseFloat(data.get(position)[8]) * Float.parseFloat(currentPoints[1])), discount) : "N/A";
            double costWDiscount = currentPoints != null ? (Float.parseFloat(data.get(position)[8]) - (Float.parseFloat(data.get(position)[8]) * Float.parseFloat(currentPoints[1]))) : -1;

            double tax = costWDiscount == -1 ?  Float.parseFloat(data.get(position)[8]) * 0.0825 : costWDiscount * 0.0825;
            double total = costWDiscount == -1 ?  Float.parseFloat(data.get(position)[8]) + tax : costWDiscount + tax;

            double totalAfterPoints = currentPoints != null && redeemPoints ? total - Float.parseFloat(String.format(Locale.US,"%.2f", (double) Math.round(Float.parseFloat(currentPoints[0]) * .01))) : total;
            double rewardEarned = Float.parseFloat(data.get(position)[8]) * .25;
            String rewards = currentUserID.equals("guestUserID") ? "Register to earn rewards" : "5% off next order & "
                    + String.format(Locale.US, "%.0f", rewardEarned) + " points";

            infoTextView.setText(String.format(Locale.US,"Request ID: %s\n\nVendor: %s\n\nService: %s\n\nDiscount: %s" +
                    "\n\nCost: $%.2f\n\nTax: $%.2f\n\nTotal: $%.2f\n\nTotal after points applied: $%.2f\n\nGRAND TOTAL: $%.2f\n\nRewards Earned: %s",
                    data.get(position)[0], data.get(position)[10], data.get(position)[3], discountApplied, costWDiscount == -1 ? Float.parseFloat(data.get(position)[8]) : costWDiscount,
                    tax, total, totalAfterPoints, totalAfterPoints, rewards));

            backBtn.setVisibility(View.GONE);
        });
    }

    @Override
    public int getItemCount()
    {
        return data.size();
    }
}

