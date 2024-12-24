package com.example.bettabeal.utils;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.example.bettabeal.R;

public class ShippingStatusHelper {
    public static int getStatusColor(Context context, String status) {
        if (status == null || status.isEmpty()) {
            return ContextCompat.getColor(context, R.color.status_pending);
        }

        switch (status.toLowerCase()) {
            case "delivered":
                return ContextCompat.getColor(context, R.color.status_delivered);
            case "shipped":
                return ContextCompat.getColor(context, R.color.status_shipped);
            case "cancelled":
                return ContextCompat.getColor(context, R.color.status_cancelled);
            default:
                return ContextCompat.getColor(context, R.color.status_pending);
        }
    }

    public static String getFormattedStatus(String status) {
        if (status == null || status.isEmpty()) {
            return "Pending";
        }
        // Capitalize first letter
        return status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase();
    }
} 