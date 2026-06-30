package com.omni.negociacaobezerros.ui.fragments.layout;


import static com.omni.negociacaobezerros.helpers.NavigationHelper.navigateBackOnToolbar;
import static com.omni.negociacaobezerros.helpers.NavigationHelper.navigateOnClick;
import static com.omni.negociacaobezerros.helpers.ViewHelper.checkedChip;
import static com.omni.negociacaobezerros.helpers.ViewHelper.isNotEmpty;
import static com.omni.negociacaobezerros.helpers.ViewHelper.parseDecimal;
import static com.omni.negociacaobezerros.helpers.ViewHelper.parseInt;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.omni.negociacaobezerros.R;
import com.omni.negociacaobezerros.databinding.FragmentCotacaoBinding;
import com.omni.negociacaobezerros.helpers.AlertHelper;
import com.omni.negociacaobezerros.helpers.TextWatcherHelper;
import com.omni.negociacaobezerros.ui.states.CotacaoUiState;
import com.omni.negociacaobezerros.ui.states.EmpresaUiState;
import com.omni.negociacaobezerros.ui.viewmodels.CotacaoViewModel;
import com.omni.negociacaobezerros.ui.viewmodels.EmpresaViewModel;
import com.omni.negociacaobezerros.utils.format.Numbers;

import java.math.BigDecimal;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CotacaoFragment extends Fragment {
    private FragmentCotacaoBinding binding;
    private CotacaoViewModel cotacaoViewModel;
    private EmpresaViewModel empresaViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCotacaoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewModels();
        navigation();
        setupListeners();
        observeUiState();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initViewModels() {
        cotacaoViewModel = new ViewModelProvider(requireActivity()).get(CotacaoViewModel.class);
        empresaViewModel = new ViewModelProvider(requireActivity()).get(EmpresaViewModel.class);
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

    private void setupListeners() {
        binding.textInputEditTextCotacaoPeso.addTextChangedListener(TextWatcherHelper.simple(this::tentarCalcular));
        binding.textInputEditTextCotacaoQuantidade.addTextChangedListener(TextWatcherHelper.simple(this::tentarCalcular));
        binding.chipCotacaoGroupSexo.setOnCheckedStateChangeListener((group, checkedIds) -> onSexoAlterado());
    }

    private void tentarCalcular() {
        if (!isFormularioPreenchido()) return;
        cotacaoViewModel.calcular(lerPeso(), lerQuantidade());
    }

    private boolean isFormularioPreenchido() {
        return isNotEmpty(binding.textInputEditTextCotacaoPeso) && isNotEmpty(binding.textInputEditTextCotacaoQuantidade);
    }

    private BigDecimal lerPeso() {
        return parseDecimal(binding.textInputEditTextCotacaoPeso);
    }

    private Integer lerQuantidade() {
        return parseInt(binding.textInputEditTextCotacaoQuantidade);
    }

    private void onSexoAlterado() {
        checkedChip(binding.chipCotacaoGroupSexo).ifPresent(cotacaoViewModel::setSexo);
    }

    private void observeUiState() {
        observeEmpresaSelecionada();
        observeCotacaoState();
        observeErro();
    }

    private void observeEmpresaSelecionada() {
        empresaViewModel.getSelecionada().observe(getViewLifecycleOwner(), this::onEmpresaSelecionadaChanged);
    }

    private void onEmpresaSelecionadaChanged(EmpresaUiState empresa) {
        binding.textViewCotacaoCompradorNome.setText(isEmpresaSelecionada(empresa)
                ? empresa.getNome() : getString(R.string.placeholder_comprador_nome));
    }

    private boolean isEmpresaSelecionada(EmpresaUiState empresa) {
        return empresa != null;
    }

    private void observeCotacaoState() {
        cotacaoViewModel.getCotacaoState().observe(getViewLifecycleOwner(), this::onCotacaoStateChanged);
    }

    private void onCotacaoStateChanged(CotacaoUiState state) {
        bindCotacaoTabela(state);
        binding.buttonCotacaoProximo.setEnabled(isCotacaoCalculada(state));
    }

    private boolean isCotacaoCalculada(CotacaoUiState state) {
        return state != null;
    }

    private void bindCotacaoTabela(CotacaoUiState state) {
        if (!isCotacaoCalculada(state)) return;
        binding.textViewCotacaoTabelaQuiloValor.setText(Numbers.formatCurrency(state.getValorPorQuilo()));
        binding.textViewCotacaoTabelaCabecaValor.setText(Numbers.formatCurrency(state.getValorPorCabeca()));
        binding.textViewCotacaoTabelaTotalValor.setText(Numbers.formatCurrency(state.getValorTotal()));
    }

    private void observeErro() {
        cotacaoViewModel.getErro().observe(getViewLifecycleOwner(), this::onErroChanged);
    }

    private void onErroChanged(Throwable erro) {
        if (erro == null) return;
        AlertHelper.showSnackBarErro(binding.getRoot(), erro.getMessage());
    }
}
