package com.omni.negociacaobezerros.ui.fragments;

import static com.omni.negociacaobezerros.ui.helpers.AlertHelper.showSnackBarErro;
import static com.omni.negociacaobezerros.ui.helpers.AlertHelper.showSnackBarSucesso;
import static com.omni.negociacaobezerros.ui.helpers.FormatHelper.formatCurrency;
import static com.omni.negociacaobezerros.ui.helpers.NavigationHelper.navegar;
import static com.omni.negociacaobezerros.ui.helpers.RecyclerViewHelper.setupHorizontalRecyclerView;
import static com.omni.negociacaobezerros.ui.helpers.ViewHelper.getCheckedChipText;
import static com.omni.negociacaobezerros.ui.helpers.ViewHelper.isEmpty;
import static com.omni.negociacaobezerros.ui.helpers.ViewHelper.isNotEmpty;
import static com.omni.negociacaobezerros.ui.helpers.ViewHelper.isNotNull;
import static com.omni.negociacaobezerros.ui.helpers.ViewHelper.parseDecimal;
import static com.omni.negociacaobezerros.ui.helpers.ViewHelper.parseInt;
import static com.omni.negociacaobezerros.ui.helpers.ViewHelper.selectChip;
import static com.omni.negociacaobezerros.ui.helpers.ViewHelper.setHelperText;
import static com.omni.negociacaobezerros.ui.helpers.ViewHelper.setText;
import static com.omni.negociacaobezerros.ui.helpers.ViewHelper.setTextSafely;
import static com.omni.negociacaobezerros.utils.DecimalUtil.createCurrencyFormat;

import android.os.Bundle;
import android.text.TextWatcher;
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
import com.omni.negociacaobezerros.ui.adapters.CategoriaAdapter;
import com.omni.negociacaobezerros.ui.adapters.RacaAdapter;
import com.omni.negociacaobezerros.ui.helpers.NavigationHelper;
import com.omni.negociacaobezerros.ui.helpers.TextWatcherHelper;
import com.omni.negociacaobezerros.ui.state.animal.AnimalState;
import com.omni.negociacaobezerros.ui.state.animal.CategoriaState;
import com.omni.negociacaobezerros.ui.state.animal.RacaState;
import com.omni.negociacaobezerros.ui.state.empresa.CorretorState;
import com.omni.negociacaobezerros.ui.state.empresa.EmpresaState;
import com.omni.negociacaobezerros.ui.state.frete.FreteState;
import com.omni.negociacaobezerros.ui.state.frete.StatusFrete;
import com.omni.negociacaobezerros.ui.state.negociacao.CotacaoState;
import com.omni.negociacaobezerros.ui.state.negociacao.FechamentoState;
import com.omni.negociacaobezerros.ui.state.negociacao.PropostaState;
import com.omni.negociacaobezerros.ui.viewmodel.AnimalViewModel;
import com.omni.negociacaobezerros.ui.viewmodel.CategoriaViewModel;
import com.omni.negociacaobezerros.ui.viewmodel.CorretorViewModel;
import com.omni.negociacaobezerros.ui.viewmodel.EmpresaViewModel;
import com.omni.negociacaobezerros.ui.viewmodel.NegociacaoViewModel;
import com.omni.negociacaobezerros.ui.viewmodel.PrecificacaoFreteViewModel;
import com.omni.negociacaobezerros.ui.viewmodel.RacaViewModel;
import com.omni.negociacaobezerros.ui.viewmodel.SimulacaoViewModel;
import com.omni.negociacaobezerros.ui.viewmodel.TransporteViewModel;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NegociacaoFragment extends Fragment {
    private static final String TAG_BOTTOM_SHEET_EMPRESA = "EmpresaBottomSheet";
    private static final String TAG_BOTTOM_SHEET_CORRETOR = "CorretorBottomSheet";

    private FragmentNegociacaoBinding binding;

    private RacaViewModel racaViewModel;
    private CategoriaViewModel categoriaViewModel;
    private TransporteViewModel transporteViewModel;
    private CorretorViewModel corretorViewModel;
    private EmpresaViewModel empresaViewModel;
    private SimulacaoViewModel simulacaoViewModel;
    private NegociacaoViewModel negociacaoViewModel;
    private PrecificacaoFreteViewModel precificacaoFreteViewModel;
    private AnimalViewModel animalViewModel;

    private TextWatcher especificacaoTextWatcher;
    private TextWatcher freteTextWatcher;
    private TextWatcher valorCabecaTextWatcher;
    private TextWatcher valorKgTextWatcher;

    private double peso;
    private int quantidade;

    private CategoriaAdapter categoriaAdapter;
    private RacaAdapter racaAdapter;

    private CotacaoState cotacaoAtual;
    private PropostaState propostaAtual;
    private FechamentoState fechamentoAtual;
    private FreteState freteAtual;
    private CorretorState corretorAtual;
    private CategoriaState categoriaAtual;
    private RacaState racaAtual;

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

    @Override
    public void onDestroyView() {
        removerTextWatchers();
        super.onDestroyView();
        binding = null;
    }

    private void inicializar() {
        extrairArgumentosDeNavegacao();
        inicializarViewModels();
        inicializarTextWatchers();
        configurarRecyclerViews();
        configurarChipGroupSexo();
        anexarTextWatchers();
        configurarEventosDeClique();
        observarEstadosDasViewModels();
        exibirInfoInicialDoAnimal();
        dispararCotacaoInicial();
    }

    private void extrairArgumentosDeNavegacao() {
        NegociacaoFragmentArgs args = NegociacaoFragmentArgs.fromBundle(requireArguments());
        quantidade = args.getCargaTotal();
        peso = args.getPesoMedio();
    }

    private void inicializarViewModels() {
        racaViewModel = new ViewModelProvider(requireActivity()).get(RacaViewModel.class);
        categoriaViewModel = new ViewModelProvider(requireActivity()).get(CategoriaViewModel.class);
        transporteViewModel = new ViewModelProvider(requireActivity()).get(TransporteViewModel.class);
        corretorViewModel = new ViewModelProvider(requireActivity()).get(CorretorViewModel.class);
        empresaViewModel = new ViewModelProvider(requireActivity()).get(EmpresaViewModel.class);
        simulacaoViewModel = new ViewModelProvider(requireActivity()).get(SimulacaoViewModel.class);
        negociacaoViewModel = new ViewModelProvider(requireActivity()).get(NegociacaoViewModel.class);
        precificacaoFreteViewModel = new ViewModelProvider(requireActivity()).get(PrecificacaoFreteViewModel.class);
        animalViewModel = new ViewModelProvider(requireActivity()).get(AnimalViewModel.class);
    }

    private void inicializarTextWatchers() {
        especificacaoTextWatcher = TextWatcherHelper.simpleTextWatcher(this::onEspecificacaoAlterada);
        freteTextWatcher = TextWatcherHelper.moneyTextWatcher(Double.MAX_VALUE, createCurrencyFormat(), this::onFreteManualAlterado);
        valorCabecaTextWatcher = TextWatcherHelper.moneyTextWatcher(Double.MAX_VALUE, createCurrencyFormat(), this::onValorCabecaAlterado);
        valorKgTextWatcher = TextWatcherHelper.moneyTextWatcher(Double.MAX_VALUE, createCurrencyFormat(), this::onValorKgAlterado);
    }

    private void anexarTextWatchers() {
        binding.campoIdadeEntrada.addTextChangedListener(especificacaoTextWatcher);
        binding.campoFreteEntrada.addTextChangedListener(freteTextWatcher);
        binding.campoValorCabecaEntrada.addTextChangedListener(valorCabecaTextWatcher);
        binding.campoValorKgEntrada.addTextChangedListener(valorKgTextWatcher);
    }

    private void removerTextWatchers() {
        if (binding == null) return;
        binding.campoIdadeEntrada.removeTextChangedListener(especificacaoTextWatcher);
        binding.campoFreteEntrada.removeTextChangedListener(freteTextWatcher);
        binding.campoValorCabecaEntrada.removeTextChangedListener(valorCabecaTextWatcher);
        binding.campoValorKgEntrada.removeTextChangedListener(valorKgTextWatcher);
    }

    private void configurarRecyclerViews() {
        configurarRecyclerViewCategorias();
        configurarRecyclerViewRacas();
    }

    private void configurarRecyclerViewCategorias() {
        categoriaAdapter = new CategoriaAdapter(this::onCategoriaSelecionadaNaLista);
        setupHorizontalRecyclerView(binding.listaCategorias, categoriaAdapter, requireContext());
    }

    private void configurarRecyclerViewRacas() {
        racaAdapter = new RacaAdapter(this::onRacaSelecionadaNaLista);
        setupHorizontalRecyclerView(binding.listaRacas, racaAdapter, requireContext());
    }

    private void configurarChipGroupSexo() {
        binding.listaSexos.setOnCheckedStateChangeListener((group, checkedIds) -> onEspecificacaoAlterada());
    }

    private void configurarEventosDeClique() {
        binding.cardEmpresa.setOnClickListener(v -> onCliqueEmpresa());
        binding.cardCorretor.setOnClickListener(v -> onCliqueCorretor());
        binding.cardFrete.setOnClickListener(v -> onCliqueFrete());
        binding.toolbar.setOnClickListener(v -> onCliqueVoltar());
        binding.botaoFinalizar.setOnClickListener(v -> onCliqueFinalizar());
    }

    private void onCliqueEmpresa() {
        exibirBottomSheetEmpresa();
    }

    private void onCliqueCorretor() {
        if (isFreteNaoPreenchido()) {
            exibirErroFreteObrigatorio();
            return;
        }
        exibirBottomSheetCorretor();
    }

    private void onCliqueFrete() {
        if (isCategoriaAusente()) {
            exibirErroCategoriaParaFrete();
            return;
        }
        navegarParaSimulacaoDeFrete();
    }

    private void onCliqueVoltar() {
        NavigationHelper.voltar(this);
    }

    private void onCliqueFinalizar() {
        if (!isProntoParaFinalizar()) {
            exibirErroCamposObrigatorios();
            return;
        }
        concluirNegociacao();
    }

    private void observarEstadosDasViewModels() {
        observarListaDeRacas();
        observarRacaSelecionada();
        observarListaDeCategorias();
        observarCategoriaSelecionada();
        observarAnimal();
        observarCotacao();
        observarProposta();
        observarFechamento();
        observarFrete();
        observarIncidenciaFrete();
        observarVariacao();
        observarCorretor();
        observarEmpresa();
    }

    private void observarListaDeRacas() {
        racaViewModel.getState().observe(getViewLifecycleOwner(), this::onListaDeRacasAtualizada);
    }

    private void observarRacaSelecionada() {
        racaViewModel.getSelecionada().observe(getViewLifecycleOwner(), this::onRacaSelecionada);
    }

    private void observarListaDeCategorias() {
        categoriaViewModel.getState().observe(getViewLifecycleOwner(), this::onListaDeCategoriasAtualizada);
    }

    private void observarCategoriaSelecionada() {
        categoriaViewModel.getSelecionada().observe(getViewLifecycleOwner(), this::onCategoriaSelecionada);
    }

    private void observarAnimal() {
        animalViewModel.getAnimalState().observe(getViewLifecycleOwner(), this::onAnimalAtualizado);
    }

    private void observarCotacao() {
        simulacaoViewModel.getState().observe(getViewLifecycleOwner(), this::onCotacaoAtualizada);
    }

    private void observarProposta() {
        negociacaoViewModel.getProposta().observe(getViewLifecycleOwner(), this::onPropostaAtualizada);
    }

    private void observarFechamento() {
        negociacaoViewModel.getFechamento().observe(getViewLifecycleOwner(), this::onFechamentoAtualizado);
    }

    private void observarFrete() {
        precificacaoFreteViewModel.getState().observe(getViewLifecycleOwner(), this::onFreteAtualizado);
    }

    private void observarIncidenciaFrete() {
        precificacaoFreteViewModel.getIncidencia().observe(getViewLifecycleOwner(), this::onIncidenciaFreteAtualizada);
    }

    private void observarVariacao() {
        negociacaoViewModel.getVariacao().observe(getViewLifecycleOwner(), this::onVariacaoAtualizada);
    }

    private void observarCorretor() {
        corretorViewModel.getSelecionado().observe(getViewLifecycleOwner(), this::onCorretorSelecionado);
    }

    private void observarEmpresa() {
        empresaViewModel.getSelecionada().observe(getViewLifecycleOwner(), this::onEmpresaSelecionada);
    }

    private void onListaDeRacasAtualizada(@NonNull List<RacaState> racas) {
        racaAdapter.submitList(racas);
    }

    private void onListaDeCategoriasAtualizada(@NonNull List<CategoriaState> categorias) {
        categoriaAdapter.submitList(categorias);
    }

    private void onRacaSelecionada(@Nullable RacaState raca) {
        salvarRacaAtual(raca);
        if (!isRacaSelecionada(raca)) return;
        notificarRacaNoAnimal(raca);
        sincronizarEstadoDoBotaoFinalizar();
    }

    private void salvarRacaAtual(@Nullable RacaState raca) {
        racaAtual = raca;
    }

    private void onCategoriaSelecionada(@Nullable CategoriaState categoria) {
        salvarCategoriaAtual(categoria);
        if (!isCategoriaSelecionada(categoria)) return;
        notificarCategoriaNoAnimal(categoria);
        recomendarTransportePorCategoria(categoria);
        sincronizarEstadoDoBotaoFinalizar();
    }

    private void salvarCategoriaAtual(@Nullable CategoriaState categoria) {
        categoriaAtual = categoria;
    }

    private void onAnimalAtualizado(@Nullable AnimalState animal) {
        if (!isAnimalValido(animal)) return;
        restaurarEspecificacaoSalva(animal);
    }

    private void onCotacaoAtualizada(@Nullable CotacaoState cotacao) {
        salvarCotacaoAtual(cotacao);
        if (!isCotacaoValida(cotacao)) return;
        exibirValoresCotadosNaTabela(cotacao);
        preencherCamposComValoresCotados(cotacao);
        sincronizarEstadoDoBotaoFinalizar();
    }

    private void salvarCotacaoAtual(@Nullable CotacaoState cotacao) {
        cotacaoAtual = cotacao;
    }

    private void onPropostaAtualizada(@Nullable PropostaState proposta) {
        salvarPropostaAtual(proposta);
        sincronizarValoresDaPropostaNaTabela(proposta);
        sincronizarEstadoDoBotaoFinalizar();
    }

    private void salvarPropostaAtual(@Nullable PropostaState proposta) {
        propostaAtual = proposta;
    }

    private void onFechamentoAtualizado(@Nullable FechamentoState fechamento) {
        salvarFechamentoAtual(fechamento);
        sincronizarValoresDeFechamentoNaTabela(fechamento);
        sincronizarEstadoDoBotaoFinalizar();
    }

    private void salvarFechamentoAtual(@Nullable FechamentoState fechamento) {
        fechamentoAtual = fechamento;
    }

    private void onFreteAtualizado(@Nullable FreteState frete) {
        salvarFreteAtual(frete);
        if (!isFreteValido(frete)) return;
        dispararNegociacaoComFrete(frete);
        sincronizarCampoFrete(frete);
        sincronizarCardFrete(frete);
    }

    private void salvarFreteAtual(@Nullable FreteState frete) {
        freteAtual = frete;
    }

    private void onCorretorSelecionado(@Nullable CorretorState corretor) {
        salvarCorretorAtual(corretor);
        if (!isCorretorSelecionado(corretor)) {
            restaurarCardCorretorVazio();
            return;
        }
        exibirCorretorNoCard(corretor);
        dispararFechamentoComCorretor(corretor);
    }

    private void salvarCorretorAtual(@Nullable CorretorState corretor) {
        corretorAtual = corretor;
    }

    private void onEmpresaSelecionada(@Nullable EmpresaState empresa) {
        if (!isEmpresaSelecionada(empresa)) return;
        exibirNomeEmpresaNoCard(empresa);
    }

    private void onVariacaoAtualizada(@NonNull Double variacao) {
        exibirVariacaoPercentual(variacao);
    }

    private void onIncidenciaFreteAtualizada(@Nullable BigDecimal incidencia) {
        if (incidencia == null || isFreteNaoPreenchido()) return;
        exibirHelperTextFrete(incidencia);
    }

    private void exibirHelperTextFrete(@NonNull BigDecimal incidencia) {
        setHelperText(binding.campoFreteLayout, formatarHelperFrete(incidencia));
    }

    private void onEspecificacaoAlterada() {
        notificarSexoNoAnimal();
        notificarIdadeNoAnimal();
        sincronizarEstadoDoBotaoFinalizar();
    }

    private void onFreteManualAlterado() {
        if (!isCampoFreteEmFoco()) return;
        if (isFreteInvalidoParaCalculo()) {
            restaurarEstadoDeFreteManual();
            return;
        }
        limparFreteSimuladoSeAtivo();
        calcularIncidenciaFreteManual();
    }

    private boolean isCampoFreteEmFoco() {
        return binding.campoFreteEntrada.hasFocus();
    }

    private boolean isFreteInvalidoParaCalculo() {return isFreteNaoPreenchido() || isValorZero(lerValorTotalFrete());}
    private void onValorCabecaAlterado() {
        processarRecalculoPorCabeca(lerValorPorCabeca());
    }

    private void onValorKgAlterado() {
        processarRecalculoPorKg(lerValorPorKg());
    }

    private void onRacaSelecionadaNaLista(@NonNull RacaState raca) {
        racaViewModel.selecionar(raca);
    }

    private void onCategoriaSelecionadaNaLista(@NonNull CategoriaState categoria) {
        categoriaViewModel.selecionar(categoria);
    }

    private void dispararCotacaoInicial() {
        simulacaoViewModel.processarCotacao(BigDecimal.valueOf(peso), quantidade);
    }

    private void dispararNegociacaoComFrete(@NonNull FreteState frete) {
        if (!isCotacaoValida(cotacaoAtual)) return;
        negociacaoViewModel.processarNegociacao(
                cotacaoAtual,
                BigDecimal.valueOf(peso),
                quantidade,
                frete.getValorTotal(),
                frete.getFreteState(),
                resolverComissaoAtual()
        );
    }

    private void dispararFechamentoComCorretor(@NonNull CorretorState corretor) {
        if (!isCotacaoValida(cotacaoAtual) || !isFreteValido(freteAtual)) return;
        negociacaoViewModel.processarNegociacao(
                cotacaoAtual,
                BigDecimal.valueOf(peso),
                quantidade,
                freteAtual.getValorTotal(),
                freteAtual.getFreteState(),
                corretor.getComissao()
        );
    }

    private void calcularIncidenciaFreteManual() {
        precificacaoFreteViewModel.calcularIncidencia(lerValorTotalFrete(), BigDecimal.valueOf(peso), quantidade);
    }

    private void limparFreteSimuladoSeAtivo() {
        if (isFreteSimulado(freteAtual)){
            limparEstadoDeFrete();
        }
    }

    private void limparEstadoDeFrete() {
        precificacaoFreteViewModel.limpar();
    }

    private void processarRecalculoPorCabeca(@NonNull BigDecimal valorPorCabeca) {
        if (isRecalculoInvalido(valorPorCabeca)) {
            restaurarPropostaEFechamento();
            limparCampoValorKg();
            return;
        }
        recalcularPropostaPorCabeca(valorPorCabeca);
    }

    private void recalcularPropostaPorCabeca(@NonNull BigDecimal valorPorCabeca) {
        negociacaoViewModel.recalcularPropostaPorCabeca(
                cotacaoAtual, fechamentoAtual,
                valorPorCabeca, BigDecimal.valueOf(peso), quantidade,
                resolverValorParcialFrete(), resolverStatusFrete()
        );
    }

    private void processarRecalculoPorKg(@NonNull BigDecimal valorPorKg) {
        if (isRecalculoInvalido(valorPorKg)) {
            restaurarPropostaEFechamento();
            limparCampoValorCabeca();
            return;
        }
        recalcularPropostaPorKg(valorPorKg);
    }

    private void recalcularPropostaPorKg(@NonNull BigDecimal valorPorKg) {
        negociacaoViewModel.recalcularPropostaPorKg(
                cotacaoAtual, fechamentoAtual,
                valorPorKg, BigDecimal.valueOf(peso), quantidade,
                resolverValorParcialFrete(), resolverStatusFrete()
        );
    }

    private void notificarRacaNoAnimal(@NonNull RacaState raca) {
        animalViewModel.setRaca(raca);
    }

    private void notificarCategoriaNoAnimal(@NonNull CategoriaState categoria) {
        animalViewModel.setCategoria(categoria);
    }

    private void notificarSexoNoAnimal() {
        animalViewModel.setSexo(lerSexoSelecionado());
    }

    private void notificarIdadeNoAnimal() {
        animalViewModel.setIdade(lerIdadePreenchida());
    }

    private void recomendarTransportePorCategoria(@NonNull CategoriaState categoria) {
        transporteViewModel.recomendar(categoria.getId(), quantidade);
    }

    private void exibirInfoInicialDoAnimal() {
        exibirQuantidadeAnimais();
        exibirPesoMedioAnimais();
    }

    private void exibirQuantidadeAnimais() {
        setText(binding.textoValorQuantidade, formatarQuantidade(quantidade));
    }

    private void exibirPesoMedioAnimais() {
        setText(binding.textoValorPeso, formatarPesoMedio(peso));
    }

    private void exibirValoresCotadosNaTabela(@NonNull CotacaoState cotacao) {
        exibirValorCabecaCotado(cotacao);
        exibirValorKgCotado(cotacao);
    }

    private void exibirValorCabecaCotado(@NonNull CotacaoState cotacao) {
        setText(binding.textoValorEtapaCotado, formatarValorEtapa(cotacao.getValorPorCabeca()));
    }

    private void exibirValorKgCotado(@NonNull CotacaoState cotacao) {
        setText(binding.textoDescricaoEtapaCotado, formatarValorEtapa(cotacao.getValorPorKg()));
    }

    private void preencherCamposComValoresCotados(@NonNull CotacaoState cotacao) {
        if(!isPropostaIndisponivel(propostaAtual)) return;
        setTextSafely(binding.campoValorCabecaEntrada, formatCurrency(cotacao.getValorPorCabeca()), valorCabecaTextWatcher, valorKgTextWatcher);
        setTextSafely(binding.campoValorKgEntrada, formatCurrency(cotacao.getValorPorKg()), valorCabecaTextWatcher, valorKgTextWatcher);
    }

    private void sincronizarValoresDaPropostaNaTabela(@Nullable PropostaState proposta) {
        if (isPropostaIndisponivel(proposta)) {
            exibirPlaceholdersDePropostaNaTabela();
            return;
        }
        preencherCamposComValoresDaProposta(proposta);
        exibirValoresDaPropostaNaTabela(proposta);
    }

    private void preencherCamposComValoresDaProposta(@NonNull PropostaState proposta) {
        setTextSafely(binding.campoValorCabecaEntrada, formatCurrency(proposta.getValorPorCabeca()), valorCabecaTextWatcher, valorKgTextWatcher);
        setTextSafely(binding.campoValorKgEntrada, formatCurrency(proposta.getValorPorKg()), valorCabecaTextWatcher, valorKgTextWatcher);
    }

    private void exibirValoresDaPropostaNaTabela(@NonNull PropostaState proposta) {
        exibirValorCabecaProposta(proposta);
        exibirValorKgProposta(proposta);
        exibirBadgeFreteProposta(proposta);
        exibirValorTotalFornecedor(proposta);
    }

    private void exibirValorCabecaProposta(@NonNull PropostaState proposta) {
        setText(binding.textoValorEtapaPedido, formatarValorEtapa(proposta.getValorPorCabeca()));
    }

    private void exibirValorKgProposta(@NonNull PropostaState proposta) {
        setText(binding.textoDescricaoEtapaPedido, formatarValorEtapa(proposta.getValorPorKg()));
    }

    private void exibirBadgeFreteProposta(@NonNull PropostaState proposta) {
        setText(binding.textoBadgeEtapaFrete, formatarBadgeAdicional(proposta.getFretePorKg()));
    }

    private void exibirValorTotalFornecedor(@NonNull PropostaState proposta) {
        setText(binding.textoValorFornecedor, formatCurrency(proposta.getValorTotal()));
    }

    private void sincronizarValoresDeFechamentoNaTabela(@Nullable FechamentoState fechamento) {
        if (isFechamentoIndisponivel(fechamento)) {
            exibirPlaceholdersDeFechamentoNaTabela();
            return;
        }
        exibirValoresDeFechamentoNaTabela(fechamento);
    }

    private void exibirValoresDeFechamentoNaTabela(@NonNull FechamentoState fechamento) {
        exibirValorCabecaFechamento(fechamento);
        exibirValorKgFechamento(fechamento);
        exibirBadgeCorretorFechamento(fechamento);
        exibirValorTotalFechamento(fechamento);
    }

    private void exibirValorCabecaFechamento(@NonNull FechamentoState fechamento) {
        setText(binding.textoValorEtapaFinal, formatarValorEtapa(fechamento.getValorPorCabeca()));
    }

    private void exibirValorKgFechamento(@NonNull FechamentoState fechamento) {
        setText(binding.textoDescricaoEtapaFinal, formatarValorEtapa(fechamento.getValorPorKg()));
    }

    private void exibirBadgeCorretorFechamento(@NonNull FechamentoState fechamento) {
        setText(binding.textoBadgeEtapaCorretor, formatarBadgeAdicional(fechamento.getComissaoPorKg()));
    }

    private void exibirValorTotalFechamento(@NonNull FechamentoState fechamento) {
        setText(binding.textoValorTotal, formatCurrency(fechamento.getValorTotal()));
    }

    private void sincronizarCampoFrete(@NonNull FreteState frete) {
        if (isFreteManualEmEdicao(frete)) return;
        exibirValorNosCampoFrete(frete);
    }

    private void exibirValorNosCampoFrete(@NonNull FreteState frete) {
        setTextSafely(binding.campoFreteEntrada, binding.campoFreteLayout,
                formatCurrency(frete.getValorTotal()), formatarHelperFrete(frete.getValorParcial()),
                freteTextWatcher);
    }

    private void sincronizarCardFrete(@NonNull FreteState frete) {
        if (isFreteManualEmEdicao(frete)) return;
        exibirTituloFrete(frete);
        exibirDescricaoFrete(frete);
    }

    private void exibirTituloFrete(@NonNull FreteState frete) {
        setText(binding.textoTituloFrete, formatarTituloFrete(frete));
    }

    private void exibirDescricaoFrete(@NonNull FreteState frete) {
        setText(binding.textoDescricaoFrete, formatarDescricaoFrete(frete));
    }

    private void exibirCorretorNoCard(@NonNull CorretorState corretor) {
        exibirNomeCorretor(corretor);
        exibirDescricaoCorretor(corretor);
    }

    private void exibirNomeCorretor(@NonNull CorretorState corretor) {
        setText(binding.textoNomeCorretor, corretor.getNome());
    }

    private void exibirDescricaoCorretor(@NonNull CorretorState corretor) {
        setText(binding.textoDescricaoCorretor, formatarDescricaoCorretor(corretor));
    }

    private void exibirNomeEmpresaNoCard(@NonNull EmpresaState empresa) {
        setText(binding.textoNomeEmpresa, empresa.getNome());
    }

    private void exibirVariacaoPercentual(@NonNull Double variacao) {
        setText(binding.textoValorVariacao, formatarVariacaoPercentual(variacao));
    }

    private void sincronizarEstadoDoBotaoFinalizar() {
        binding.botaoFinalizar.setEnabled(isProntoParaFinalizar());
    }

    private void restaurarCardCorretorVazio() {
        restaurarNomeCorretorVazio();
        restaurarDescricaoCorretorVazio();
    }

    private void restaurarNomeCorretorVazio() {
        setText(binding.textoNomeCorretor, getString(R.string.texto_sem_corretor));
    }

    private void restaurarDescricaoCorretorVazio() {
        setText(binding.textoDescricaoCorretor, getString(R.string.descricao_sem_comissao));
    }

    private void restaurarCardFreteVazio() {
        restaurarTituloFreteVazio();
        restaurarDescricaoFreteVazio();
    }

    private void restaurarTituloFreteVazio() {
        setText(binding.textoTituloFrete, getString(R.string.titulo_frete));
    }

    private void restaurarDescricaoFreteVazio() {
        setText(binding.textoDescricaoFrete, getString(R.string.descricao_frete_vazio));
    }

    private void restaurarHelperTextFrete() {
        setHelperText(binding.campoFreteLayout, formatarHelperFrete(BigDecimal.ZERO));
    }

    private void restaurarPropostaEFechamento() {
        exibirPlaceholdersDePropostaNaTabela();
        exibirPlaceholdersDeFechamentoNaTabela();
    }

    private void restaurarEstadoDeFreteManual() {
        restaurarHelperTextFrete();
        restaurarPropostaEFechamento();
        restaurarCardFreteVazio();
        restaurarCardCorretorVazio();
        exibirValoresCotadosNaTabela(cotacaoAtual);
        preencherCamposComValoresCotados(cotacaoAtual);
    }

    private void restaurarEspecificacaoSalva(@NonNull AnimalState animal) {
        restaurarIdadeSalva(animal.getIdade());
        restaurarSexoSalvo(animal.getSexo());
    }

    private void restaurarIdadeSalva(@Nullable Integer idade) {
        if (!isIdadeValida(idade)) return;
        setTextSafely(binding.campoIdadeEntrada, String.valueOf(idade), especificacaoTextWatcher);
    }

    private void restaurarSexoSalvo(@Nullable String sexo) {
        if (!isSexoValido(sexo)) return;
        selectChip(binding.listaSexos, sexo);
    }

    private void exibirPlaceholdersDePropostaNaTabela() {
        exibirPlaceholderValorCabecaProposta();
        exibirPlaceholderValorKgProposta();
        exibirPlaceholderBadgeFreteProposta();
        exibirPlaceholderValorFornecedor();
    }

    private void exibirPlaceholderValorCabecaProposta() {
        setText(binding.textoValorEtapaPedido, getString(R.string.placeholder_valor_monetario));
    }

    private void exibirPlaceholderValorKgProposta() {
        setText(binding.textoDescricaoEtapaPedido, getString(R.string.placeholder_valor_por_kg_default));
    }

    private void exibirPlaceholderBadgeFreteProposta() {
        setText(binding.textoBadgeEtapaFrete, getString(R.string.placeholder_badge_diferenca_default));
    }

    private void exibirPlaceholderValorFornecedor() {
        setText(binding.textoValorFornecedor, getString(R.string.placeholder_valor_total));
    }

    private void exibirPlaceholdersDeFechamentoNaTabela() {
        exibirPlaceholderValorCabecaFechamento();
        exibirPlaceholderValorKgFechamento();
        exibirPlaceholderBadgeCorretorFechamento();
        exibirPlaceholderValorTotalFechamento();
    }

    private void exibirPlaceholderValorCabecaFechamento() {
        setText(binding.textoValorEtapaFinal, getString(R.string.placeholder_valor_monetario));
    }

    private void exibirPlaceholderValorKgFechamento() {
        setText(binding.textoDescricaoEtapaFinal, getString(R.string.placeholder_valor_por_kg_default));
    }

    private void exibirPlaceholderBadgeCorretorFechamento() {
        setText(binding.textoBadgeEtapaCorretor, getString(R.string.placeholder_badge_diferenca_default));
    }

    private void exibirPlaceholderValorTotalFechamento() {
        setText(binding.textoValorTotal, getString(R.string.placeholder_valor_total));
    }

    private void limparCampoValorKg() {
        setTextSafely(binding.campoValorKgEntrada, formatCurrency(BigDecimal.ZERO), valorCabecaTextWatcher, valorKgTextWatcher);
    }

    private void limparCampoValorCabeca() {
        setTextSafely(binding.campoValorCabecaEntrada, formatCurrency(BigDecimal.ZERO), valorCabecaTextWatcher, valorKgTextWatcher);
    }

    private void navegarParaSimulacaoDeFrete() {
        navegar(this, R.id.negociacaoFragment,
                NegociacaoFragmentDirections
                        .actionNegociacaoFragmentToSimulacaoFreteeFragment()
                        .setCargaTotal(quantidade).setPesoMedio((float) peso));
    }

    private void exibirBottomSheetEmpresa() {
        FragmentManager fm = getChildFragmentManager();
        if (fm.findFragmentByTag(TAG_BOTTOM_SHEET_EMPRESA) != null) return;
        new EmpresaBottomSheetDialogFragment().show(fm, TAG_BOTTOM_SHEET_EMPRESA);
    }

    private void exibirBottomSheetCorretor() {
        FragmentManager fm = getChildFragmentManager();
        if (fm.findFragmentByTag(TAG_BOTTOM_SHEET_CORRETOR) != null) return;
        new CorretorBottomSheetDialogFragment().show(fm, TAG_BOTTOM_SHEET_CORRETOR);
    }

    private void concluirNegociacao() {
        exibirSucessoFinalizacao();
        voltarParaTelaPrevious();
    }

    private void voltarParaTelaPrevious() {
        NavigationHelper.voltar(this);
    }

    private void exibirErroCategoriaParaFrete() {
        showSnackBarErro(binding.getRoot(), getString(R.string.aviso_selecione_categoria_frete));
    }

    private void exibirErroFreteObrigatorio() {
        showSnackBarErro(binding.getRoot(), getString(R.string.aviso_informe_frete_antes_corretor));
    }

    private void exibirErroCamposObrigatorios() {
        showSnackBarErro(binding.getRoot(), getString(R.string.aviso_preencha_campos_obrigatorios));
    }

    private void exibirSucessoFinalizacao() {
        showSnackBarSucesso(binding.getRoot(), getString(R.string.sucesso_negociacao_finalizada));
    }

    @NonNull
    private BigDecimal resolverComissaoAtual() {
        return isCorretorSelecionado(corretorAtual) ? corretorAtual.getComissao() : BigDecimal.ZERO;
    }

    @NonNull
    private BigDecimal resolverValorParcialFrete() {
        return freteAtual != null ? freteAtual.getValorParcial() : BigDecimal.ZERO;
    }

    @NonNull
    private StatusFrete resolverStatusFrete() {
        return freteAtual != null ? freteAtual.getFreteState() : StatusFrete.NAO_SELECIONADO;
    }

    @NonNull
    private BigDecimal lerValorTotalFrete() {
        return parseDecimal(binding.campoFreteEntrada);
    }

    @NonNull
    private BigDecimal lerValorPorCabeca() {
        return parseDecimal(binding.campoValorCabecaEntrada);
    }

    @NonNull
    private BigDecimal lerValorPorKg() {
        return parseDecimal(binding.campoValorKgEntrada);
    }

    @Nullable
    private String lerSexoSelecionado() {
        return getCheckedChipText(binding.listaSexos);
    }

    @Nullable
    private Integer lerIdadePreenchida() {
        return temIdadePreenchida() ? parseInt(binding.campoIdadeEntrada) : null;
    }

    private boolean temIdadePreenchida() {
        return isNotEmpty(binding.campoIdadeEntrada);
    }

    private boolean temSexoSelecionado() {
        return isNotEmpty(lerSexoSelecionado());
    }

    private boolean isFreteNaoPreenchido() {
        return isEmpty(binding.campoFreteEntrada);
    }

    private boolean isCategoriaAusente() {
        return !isCategoriaSelecionada(categoriaAtual);
    }

    private boolean isAnimalValido(@Nullable AnimalState animal) {
        return isNotNull(animal);
    }

    private boolean isIdadeValida(@Nullable Integer idade) {
        return isNotNull(idade);
    }

    private boolean isSexoValido(@Nullable String sexo) {
        return isNotNull(sexo);
    }

    private boolean isCotacaoValida(@Nullable CotacaoState cotacao) {
        return isNotNull(cotacao);
    }

    private boolean isFreteValido(@Nullable FreteState frete) {
        return isNotNull(frete);
    }

    private boolean isCategoriaSelecionada(@Nullable CategoriaState categoria) {
        return isNotNull(categoria);
    }

    private boolean isRacaSelecionada(@Nullable RacaState raca) {
        return isNotNull(raca);
    }

    private boolean isCorretorSelecionado(@Nullable CorretorState corretor) {
        return isNotNull(corretor);
    }

    private boolean isEmpresaSelecionada(@Nullable EmpresaState empresa) {
        return isNotNull(empresa);
    }

    private boolean isValorZero(@NonNull BigDecimal valor) {
        return isEmpty(valor);
    }

    private boolean isFreteManualEmEdicao(@NonNull FreteState frete) {
        return frete.getFreteState() == StatusFrete.MANUAL && !isFreteNaoPreenchido();
    }

    private boolean isFreteSimulado(@Nullable FreteState frete) {
        return frete != null && frete.getFreteState() == StatusFrete.SIMULADO;
    }

    private boolean isPropostaIndisponivel(@Nullable PropostaState proposta) {
        return proposta == null || !proposta.isFreteDescontado();
    }

    private boolean isFechamentoIndisponivel(@Nullable FechamentoState fechamento) {
        return fechamento == null || !fechamento.isComissaoAplicada();
    }

    private boolean isRecalculoInvalido(@NonNull BigDecimal valor) {
        return isValorZero(valor) || !isCotacaoValida(cotacaoAtual) || !isFreteValido(freteAtual);
    }

    private boolean isFormularioCompleto() {
        return temIdadePreenchida()
                && temSexoSelecionado()
                && isCategoriaSelecionada(categoriaAtual)
                && isRacaSelecionada(racaAtual);
    }

    private boolean isNegociacaoCompleta() {
        return isCotacaoValida(cotacaoAtual)
                && !isPropostaIndisponivel(propostaAtual)
                && !isFechamentoIndisponivel(fechamentoAtual);
    }

    private boolean isProntoParaFinalizar() {
        return isFormularioCompleto() && isNegociacaoCompleta();
    }

    @NonNull
    private String formatarQuantidade(int quantidade) {
        return String.format(Locale.getDefault(), "%d", quantidade);
    }

    @NonNull
    private String formatarPesoMedio(double peso) {
        return String.format(Locale.getDefault(), "%.2f", peso);
    }

    @NonNull
    private String formatarValorEtapa(@NonNull BigDecimal valor) {
        return String.format(Locale.getDefault(), "R$ %s", formatCurrency(valor));
    }

    @NonNull
    private String formatarBadgeAdicional(@NonNull BigDecimal valor) {
        return String.format(Locale.getDefault(), "+R$ %s/kg", formatCurrency(valor));
    }

    @NonNull
    private String formatarTituloFrete(@NonNull FreteState frete) {
        return String.format(Locale.getDefault(), "R$ %s/c", formatCurrency(frete.getValorTotal()));
    }

    @NonNull
    private String formatarDescricaoFrete(@NonNull FreteState frete) {
        return String.format(Locale.getDefault(), "R$ %s/kg", formatCurrency(frete.getValorParcial()));
    }

    @NonNull
    private String formatarHelperFrete(@NonNull BigDecimal incidencia) {
        return getString(R.string.helper_frete, formatCurrency(incidencia));
    }

    @NonNull
    private String formatarDescricaoCorretor(@NonNull CorretorState corretor) {
        return String.format(Locale.getDefault(), "R$ %s/%s - R$ %s/t",
                formatCurrency(corretor.getComissao()), corretor.getTipoComissao(),
                formatCurrency(corretor.getComissao().multiply(BigDecimal.valueOf(quantidade))));
    }

    @NonNull
    private String formatarVariacaoPercentual(@NonNull Double variacao) {
        return String.format(Locale.getDefault(), "%.2f%%", variacao);
    }
}