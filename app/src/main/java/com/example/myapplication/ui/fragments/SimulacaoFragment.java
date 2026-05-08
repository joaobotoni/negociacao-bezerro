package com.example.myapplication.ui.fragments;

import static com.example.myapplication.ui.helpers.FormatHelper.formatCurrency;
import static com.example.myapplication.ui.helpers.FormatHelper.formatInteger;
import static com.example.myapplication.ui.helpers.TextWatcherHelper.SimpleTextWatcher;
import static com.example.myapplication.ui.helpers.ViewHelper.anyEmpty;
import static com.example.myapplication.ui.helpers.ViewHelper.getFloat;
import static com.example.myapplication.ui.helpers.ViewHelper.getInt;
import static com.example.myapplication.ui.helpers.ViewHelper.setText;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentSimulacaoBinding;
import com.example.myapplication.ui.state.SimulacaoState;
import com.example.myapplication.ui.viewmodel.SimulacaoViewModel;

import java.math.BigDecimal;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SimulacaoFragment extends Fragment {
    private FragmentSimulacaoBinding binding;
    private SimulacaoViewModel simulacaoViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSimulacaoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inicializarDependencias();
        configurarComportamentosDeTela();
        observarEstadoDaViewModel();
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }

    private void inicializarDependencias() {
        simulacaoViewModel = new ViewModelProvider(requireActivity()).get(SimulacaoViewModel.class);
    }

    private void configurarComportamentosDeTela() {
        processarNovosValores();
        configurarAcaoBotaoProsseguir();
    }

    private void processarNovosValores() {
        binding.campoPesoEntrada.addTextChangedListener(SimpleTextWatcher(this::lidarComMudancaDeEntrada));
        binding.campoQuantidadeEntrada.addTextChangedListener(SimpleTextWatcher(this::lidarComMudancaDeEntrada));
    }

    private void lidarComMudancaDeEntrada() {
        atualizarEstadoDoBotaoProsseguir();
        simularNegociacao();
    }

    private void atualizarEstadoDoBotaoProsseguir() {
        boolean habilitado = entradasSaoValidas();
        definirEstadoHabilitadoDoBotao(habilitado);
    }

    private void definirEstadoHabilitadoDoBotao(boolean habilitado) {
        binding.botaoProsseguir.setEnabled(habilitado);
    }

    private void configurarAcaoBotaoProsseguir() {
        binding.botaoProsseguir.setOnClickListener(v -> processarTentativaDeProsseguir());
    }

    private void observarEstadoDaViewModel() {
        simulacaoViewModel.getState().observe(getViewLifecycleOwner(), this::atualizarCartao);
    }

    private void simularNegociacao() {
        if (!entradasSaoValidas()) {
            simulacaoViewModel.limpar();
            return;
        }
        int quantidade = obterCargaTotal();
        BigDecimal peso = new BigDecimal(obterPesoMedio());
        simulacaoViewModel.simular(peso, quantidade);
    }

    private void processarTentativaDeProsseguir() {
        if (!entradasSaoValidas()) {
            return;
        }
        navegarParaNegociacao();
    }

    private boolean entradasSaoValidas() {
        return !anyEmpty(binding.campoQuantidadeEntrada, binding.campoPesoEntrada);
    }

    private int obterCargaTotal() {
        return getInt(binding.campoQuantidadeEntrada);
    }

    private float obterPesoMedio() {
        return getFloat(binding.campoPesoEntrada);
    }

    private void atualizarCartao(SimulacaoState estado) {
        if (estado == null) {
            limparCartao();
            return;
        }

        atualizarValorTotal(estado);
        atualizarQuantidade(estado);
        atualizarValorPorCabeca(estado);
        atualizarValorPorKg(estado);
    }

    private void limparCartao() {
        exibirValorTotal(getString(R.string.placeholder_valor_total));
        exibirQuantidade(getString(R.string.placeholder_valor_quantidade));
        exibirValorPorCabeca(getString(R.string.placeholder_valor_monetario));
        exibirValorPorKg(getString(R.string.placeholder_valor_monetario));
    }

    private void atualizarValorTotal(@NonNull SimulacaoState estado) {
        String valorFormatado = formatCurrency(estado.getValorTotal());
        exibirValorTotal(valorFormatado);
    }

    private void atualizarQuantidade(@NonNull SimulacaoState estado) {
        String quantidadeFormatada = formatInteger(estado.getQuantidade());
        exibirQuantidade(quantidadeFormatada);
    }

    private void atualizarValorPorCabeca(@NonNull SimulacaoState estado) {
        String valorFormatado = formatCurrency(estado.getValorPorCabeca());
        exibirValorPorCabeca(valorFormatado);
    }

    private void atualizarValorPorKg(@NonNull SimulacaoState estado) {
        String valorFormatado = formatCurrency(estado.getValorPorKg());
        exibirValorPorKg(valorFormatado);
    }

    private void exibirValorTotal(String total) {
        setText(binding.textoValorTotalDestacado, total);
    }

    private void exibirQuantidade(String quantidade) {
        setText(binding.textoValorQuantidade, quantidade);
    }

    private void exibirValorPorCabeca(String porCabeca) {
        setText(binding.textoValorPorCabeca, porCabeca);
    }

    private void exibirValorPorKg(String porKg) {
        setText(binding.textoValorPorKg, porKg);
    }

    private void navegarParaNegociacao() {
        SimulacaoFragmentDirections.ActionSimulacaoFragmentToNegociacaoFragment directions =
                SimulacaoFragmentDirections.actionSimulacaoFragmentToNegociacaoFragment()
                        .setCargaTotal(obterCargaTotal()).setPesoMedio(obterPesoMedio());
        NavHostFragment.findNavController(this).navigate(directions);
    }
}