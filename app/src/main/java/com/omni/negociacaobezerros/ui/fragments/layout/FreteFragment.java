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
import com.omni.negociacaobezerros.databinding.FragmentFreteBinding;
import com.omni.negociacaobezerros.ui.helpers.NavigationHelper;

public class FreteFragment extends Fragment {
    private FragmentFreteBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFreteBinding.inflate(inflater, container, false);
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
        toNegociacao();
        back();
    }

    private void toNegociacao() {
        NavigationHelper.navigateOnClick(this, R.id.freteFragment,
                FreteFragmentDirections.actionFreteFragmentToNegociacaoFragment(), binding.buttonFreteFinalizar);
    }

    private void back() {
        NavigationHelper.navigateBackOnToolbar(this, binding.constraintLayoutFreteToolbar);
    }
}
