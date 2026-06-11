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
import com.omni.negociacaobezerros.databinding.FragmentSincronizacaoBinding;
import com.omni.negociacaobezerros.ui.helpers.NavigationHelper;

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
        navigate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void navigate() {
        toConexao();
        back();
    }

    private void toConexao() {
        NavigationHelper.navigateOnClick(this, R.id.sincronizacaoFragment,
                SincronizacaoFragmentDirections.actionSincronizacaoFragmentToConexaoFragment(), binding.cardViewSincronizacao);
    }


    private void back() {
        NavigationHelper.navigateBackOnToolbar(this, binding.constraintLayoutSincronizacaoToolbar);
    }

}
