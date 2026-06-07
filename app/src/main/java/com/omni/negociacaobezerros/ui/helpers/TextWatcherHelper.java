package com.omni.negociacaobezerros.ui.helpers;

import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.NonNull;

import java.math.BigDecimal;
import java.text.NumberFormat;

public final class TextWatcherHelper {

    private TextWatcherHelper() {
        throw new AssertionError("TextWatcherHelper é uma classe utilitária e não deve ser instanciada.");
    }

    public interface TextFormatter {
        @NonNull String format(@NonNull String input);
    }

    public abstract static class BaseTextWatcher implements TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override public void afterTextChanged(Editable s) {}
    }
    public static class SimpleTextWatcher extends BaseTextWatcher {
        private final Runnable onChanged;

        public SimpleTextWatcher(@NonNull Runnable onChanged) {
            this.onChanged = onChanged;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            onChanged.run();
        }
    }

    public static class SearchTextWatcher extends BaseTextWatcher {
        private final int minLength;
        private final Runnable onChanged;
        private final Runnable onCleared;

        public SearchTextWatcher(int minLength, @NonNull Runnable onChanged, @NonNull Runnable onCleared) {
            if (minLength < 1) throw new IllegalArgumentException("minLength deve ser >= 1");
            this.minLength = minLength;
            this.onChanged = onChanged;
            this.onCleared = onCleared;
        }

        public SearchTextWatcher(int minLength, @NonNull Runnable onChanged) {
            this(minLength, onChanged, () -> {});
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s == null) return;
            if (s.length() == 0) onCleared.run();
            else if (s.length() >= minLength) onChanged.run();
        }
    }

    public static class FormattingTextWatcher extends BaseTextWatcher {
        private final TextFormatter formatter;
        private final Runnable onChanged;
        private boolean isUpdating = false;

        public FormattingTextWatcher(@NonNull TextFormatter formatter, @NonNull Runnable onChanged) {
            this.formatter = formatter;
            this.onChanged = onChanged;
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (isUpdating) return;
            String current = s.toString();
            String formatted = formatter.format(current);
            if (!current.equals(formatted)) {
                isUpdating = true;
                s.replace(0, s.length(), formatted);
                isUpdating = false;
            }
            onChanged.run();
        }
    }

    public static class CurrencyFormatter implements TextFormatter {
        private final BigDecimal maxValue;
        private final NumberFormat format;
        public CurrencyFormatter(@NonNull BigDecimal maxValue, @NonNull NumberFormat format) {
            this.maxValue = maxValue;
            this.format = format;
        }

        @NonNull
        @Override
        public String format(@NonNull String input) {
            String digits = input.replaceAll("[^\\d]", "");
            if (digits.isEmpty()) return "";
            BigDecimal value = new BigDecimal(digits).movePointLeft(2);
            return value.compareTo(maxValue) > 0 ? input : format.format(value);
        }
    }
    @NonNull
    public static TextWatcher simple(@NonNull Runnable onChanged) {
        return new SimpleTextWatcher(onChanged);
    }

    @NonNull
    public static TextWatcher search(int minLength, @NonNull Runnable onChanged) {
        return new SearchTextWatcher(minLength, onChanged);
    }

    @NonNull
    public static TextWatcher search(int minLength, @NonNull Runnable onChanged, @NonNull Runnable onCleared) {
        return new SearchTextWatcher(minLength, onChanged, onCleared);
    }

    @NonNull
    public static TextWatcher money(@NonNull BigDecimal maxValue, @NonNull NumberFormat format, @NonNull Runnable onChanged) {
        return new FormattingTextWatcher(new CurrencyFormatter(maxValue, format), onChanged);
    }
}