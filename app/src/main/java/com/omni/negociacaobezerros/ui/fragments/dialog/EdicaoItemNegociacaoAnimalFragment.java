package com.omni.negociacaobezerros.ui.fragments.dialog;

import static com.omni.negociacaobezerros.helpers.ViewHelper.isEmpty;
import static com.omni.negociacaobezerros.helpers.ViewHelper.parseDouble;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.omni.negociacaobezerros.databinding.FragmentEdicaoItemNegociacaoAnimalBinding;
import com.omni.negociacaobezerros.ui.viewmodels.NegociacaoAnimalViewModel;
import com.omni.negociacaobezerros.utils.format.Numbers;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EdicaoItemNegociacaoAnimalFragment extends DialogFragment {
    private static final String ARG_ID = "arg_id";
    private static final String ARG_PESO = "arg_peso";

    private FragmentEdicaoItemNegociacaoAnimalBinding binding;
    private NegociacaoAnimalViewModel negociacaoAnimalViewModel;

    public static EdicaoItemNegociacaoAnimalFragment newInstance(int id, double peso) {
        Bundle args = new Bundle();
        args.putInt(ARG_ID, id);
        args.putDouble(ARG_PESO, peso);
        EdicaoItemNegociacaoAnimalFragment fragment = new EdicaoItemNegociacaoAnimalFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        iniciarSetup();
        return new MaterialAlertDialogBuilder(requireContext())
                .setView(binding.getRoot())
                .create();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void iniciarSetup() {
        configurarViewModel();
        configurarBinding();
        preencherPesoAtual();
        configurarBotaoConfirmar();
    }

    private void configurarViewModel() {
        negociacaoAnimalViewModel = new ViewModelProvider(requireActivity()).get(NegociacaoAnimalViewModel.class);
    }

    private void configurarBinding() {
        binding = FragmentEdicaoItemNegociacaoAnimalBinding.inflate(LayoutInflater.from(requireContext()));
    }

    private void preencherPesoAtual() {
        binding.textInputEditTextEdicaoItemPeso.setText(Numbers.formatDouble(lerPesoArgumento()));
    }

    private void configurarBotaoConfirmar() {
        binding.buttonEdicaoItemConfirmar.setOnClickListener(v -> onConfirmarClicked());
    }

    private void onConfirmarClicked() {
        if (isEmpty(binding.textInputEditTextEdicaoItemPeso)) return;
        negociacaoAnimalViewModel.editar(lerIdArgumento(), parseDouble(binding.textInputEditTextEdicaoItemPeso));
        dismiss();
    }

    private int lerIdArgumento() {
        return requireArguments().getInt(ARG_ID);
    }

    private double lerPesoArgumento() {
        return requireArguments().getDouble(ARG_PESO);
    }
}
