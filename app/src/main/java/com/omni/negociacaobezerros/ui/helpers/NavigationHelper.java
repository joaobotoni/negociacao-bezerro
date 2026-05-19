package com.omni.negociacaobezerros.ui.helpers;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

public final class NavigationHelper {

    private NavigationHelper() {
        throw new AssertionError("NavigationHelper é uma classe utilitária e não deve ser instanciada.");
    }

    public static void navegar(@NonNull Fragment fragment, @IdRes int destinoAtual, @NonNull NavDirections direcoes) {
        NavController controller = NavHostFragment.findNavController(fragment);
        NavDestination atual = controller.getCurrentDestination();
        if (atual != null && atual.getId() == destinoAtual) {
            controller.navigate(direcoes);
        }
    }

    public static void voltar(@NonNull Fragment fragment) {
        NavController controller = NavHostFragment.findNavController(fragment);
        if (controller.getPreviousBackStackEntry() != null) {
            controller.popBackStack();
        }
    }
}
