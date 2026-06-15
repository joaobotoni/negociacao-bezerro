package com.omni.negociacaobezerros.ui.fragments.sheet;

import static com.omni.negociacaobezerros.ui.helpers.RecyclerViewHelper.setupVerticalRecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.omni.negociacaobezerros.databinding.FragmentBottomSheetCategoriaBinding;
import com.omni.negociacaobezerros.ui.adapters.CategoriaAdapter;
import com.omni.negociacaobezerros.ui.states.CategoriaUiState;
import com.omni.negociacaobezerros.ui.viewmodels.CategoriaViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CategoriaBottomSheetDialogFragment extends BottomSheetDialogFragment {
    private FragmentBottomSheetCategoriaBinding binding;
    private CategoriaAdapter adapter;
    private CategoriaViewModel categoriaViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBottomSheetCategoriaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        iniciarSetup();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
        return dialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void iniciarSetup() {
        configurarViewModel();
        configurarRecyclerViewCategoria();
        configurarObservadores();
    }

    private void configurarViewModel() {
        categoriaViewModel = new ViewModelProvider(requireActivity()).get(CategoriaViewModel.class);
        categoriaViewModel.carregar();
    }

    private void configurarObservadores() {
        observarCategoria();
    }

    private void observarCategoria() {
        categoriaViewModel.getState().observe(getViewLifecycleOwner(), this::atualizarListaCategoria);
    }

    private void atualizarListaCategoria(List<CategoriaUiState> categorias) {
        adapter.submitList(categorias);
    }

    private void configurarRecyclerViewCategoria() {
        adapter = new CategoriaAdapter(this::aoSelecionarCategoriaNaLista);
        setupVerticalRecyclerView(binding.recyclerViewCategorias, adapter, requireContext());
    }

    private void aoSelecionarCategoriaNaLista(CategoriaUiState categoriaUiState) {
        categoriaViewModel.selecionar(categoriaUiState);
        dismiss();
    }
}