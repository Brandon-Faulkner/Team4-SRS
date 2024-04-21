package com.team4.srs.ui.orders;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.team4.srs.MainActivity;
import com.team4.srs.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    public static class OrderViewHolder extends RecyclerView.ViewHolder
    {
        TextView requestID, vendor, service, description, dateTime, other, cost, status;
        Button leftBtn, rightBtn;
        View view;
        public OrderViewHolder(@NonNull View itemView)
        {
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

    private MainActivity mainActivity;
    private final List<String[]> data;
    private final String currentUserID;
    private final String userType;
    private final boolean isPaid;
    private final boolean isCurrentVendorRequests;

    public OrderAdapter(List<String[]> list, String currentUserID, String userType, boolean isPaid, boolean isCurrentVendorRequests) {
        data = list;
        this.currentUserID = currentUserID;
        this.userType = userType;
        this.isPaid = isPaid;
        this.isCurrentVendorRequests = isCurrentVendorRequests;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        //Create a new view, which defines the ui of the list item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_orders_cards, parent,false);

        mainActivity = ((MainActivity) view.getContext());

        return new OrderViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position)
    {
        //Setup data on the cards
        holder.requestID.setText(String.format("Request ID: %s", data.get(position)[0]));
        holder.service.setText(String.format("SERVICE: %s", data.get(position)[3]));
        holder.description.setText(String.format("DESCRIPTION: %s", data.get(position)[4]));
        holder.dateTime.setText(String.format("DATE & TIME: %s on %s", data.get(position)[5], data.get(position)[6]));
        holder.other.setText(String.format("OTHER INFO: %s", data.get(position)[7]));

        if (userType.equals("customer")) {
            holder.vendor.setVisibility(View.VISIBLE);
            holder.vendor.setText(String.format("VENDOR: %s", data.get(position)[10] == null ? "N/A" : data.get(position)[10]));
        } else holder.vendor.setVisibility(View.GONE);

        String costString;
        if (data.get(position)[8] == null) {
            costString = "N/A";
            if (userType.equals("vendor")) {
                holder.leftBtn.setVisibility(View.VISIBLE);
                holder.rightBtn.setVisibility(View.VISIBLE);
            }
        } else {
            costString = "$" + data.get(position)[8];
            if (userType.equals("vendor") && !isCurrentVendorRequests) {
                holder.leftBtn.setVisibility(View.GONE);
                holder.rightBtn.setVisibility(View.GONE);
            }
        }
        if (!isCurrentVendorRequests) {
            holder.cost.setText(String.format("%s", userType.equals("vendor") ? "YOUR BID: " + costString : "BID: " + costString));
        } else {
            holder.cost.setText(String.format("COST: %s", costString));
        }

        holder.status.setText(String.format("STATUS: %s", data.get(position)[9]));

        boolean isCustomerWaiting = data.get(position)[9].equals("Waiting for Bid") || data.get(position)[9].equals("Bids Placed");

        //Setup buttons based on user type and order type
        if (!isPaid) {
            String leftText, rightText;
            if (userType.equals("vendor")) {
                if (isCurrentVendorRequests) {
                    leftText = "View Map";
                    rightText = "Change Status";
                } else {
                    leftText = "View Map";
                    rightText = "Offer Bid";
                }
            } else {
                leftText = "Cancel Request";
                if (isCustomerWaiting) {
                    rightText = "Accept Bid";
                } else {
                    rightText = "Change Date";
                }
            }
            holder.leftBtn.setText(leftText);
            holder.leftBtn.setOnClickListener(v -> {
                switch (holder.leftBtn.getText().toString())
                {
                    case "View Map":
                        String currentUserAddress, destinationAddress;
                        if (userType.equals("vendor")){
                            currentUserAddress = mainActivity.sqLiteHandler.getVendorsAddress(currentUserID);
                            destinationAddress = mainActivity.sqLiteHandler.getUsersAddress(data.get(position)[2]);
                        } else {
                            currentUserAddress = mainActivity.sqLiteHandler.getUsersAddress(currentUserID);
                            destinationAddress = mainActivity.sqLiteHandler.getVendorsAddress(data.get(position)[1]);
                        }

                        Bundle args = new Bundle();
                        args.putString("customerAddress", currentUserAddress);
                        args.putString("vendorAddress", destinationAddress);
                        mainActivity.switchFragment(R.id.navigation_map, args);
                        break;
                    case "Cancel Request":
                        setupCancelDialog(holder, position);
                        break;
                }
            });

            holder.rightBtn.setText(rightText);
            holder.rightBtn.setOnClickListener(v -> {
                switch (holder.rightBtn.getText().toString())
                {
                    case "Change Status":
                        setupStatusDialog(holder.status, position);
                        break;
                    case "Offer Bid":
                        setupBidDialog(holder, holder.cost, holder.status, position);
                        break;
                    case "Accept Bid":
                        if (data.get(position)[8] == null) {
                            Toast.makeText(holder.rightBtn.getContext(), "There is no bid to accept at this time.", Toast.LENGTH_LONG).show();
                        } else {
                            if (mainActivity.sqLiteHandler.acceptCustomerRequestBid(data.get(position)[0], data.get(position)[1], data.get(position)[2], data.get(position)[8])) {
                                Toast.makeText(holder.rightBtn.getContext(), "Bid has been accepted!", Toast.LENGTH_LONG).show();
                                List<Integer> indexes = new ArrayList<>();
                                for (int i = 0; i < data.size(); i++) {
                                    if (data.get(i)[1] != null) {
                                        if (data.get(i)[0].equals(data.get(position)[0])) indexes.add(i);
                                    }
                                }
                                indexes.sort(Collections.reverseOrder());
                                for (int i = 0; i < indexes.size(); i++)
                                {
                                    mainActivity.sqLiteHandler.removeOldVendorBids(data.get(indexes.get(i))[0], data.get(indexes.get(i))[1]);
                                    data.remove(data.get(indexes.get(i)));
                                }
                                notifyDataSetChanged();
                            }
                        }
                        break;
                    case "Change Date":
                        setupDateDialog(holder.dateTime, position);
                        break;
                }
            });

            if (isCurrentVendorRequests && data.get(position)[9].equals("Bids Placed") && userType.equals("vendor")) {
                holder.leftBtn.setVisibility(View.GONE);
                holder.rightBtn.setVisibility(View.GONE);
            }

            if (userType.equals("customer") && (data.get(position)[9].equals("In Progress") || data.get(position)[9].equals("Waiting for Payment"))) {
                holder.leftBtn.setVisibility(View.GONE);
                holder.rightBtn.setVisibility(View.GONE);
            }
        } else {
            holder.leftBtn.setVisibility(View.GONE);
            holder.rightBtn.setVisibility(View.GONE);
        }
    }

    private final int[] selectedStatus = {-1};
    private final String[] statusArray = {"In Progress", "Waiting for Payment"};
    private void setupStatusDialog(TextView status, int position) {
        //Initialize alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(status.getContext(), R.style.CustomDialogTheme);

        builder.setTitle("Select Status");
        builder.setCancelable(false);

        builder.setSingleChoiceItems(statusArray, selectedStatus[0], (dialog, which) -> {
            selectedStatus[0] = which;
            status.setText(String.format("STATUS: %s", statusArray[which]));
            mainActivity.sqLiteHandler.updateRequestStatus(data.get(position)[0], statusArray[which]);
            Toast.makeText(status.getContext(), "Status has been updated", Toast.LENGTH_LONG).show();
            dialog.dismiss();
        });

        builder.setNegativeButton("Cancel", (dialog, which) ->
                dialog.dismiss());

        builder.show();
    }

    private void setupDateDialog(TextView dateTime, int position) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(dateTime.getContext(), (view, yearInput, monthOfYear, dayOfMonth) ->
        {
            String date = (monthOfYear + 1) + "/" + dayOfMonth + "/" + yearInput;
            dateTime.setText(String.format("DATE & TIME: %s on %s", data.get(position)[5], date));
            mainActivity.sqLiteHandler.updateRequestDate(data.get(position)[0], date);
            Toast.makeText(dateTime.getContext(), "Date has been changed", Toast.LENGTH_LONG).show();
        }, year, month, day);

        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
        datePickerDialog.show();
    }

    private void setupBidDialog(OrderViewHolder holder, TextView cost, TextView status, int position) {
        //Initialize alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(cost.getContext(), R.style.CustomDialogTheme);

        builder.setTitle("Enter Bid");
        final EditText input = new EditText(cost.getContext());
        final LinearLayout linearLayout = new LinearLayout(cost.getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setHint("Enter Bid...");

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        p.gravity = Gravity.CENTER;
        input.setLayoutParams(p);

        linearLayout.addView(input);
        linearLayout.setPadding(60, 0, 60, 0);

        builder.setView(linearLayout);
        builder.setPositiveButton("Ok", (dialog, which) ->
        {
            if (!input.getText().toString().isEmpty()) {
                if (mainActivity.sqLiteHandler.insertVendorBid(data.get(position)[0], currentUserID, input.getText().toString())) {
                    cost.setText(String.format("YOUR BID: $%s", input.getText().toString()));
                    status.setText(String.format("STATUS: %s", "Bids Placed"));
                    holder.leftBtn.setVisibility(View.GONE);
                    holder.rightBtn.setVisibility(View.GONE);
                    Toast.makeText(cost.getContext(), "Bid has been placed!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(cost.getContext(), "Unable to place bid. Please try again", Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) ->
                dialog.dismiss());

        builder.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setupCancelDialog(OrderViewHolder holder, int position) {
        //Initialize alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(holder.view.getContext(), R.style.CustomDialogTheme);

        builder.setTitle("Cancel Request");
        builder.setMessage("Are you sure you want to cancel? If you cancel within 24 hours of your scheduled date, you will lose points.");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton(android.R.string.yes, (dialog, which) -> {
            if (mainActivity.sqLiteHandler.cancelCustomerRequest(data.get(position)[0])) {
                Toast.makeText(holder.view.getContext(), "Request: " + data.get(position)[0] + " has been cancelled", Toast.LENGTH_LONG).show();
                data.remove(holder.getAdapterPosition());
                notifyDataSetChanged();
            } else {
                Toast.makeText(holder.view.getContext(), "Error cancelling request. Please try again", Toast.LENGTH_LONG).show();
            }
            dialog.dismiss();
        });

        builder.setNegativeButton(android.R.string.no, null);
        builder.show();
    }

    @Override
    public int getItemCount()
    {
        return data.size();
    }
}

