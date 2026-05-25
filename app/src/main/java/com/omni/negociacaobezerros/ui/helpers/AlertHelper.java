package com.omni.negociacaobezerros.ui.helpers;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.omni.negociacaobezerros.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

public final class AlertHelper {

    private static final int SNACKBAR_TOP_MARGIN_DP = 20;

    private AlertHelper() {
        throw new AssertionError("AlertHelper é uma classe utilitária e não deve ser instanciada.");
    }
    public static void showSnackBarSucesso(View view, String message) {
        showSnackBar(view, message, android.R.color.holo_green_dark, Gravity.BOTTOM);
    }

    public static void showSnackBarErro(View view, String message) {
        showSnackBar(view, message, android.R.color.holo_red_dark, Gravity.BOTTOM);
    }

    public static void showSnackBarSucessoTop(View view, String message) {
        showSnackBar(view, message, android.R.color.holo_green_dark, Gravity.TOP);
    }
    public static void showSnackBarErroTop(View view, String message) {
        showSnackBar(view, message, android.R.color.holo_red_dark, Gravity.TOP);
    }

    public static void showDialog(
            Context context,
            String title,
            String message,
            DialogInterface.OnClickListener positiveListener,
            DialogInterface.OnClickListener negativeListener
    ) {
        if (!isDialogValid(context, title, message)) return;

        new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.dialogo_botao_positivo), positiveListener)
                .setNegativeButton(context.getString(R.string.dialogo_botao_negativo), negativeListener)
                .show();
    }

    private static void showSnackBar(View view, String message, int colorRes, int gravity) {
        if (!isSnackBarValid(view, message)) return;

        Snackbar snackbar = buildSnackBar(view, message, colorRes);
        applyGravity(snackbar, view, gravity);
        snackbar.show();
    }

    private static Snackbar buildSnackBar(View view, String message, int colorRes) {
        return Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(ContextCompat.getColor(view.getContext(), colorRes))
                .setTextColor(Color.WHITE);
    }

    private static void applyGravity(Snackbar snackbar, View anchorView, int gravity) {
        View snackbarView = snackbar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
        params.gravity = gravity | Gravity.CENTER_HORIZONTAL;

        if (isTopGravity(gravity)) {
            params.topMargin = getTopMargin(anchorView);
        }

        snackbarView.setLayoutParams(params);
    }

    private static int getTopMargin(View view) {
        return getStatusBarHeight(view) + dpToPx(view.getContext(), SNACKBAR_TOP_MARGIN_DP);
    }

    private static int getStatusBarHeight(View view) {
        WindowInsetsCompat insets = ViewCompat.getRootWindowInsets(view);
        if (!hasValidInsets(insets)) return 0;
        return insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
    }

    private static int dpToPx(Context context, int dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }


    private static boolean isSnackBarValid(View view, String message) {
        return view != null && message != null;
    }

    private static boolean isDialogValid(Context context, String title, String message) {
        return context != null && title != null && message != null;
    }

    private static boolean isTopGravity(int gravity) {
        return (gravity & Gravity.TOP) == Gravity.TOP;
    }

    private static boolean hasValidInsets(WindowInsetsCompat insets) {
        return insets != null;
    }
}