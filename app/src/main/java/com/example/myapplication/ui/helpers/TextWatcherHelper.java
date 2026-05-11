package com.example.myapplication.ui.helpers;

import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.NonNull;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class TextWatcherHelper {

    private static final Locale LOCALE_BR = new Locale("pt", "BR");
    private static final DecimalFormatSymbols SYMBOLS = new DecimalFormatSymbols(LOCALE_BR);
    public static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0.00", SYMBOLS);
    @NonNull
    public static TextWatcher SimpleTextWatcher(@NonNull Runnable runnable) {
        return new TextWatcher() {
            @Override public void afterTextChanged(Editable s) {}

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                runnable.run();
            }
        };
    }

    @NonNull
    public static TextWatcher SearchTextWatcher(@NonNull Runnable runnable) {
        return new TextWatcher() {
            @Override public void afterTextChanged(Editable s) {}
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 3) {
                    runnable.run();
                }
            }
        };
    }

    @NonNull
    public static TextWatcher MoneyTextWatcher(@NonNull Runnable runnable) {
        return new TextWatcher() {
            private boolean isUpdating = false;

            @Override
            public void afterTextChanged(Editable s) {
                if (isUpdating) return;
                String formatted = format(s.toString());
                boolean unchanged = s.toString().equals(formatted);
                if (unchanged && !formatted.isEmpty()) return;
                isUpdating = true;
                s.replace(0, s.length(), formatted);
                isUpdating = false;
                runnable.run();
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        };
    }


    private static String format(String input) {
        String digits = input.replaceAll("[^\\d]", "");
        if (digits.isEmpty()) return "";
        double value = Double.parseDouble(digits) / 100.0;
        if (value > 9_999_999.99) return input;
        return CURRENCY_FORMAT.format(value);
    }
}