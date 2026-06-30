package com.omni.negociacaobezerros.ui.fragments.layout;

import static com.omni.negociacaobezerros.helpers.NavigationHelper.navigateBackOnToolbar;
import static com.omni.negociacaobezerros.helpers.NavigationHelper.popTo;
import static com.omni.negociacaobezerros.utils.format.Decimals.ARREDONDAMENTO_PADRAO;
import static com.omni.negociacaobezerros.utils.format.Decimals.ESCALA_PERCENTUAL;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.omni.negociacaobezerros.R;
import com.omni.negociacaobezerros.data.repositories.synchronizable.NegociacaoAnimalRepository;
import com.omni.negociacaobezerros.data.repositories.synchronizable.NegociacaoGadoRepository;
import com.omni.negociacaobezerros.data.source.local.entities.NegociacaoAnimal;
import com.omni.negociacaobezerros.data.source.local.entities.NegociacaoGado;
import com.omni.negociacaobezerros.databinding.FragmentFinalizacaoBinding;
import com.omni.negociacaobezerros.helpers.AlertHelper;
import com.omni.negociacaobezerros.helpers.TaskHelper;
import com.omni.negociacaobezerros.ui.states.CorretorUiState;
import com.omni.negociacaobezerros.ui.states.CotacaoUiState;
import com.omni.negociacaobezerros.ui.states.EmpresaUiState;
import com.omni.negociacaobezerros.ui.states.FreteUiState;
import com.omni.negociacaobezerros.ui.states.NegociacaoAnimalResumoUiState;
import com.omni.negociacaobezerros.ui.states.NegociacaoAnimalUiState;
import com.omni.negociacaobezerros.ui.states.PrecificacaoBezerroUiState;
import com.omni.negociacaobezerros.ui.viewmodels.CorretorViewModel;
import com.omni.negociacaobezerros.ui.viewmodels.CotacaoViewModel;
import com.omni.negociacaobezerros.ui.viewmodels.EmpresaViewModel;
import com.omni.negociacaobezerros.ui.viewmodels.FreteViewModel;
import com.omni.negociacaobezerros.ui.viewmodels.NegociacaoAnimalViewModel;
import com.omni.negociacaobezerros.ui.viewmodels.NegociacaoViewModel;
import com.omni.negociacaobezerros.utils.document.Files;
import com.omni.negociacaobezerros.utils.document.pdf.PdfGenerator;
import com.omni.negociacaobezerros.utils.document.pdf.PdfPageConfig;
import com.omni.negociacaobezerros.utils.document.pdf.TextAlignment;
import com.omni.negociacaobezerros.utils.document.pdf.bands.FooterBand;
import com.omni.negociacaobezerros.utils.document.pdf.bands.RowBand;
import com.omni.negociacaobezerros.utils.document.pdf.bands.SpacerBand;
import com.omni.negociacaobezerros.utils.document.pdf.bands.TitleBand;
import com.omni.negociacaobezerros.utils.format.Numbers;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FinalizacaoFragment extends Fragment {
    private static final String MIME_PDF = "application/pdf";
    private static final String TIPO_VALOR_PADRAO = "POR_CABECA";
    private static final String STATUS_PADRAO = "PENDENTE";

    @Inject NegociacaoGadoRepository negociacaoGadoRepository;
    @Inject NegociacaoAnimalRepository negociacaoAnimalRepository;
    @Inject TaskHelper taskHelper;

    private FragmentFinalizacaoBinding binding;
    private EmpresaViewModel empresaViewModel;
    private CorretorViewModel corretorViewModel;
    private CotacaoViewModel cotacaoViewModel;
    private NegociacaoViewModel negociacaoViewModel;
    private FreteViewModel freteViewModel;
    private NegociacaoAnimalViewModel negociacaoAnimalViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFinalizacaoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewModels();
        navigation();
        setupListeners();
        persistirNegociacao();
    }

    private void initViewModels() {
        empresaViewModel = new ViewModelProvider(requireActivity()).get(EmpresaViewModel.class);
        corretorViewModel = new ViewModelProvider(requireActivity()).get(CorretorViewModel.class);
        cotacaoViewModel = new ViewModelProvider(requireActivity()).get(CotacaoViewModel.class);
        negociacaoViewModel = new ViewModelProvider(requireActivity()).get(NegociacaoViewModel.class);
        freteViewModel = new ViewModelProvider(requireActivity()).get(FreteViewModel.class);
        negociacaoAnimalViewModel = new ViewModelProvider(requireActivity()).get(NegociacaoAnimalViewModel.class);
    }

    private void navigation(){
        back();
    }

    private void back() {
        navigateBackOnToolbar(this, binding.constraintLayoutFinalizacaoToolbar);
    }

    private void setupListeners() {
        binding.buttonFinalizacaoCompartilhar.setOnClickListener(v -> onCompartilharClicked());
        binding.buttonFinalizacaoExportarPdf.setOnClickListener(v -> onExportarPdfClicked());
        binding.buttonFinalizacaoNovaNegociacao.setOnClickListener(v -> onNovaNegociacaoClicked());
    }

    private void persistirNegociacao() {
        NegociacaoGado negociacaoGado = construirNegociacaoGado();
        List<NegociacaoAnimal> animais = construirAnimais(negociacaoGado.getIdNegociacaoGado());
        taskHelper.execute(() -> salvarNegociacao(negociacaoGado, animais), this::onNegociacaoSalva, this::onErroPersistencia);
    }

    private NegociacaoGado salvarNegociacao(NegociacaoGado negociacaoGado, List<NegociacaoAnimal> animais) {
        negociacaoGadoRepository.insert(negociacaoGado);
        if (!animais.isEmpty()) negociacaoAnimalRepository.insertAll(animais);
        return negociacaoGado;
    }

    private void onNegociacaoSalva(NegociacaoGado negociacaoGado) {
        bindResumo(negociacaoGado);
        binding.buttonFinalizacaoNovaNegociacao.setEnabled(true);
    }

    private void onErroPersistencia(Throwable erro) {
        AlertHelper.showSnackBarErro(binding.getRoot(), erro.getMessage());
    }

    private void bindResumo(NegociacaoGado negociacaoGado) {
        EmpresaUiState empresa = empresaViewModel.getSelecionada().getValue();
        binding.textViewFinalizacaoSubtituloSucesso.setText(
                getString(R.string.finalizacao_subtitulo_empresa, empresa != null ? empresa.getNome() : ""));
        binding.textViewFinalizacaoFornecedorValor.setText(Numbers.formatCurrency(BigDecimal.valueOf(negociacaoGado.getValorCabNeg())));
        binding.textViewFinalizacaoFreteValor.setText(Numbers.formatCurrency(BigDecimal.valueOf(negociacaoGado.getValorFrete())));
        binding.textViewFinalizacaoCorretorValor.setText(Numbers.formatCurrency(BigDecimal.valueOf(negociacaoGado.getValorComissao())));
        binding.textViewFinalizacaoTotalValor.setText(Numbers.formatCurrency(BigDecimal.valueOf(negociacaoGado.getValorTotal())));
        bindDelta(negociacaoGado);
    }

    private void bindDelta(NegociacaoGado negociacaoGado) {
        BigDecimal cotadoTotal = BigDecimal.valueOf(negociacaoGado.getValorCabRef())
                .multiply(BigDecimal.valueOf(negociacaoGado.getQtdeAnimais()));
        BigDecimal total = BigDecimal.valueOf(negociacaoGado.getValorTotal());
        BigDecimal delta = total.subtract(cotadoTotal);
        BigDecimal percentual = calcularPercentual(delta, cotadoTotal);
        String sinal = delta.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
        binding.textViewFinalizacaoTotalDelta.setText(getString(R.string.finalizacao_total_delta,
                sinal + Numbers.formatCurrency(delta), Numbers.formatDouble(percentual.doubleValue())));
    }

    private BigDecimal calcularPercentual(BigDecimal delta, BigDecimal cotadoTotal) {
        if (cotadoTotal.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return delta.divide(cotadoTotal, ESCALA_PERCENTUAL, ARREDONDAMENTO_PADRAO).multiply(BigDecimal.valueOf(100));
    }

    private void onCompartilharClicked() {
        executarComPdf(this::compartilharArquivo);
    }

    private void onExportarPdfClicked() {
        executarComPdf(this::exportarArquivo);
    }

    private void executarComPdf(Consumer<File> acao) {
        NegociacaoGado negociacaoGado = construirNegociacaoGado();
        taskHelper.execute(() -> gerarPdf(negociacaoGado), acao::accept, this::onErroPersistencia);
    }

    private void compartilharArquivo(File arquivo) {
        Files.share(requireActivity(), arquivo, MIME_PDF, getString(R.string.acao_compartilhar));
    }

    private void exportarArquivo(File arquivo) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            compartilharArquivo(arquivo);
            return;
        }
        salvarViaMediaStore(arquivo);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void salvarViaMediaStore(File arquivo) {
        try {
            Files.save(requireContext(), arquivo, MIME_PDF);
            AlertHelper.showSnackBarSucesso(binding.getRoot(), getString(R.string.sucesso_negociacao_finalizada));
        } catch (IOException e) {
            AlertHelper.showSnackBarErro(binding.getRoot(), e.getMessage());
        }
    }

    private File gerarPdf(NegociacaoGado negociacaoGado) throws IOException {
        PdfGenerator generator = new PdfGenerator(PdfPageConfig.a4Portrait());
        generator.addBand(new TitleBand(getString(R.string.finalizacao_pdf_titulo)));
        generator.addBand(linhaResumo(getString(R.string.resumo_fornecedor_label), Numbers.formatCurrency(BigDecimal.valueOf(negociacaoGado.getValorCabNeg()))));
        generator.addBand(linhaResumo(getString(R.string.resumo_frete_label), Numbers.formatCurrency(BigDecimal.valueOf(negociacaoGado.getValorFrete()))));
        generator.addBand(linhaResumo(getString(R.string.resumo_corretor_label), Numbers.formatCurrency(BigDecimal.valueOf(negociacaoGado.getValorComissao()))));
        generator.addBand(new SpacerBand(8f));
        generator.addBand(linhaResumo(getString(R.string.finalizacao_total_label), Numbers.formatCurrency(BigDecimal.valueOf(negociacaoGado.getValorTotal()))));
        generator.setFooter(new FooterBand("NEG-" + negociacaoGado.getIdNegociacaoGado()));
        String fileName = getString(R.string.finalizacao_pdf_nome_arquivo, negociacaoGado.getIdNegociacaoGado());
        return generator.generate(requireContext(), fileName);
    }

    private RowBand linhaResumo(String label, String valor) {
        return new RowBand(11f, 18f,
                new RowBand.Column(label, 1f, TextAlignment.LEFT),
                new RowBand.Column(valor, 1f, TextAlignment.RIGHT));
    }

    private void onNovaNegociacaoClicked() {
        limparEstadoNegociacao();
        popTo(this, R.id.homeFragment);
    }

    private void limparEstadoNegociacao() {
        empresaViewModel.limparSelecao();
        corretorViewModel.limpar();
        cotacaoViewModel.limpar();
        freteViewModel.limpar();
        negociacaoViewModel.limpar();
        negociacaoAnimalViewModel.limpar();
    }

    private NegociacaoGado construirNegociacaoGado() {
        EmpresaUiState empresa = empresaViewModel.getSelecionada().getValue();
        CorretorUiState corretor = corretorViewModel.getSelecionado().getValue();
        CotacaoUiState cotacao = cotacaoViewModel.getCotacaoState().getValue();
        PrecificacaoBezerroUiState proposta = negociacaoViewModel.getPropostaState().getValue();
        PrecificacaoBezerroUiState fechamento = negociacaoViewModel.getFechamentoState().getValue();
        FreteUiState frete = freteViewModel.getFreteState().getValue();
        BigDecimal peso = cotacaoViewModel.getPeso().getValue();
        Integer quantidade = cotacaoViewModel.getQuantidade().getValue();
        NegociacaoAnimalResumoUiState resumo = negociacaoAnimalViewModel.getResumoState().getValue();

        NegociacaoGado negociacaoGado = new NegociacaoGado();
        negociacaoGado.setIdNegociacaoGado(gerarIdNegociacao());
        negociacaoGado.setIdEmpresa(empresa != null ? empresa.getId() : 0);
        negociacaoGado.setIdCorretor(corretor != null ? corretor.getId() : 0);
        negociacaoGado.setIdCategoriaNeg(0);
        negociacaoGado.setDescricao(descricaoNegociacao(empresa, quantidade));
        negociacaoGado.setDataNegociacao(new Date());
        negociacaoGado.setValorKgRef(valorOuZero(cotacao != null ? cotacao.getValorPorQuilo() : null));
        negociacaoGado.setValorCabRef(valorOuZero(cotacao != null ? cotacao.getValorPorCabeca() : null));
        negociacaoGado.setPesoMedio(pesoMedio(resumo, peso));
        negociacaoGado.setQtdeAnimais(quantidade != null ? quantidade : 0);
        negociacaoGado.setTipoValor(TIPO_VALOR_PADRAO);
        negociacaoGado.setValorAnimal(valorOuZero(fechamento != null ? fechamento.getValorPorCabeca() : null));
        negociacaoGado.setValorTotal(valorTotal(resumo, fechamento, quantidade));
        negociacaoGado.setValorFrete(valorOuZero(frete != null ? frete.getValorTotal() : null));
        negociacaoGado.setValorComissao(valorComissao(corretor, peso, quantidade));
        negociacaoGado.setValorCabNeg(valorOuZero(proposta != null ? proposta.getValorPorCabeca() : null));
        negociacaoGado.setValorKgNeg(valorOuZero(proposta != null ? proposta.getValorPorQuilo() : null));
        negociacaoGado.setStatus(STATUS_PADRAO);
        return negociacaoGado;
    }

    private List<NegociacaoAnimal> construirAnimais(int idNegociacaoGado) {
        List<NegociacaoAnimalUiState> animaisUi = negociacaoAnimalViewModel.getAnimaisState().getValue();
        List<NegociacaoAnimal> animais = new ArrayList<>();
        if (animaisUi == null) return animais;
        for (NegociacaoAnimalUiState animal : animaisUi) {
            animais.add(new NegociacaoAnimal(animal.getId(), idNegociacaoGado, animal.getPeso(),
                    valorOuZero(animal.getValorPorQuilo()), valorOuZero(animal.getValorPorCabeca())));
        }
        return animais;
    }

    private int gerarIdNegociacao() {
        return (int) System.currentTimeMillis();
    }

    private String descricaoNegociacao(EmpresaUiState empresa, Integer quantidade) {
        return String.format(Locale.getDefault(), "%s - %d cab.",
                empresa != null ? empresa.getNome() : "", quantidade != null ? quantidade : 0);
    }

    private double pesoMedio(NegociacaoAnimalResumoUiState resumo, BigDecimal pesoCotacao) {
        if (resumo != null && resumo.getProgresso() > 0) return resumo.getPesoMedioKg();
        return pesoCotacao != null ? pesoCotacao.doubleValue() : 0.0;
    }

    private double valorTotal(NegociacaoAnimalResumoUiState resumo, PrecificacaoBezerroUiState fechamento, Integer quantidade) {
        if (resumo != null && resumo.getProgresso() > 0) return valorOuZero(resumo.getValorTotal());
        if (fechamento == null || quantidade == null) return 0.0;
        return fechamento.getValorPorCabeca().multiply(BigDecimal.valueOf(quantidade)).doubleValue();
    }

    private double valorComissao(CorretorUiState corretor, BigDecimal peso, Integer quantidade) {
        if (corretor == null || peso == null || quantidade == null) return 0.0;
        return corretor.getComissao().multiply(peso).multiply(BigDecimal.valueOf(quantidade)).doubleValue();
    }

    private double valorOuZero(BigDecimal valor) {
        return valor != null ? valor.doubleValue() : 0.0;
    }
}
