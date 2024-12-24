package com.example.bettabeal.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyFormatter {
    public static String formatRupiah(double amount) {
        return String.format("Rp%,.2f", amount)
                    .replace(",", "~")
                    .replace(".", ",")
                    .replace("~", ".");
    }
} 