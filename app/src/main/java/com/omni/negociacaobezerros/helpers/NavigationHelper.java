package com.omni.negociacaobezerros.helpers;

import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.appbar.MaterialToolbar;

public final class NavigationHelper {

    public interface OnMenuItemSelectedListener {
        boolean onMenuItemSelected(@IdRes int itemId);
    }

    private NavigationHelper() {
        throw new AssertionError("NavigationHelper é uma classe utilitária e não deve ser instanciada.");
    }

    public static void setupMenuItems(@NonNull MaterialToolbar toolbar, @NonNull OnMenuItemSelectedListener listener) {
        toolbar.setOnMenuItemClickListener(item -> listener.onMenuItemSelected(item.getItemId()));
    }

    public static void navigateOnMenuItem(@NonNull Fragment fragment, @IdRes int destinoAtual,
                                          @NonNull NavDirections direcoes,
                                          @NonNull MaterialToolbar toolbar,
                                          @IdRes int menuItemId) {
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == menuItemId) {
                navigate(fragment, destinoAtual, direcoes);
                return true;
            }
            return false;
        });
    }

    public static void navigateOnClick(@NonNull Fragment fragment, @IdRes int destinoAtual, @NonNull NavDirections direcoes, @NonNull View view) {
        view.setOnClickListener(v -> navigate(fragment, destinoAtual, direcoes));
    }

    public static void navigate(@NonNull Fragment fragment, @IdRes int destinoAtual, @NonNull NavDirections direcoes) {
        NavController navController = findNavController(fragment);
        navigateIfCurrentDestination(navController, destinoAtual, direcoes);
    }

    public static void navigateBackOnToolbar(@NonNull Fragment fragment, @NonNull MaterialToolbar toolbar) {
        toolbar.setNavigationOnClickListener(v -> back(fragment));
    }

    public static void navigateBackOnClick(@NonNull Fragment fragment, @NonNull View view) {
        view.setOnClickListener(v -> back(fragment));
    }

    public static void back(@NonNull Fragment fragment) {
        NavController controller = findNavController(fragment);
        if (controller.getPreviousBackStackEntry() != null) {
            controller.popBackStack();
        }
    }

    private static NavController findNavController(@NonNull Fragment fragment) {
        return NavHostFragment.findNavController(fragment);
    }

    private static void navigateIfCurrentDestination(@NonNull NavController navController, @IdRes int destinoAtual, @NonNull NavDirections direcoes) {
        NavDestination currentDestination = navController.getCurrentDestination();
        if (currentDestination != null && currentDestination.getId() == destinoAtual) {
            navController.navigate(direcoes);
        }
    }
}