package com.omni.negociacaobezerros.ui.fragments.layout;

import static com.omni.negociacaobezerros.helpers.NavigationHelper.navigateBackOnToolbar;
import static com.omni.negociacaobezerros.helpers.NavigationHelper.navigateOnClick;
import static com.omni.negociacaobezerros.helpers.ViewHelper.isEmpty;
import static com.omni.negociacaobezerros.helpers.ViewHelper.parseDecimal;
import static com.omni.negociacaobezerros.helpers.ViewHelper.setTextSafely;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.omni.negociacaobezerros.R;
import com.omni.negociacaobezerros.databinding.FragmentNegociacaoBinding;
import com.omni.negociacaobezerros.helpers.AlertHelper;
import com.omni.negociacaobezerros.helpers.TextWatcherHelper;
import com.omni.negociacaobezerros.ui.fragments.sheet.CorretorBottomSheetDialogFragment;
import com.omni.negociacaobezerros.ui.states.CorretorUiState;
import com.omni.negociacaobezerros.ui.states.CotacaoUiState;
import com.omni.negociacaobezerros.ui.states.FreteUiState;
import com.omni.negociacaobezerros.ui.states.PrecificacaoBezerroUiState;
import com.omni.negociacaobezerros.ui.viewmodels.CorretorViewModel;
import com.omni.negociacaobezerros.ui.viewmodels.CotacaoViewModel;
import com.omni.negociacaobezerros.ui.viewmodels.FreteViewModel;
import com.omni.negociacaobezerros.ui.viewmodels.NegociacaoViewModel;
import com.omni.negociacaobezerros.utils.format.Numbers;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NegociacaoFragment extends Fragment {
    private static final String TAG_BOTTOM_SHEET_CORRETOR = "CorretorBottomSheet";
    private static final DecimalFormat FORMATO_DECIMAL = new DecimalFormat("#,##0.00", new DecimalFormatSymbols(Locale.getDefault()));

    private FragmentNegociacaoBinding binding;
    private CotacaoViewModel cotacaoViewModel;
    private FreteViewModel freteViewModel;
    private CorretorViewModel corretorViewModel;
    private NegociacaoViewModel negociacaoViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNegociacaoBinding.inflate(inflater, container, false);
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
        freteViewModel = new ViewModelProvider(requireActivity()).get(FreteViewModel.class);
        corretorViewModel = new ViewModelProvider(requireActivity()).get(CorretorViewModel.class);
        negociacaoViewModel = new ViewModelProvider(requireActivity()).get(NegociacaoViewModel.class);
    }

    private void navigation(){
        toFrete();
        toNegociacaoAnimal();
        toFinalizacao();
        back();
    }

    private void toFrete() {
        navigateOnClick(this, R.id.negociacaoFragment,
                NegociacaoFragmentDirections.actionNegociacaoFragmentToFreteFragment(), binding.cardViewSimularFrete);
    }

    private void toNegociacaoAnimal() {
        navigateOnClick(this, R.id.negociacaoFragment,
                NegociacaoFragmentDirections.actionNegociacaoFragmentToNegociacaoAnimalFragment(), binding.buttonNegociacaoProximo);
    }

    private void toFinalizacao() {
       navigateOnClick(this, R.id.negociacaoFragment,
                NegociacaoFragmentDirections.actionNegociacaoFragmentToFinalizacaoFragment(), binding.buttonNegociacaoFinalizar);
    }
    private void back() {
        navigateBackOnToolbar(this, binding.constraintLayoutNegociacaoToolbar);
    }

    private void setupListeners() {
        binding.cardViewSelecionarCorretor.setOnClickListener(v -> abrirCorretorBottomSheet());
        binding.editValorKg.addTextChangedListener(TextWatcherHelper.simple(this::onValorKgEditadoManualmente));
        binding.editValorCabeca.addTextChangedListener(TextWatcherHelper.simple(this::onValorCabecaEditadoManualmente));
        binding.editValorFrete.addTextChangedListener(TextWatcherHelper.simple(this::onValorFreteEditadoManualmente));
    }

    private void abrirCorretorBottomSheet() {
        FragmentManager fm = getChildFragmentManager();
        if (fm.findFragmentByTag(TAG_BOTTOM_SHEET_CORRETOR) != null) return;
        new CorretorBottomSheetDialogFragment().show(fm, TAG_BOTTOM_SHEET_CORRETOR);
    }

    private void onValorKgEditadoManualmente() {
        if (isEmpty(binding.editValorKg) || !binding.editValorKg.hasFocus()) return;
        negociacaoViewModel.recalcularPorKgManual(parseDecimal(binding.editValorKg));
    }

    private void onValorCabecaEditadoManualmente() {
        if (isEmpty(binding.editValorCabeca) || !binding.editValorCabeca.hasFocus()) return;
        negociacaoViewModel.recalcularPorCabecaManual(parseDecimal(binding.editValorCabeca));
    }

    private void onValorFreteEditadoManualmente() {
        if (isEmpty(binding.editValorFrete) || !binding.editValorFrete.hasFocus()) return;
        negociacaoViewModel.setFrete(parseDecimal(binding.editValorFrete));
    }

    private void observeUiState() {
        observeCotacao();
        observeFrete();
        observeCorretor();
        observeProposta();
        observeFechamento();
        observeVariacao();
        observeBotaoAtivo();
        observeErro();
    }

    private void observeCotacao() {
        cotacaoViewModel.getCotacaoState().observe(getViewLifecycleOwner(), this::onCotacaoStateChanged);
        cotacaoViewModel.getPeso().observe(getViewLifecycleOwner(), negociacaoViewModel::setPeso);
        cotacaoViewModel.getQuantidade().observe(getViewLifecycleOwner(), negociacaoViewModel::setQuantidade);
    }

    private void onCotacaoStateChanged(CotacaoUiState state) {
        if (state == null) return;
        binding.textoValorKg.setText(formatarDecimal(state.getValorPorQuilo()));
        binding.textoValorCab.setText(formatarDecimal(state.getValorPorCabeca()));
    }

    private void observeFrete() {
        freteViewModel.getFreteState().observe(getViewLifecycleOwner(), this::onFreteStateChanged);
    }

    private void onFreteStateChanged(FreteUiState state) {
        String helper = getString(R.string.frete_helper, formatarDecimal(valorPorKgOuZero(state)));
        setTextSafely(binding.editValorFrete, binding.layoutInputValorFrete, formatarDecimal(valorPorKgOuZero(state)), helper);
        negociacaoViewModel.setFrete(valorPorKgOuZero(state));
    }

    private BigDecimal valorPorKgOuZero(FreteUiState state) {
        return state != null ? state.getValorPorKg() : BigDecimal.ZERO;
    }

    private void observeCorretor() {
        corretorViewModel.getSelecionado().observe(getViewLifecycleOwner(), this::onCorretorSelecionadoChanged);
    }

    private void onCorretorSelecionadoChanged(CorretorUiState corretor) {
        binding.textViewCorretorTitle.setText(isCorretorSelecionado(corretor)
                ? corretor.getNome() : getString(R.string.negociacao_label_sem_corretor));
        binding.textViewCorretorDescricao.setText(isCorretorSelecionado(corretor)
                ? Numbers.formatCurrency(corretor.getComissao()) : getString(R.string.negociacao_descricao_selecionar_corretor));
        negociacaoViewModel.setComissao(isCorretorSelecionado(corretor) ? corretor.getComissao() : BigDecimal.ZERO);
    }

    private boolean isCorretorSelecionado(CorretorUiState corretor) {
        return corretor != null;
    }

    private void observeProposta() {
        negociacaoViewModel.getPropostaState().observe(getViewLifecycleOwner(), this::onPropostaStateChanged);
    }

    private void onPropostaStateChanged(PrecificacaoBezerroUiState proposta) {
        if (proposta == null) return;
        binding.textoValorEtapaPedido.setText(Numbers.formatCurrency(proposta.getValorPorCabeca()));
        binding.textoDescricaoEtapaPedido.setText(getString(R.string.placeholder_valor_por_kg, formatarDecimal(proposta.getValorPorQuilo())));
        setTextSafely(binding.editValorKg, formatarDecimal(proposta.getValorPorQuilo()));
        setTextSafely(binding.editValorCabeca, formatarDecimal(proposta.getValorPorCabeca()));
    }

    private void observeFechamento() {
        negociacaoViewModel.getFechamentoState().observe(getViewLifecycleOwner(), this::onFechamentoStateChanged);
    }

    private void onFechamentoStateChanged(PrecificacaoBezerroUiState fechamento) {
        if (fechamento == null) return;
        binding.textoValorEtapaFinal.setText(Numbers.formatCurrency(fechamento.getValorPorCabeca()));
        binding.textoDescricaoEtapaFinal.setText(getString(R.string.placeholder_valor_por_kg, formatarDecimal(fechamento.getValorPorQuilo())));
    }

    private void observeVariacao() {
        negociacaoViewModel.getVariacaoPercentual().observe(getViewLifecycleOwner(), this::onVariacaoChanged);
    }

    private void onVariacaoChanged(BigDecimal variacao) {
        if (variacao == null) return;
        binding.textoValorDistancia.setText(getString(R.string.formato_percentual, formatarDecimal(variacao)));
    }

    private void observeBotaoAtivo() {
        negociacaoViewModel.getBotaoAtivo().observe(getViewLifecycleOwner(), this::onBotaoAtivoChanged);
    }

    private void onBotaoAtivoChanged(Boolean ativo) {
        binding.buttonNegociacaoProximo.setEnabled(Boolean.TRUE.equals(ativo));
        binding.buttonNegociacaoFinalizar.setEnabled(Boolean.TRUE.equals(ativo));
    }

    private void observeErro() {
        negociacaoViewModel.getErro().observe(getViewLifecycleOwner(), this::onErroChanged);
    }

    private void onErroChanged(Throwable erro) {
        if (erro == null) return;
        AlertHelper.showSnackBarErro(binding.getRoot(), erro.getMessage());
    }

    private String formatarDecimal(BigDecimal valor) {
        return valor != null ? FORMATO_DECIMAL.format(valor) : FORMATO_DECIMAL.format(BigDecimal.ZERO);
    }
}
