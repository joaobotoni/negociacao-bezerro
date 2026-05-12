package com.example.myapplication.ui.helpers;

import static com.example.myapplication.utils.DecimalUtil.CURRENCY_FORMAT;
import static com.example.myapplication.utils.DecimalUtil.MAX_VALUE;

import android.text.Editable;
import android.text.TextWatcher;
import androidx.annotation.NonNull;

public class TextWatcherHelper {


    @NonNull
    public static TextWatcher SimpleTextWatcher(@NonNull Runnable runnable) {
        return new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                runnable.run();
            }
        };
    }

    @NonNull
    public static TextWatcher SearchTextWatcher(@NonNull Runnable runnable) {
        final int MIN_SEARCH_LENGTH = 3;
        return new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (hasMinSearchLength(s)) runnable.run();
            }

            private boolean hasMinSearchLength(CharSequence s) {
                return s.length() >= MIN_SEARCH_LENGTH;
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
                String formatted = formatCurrency(s.toString());
                if (isUnchanged(s.toString(), formatted)) return;
                applyFormat(s, formatted);
                runnable.run();
            }
            private void applyFormat(Editable s, String formatted) {
                isUpdating = true;
                s.replace(0, s.length(), formatted);
                isUpdating = false;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            private String formatValue(String input) {
                return input.replaceAll("[^\\d]", "");
            }
            private double formatDecimal(String digits) {
                return Double.parseDouble(digits) / 100.0;
            }
            private String formatCurrency(String input) {
                String digits = formatValue(input);
                if (digits.isEmpty()) return "";
                double value = formatDecimal(digits);
                return isAboveLimit(value) ? input : CURRENCY_FORMAT.format(value);
            }

            private boolean isUnchanged(String current, String formatted) {
                return current.equals(formatted) && !formatted.isEmpty();
            }

            private boolean isAboveLimit(double value) {
                return value > MAX_VALUE;
            }
        };
    }
}