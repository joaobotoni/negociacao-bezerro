package com.omni.negociacaobezerros.ui.fragments.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import com.omni.negociacaobezerros.R;
import com.omni.negociacaobezerros.databinding.FragmentHomeBinding;
import com.omni.negociacaobezerros.ui.helpers.NavigationHelper;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navigate();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void navigate(){
        toMenu();
        toCotacao();
        back();
    }


    private void toMenu() {
        NavigationHelper.setupMenuItems(binding.constraintLayoutHomeToolbar, itemId -> {
            if (itemId == R.id.menu_home_sincronizacao) {
                NavigationHelper.navigate(this, R.id.homeFragment,
                        HomeFragmentDirections.actionHomeFragmentToSincronizacaoFragment());
                return true;
            }
            if (itemId == R.id.menu_home_conexao) {
                NavigationHelper.navigate(this, R.id.homeFragment,
                        HomeFragmentDirections.actionHomeFragmentToConexaoFragment());
                return true;
            }
            return false;
        });
    }

    private void toCotacao() {
        NavigationHelper.navigateOnClick(this, R.id.homeFragment,
                HomeFragmentDirections.actionHomeFragmentToCotacaoFragment(), binding.buttonHomeProximo);
    }

    private void back() {
        NavigationHelper.navigateBackOnToolbar(this, binding.constraintLayoutHomeToolbar);
    }
}
