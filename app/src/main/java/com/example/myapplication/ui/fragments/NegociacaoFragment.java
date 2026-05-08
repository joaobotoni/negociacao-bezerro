package com.example.myapplication.ui.fragments;

import static com.example.myapplication.ui.helpers.FormatHelper.formatCurrency;
import static com.example.myapplication.ui.helpers.FormatHelper.formatDouble;
import static com.example.myapplication.ui.helpers.FormatHelper.formatInteger;
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
    private TextWatcher valorPorCabecaTextWatcher;
    private TextWatcher valorPorKgTextWatcher;
    private int cargaTotalDoLote;
    private double pesoMedio;

    // Ciclo de Vida do Fragment
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentNegociacaoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        extrairArgumentosDeNavegacao();
        inicializarDependencias();
        configurarComportamentosDeTela();
        observarEstadosDasViewModels();
        iniciarDadosDaNegociacao();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Inicialização e Configuração
    private void extrairArgumentosDeNavegacao() {
        NegociacaoFragmentArgs args = NegociacaoFragmentArgs.fromBundle(requireArguments());
        cargaTotalDoLote = args.getCargaTotal();
        pesoMedio = args.getPesoMedio();
    }

    private void inicializarDependencias() {
        racaViewModel = new ViewModelProvider(requireActivity()).get(RacaViewModel.class);
        categoriaViewModel = new ViewModelProvider(requireActivity()).get(CategoriaViewModel.class);
        transporteViewModel = new ViewModelProvider(requireActivity()).get(TransporteViewModel.class);
        corretorViewModel = new ViewModelProvider(requireActivity()).get(CorretorViewModel.class);
        empresaViewModel = new ViewModelProvider(requireActivity()).get(EmpresaViewModel.class);
        negociacaoViewModel = new ViewModelProvider(requireActivity()).get(NegociacaoViewModel.class);
        precificacaoFreteViewModel = new ViewModelProvider(requireActivity()).get(PrecificacaoFreteViewModel.class);
    }

    private void configurarComportamentosDeTela() {
        configurarRecyclerViewRacas();
        configurarRecyclerViewCategoria();
        configurarEventosDeClique();
        configurarTextWatcherFrete();
    }

    private void iniciarDadosDaNegociacao() {
        exibirQuantidadeAnimais(formatInteger(cargaTotalDoLote));
        exibirPesoMedio(formatDouble(pesoMedio));
        negociacaoViewModel.processarCotacao(BigDecimal.valueOf(pesoMedio), cargaTotalDoLote);
    }

    // Observadores de Estado (LiveData)
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
        negociacaoViewModel.getState().observe(getViewLifecycleOwner(), this::processarEstadoDaNegociacao);
    }

    private void observarEstadoDoCorretor() {
        corretorViewModel.getCorretorSelecionado().observe(getViewLifecycleOwner(), this::processarSelecaoDeCorretor);
    }

    private void observarEstadoDaEmpresa() {
        empresaViewModel.getEmpresaSelecionada().observe(getViewLifecycleOwner(), this::processarSelecaoDeEmpresa);
    }

    private void observarEstadoDoFrete() {
        precificacaoFreteViewModel.getState().observe(getViewLifecycleOwner(), this::processarEstadoDoFrete);
    }

    // Configuração de UI e Eventos (RecyclerViews, Cliques, Watchers)
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

    private void configurarEventosDeClique() {
        binding.cardEmpresa.setOnClickListener(v -> exibirBottomSheetEmpresa());
        binding.cardCorretor.setOnClickListener(v -> exibirBottomSheetCorretor());
        binding.cardFrete.setOnClickListener(v -> navegarParaSimulacaoDeFrete());
        binding.botaoFinalizar.setOnClickListener(v -> executarFinalizacao());
        binding.toolbar.setOnClickListener(v -> executarNavegacaoDeRetorno());
    }

    private void configurarTextWatcherFrete() {
        freteTextWatcher = TextWatcherHelper.SimpleTextWatcher(this::onFreteManualAlterado);
        binding.campoFreteEntrada.addTextChangedListener(freteTextWatcher);
    }

    private void removerTextWatcherFrete(){
        binding.campoFreteEntrada.removeTextChangedListener(freteTextWatcher);
    }

    // Processamento de Lógica de Negócio e Estados
    private void processarEstadoDaNegociacao(NegociacaoState estado) {
        if (estado == null) return;
        processarSessaoInicial(estado);
        processarSessaoDeFrete(estado);
        processarSessaoDeComissao(estado.getProposta(), estado.getFechamento());
    }

    private void processarSessaoInicial(NegociacaoState estado) {
        atualizarSessaoCotado(estado.getCotacao());
        preencherCamposIniciais(estado.getCotacao());
    }

    private void processarSessaoDeFrete(@NonNull NegociacaoState estado) {
        PropostaState propostaState = estado.getProposta();
        if (propostaState == null) return;
        if (!propostaState.isFreteDescontado()) return;
        atualizarSessaoPedido(propostaState);
        atualizarHelperFrete(propostaState);
        atualizarBadgeFrete(propostaState);
        atualizarValorFornecedor(propostaState);
    }

    private void processarSessaoDeComissao(PropostaState propostaState, FechamentoState fechamento) {
        if (propostaState == null) return;
        if (!propostaState.isFreteDescontado()) return;
        if (fechamento == null) return;
        if (!fechamento.isComissaoAplicada()) return;
        atualizarSessaoFinal(fechamento);
        atualizarBadgeCorretor(fechamento);
        atualizarValorFornecedor(propostaState);
        atualizarValorTotal(fechamento);
        atualizarVariacao(fechamento);
    }

    private void aoAlterarCategoriaSelecionada(CategoriaState categoria) {
        if (categoria == null) return;
        transporteViewModel.recomendar(categoria.getId(), cargaTotalDoLote);
    }

    private void aoSelecionarRacaNaLista(RacaState racaUiState) {
        racaViewModel.selecionarRaca(racaUiState);
    }

    private void aoSelecionarCategoriaNaLista(CategoriaState categoriaState) {
        categoriaViewModel.selecionarCategoria(categoriaState);
    }

    private void onFreteManualAlterado() {
        if (!isNotEmpty(binding.campoFreteEntrada)) {
            negociacaoViewModel.limparProposta();
            removerCorretorDaNegociacao();
            return;
        }
        BigDecimal freteTotal = getBigDecimal(binding.campoFreteEntrada);
        negociacaoViewModel.processarProposta(BigDecimal.valueOf(pesoMedio), cargaTotalDoLote, freteTotal, FreteState.MANUAL);
    }

    private void processarEstadoDoFrete(PrecificacaoFreteState estado) {
        if (estado == null) return;
        negociacaoViewModel.processarProposta(BigDecimal.valueOf(pesoMedio), cargaTotalDoLote, estado.getValorTotal(), FreteState.SIMULADO);
        preencherValorToltalFrete(estado);
        exibirDescricaoFrete("R$ " + formatCurrency(estado.getValorTotal()));
    }

    private void processarSelecaoDeCorretor(CorretorState corretor) {
        if (corretor == null) {
            removerCorretorDaNegociacao();
            return;
        }
        aplicarCorretorNaNegociacao(corretor);
        atualizarInterfaceComNovoCorretor(corretor);
    }

    private void aplicarCorretorNaNegociacao(@NonNull CorretorState corretor) {
        BigDecimal valorKgCotado = negociacaoViewModel.getState().getValue().getCotacao().getValorPorKg();
        negociacaoViewModel.processarFechamento(BigDecimal.valueOf(pesoMedio), cargaTotalDoLote, corretor.getComissao(), valorKgCotado);
    }

    private void removerCorretorDaNegociacao() {
        negociacaoViewModel.limparFechamento();
    }

    private void processarSelecaoDeEmpresa(EmpresaState empresa) {
        if (empresa == null) return;
        exibirNomeEmpresa(empresa.getNome());
    }

    // Atualização da Interface (UI Getters/Setters e Exibição)
    private void atualizarListaRacas(List<RacaState> racas) {
        racaAdapter.submitList(racas);
    }

    private void atualizarListaCategoria(List<CategoriaState> categorias) {
        categoriaAdapter.submitList(categorias);
    }

    private void preencherCamposIniciais(CotacaoState cotacao) {
        if (cotacao == null) return;
        preencherValorPorCabeca(cotacao);
        preencherValorPorKg(cotacao);
    }

    private void preencherValorToltalFrete(@NonNull PrecificacaoFreteState cotacao) {
        setText(binding.campoFreteEntrada, formatCurrency(cotacao.getValorTotal()));
    }

    private void preencherValorPorCabeca(@NonNull CotacaoState cotacao) {
        setText(binding.campoValorCabecaEntrada, formatCurrency(cotacao.getValorPorCabeca()));
    }

    private void preencherValorPorKg(@NonNull CotacaoState cotacao) {
        setText(binding.campoValorKgEntrada, formatCurrency(cotacao.getValorPorKg()));
    }

    private void atualizarSessaoCotado(CotacaoState cotacao) {
        if (cotacao == null) return;
        atualizarValorEtapaCotado(cotacao);
        atualizarDescricaoEtapaCotado(cotacao);
    }

    private void atualizarValorEtapaCotado(@NonNull CotacaoState cotacao) {
        exibirValorEtapaCotado(formatCurrency(cotacao.getValorPorCabeca()));
    }

    private void atualizarDescricaoEtapaCotado(@NonNull CotacaoState cotacao) {
        exibirDescricaoEtapaCotado("R$ " + formatCurrency(cotacao.getValorPorKg()) + "/kg");
    }

    private void atualizarSessaoPedido(PropostaState propostaState) {
        atualizarValorEtapaPedido(propostaState);
        atualizarDescricaoEtapaPedido(propostaState);
    }

    private void atualizarValorEtapaPedido(@NonNull PropostaState propostaState) {
        exibirValorEtapaPedido(formatCurrency(propostaState.getValorPorCabeca()));
    }

    private void atualizarDescricaoEtapaPedido(@NonNull PropostaState propostaState) {
        exibirDescricaoEtapaPedido("R$ " + formatCurrency(propostaState.getValorPorKg()) + "/kg");
    }

    private void atualizarSessaoFinal(FechamentoState fechamento) {
        atualizarValorEtapaFinal(fechamento);
        atualizarDescricaoEtapaFinal(fechamento);
    }

    private void atualizarValorEtapaFinal(@NonNull FechamentoState fechamento) {
        exibirValorEtapaFinal(formatCurrency(fechamento.getValorPorCabeca()));
    }

    private void atualizarDescricaoEtapaFinal(@NonNull FechamentoState fechamento) {
        exibirDescricaoEtapaFinal("R$ " + formatCurrency(fechamento.getValorPorKg()) + "/kg");
    }

    private void atualizarHelperFrete(@NonNull PropostaState propostaState) {
        exibirHelperTextFrete(formatCurrency(propostaState.getFretePorKg()));
    }

    private void atualizarBadgeFrete(@NonNull PropostaState propostaState) {
        exibirBadgeFrete("+ R$ " + formatCurrency(propostaState.getFretePorKg()) + "/kg");
    }

    private void atualizarBadgeCorretor(@NonNull FechamentoState fechamento) {
        exibirBadgeCorretor("+ R$ " + formatCurrency(fechamento.getComissaoPorKg()) + "/kg");
    }

    private void atualizarValorFornecedor(@NonNull PropostaState propostaState) {
        exibirValorFornecedor(formatCurrency(propostaState.getValorTotal()));
    }

    private void atualizarValorTotal(@NonNull FechamentoState fechamento) {
        exibirValorTotal(formatCurrency(fechamento.getValorTotal()));
    }

    private void atualizarVariacao(@NonNull FechamentoState fechamento) {
        exibirValorVariacao(formatCurrency(BigDecimal.valueOf(fechamento.getVariacaoPercentual())) + "%");
    }

    private void atualizarInterfaceComNovoCorretor(@NonNull CorretorState corretor) {
        exibirNomeCorretor(corretor.getNome());
        exibirDescricaoCorretor(formatarDescricaoDoCorretor(corretor));
    }

    @NonNull
    private String formatarDescricaoDoCorretor(@NonNull CorretorState corretor) {
        return "R$ " + formatCurrency(corretor.getComissao()) + "/" + corretor.getTipoComissao();
    }

    private void exibirHelperTextFrete(String incidencia) {
        setHelperText(binding.campoFreteLayout, getString(R.string.helper_frete, incidencia));
    }

    private void exibirQuantidadeAnimais(String quantidade) {
        setText(binding.textoValorQuantidade, quantidade);
    }

    private void exibirPesoMedio(String peso) {
        setText(binding.textoValorPeso, peso);
    }

    private void exibirDescricaoFrete(String descricao) {
        setText(binding.textoDescricaoFrete, descricao);
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
        setText(binding.textoBadgeEtapaFrete, valor);
    }

    private void exibirBadgeCorretor(String valor) {
        setText(binding.textoBadgeEtapaCorretor, valor);
    }

    private void exibirValorFornecedor(String valor) {
        setText(binding.textoValorFornecedor, valor);
    }

    private void exibirValorTotal(String valor) {
        setText(binding.textoValorTotal, valor);
    }

    private void exibirValorVariacao(String variacao) {
        setText(binding.textoValorVariacao, variacao);
    }

    // Navegação e Modais (BottomSheet)
    private void exibirBottomSheetEmpresa() {
        new EmpresaBottomSheetDialogFragment().show(getChildFragmentManager(), null);
    }

    private void exibirBottomSheetCorretor() {
        new CorretorBottomSheetDialogFragment().show(getChildFragmentManager(), null);
    }

    private void navegarParaSimulacaoDeFrete() {
        if (isCategoriaInvalidaParaFrete()) {
            exibirErroDeCategoriaParaFrete();
            return;
        }
        executarNavegacaoSimulacaoFrete();
    }

    private void executarNavegacaoSimulacaoFrete() {
        NegociacaoFragmentDirections.ActionNegociacaoFragmentToSimulacaoFreteeFragment directions =
                NegociacaoFragmentDirections.actionNegociacaoFragmentToSimulacaoFreteeFragment()
                        .setCargaTotal(cargaTotalDoLote);
        NavHostFragment.findNavController(this).navigate(directions);
    }

    private void executarNavegacaoDeRetorno() {
        NavHostFragment.findNavController(this).popBackStack();
    }

    // Validação e Finalização
    private void executarFinalizacao() {
        if (processarErrosDeValidacao()) return;
        executarNavegacaoDeRetorno();
    }

    private boolean processarErrosDeValidacao() {
        String mensagemErro = encontrarPrimeiroCampoObrigatorioVazio();
        if (isValidacaoAprovada(mensagemErro)) return false;
        exibirMensagemDeErro(mensagemErro);
        return true;
    }

    private boolean isValidacaoAprovada(String mensagemErro) {
        return mensagemErro == null;
    }

    private void exibirMensagemDeErro(String mensagemErro) {
        AlertHelper.showSnackBarErro(binding.getRoot(), mensagemErro);
    }

    @Nullable
    private String encontrarPrimeiroCampoObrigatorioVazio() {
        if (!isEmpresaPreenchida()) return getString(R.string.validacao_empresa_obrigatoria);
        if (!isCategoriaPreenchida()) return getString(R.string.validacao_categoria_obrigatoria);
        if (!isRacaPreenchida()) return getString(R.string.validacao_raca_obrigatoria);
        if (!isSexoPreenchido()) return getString(R.string.validacao_sexo_obrigatorio);
        if (!isIdadePreenchida()) return getString(R.string.validacao_idade_obrigatoria);
        if (!isCorretorPreenchido()) return getString(R.string.validacao_corretor_obrigatorio);
        if (!isFretePreenchido()) return getString(R.string.validacao_frete_obrigatorio);
        return null;
    }

    private boolean isEmpresaPreenchida() {
        return empresaViewModel.getEmpresaSelecionada().getValue() != null;
    }

    private boolean isCategoriaPreenchida() {
        return categoriaViewModel.getCategoriaSelecionada().getValue() != null;
    }

    private boolean isRacaPreenchida() {
        return racaViewModel.getRacaSelecionada().getValue() != null;
    }

    private boolean isSexoPreenchido() {
        return binding.listaSexos.getCheckedChipId() != View.NO_ID;
    }

    private boolean isIdadePreenchida() {
        return isNotEmpty(binding.campoIdadeEntrada);
    }

    private boolean isCorretorPreenchido() {
        return corretorViewModel.getCorretorSelecionado().getValue() != null;
    }

    private boolean isFretePreenchido() {
        NegociacaoState estado = negociacaoViewModel.getState().getValue();
        return isFreteAplicadoNoEstado(estado.getProposta()) || isFretePreenchidoManualmente();
    }

    private boolean isFreteAplicadoNoEstado(PropostaState propostaState) {
        if (propostaState == null) return false;
        return propostaState.isFreteDescontado();
    }

    private boolean isFretePreenchidoManualmente() {
        return isNotEmpty(binding.campoFreteEntrada);
    }

    private boolean isCategoriaInvalidaParaFrete() {
        return categoriaViewModel.getCategoriaSelecionada().getValue() == null;
    }

    private void exibirErroDeCategoriaParaFrete() {
        AlertHelper.showSnackBarErro(binding.getRoot(), getString(R.string.aviso_selecione_categoria_frete));
    }
}