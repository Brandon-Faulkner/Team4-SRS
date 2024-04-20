package com.team4.srs.ui.payment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.team4.srs.MainActivity;
import com.team4.srs.R;
import com.team4.srs.databinding.FragmentPaymentBinding;
import com.team4.srs.ui.orders.OrderAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PaymentFragment extends Fragment
{
    private FragmentPaymentBinding binding;

    MainActivity mainActivity;

    String currentUserID, currentRequestID;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPaymentBinding.inflate(inflater, container, false);

        mainActivity = ((MainActivity) requireActivity());

        //Find out if someone is logged in or just a guest
        if (mainActivity.loggedInUser.isEmpty()) currentUserID = MainActivity.GUEST_ID;
        else currentUserID = mainActivity.loggedInUser;

        //Show recycler
        binding.paymentScrollview.setVisibility(View.VISIBLE);
        binding.paymentBackBtn.setVisibility(View.VISIBLE);
        binding.paymentInfoLayout.setVisibility(View.GONE);

        //Get orders ready for payment
        List<String[]> data = mainActivity.sqLiteHandler.getCustomerOrders(currentUserID, false, false, false, true);

        if (!data.isEmpty()) {
            binding.paymentNoResultTitle.setVisibility(View.GONE);
            binding.paymentRecyclerView.setAdapter(new PaymentAdapter(data, currentUserID, getContext(),
                    binding.paymentScrollview, binding.paymentInfoLayout, binding.paymentBackBtn, binding.paymentOrderInfo));
            binding.paymentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        } else {
            binding.paymentNoResultTitle.setVisibility(View.VISIBLE);
            binding.paymentRecyclerView.setVisibility(View.GONE);
        }

        binding.paymentBackBtn.setOnClickListener(v -> {
            mainActivity.popFragmentStack();
        });

        binding.paymentCancelBtn.setOnClickListener(v -> {
            mainActivity.crossfadeViews(binding.paymentScrollview, binding.paymentInfoLayout, 250);
            binding.paymentBackBtn.setVisibility(View.VISIBLE);
        });

        binding.paymentSubmitBtn.setOnClickListener(v -> {

            if (verifyCardInput()) {
                String requestID = binding.paymentOrderInfo.getText().toString().split("Request ID: ")[1].split("Vendor:")[0].trim();
                Toast.makeText(mainActivity, "Payment submitted. Thank you!", Toast.LENGTH_LONG).show();

                mainActivity.sqLiteHandler.updateRequestStatus(requestID, "Paid");

                if (!currentUserID.equals("guestUserID")) {
                    SharedPreferences settings = mainActivity.getSharedPreferences(MainActivity.PREFS_NAME, 0);
                    boolean redeemPoints = settings.getBoolean("redeem_points", false);

                    String pointsEarned = binding.paymentOrderInfo.getText().toString().split("order & ")[1].split(" points")[0];
                    mainActivity.sqLiteHandler.updateCustomerRewards(currentUserID, ".05", pointsEarned, redeemPoints);
                }

                mainActivity.switchFragment(R.id.navigation_dashboard, null);
            }
        });

        return binding.getRoot();
    }

    private boolean verifyCardInput() {
        binding.cardNumberInput.setError(null);
        binding.cardExpInput.setError(null);
        binding.cardCvvInput.setError(null);

        if (!mainActivity.isInputEmpty(binding.cardNumberInput, "Please enter card number")) return false;
        if (!mainActivity.isInputEmpty(binding.cardExpInput, "Please enter card expiration")) return false;
        if (!mainActivity.isInputEmpty(binding.cardCvvInput, "Please enter card CVV")) return false;

        String cardNum = binding.cardNumberInput.getText().toString();
        String cardExp = binding.cardExpInput.getText().toString();
        String cardCVV = binding.cardCvvInput.getText().toString();

        if (cardNum.length() != 16) {
            binding.cardNumberInput.setError("Please enter full card number");
            Toast.makeText(mainActivity, "Please enter full card number", Toast.LENGTH_LONG).show();
            return false;
        }
        if (cardExp.length() != 4) {
            binding.cardExpInput.setError("Please enter full card expiration");
            Toast.makeText(mainActivity, "Please enter full card expiration", Toast.LENGTH_LONG).show();
            return false;
        }
        if (cardCVV.length() != 3) {
            binding.cardCvvInput.setError("Please enter full card cvv");
            Toast.makeText(mainActivity, "Please enter full card CVV", Toast.LENGTH_LONG).show();
            return false;
        }

        int cardMonth = Integer.parseInt(cardExp.substring(0, 2));
        if (!(cardMonth >= 1 && cardMonth <=12)) {
            binding.cardExpInput.setError("Please enter a valid expiration month");
            Toast.makeText(mainActivity, "Please enter a valid expiration month", Toast.LENGTH_LONG).show();
            return false;
        }

        int cardYear = Integer.parseInt(cardExp.substring(2, 4));
        DateFormat currYearFormat = new SimpleDateFormat("yy", Locale.US);
        int currYear = Integer.parseInt(currYearFormat.format(Calendar.getInstance().getTime()));
        if ((cardYear < currYear) || (cardYear > (currYear + 10))) {
            binding.cardExpInput.setError("Please enter a valid expiration year");
            Toast.makeText(mainActivity, "Please enter a valid expiration year", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}
