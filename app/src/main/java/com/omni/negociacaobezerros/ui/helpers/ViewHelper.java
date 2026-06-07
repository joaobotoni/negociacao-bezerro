package com.omni.negociacaobezerros.ui.helpers;

import android.content.Context;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.PluralsRes;
import androidx.annotation.StringRes;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.omni.negociacaobezerros.utils.format.Numbers;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;

public final class ViewHelper {

    private ViewHelper() {
        throw new AssertionError("ViewHelper é uma classe utilitária e não deve ser instanciada.");
    }

    public static boolean isNull(@Nullable Object value) {
        return value == null;
    }

    public static boolean isNotNull(@Nullable Object value) {
        return value != null;
    }

    @SafeVarargs
    public static <T> boolean anyNull(@Nullable T... values) {
        if (values == null) return true;
        for (T v : values) if (v == null) return true;
        return false;
    }

    @SafeVarargs
    public static <T> boolean noneNull(@Nullable T... values) {
        if (values == null) return false;
        for (T v : values) if (v == null) return false;
        return true;
    }

    public static boolean isEmpty(@Nullable String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isEmpty(@Nullable TextView value) {
        return value == null || text(value).isEmpty();
    }

    public static boolean isEmpty(@Nullable Collection<?> value) {
        return value == null || value.isEmpty();
    }

    public static boolean isEmpty(@Nullable Object[] value) {
        return value == null || value.length == 0;
    }

    public static boolean isNullOrZero(@Nullable Integer value) {
        return value == null || value == 0;
    }

    public static boolean isNullOrZero(@Nullable Double value) {
        return value == null || value == 0.0;
    }

    public static boolean isNullOrZero(@Nullable BigDecimal value) {
        return value == null || value.compareTo(BigDecimal.ZERO) == 0;
    }

    public static boolean isNotEmpty(@Nullable String value) {
        return !isEmpty(value);
    }

    public static boolean isNotEmpty(@Nullable TextView value) {
        return !isEmpty(value);
    }

    public static boolean isNotEmpty(@Nullable Collection<?> value) {
        return !isEmpty(value);
    }

    public static boolean isNotEmpty(@Nullable Object[] value) {
        return !isEmpty(value);
    }


    @NonNull
    public static <T> T orElse(@Nullable T value, @NonNull T fallback) {
        return value != null ? value : fallback;
    }


    @NonNull
    public static String text(@Nullable TextView view) {
        if (view == null || view.getText() == null) return "";
        return view.getText().toString().trim();
    }

    @NonNull
    public static Integer parseInt(@Nullable EditText view) {
        return Numbers.parseInt(text(view));
    }

    @NonNull
    public static Float parseFloat(@Nullable EditText view) {
        return Numbers.parseFloat(text(view));
    }

    @NonNull
    public static Double parseDouble(@Nullable EditText view) {
        return Numbers.parseDouble(text(view));
    }

    @NonNull
    public static BigDecimal parseDecimal(@Nullable EditText view) {
        return Numbers.parseDecimal(text(view));
    }

    public static void setText(@NonNull TextView view, @Nullable String text) {
        view.setText(text != null ? text.trim() : "");
    }

    public static void setText(@NonNull TextView view, @NonNull Context context,
                               @StringRes int resId, @NonNull Object... args) {
        for (Object arg : args) {
            if (arg == null) {
                view.setText("");
                return;
            }
        }
        view.setText(context.getString(resId, args));
    }

    public static void setPluralText(@NonNull TextView view, @NonNull Context context,
                                     @PluralsRes int resId, @Nullable Integer quantity) {
        if (quantity == null) {
            view.setText("");
            return;
        }
        view.setText(context.getResources().getQuantityString(resId, quantity, quantity));
    }

    public static void setHelperText(@NonNull TextInputLayout layout, @Nullable String text) {
        layout.setHelperText(text != null ? text.trim() : "");
    }


    public static void setTextSafely(@NonNull EditText field, @NonNull String value,
                                     @NonNull TextWatcher... watchers) {
        if (field.hasFocus()) return;
        removeWatchers(field, watchers);
        try {
            field.setText(value);
            field.setSelection(field.getText().length());
        } finally {
            addWatchers(field, watchers);
        }
    }

    public static void setTextSafely(@NonNull EditText field, @NonNull TextInputLayout layout,
                                     @NonNull String value, @NonNull String helperText,
                                     @NonNull TextWatcher... watchers) {
        setTextSafely(field, value, watchers);
        setHelperText(layout, helperText);
    }


    public static void selectChip(@NonNull ChipGroup group, @NonNull String text) {
        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);
            if (child instanceof Chip) {
                Chip chip = (Chip) child;
                if (chip.getText().toString().equals(text)) {
                    chip.setChecked(true);
                    return;
                }
            }
        }
    }

    @NonNull
    public static Optional<String> checkedChip(@NonNull ChipGroup group) {
        Chip chip = group.findViewById(group.getCheckedChipId());
        return Optional.ofNullable(chip).map(c -> c.getText().toString());
    }


    public static void setVisible(boolean visible, @NonNull View... views) {
        int state = visible ? View.VISIBLE : View.GONE;
        for (View v : views) if (v != null) v.setVisibility(state);
    }

    private static void removeWatchers(@NonNull EditText field, @NonNull TextWatcher[] watchers) {
        for (TextWatcher w : watchers) field.removeTextChangedListener(w);
    }

    private static void addWatchers(@NonNull EditText field, @NonNull TextWatcher[] watchers) {
        for (TextWatcher w : watchers) field.addTextChangedListener(w);
    }

    public static void clear(@NonNull TextView... views) {
        for (TextView v : views) if (v != null) v.setText("");
    }
}