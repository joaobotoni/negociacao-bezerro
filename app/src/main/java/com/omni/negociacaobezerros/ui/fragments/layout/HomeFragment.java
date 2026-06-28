package com.omni.negociacaobezerros.ui.fragments.layout;

import static com.omni.negociacaobezerros.helpers.NavigationHelper.navigate;
import static com.omni.negociacaobezerros.helpers.NavigationHelper.navigateBackOnToolbar;
import static com.omni.negociacaobezerros.helpers.NavigationHelper.navigateOnClick;
import static com.omni.negociacaobezerros.helpers.NavigationHelper.setupMenuItems;
import static com.omni.negociacaobezerros.helpers.ViewHelper.setVisible;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.omni.negociacaobezerros.R;
import com.omni.negociacaobezerros.databinding.FragmentHomeBinding;
import com.omni.negociacaobezerros.ui.fragments.sheet.EmpresaBottomSheetDialogFragment;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;

    private static final String TAG_BOTTOM_SHEET_EMPRESA = "EmpresaBottomSheet";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inicializar();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void inicializar(){
        navigation();
        showEmptyState();
        configurarEventosDeClique();
    }

    private void navigation() {
        toMenu();
        toCotacao();
        back();
    }

    private void configurarEventosDeClique() {
        binding.cardViewHomeSelecionarEmpresa.setOnClickListener(v -> onCliqueEmpresa());
    }

    private void toMenu() {
        setupMenuItems(binding.constraintLayoutHomeToolbar, itemId -> {
            if (itemId == R.id.menu_home_sincronizacao) {
                navigate(this, R.id.homeFragment, HomeFragmentDirections.actionHomeFragmentToSincronizacaoFragment());
                return true;
            }
            if (itemId == R.id.menu_home_conexao) {
                navigate(this, R.id.homeFragment, HomeFragmentDirections.actionHomeFragmentToConexaoFragment());
                return true;
            }
            return false;
        });
    }

    private void toCotacao() {
        navigateOnClick(this, R.id.homeFragment,
                HomeFragmentDirections.actionHomeFragmentToCotacaoFragment(), binding.buttonHomeProximo);
    }

    private void back() {
       navigateBackOnToolbar(this, binding.constraintLayoutHomeToolbar);
    }

    private void showEmptyState() {
        setVisible(true, binding, R.id.layout_home_empty_state);
    }

    private void onCliqueEmpresa() {
        exibirBottomSheetEmpresa();
    }

    private void exibirBottomSheetEmpresa() {
        FragmentManager fm = getChildFragmentManager();
        if (isBottomSheetEmpresaVisivel(fm)) return;
        new EmpresaBottomSheetDialogFragment().show(fm, TAG_BOTTOM_SHEET_EMPRESA);
    }

    private boolean isBottomSheetEmpresaVisivel(@NonNull FragmentManager fm) {
        return fm.findFragmentByTag(TAG_BOTTOM_SHEET_EMPRESA) != null;
    }
}
