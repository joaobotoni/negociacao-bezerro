package com.example.myapplication.ui.fragments;

import static com.example.myapplication.ui.helpers.FormatHelper.formatCurrency;
import static com.example.myapplication.ui.helpers.FormatHelper.formatDouble;
import static com.example.myapplication.ui.helpers.FormatHelper.formatInteger;
import static com.example.myapplication.ui.helpers.FormatHelper.getDecimal;
import static com.example.myapplication.ui.helpers.ViewHelper.isNotEmpty;
import static com.example.myapplication.ui.helpers.ViewHelper.setText;
import static com.example.myapplication.utils.BigDecimalUtil.ARREDONDAMENTO_PADRAO;
import static com.example.myapplication.utils.BigDecimalUtil.ESCALA_CALCULO;
import static com.example.myapplication.utils.BigDecimalUtil.ESCALA_MONETARIA;

import android.os.Bundle;
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
import com.example.myapplication.ui.helpers.AlertHelper;

import com.example.myapplication.ui.adapters.CategoriaAdapter;
import com.example.myapplication.ui.adapters.RacaAdapter;

import com.example.myapplication.ui.state.CategoriaUiState;
import com.example.myapplication.ui.state.CorretorUiState;
import com.example.myapplication.ui.state.EmpresaUiState;
import com.example.myapplication.ui.state.NegociacaoUiState;
import com.example.myapplication.ui.state.RacaUiState;

import com.example.myapplication.ui.state.negociacao.Cotacao;
import com.example.myapplication.ui.state.negociacao.Fechamento;
import com.example.myapplication.ui.state.negociacao.Proposta;
import com.example.myapplication.ui.viewmodel.CategoriaViewModel;
import com.example.myapplication.ui.viewmodel.CorretorViewModel;
import com.example.myapplication.ui.viewmodel.EmpresaViewModel;
import com.example.myapplication.ui.viewmodel.NegociacaoViewModel;
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
    private int cargaTotalDoLote;
    private double pesoMedio;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
    }

    private void configurarComportamentosDeTela() {
        configurarRecyclerViewRacas();
        configurarRecyclerViewCategoria();
        configurarEventosDeClique();
        configurarEscutaDeResultadoDeFrete();
    }

    private void observarEstadosDasViewModels() {
        observarEstadoDasRacas();
        observarEstadosDasCategorias();
        observarEstadoDaNegociacao();
        observarEstadoDoCorretor();
        observarEstadoDaEmpresa();
    }

    private void iniciarDadosDaNegociacao() {
        exibirQuantidadeAnimais(formatInteger(cargaTotalDoLote));
        exibirPesoMedio(formatDouble(pesoMedio));
        negociacaoViewModel.processarCotacao(BigDecimal.valueOf(pesoMedio), cargaTotalDoLote);
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

    private void configurarEventosDeClique() {
        binding.cardEmpresa.setOnClickListener(v -> exibirBottomSheetEmpresa());
        binding.cardCorretor.setOnClickListener(v -> exibirBottomSheetCorretor());
        binding.cardFrete.setOnClickListener(v -> navegarParaSimulacaoDeFrete());
        binding.botaoFinalizar.setOnClickListener(v -> executarFinalizacao());
        binding.toolbar.setOnClickListener(v -> executarNavegacaoDeRetorno());
    }

    private void configurarEscutaDeResultadoDeFrete() {
        getParentFragmentManager().setFragmentResultListener(
                SimulacaoFreteFragment.CHAVE_RESULTADO_FRETE,
                getViewLifecycleOwner(),
                (chave, resultado) -> processarResultadoDeFrete(resultado)
        );
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

    private void aoAlterarCategoriaSelecionada(CategoriaUiState categoria) {
        if (categoria == null) return;
        transporteViewModel.recomendar(categoria.getId(), cargaTotalDoLote);
    }

    private void aoSelecionarRacaNaLista(RacaUiState racaUiState) {
        racaViewModel.selecionarRaca(racaUiState);
    }

    private void aoSelecionarCategoriaNaLista(CategoriaUiState categoriaUiState) {
        categoriaViewModel.selecionarCategoria(categoriaUiState);
    }

    private void processarEstadoDaNegociacao(NegociacaoUiState estado) {
        if (estado == null) return;
        processarSessaoInicial(estado);
        processarSessaoDeFrete(estado);
        processarSessaoDeComissao(estado);
    }

    private void processarSessaoInicial(NegociacaoUiState estado) {
        atualizarSessaoCotado(estado);
        preencherCamposIniciais(estado);
    }

    private void processarSessaoDeFrete(@NonNull NegociacaoUiState estado) {
        if (!estado.isFreteAplicado()) return;
        atualizarSessaoPedido(estado);
        atualizarBadgeFrete(estado);
        atualizarValorFornecedor(estado);
    }

    private void processarSessaoDeComissao(@NonNull Proposta proposta, Fechamento fechamento) {
        if (!proposta.isFreteDescontado()) return;
        if (!fechamento.isComissaoAplicada()) return;
        atualizarSessaoFinal(estado);
        atualizarBadgeCorretor(estado);
        atualizarValorFornecedor(estado);
        atualizarValorTotal(estado);
        atualizarVariacao(estado);
    }

    private void preencherCamposIniciais(Proposta estado) {
        preencherValorPorCabeca(estado);
        preencherValorPorKg(estado);
    }

    private void atualizarSessaoCotado(Cotacao estado) {
        atualizarValorEtapaCotado(estado);
        atualizarDescricaoEtapaCotado(estado);
    }

    private void atualizarSessaoPedido(Proposta estado) {
        atualizarValorEtapaPedido(estado);
        atualizarDescricaoEtapaPedido(estado);
    }

    private void atualizarSessaoFinal(Fechamento estado) {
        atualizarValorEtapaFinal(estado);
        atualizarDescricaoEtapaFinal(estado);
    }

    private void processarResultadoDeFrete(Bundle resultado) {
        BigDecimal totalFrete = extrairValorDoFrete(resultado);
        aplicarFreteNaNegociacao(totalFrete);
        atualizarInterfaceComNovoFrete(totalFrete);
    }


    @NonNull
    private BigDecimal extrairValorDoFrete(@NonNull Bundle resultado) {
        String valorFreteStr = resultado.getString(SimulacaoFreteFragment.EXTRA_VALOR_FRETE);
        return getDecimal(valorFreteStr);
    }


    private void aplicarFreteNaNegociacao(BigDecimal totalFrete) {
        negociacaoViewModel.processarProposta(BigDecimal.valueOf(pesoMedio), cargaTotalDoLote, converterFreteTotalParaPorKg(totalFrete));
    }

    private BigDecimal converterFreteTotalParaPorKg(BigDecimal freteTotalLote) {
        BigDecimal pesoTotal = BigDecimal.valueOf(pesoMedio).multiply(BigDecimal.valueOf(cargaTotalDoLote));
        if (pesoTotal.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return freteTotalLote.divide(pesoTotal, ESCALA_CALCULO, ARREDONDAMENTO_PADRAO).setScale(ESCALA_MONETARIA, ARREDONDAMENTO_PADRAO);
    }

    private void aplicarCorretorNaNegociacao(@NonNull CorretorUiState corretor) {
        BigDecimal comissaoPorKg = corretor.getComissao().divide(BigDecimal.valueOf(pesoMedio), ESCALA_CALCULO, ARREDONDAMENTO_PADRAO);
        negociacaoViewModel.processarFechamento(BigDecimal.valueOf(pesoMedio), cargaTotalDoLote, comissaoPorKg);
    }

    private void removerCorretorDaNegociacao() {
        negociacaoViewModel.limpar(negociacaoBuilder);
    }
    private void atualizarInterfaceComNovoFrete(BigDecimal totalFrete) {
        String freteFormatado = formatarValorDoFrete(totalFrete);
        exibirDescricaoFrete(freteFormatado);
    }

    @NonNull
    private String formatarValorDoFrete(BigDecimal totalFrete) {
        return "R$ " + formatCurrency(totalFrete);
    }

    private void processarSelecaoDeCorretor(CorretorUiState corretor) {
        if (corretor == null) {
            removerCorretorDaNegociacao();
            return;
        }
        aplicarCorretorNaNegociacao(corretor);
        atualizarInterfaceComNovoCorretor(corretor);
    }


    private void atualizarInterfaceComNovoCorretor(@NonNull CorretorUiState corretor) {
        exibirNomeCorretor(corretor.getNome());
        exibirDescricaoCorretor(formatarDescricaoDoCorretor(corretor));
    }

    @NonNull
    private String formatarDescricaoDoCorretor(@NonNull CorretorUiState corretor) {
        return "R$ " + formatCurrency(corretor.getComissao()) + "/" + corretor.getTipoComissao();
    }

    private void processarSelecaoDeEmpresa(EmpresaUiState empresa) {
        if (empresa == null) return;
        exibirNomeEmpresa(empresa.getNome());
    }

    private void atualizarListaRacas(List<RacaUiState> racas) {
        racaAdapter.submitList(racas);
    }

    private void atualizarListaCategoria(List<CategoriaUiState> categorias) {
        categoriaAdapter.submitList(categorias);
    }

    private void preencherValorPorCabeca(@NonNull Proposta estado) {
        String valorFormatado = formatCurrency(estado.getValorPorCabeca());
        atualizarCampoValorPorCabeca(valorFormatado);
    }

    private void preencherValorPorKg(@NonNull Proposta estado) {
        String valorFormatado = formatCurrency(estado.getValorPorKg());
        atualizarCampoValorPorKg(valorFormatado);
    }

    private void atualizarCampoValorPorCabeca(String valor) {
        setText(binding.campoValorCabecaEntrada, valor);
    }

    private void atualizarCampoValorPorKg(String valor) {
        setText(binding.campoValorKgEntrada, valor);
    }

    private void atualizarValorEtapaCotado(@NonNull Cotacao estado) {
        String valorFormatado = formatCurrency(estado.getValorPorCabeca());
        exibirValorEtapaCotado(valorFormatado);
    }

    private void atualizarDescricaoEtapaCotado(@NonNull Cotacao estado) {
        String descricao = "R$ " + formatCurrency(estado.getValorPorKg()) + "/kg";
        exibirDescricaoEtapaCotado(descricao);
    }

    private void atualizarValorEtapaPedido(@NonNull Proposta estado) {
        String valorFormatado = formatCurrency(estado.getValorPorCabeca());
        exibirValorEtapaPedido(valorFormatado);
    }

    private void atualizarDescricaoEtapaPedido(@NonNull Proposta estado) {
        String descricao = "R$ " + formatCurrency(estado.getValorPorKg()) + "/kg";
        exibirDescricaoEtapaPedido(descricao);
    }

    private void atualizarValorEtapaFinal(@NonNull Fechamento estado) {
        String valorFormatado = formatCurrency(estado.getValorPorCabeca());
        exibirValorEtapaFinal(valorFormatado);
    }

    private void atualizarDescricaoEtapaFinal(@NonNull Fechamento estado) {
        String descricao = "R$ " + formatCurrency(estado.getValorPorKg()) + "/kg";
        exibirDescricaoEtapaFinal(descricao);
    }

    private void atualizarBadgeFrete(@NonNull Proposta estado) {
        String badge = "+ R$ " + formatCurrency(estado.getValorPorKg()) + "/kg";
        exibirBadgeFrete(badge);
    }

    private void atualizarBadgeCorretor(@NonNull Fechamento estado) {
        String badge = "+ R$ " + formatCurrency(estado.getComissaoPorKg()) + "/kg";
        exibirBadgeCorretor(badge);
    }

    private void atualizarValorFornecedor(@NonNull Proposta estado) {
        String valorFormatado = formatCurrency(estado.getValorTotal());
        exibirValorFornecedor(valorFormatado);
    }

    private void atualizarValorTotal(@NonNull Fechamento estado) {
        String valorFormatado = formatCurrency(estado.getValorTotal());
        exibirValorTotal(valorFormatado);
    }

    private void atualizarVariacao(@NonNull Fechamento estado) {
        String variacaoFormatada = formatCurrency(BigDecimal.valueOf(estado.getVariacaoPercentual())) + "%";
        exibirValorVariacao(variacaoFormatada);
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

    private boolean isCategoriaInvalidaParaFrete() {
        return categoriaViewModel.getCategoriaSelecionada().getValue() == null;
    }

    private void exibirErroDeCategoriaParaFrete() {
        AlertHelper.showSnackBarErro(binding.getRoot(), getString(R.string.aviso_selecione_categoria_frete));
    }

    private void executarNavegacaoSimulacaoFrete() {
        NegociacaoFragmentDirections.ActionNegociacaoFragmentToSimulacaoFreteeFragment directions =
                NegociacaoFragmentDirections.actionNegociacaoFragmentToSimulacaoFreteeFragment().setCargaTotal(cargaTotalDoLote);
        NavHostFragment.findNavController(this).navigate(directions);
    }

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
        NegociacaoUiState estado = negociacaoViewModel.getState().getValue();
        return isFreteAplicadoNoEstado(estado.getProposta()) || isFretePreenchidoManualmente();
    }

    private boolean isFreteAplicadoNoEstado(Proposta estado) {
        if (estado == null) return false;
        return estado.isFreteDescontado();
    }

    private boolean isFretePreenchidoManualmente() {
        return isNotEmpty(binding.campoFreteEntrada);
    }

    private void executarNavegacaoDeRetorno() {
        NavHostFragment.findNavController(this).popBackStack();
    }
}