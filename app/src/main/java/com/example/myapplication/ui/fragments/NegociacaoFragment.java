package com.example.myapplication.ui.fragments;

import static com.example.myapplication.ui.helpers.FormatHelper.formatCurrency;
import static com.example.myapplication.ui.helpers.ViewHelper.getBigDecimal;
import static com.example.myapplication.ui.helpers.ViewHelper.isNotEmpty;
import static com.example.myapplication.ui.helpers.ViewHelper.setHelperText;
import static com.example.myapplication.ui.helpers.ViewHelper.setText;

import android.os.Bundle;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentNegociacaoBinding;
import com.example.myapplication.ui.adapters.CategoriaAdapter;
import com.example.myapplication.ui.adapters.RacaAdapter;
import com.example.myapplication.ui.helpers.AlertHelper;
import com.example.myapplication.ui.helpers.TextWatcherHelper;
import com.example.myapplication.ui.state.CategoriaState;
import com.example.myapplication.ui.state.CorretorState;
import com.example.myapplication.ui.state.EmpresaState;
import com.example.myapplication.ui.state.FreteState;
import com.example.myapplication.ui.state.NegociacaoState;
import com.example.myapplication.ui.state.PrecificacaoFreteState;
import com.example.myapplication.ui.state.RacaState;
import com.example.myapplication.ui.state.negociacao.CotacaoState;
import com.example.myapplication.ui.state.negociacao.FechamentoState;
import com.example.myapplication.ui.state.negociacao.PropostaState;
import com.example.myapplication.ui.viewmodel.CategoriaViewModel;
import com.example.myapplication.ui.viewmodel.CorretorViewModel;
import com.example.myapplication.ui.viewmodel.EmpresaViewModel;
import com.example.myapplication.ui.viewmodel.NegociacaoViewModel;
import com.example.myapplication.ui.viewmodel.PrecificacaoFreteViewModel;
import com.example.myapplication.ui.viewmodel.RacaViewModel;
import com.example.myapplication.ui.viewmodel.TransporteViewModel;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NegociacaoFragment extends Fragment {

    private FragmentNegociacaoBinding binding;
    private CategoriaAdapter categoriaAdapter;
    private RacaAdapter racaAdapter;
    private RacaViewModel racaViewModel;
    private CategoriaViewModel categoriaViewModel;
    private TransporteViewModel transporteViewModel;
    private CorretorViewModel corretorViewModel;
    private EmpresaViewModel empresaViewModel;
    private NegociacaoViewModel negociacaoViewModel;
    private PrecificacaoFreteViewModel precificacaoFreteViewModel;
    private TextWatcher freteTextWatcher;
    private double peso;
    private int quantidade;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNegociacaoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inicializar();
    }

    private void inicializar() {
        extrairArgumentosDeNavegacao();
        iniciarDadosEspecificacao();
        inicializarDependencias();
        configurarEventosDeClique();
        configurarComportamentosDeTela();
        observarEstadosDasViewModels();
        processarCotacao();
    }

    private void extrairArgumentosDeNavegacao() {
        NegociacaoFragmentArgs args = NegociacaoFragmentArgs.fromBundle(requireArguments());
        quantidade = args.getCargaTotal();
        peso = args.getPesoMedio();
    }

    private void inicializarDependencias() {
        racaViewModel = new ViewModelProvider(requireActivity()).get(RacaViewModel.class);
        categoriaViewModel = new ViewModelProvider(requireActivity()).get(CategoriaViewModel.class);
        transporteViewModel = new ViewModelProvider(requireActivity()).get(TransporteViewModel.class);
        corretorViewModel = new ViewModelProvider(requireActivity()).get(CorretorViewModel.class);
        empresaViewModel = new ViewModelProvider(requireActivity()).get(EmpresaViewModel.class);
        negociacaoViewModel = new ViewModelProvider(requireActivity()).get(NegociacaoViewModel.class);
        precificacaoFreteViewModel = new ViewModelProvider(requireActivity()).get(PrecificacaoFreteViewModel.class);
        freteTextWatcher = TextWatcherHelper.SimpleTextWatcher(this::aoFreteManualAlterado);
    }

    private void configurarComportamentosDeTela() {
        configurarRecyclerViewRacas();
        configurarRecyclerViewCategoria();
        configurarTextWatcherFrete();
    }

    private void configurarRecyclerViewRacas() {
        racaAdapter = new RacaAdapter(this::aoSelecionarRacaNaLista);
        configurarRecyclerViewHorizontal(binding.listaRacas, racaAdapter);
    }

    private void configurarRecyclerViewCategoria() {
        categoriaAdapter = new CategoriaAdapter(this::aoSelecionarCategoriaNaLista);
        configurarRecyclerViewHorizontal(binding.listaCategorias, categoriaAdapter);
    }

    private void configurarRecyclerViewHorizontal(@NonNull RecyclerView recyclerView, RecyclerView.Adapter<?> adapter) {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
    }

    private void configurarTextWatcherFrete() {
        binding.campoFreteEntrada.addTextChangedListener(freteTextWatcher);
    }

    private void configurarEventosDeClique() {
        configurarCliqueEmpresa();
        configurarCliqueCorretor();
        configurarCliqueFrete();
        configurarCliqueVoltar();
    }

    private void configurarCliqueEmpresa() {
        binding.cardEmpresa.setOnClickListener(v -> exibirBottomSheetEmpresa());
    }

    private void configurarCliqueCorretor() {
        binding.cardCorretor.setOnClickListener(v -> exibirBottomSheetCorretor());
    }

    private void configurarCliqueFrete() {
        binding.cardFrete.setOnClickListener(v -> navegarParaSimulacaoDeFrete());
    }

    private void configurarCliqueVoltar() {
        binding.toolbar.setOnClickListener(v -> executarNavegacaoDeRetorno());
    }

    private void observarEstadosDasViewModels() {
        observarEstadoDasRacas();
        observarEstadosDasCategorias();
        observarEstadoDaNegociacao();
        observarEstadoDoCorretor();
        observarEstadoDaEmpresa();
        observarEstadoDoFrete();
    }

    private void observarEstadoDasRacas() {
        racaViewModel.getState().observe(getViewLifecycleOwner(), this::atualizarListaRacas);
    }

    private void observarEstadosDasCategorias() {
        categoriaViewModel.getState().observe(getViewLifecycleOwner(), this::atualizarListaCategoria);
        categoriaViewModel.getCategoriaSelecionada().observe(getViewLifecycleOwner(), this::aoAlterarCategoriaSelecionada);
    }

    private void observarEstadoDaNegociacao() {
        negociacaoViewModel.getState().observe(getViewLifecycleOwner(), this::atualizarTabela);
    }

    private void observarEstadoDoCorretor() {
        corretorViewModel.getCorretorSelecionado().observe(getViewLifecycleOwner(), this::aoSelecionarCorretorNaLista);
    }

    private void observarEstadoDaEmpresa() {
        empresaViewModel.getEmpresaSelecionada().observe(getViewLifecycleOwner(), this::aoSelecionarEmpresaNaLista);
    }

    private void observarEstadoDoFrete() {
        precificacaoFreteViewModel.getState().observe(getViewLifecycleOwner(), this::atualizarEstadoCardFrete);
    }

    private void iniciarDadosEspecificacao() {
        atualizarQuantidadeAnimais();
        atualizarPesoMedioAnimais();
    }

    private void atualizarQuantidadeAnimais() {
        exibirTextoQuantidade(String.format(Locale.getDefault(), "%d", quantidade));
    }

    private void atualizarPesoMedioAnimais() {
        exibirTextoPesoMedio(String.format(Locale.getDefault(), "%.2f", peso));
    }

    private void processarCotacao() {
        negociacaoViewModel.processarCotacao(BigDecimal.valueOf(peso), quantidade);
    }

    private void processarProposta(PrecificacaoFreteState freteState) {
        negociacaoViewModel.processarProposta(BigDecimal.valueOf(peso), quantidade,
                freteState.getValorTotal(), freteState.getFreteState());
    }

    private void processarFechamento(CorretorState corretorState) {
        negociacaoViewModel.processarFechamento(BigDecimal.valueOf(peso), quantidade,
                corretorState.getComissao());
    }

    private void aoFreteManualAlterado() {
        if (!isNotEmpty(binding.campoFreteEntrada)) {
            limparFreteEDependencias();
            return;
        }
        precificacaoFreteViewModel.calcularIncidencia(obterValorTotalFrete(), BigDecimal.valueOf(peso), quantidade, FreteState.MANUAL);
    }

    private void limparFreteEDependencias() {
        limparHelperTextFrete();
        limparProposta();
        limparFechamento();
        limparCardFrete();
        limparSelecaoCorretor();
        limparCardCorretor();
    }

    private void aoSelecionarRacaNaLista(RacaState racaUiState) {
        racaViewModel.selecionarRaca(racaUiState);
    }

    private void aoSelecionarCategoriaNaLista(CategoriaState categoriaState) {
        categoriaViewModel.selecionarCategoria(categoriaState);
    }

    private void aoAlterarCategoriaSelecionada(CategoriaState categoriaState) {
        if (categoriaState == null) return;
        transporteViewModel.recomendar(categoriaState.getId(), quantidade);
    }

    private void aoSelecionarEmpresaNaLista(EmpresaState empresaState) {
        if (empresaState == null) return;
        atualizarNomeEmpresa(empresaState);
    }

    private void aoSelecionarCorretorNaLista(CorretorState corretorState) {
        if (corretorState == null) {
            limparFechamento();
            limparCardCorretor();
            return;
        }
        atualizarEstadoCorretor(corretorState);
        processarFechamento(corretorState);
    }

    private void atualizarListaRacas(List<RacaState> racas) {
        racaAdapter.submitList(racas);
    }

    private void atualizarListaCategoria(List<CategoriaState> categorias) {
        categoriaAdapter.submitList(categorias);
    }

    private void atualizarTabela(NegociacaoState negociacaoState) {
        if (negociacaoState == null) return;
        atualizarValoresCotado(negociacaoState.getCotacao());
        atualizarValoresPedido(negociacaoState.getProposta());
        atualizarValoresFechamento(negociacaoState.getFechamento());
    }

    private void atualizarValoresCotado(CotacaoState cotacaoState) {
        if (cotacaoState == null) return;
        preencherCamposIniciais(cotacaoState);
        exibirValorEtapaCotado(formatCurrency(cotacaoState.getValorPorCabeca()));
        exibirDescricaoEtapaCotado(formatCurrency(cotacaoState.getValorPorKg()));
    }

    private void atualizarValoresPedido(PropostaState propostaState) {
        if (propostaState == null) return;
        if (!propostaState.isFreteDescontado()) return;
        exibirValorEtapaPedido(formatCurrency(propostaState.getValorPorCabeca()));
        exibirDescricaoEtapaPedido(formatCurrency(propostaState.getValorPorKg()));
        exibirBadgeFrete(formatCurrency(propostaState.getFretePorKg()));
        exibirValorFornecedor(formatCurrency(propostaState.getValorTotal()));
    }

    private void atualizarValoresFechamento(FechamentoState fechamentoState) {
        if (fechamentoState == null) return;
        if (fechamentoState.isComissaoAplicada()) return;
        exibirValorEtapaFinal(formatCurrency(fechamentoState.getValorPorCabeca()));
        exibirDescricaoEtapaFinal(formatCurrency(fechamentoState.getValorPorKg()));
        exibirBadgeCorretor(formatCurrency(fechamentoState.getComissaoPorKg()));
        exibirValorTotal(formatCurrency(fechamentoState.getValorTotal()));
    }

    private void atualizarEstadoCardFrete(PrecificacaoFreteState freteState) {
        if (freteState == null) return;
        processarProposta(freteState);
        atualizarEstadoCampoFrete(freteState);
        exibirTextValorFrete(formatCurrency(freteState.getValorTotal()));
        exibirTextValorFretePorKg(formatCurrency(freteState.getValorParcial()));
    }

    private void atualizarEstadoCampoFrete(PrecificacaoFreteState freteState) {
        preencherCampoValorFrete(freteState);
        exibirHelperTextFrete(formatCurrency(freteState.getValorParcial()));
    }

    private void preencherCampoValorFrete(PrecificacaoFreteState freteState) {
        if (freteState.getFreteState() == FreteState.MANUAL) return;
        binding.campoFreteEntrada.removeTextChangedListener(freteTextWatcher);
        preencherCampoValorFrete(formatCurrency(freteState.getValorTotal()));
        binding.campoFreteEntrada.addTextChangedListener(freteTextWatcher);
    }

    private void atualizarNomeEmpresa(EmpresaState empresaState) {
        exibirNomeEmpresa(empresaState.getNome());
    }

    private void atualizarEstadoCorretor(CorretorState corretorState) {
        exibirNomeCorretor(corretorState.getNome());
        exibirDescricaoCorretor(formatarDescricaoCorretor(corretorState));
    }

    private String formatarDescricaoCorretor(CorretorState corretorState) {
        BigDecimal comissao = corretorState.getComissao();
        BigDecimal total = comissao.multiply(BigDecimal.valueOf(quantidade));
        return String.format(Locale.getDefault(), "R$ %s/%s · R$ %s",
                formatCurrency(comissao), corretorState.getTipoComissao(), formatCurrency(total));
    }

    private void exibirTextoQuantidade(String valor) {
        setText(binding.textoValorQuantidade, valor);
    }

    private void exibirTextoPesoMedio(String valor) {
        setText(binding.textoValorPeso, valor);
    }

    private void preencherCamposIniciais(CotacaoState cotacaoState) {
        preencherCampoValorPorCabeca(formatCurrency(cotacaoState.getValorPorCabeca()));
        preencherCampoValorPorKg(formatCurrency(cotacaoState.getValorPorKg()));
    }

    private void preencherCampoValorFrete(String valor) {
        setText(binding.campoFreteEntrada, valor);
    }

    private void preencherCampoValorPorCabeca(String valor) {
        setText(binding.campoValorCabecaEntrada, valor);
    }

    private void preencherCampoValorPorKg(String valor) {
        setText(binding.campoValorKgEntrada, valor);
    }

    private void exibirHelperTextFrete(String incidencia) {
        setHelperText(binding.campoFreteLayout, getString(R.string.helper_frete, incidencia));
    }

    private void exibirTextValorFrete(String valor) {
        setText(binding.textoTituloFrete, String.format(Locale.getDefault(), "R$: %s", valor));
    }

    private void exibirTextValorFretePorKg(String valor) {
        setText(binding.textoDescricaoFrete, String.format(Locale.getDefault(), "R$: %s", valor));
    }

    private void exibirNomeCorretor(String nome) {
        setText(binding.textoNomeCorretor, nome);
    }

    private void exibirDescricaoCorretor(String descricao) {
        setText(binding.textoDescricaoCorretor, descricao);
    }

    private void exibirNomeEmpresa(String nome) {
        setText(binding.textoNomeEmpresa, nome);
    }

    private void exibirValorEtapaCotado(String valor) {
        setText(binding.textoValorEtapaCotado, valor);
    }

    private void exibirDescricaoEtapaCotado(String descricao) {
        setText(binding.textoDescricaoEtapaCotado, descricao);
    }

    private void exibirValorEtapaPedido(String valor) {
        setText(binding.textoValorEtapaPedido, valor);
    }

    private void exibirDescricaoEtapaPedido(String descricao) {
        setText(binding.textoDescricaoEtapaPedido, descricao);
    }

    private void exibirValorEtapaFinal(String valor) {
        setText(binding.textoValorEtapaFinal, valor);
    }

    private void exibirDescricaoEtapaFinal(String descricao) {
        setText(binding.textoDescricaoEtapaFinal, descricao);
    }

    private void exibirBadgeFrete(String valor) {
        setText(binding.textoBadgeEtapaFrete, String.format(Locale.getDefault(), "+R$ %s", valor));
    }

    private void exibirBadgeCorretor(String valor) {
        setText(binding.textoBadgeEtapaCorretor, String.format(Locale.getDefault(), "+R$ %s", valor));
    }

    private void exibirValorFornecedor(String valor) {
        setText(binding.textoValorFornecedor, valor);
    }

    private void exibirValorTotal(String valor) {
        setText(binding.textoValorTotal, valor);
    }

    private void exibirBottomSheetEmpresa() {
        new EmpresaBottomSheetDialogFragment().show(getChildFragmentManager(), null);
    }

    private void exibirBottomSheetCorretor() {
        new CorretorBottomSheetDialogFragment().show(getChildFragmentManager(), null);
    }

    private void limparProposta() {
        negociacaoViewModel.limparProposta();
    }

    private void limparFechamento() {
        negociacaoViewModel.limparFechamento();
    }

    private void limparSelecaoCorretor() {
        corretorViewModel.limparSelecao();
    }

    private void limparCardFrete() {
        exibirTextValorFrete(getString(R.string.titulo_frete));
        exibirTextValorFretePorKg(getString(R.string.descricao_frete_vazio));
    }

    private void limparCardCorretor() {
        exibirNomeCorretor(getString(R.string.texto_sem_corretor));
        exibirDescricaoCorretor(getString(R.string.descricao_sem_comissao));
    }

    private void limparHelperTextFrete() {
        exibirHelperTextFrete(formatCurrency(BigDecimal.ZERO));
    }

    private BigDecimal obterValorTotalFrete() {
        return getBigDecimal(binding.campoFreteEntrada);
    }

    private void navegarParaSimulacaoDeFrete() {
        if (isCategoriaInvalidaParaFrete()) {
            exibirErroDeCategoriaParaFrete();
            return;
        }
        executarNavegacaoSimulacaoFrete();
    }

    private boolean isCategoriaInvalidaParaFrete() {
        return categoriaViewModel.getCategoriaSelecionada().getValue() == null;
    }

    private void exibirErroDeCategoriaParaFrete() {
        AlertHelper.showSnackBarErro(binding.getRoot(), getString(R.string.aviso_selecione_categoria_frete));
    }

    private void executarNavegacaoSimulacaoFrete() {
        NegociacaoFragmentDirections.ActionNegociacaoFragmentToSimulacaoFreteeFragment directions =
                NegociacaoFragmentDirections.actionNegociacaoFragmentToSimulacaoFreteeFragment()
                        .setCargaTotal(quantidade)
                        .setPesoMedio((float) peso);
        NavHostFragment.findNavController(this).navigate(directions);
    }

    private void executarNavegacaoDeRetorno() {
        NavHostFragment.findNavController(this).popBackStack();
    }
}