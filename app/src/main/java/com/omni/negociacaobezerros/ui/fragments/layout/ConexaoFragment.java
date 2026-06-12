package com.omni.negociacaobezerros.ui.fragments.layout;

import static com.omni.negociacaobezerros.ui.helpers.NavigationHelper.navigateBackOnToolbar;
import static com.omni.negociacaobezerros.ui.helpers.NavigationHelper.navigateOnMenuItem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.omni.negociacaobezerros.R;
import com.omni.negociacaobezerros.databinding.FragmentConexaoBinding;
import com.omni.negociacaobezerros.ui.helpers.TextWatcherHelper;

public class ConexaoFragment extends Fragment {
    private FragmentConexaoBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentConexaoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navigation();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void navigation() {
        toSincronizacao();
        back();
    }

    private void toSincronizacao() {
        navigateOnMenuItem(this, R.id.conexaoFragment,
                ConexaoFragmentDirections.actionConexaoFragmentToSincronizacaoFragment(),
                binding.constraintLayoutConexaoToolbar, R.id.menu_conexao_sincronizacao);
    }

    private void back() {
       navigateBackOnToolbar(this, binding.constraintLayoutConexaoToolbar);
    }
}
