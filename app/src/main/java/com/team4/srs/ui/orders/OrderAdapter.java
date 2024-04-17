package com.team4.srs.ui.orders;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.res.Configuration;
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

import java.util.Calendar;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    public static class OrderViewHolder extends RecyclerView.ViewHolder
    {
        TextView requestID, service, description, dateTime, other, cost, status;
        Button leftBtn, rightBtn;
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
            leftBtn = itemView.findViewById(R.id.orders_card_left_btn);
            rightBtn = itemView.findViewById(R.id.orders_card_right_btn);
            view = itemView;
        }
    }

    private MainActivity mainActivity;
    private final List<String[]> data;
    private final String vendorID;
    private final String userType;
    private final boolean isPaid;
    private final boolean isCurrentVendorRequests;

    public OrderAdapter(List<String[]> list, String vendorID, String userType, boolean isPaid, boolean isCurrentVendorRequests) {
        data = list;
        this.vendorID = vendorID;
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

        boolean isCustomerWaiting = data.get(position)[9].equals("Waiting for Bid");

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
                if (isCustomerWaiting) {
                    leftText = "Cancel Request";
                    rightText = "Accept Bid";
                } else {
                    leftText = "Cancel Request";
                    rightText = "Change Date";
                }
            }
            holder.leftBtn.setText(leftText);
            holder.leftBtn.setOnClickListener(v -> {
                switch (holder.leftBtn.getText().toString())
                {
                    case "View Map":
                        mainActivity.switchFragment(R.id.navigation_map, null);
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
                        setupBidDialog(holder.cost, position);
                        break;
                    case "Accept Bid":
                        if (data.get(position)[8].equals("N/A")) {
                            Toast.makeText(holder.rightBtn.getContext(), "There is no bid to accept at this time.", Toast.LENGTH_LONG).show();
                        } else {
                            if (mainActivity.sqLiteHandler.acceptCustomerRequestBid(data.get(position)[0], data.get(position)[2])) {
                                Toast.makeText(holder.rightBtn.getContext(), "Bid has been accepted!", Toast.LENGTH_LONG).show();
                                holder.rightBtn.setText(String.format("%s", "Change Date"));
                                holder.status.setText(String.format("STATUS: %s", "Accepted"));
                            }
                        }
                        break;
                    case "Change Date":
                        setupDateDialog(holder.dateTime, position);
                        break;
                }
            });
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
            status.setText(statusArray[which]);
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

    private void setupBidDialog(TextView cost, int position) {
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
                if (mainActivity.sqLiteHandler.updateRequestCost(data.get(position)[0], vendorID, input.getText().toString())) {
                    cost.setText(String.format("COST: %s", input.getText().toString()));
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

