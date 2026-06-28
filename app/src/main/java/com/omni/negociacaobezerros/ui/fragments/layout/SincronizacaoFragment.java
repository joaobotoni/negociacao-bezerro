package com.omni.negociacaobezerros.ui.fragments.layout;

import static com.omni.negociacaobezerros.helpers.NavigationHelper.navigateBackOnToolbar;
import static com.omni.negociacaobezerros.helpers.NavigationHelper.navigateOnClick;
import static com.omni.negociacaobezerros.helpers.ViewHelper.setVisible;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.omni.negociacaobezerros.R;
import com.omni.negociacaobezerros.databinding.FragmentSincronizacaoBinding;

public class SincronizacaoFragment extends Fragment {

    private FragmentSincronizacaoBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSincronizacaoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navigation();
        showEmptyState();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void navigation() {
        toConexao();
        back();
    }

    private void toConexao() {
        navigateOnClick(this, R.id.sincronizacaoFragment,
                SincronizacaoFragmentDirections.actionSincronizacaoFragmentToConexaoFragment(), binding.cardViewSincronizacao);
    }

    private void back() {
       navigateBackOnToolbar(this, binding.constraintLayoutSincronizacaoToolbar);
    }

    private void showEmptyState(){
       setVisible(true, binding, R.id.layout_sincronizacao_empty_state);
    }
}
