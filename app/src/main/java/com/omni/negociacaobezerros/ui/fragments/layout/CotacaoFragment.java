package com.omni.negociacaobezerros.ui.fragments.layout;


import static com.omni.negociacaobezerros.helpers.NavigationHelper.navigateBackOnToolbar;
import static com.omni.negociacaobezerros.helpers.NavigationHelper.navigateOnClick;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.omni.negociacaobezerros.R;
import com.omni.negociacaobezerros.databinding.FragmentCotacaoBinding;

public class CotacaoFragment extends Fragment {
    private FragmentCotacaoBinding binding;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCotacaoBinding.inflate(inflater, container, false);
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

    private void navigation(){
        toNegociacao();
        back();
    }

    private void toNegociacao() {
        navigateOnClick(this, R.id.cotacaoFragment,
                CotacaoFragmentDirections.actionCotacaoFragmentToNegociacaoFragment(),
                binding.buttonCotacaoProximo);
    }

    private void back() {
        navigateBackOnToolbar(this, binding.constraintLayoutCotacaoToolbar);
    }

}
