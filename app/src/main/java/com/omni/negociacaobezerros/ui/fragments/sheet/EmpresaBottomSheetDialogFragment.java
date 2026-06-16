package com.omni.negociacaobezerros.ui.fragments.sheet;

import static com.omni.negociacaobezerros.helpers.RecyclerViewHelper.setupVerticalRecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.omni.negociacaobezerros.databinding.FragmentBottomSheetEmpresaBinding;
import com.omni.negociacaobezerros.ui.adapters.EmpresaAdapter;
import com.omni.negociacaobezerros.ui.states.EmpresaUiState;
import com.omni.negociacaobezerros.ui.viewmodels.EmpresaViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EmpresaBottomSheetDialogFragment extends BottomSheetDialogFragment {
    private FragmentBottomSheetEmpresaBinding binding;
    private EmpresaAdapter adapter;
    private EmpresaViewModel empresaViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBottomSheetEmpresaBinding.inflate(inflater, container, false);
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
        configurarRecyclerViewEmpresa();
        configurarObservadores();
    }

    private void configurarViewModel() {
        empresaViewModel = new ViewModelProvider(requireActivity()).get(EmpresaViewModel.class);
        empresaViewModel.carregar();
    }

    private void configurarObservadores() {
        observarEmpresa();
    }

    private void observarEmpresa() {
        empresaViewModel.getState().observe(getViewLifecycleOwner(), this::atualizarListaEmpresa);
    }

    private void atualizarListaEmpresa(List<EmpresaUiState> categorias) {
        adapter.submitList(categorias);
    }

    private void configurarRecyclerViewEmpresa() {
        adapter = new EmpresaAdapter(this::aoSelecionarEmpresaNaLista);
        setupVerticalRecyclerView(binding.recyclerViewEmpresas, adapter, requireContext());
    }

    private void aoSelecionarEmpresaNaLista(EmpresaUiState empresaUiState) {
        empresaViewModel.selecionar(empresaUiState);
        dismiss();
    }
}
