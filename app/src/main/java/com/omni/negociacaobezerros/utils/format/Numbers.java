package com.omni.negociacaobezerros.utils.format;

import static com.omni.negociacaobezerros.utils.format.Decimals.brl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;


public final class Numbers {
    private static final int DEFAULT_INT = 0;
    private static final float DEFAULT_FLOAT = 0.0f;
    private static final double DEFAULT_DOUBLE = 0.0;
    private static final String DECIMAL_PATTERN = "%.2f";
    private static final String DEFAULT_CURRENCY = "0,00";
    private static final String DEFAULT_INT_STR = "0";

    private Numbers() {
        throw new AssertionError("Numbers é uma classe utilitária e não deve ser instanciada.");
    }


    @NonNull
    public static Integer parseInt(@Nullable String value) {
        String trimmed = trimOrNull(value);
        if (trimmed == null) return DEFAULT_INT;
        try {
            return Integer.parseInt(trimmed);
        } catch (NumberFormatException ignored) {
            return DEFAULT_INT;
        }
    }

    @NonNull
    public static Float parseFloat(@Nullable String value) {
        String trimmed = trimOrNull(value);
        if (trimmed == null) return DEFAULT_FLOAT;
        try {
            return Float.parseFloat(trimmed);
        } catch (NumberFormatException ignored) {
            return DEFAULT_FLOAT;
        }
    }


    @NonNull
    public static Double parseDouble(@Nullable String value) {
        String trimmed = trimOrNull(value);
        if (trimmed == null) return DEFAULT_DOUBLE;
        try {
            return Double.parseDouble(trimmed);
        } catch (NumberFormatException ignored) {
            return DEFAULT_DOUBLE;
        }
    }


    @NonNull
    public static BigDecimal parseDecimal(@Nullable String value) {
        String trimmed = trimOrNull(value);
        if (trimmed == null) return BigDecimal.ZERO;
        try {
            DecimalFormat format = brl();
            format.setParseBigDecimal(true);
            Number parsed = format.parse(trimmed);
            return isValidBigDecimal(parsed) ? (BigDecimal) parsed : BigDecimal.ZERO;
        } catch (ParseException ignored) {
            return BigDecimal.ZERO;
        }
    }

    @NonNull
    public static BigDecimal parseDecimal(@Nullable Float value) {
        if (isNotFiniteFloat(value)) return BigDecimal.ZERO;
        return parseDecimal(value.doubleValue());
    }

    @NonNull
    public static BigDecimal parseDecimal(@Nullable Double value) {
        if (isNotFiniteDouble(value)) return BigDecimal.ZERO;
        return BigDecimal.valueOf(value);
    }

    @NonNull
    public static BigDecimal parseDecimal(@Nullable Integer value) {
        if (value == null) return BigDecimal.ZERO;
        return BigDecimal.valueOf(value);
    }

    @NonNull
    public static String formatCurrency(@Nullable BigDecimal value) {
        if (value == null) return DEFAULT_CURRENCY;
        return brl().format(value);
    }

    @NonNull
    public static String formatInteger(@Nullable Integer value) {
        if (value == null) return DEFAULT_INT_STR;
        return Integer.toString(value);
    }

    @NonNull
    public static String formatDouble(@Nullable Double value) {
        if (isNotFiniteDouble(value)) {
            return String.format(Locale.getDefault(), DECIMAL_PATTERN, DEFAULT_DOUBLE);
        }
        return String.format(Locale.getDefault(), DECIMAL_PATTERN, value);
    }

    @NonNull
    public static String formatFloat(@Nullable Float value) {
        if (isNotFiniteFloat(value)) {
            return String.format(Locale.getDefault(), DECIMAL_PATTERN, DEFAULT_FLOAT);
        }
        return String.format(Locale.getDefault(), DECIMAL_PATTERN, value);
    }

    @Nullable
    private static String trimOrNull(@Nullable String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }


    private static boolean isNotFiniteFloat(@Nullable Float value) {
        return value == null || !Float.isFinite(value);
    }


    private static boolean isNotFiniteDouble(@Nullable Double value) {
        return value == null || !Double.isFinite(value);
    }

    private static boolean isValidBigDecimal(@Nullable Number value) {
        return value instanceof BigDecimal;
    }
}