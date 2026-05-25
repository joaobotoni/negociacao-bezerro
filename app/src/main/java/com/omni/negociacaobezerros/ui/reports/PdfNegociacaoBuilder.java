package com.omni.negociacaobezerros.ui.reports;

import static com.omni.negociacaobezerros.ui.helpers.FormatHelper.formatCurrency;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.omni.negociacaobezerros.ui.helpers.FormatHelper;
import com.omni.negociacaobezerros.ui.state.animal.AnimalState;
import com.omni.negociacaobezerros.ui.state.empresa.CorretorState;
import com.omni.negociacaobezerros.ui.state.empresa.EmpresaState;
import com.omni.negociacaobezerros.ui.state.frete.FreteState;
import com.omni.negociacaobezerros.ui.state.negociacao.CotacaoState;
import com.omni.negociacaobezerros.ui.state.negociacao.FechamentoState;
import com.omni.negociacaobezerros.ui.state.negociacao.PropostaState;
import com.omni.negociacaobezerros.utils.pdf.PdfGenerator;
import com.omni.negociacaobezerros.utils.pdf.PdfPageConfig;
import com.omni.negociacaobezerros.utils.pdf.TextAlignment;
import com.omni.negociacaobezerros.utils.pdf.bands.FooterBand;
import com.omni.negociacaobezerros.utils.pdf.bands.RowBand;
import com.omni.negociacaobezerros.utils.pdf.bands.SpacerBand;
import com.omni.negociacaobezerros.utils.pdf.bands.TitleBand;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class PdfNegociacaoBuilder {
    private PdfNegociacaoBuilder() {
    }
    @NonNull
    public static File gerarRelatorio(@NonNull Context context, @Nullable EmpresaState empresaState, @Nullable CorretorState corretor, @Nullable AnimalState animalState, @Nullable CotacaoState cotacao, @Nullable PropostaState proposta, @Nullable FechamentoState fechamento, @Nullable FreteState frete) throws IOException {
        return montarEGerarPdf(context, empresaState, corretor, animalState, cotacao, proposta, fechamento, frete, null);
    }

    @NonNull
    public static File gerarRelatorio(@NonNull Context context, @Nullable EmpresaState empresaState, @Nullable CorretorState corretor, @Nullable AnimalState animalState, @Nullable CotacaoState cotacao, @Nullable PropostaState proposta, @Nullable FechamentoState fechamento, @NonNull BigDecimal freteManual) throws IOException {
        return montarEGerarPdf(context, empresaState, corretor, animalState, cotacao, proposta, fechamento, null, freteManual);
    }
    @NonNull
    private static File montarEGerarPdf(@NonNull Context context, @Nullable EmpresaState empresaState, @Nullable CorretorState corretor, @Nullable AnimalState animalState, @Nullable CotacaoState cotacao, @Nullable PropostaState proposta, @Nullable FechamentoState fechamento, @Nullable FreteState frete, @Nullable BigDecimal freteManual) throws IOException {

        final String dataHoraGeracao = Formatter.formatarDataHoraAtual();
        final boolean dadosAnimalDisponiveis = Validator.dadosAnimalDisponiveis(animalState);
        final boolean dadosCotacaoDisponiveis = Validator.dadosCotacaoDisponiveis(cotacao);
        final boolean dadosPropostaDisponiveis = Validator.dadosPropostaDisponiveis(proposta);
        final boolean freteCalculadoDisponivel = Validator.freteCalculadoEstaDisponivel(frete);
        final boolean freteManualFoiInformado = Validator.freteManualFoiInformado(freteManual);
        final boolean algumFreteFoiInformado = freteCalculadoDisponivel || freteManualFoiInformado;
        final boolean dadosCorretorDisponiveis = Validator.dadosCorretorDisponiveis(corretor);
        final boolean dadosFechamentoDisponiveis = Validator.dadosFechamentoDisponiveis(fechamento);

        final PdfGenerator geradorPdf = new PdfGenerator(PdfPageConfig.a4Portrait());
        geradorPdf.setFooter(new FooterBand("Negociação  •  " + dataHoraGeracao));

        Renderer.adicionarBandaCabecalhoComMetadados(geradorPdf, dataHoraGeracao, empresaState);

        if (dadosAnimalDisponiveis)
            Renderer.adicionarSecaoEspecificacaoAnimal(geradorPdf, animalState);

        if (dadosCotacaoDisponiveis) Renderer.adicionarSecaoDadosCotacao(geradorPdf, cotacao);

        if (dadosPropostaDisponiveis) Renderer.adicionarSecaoValoresProposta(geradorPdf, proposta);

        if (algumFreteFoiInformado)
            Renderer.adicionarSecaoDetalhamentoFrete(geradorPdf, frete, freteManual, proposta);

        if (dadosCorretorDisponiveis && dadosCotacaoDisponiveis)
            Renderer.adicionarSecaoComissaoCorretor(geradorPdf, corretor, cotacao, fechamento);

        if (dadosFechamentoDisponiveis)
            Renderer.adicionarSecaoValoresFechamento(geradorPdf, fechamento);

        if (dadosFechamentoDisponiveis || dadosPropostaDisponiveis)
            Renderer.adicionarBandaTotalGeralNegociacao(geradorPdf, dadosFechamentoDisponiveis ? fechamento.getValorTotal() : proposta.getValorTotal());

        return geradorPdf.generate(context, "negociacao_" + System.currentTimeMillis() + ".pdf");
    }

    private static final class Validator {

        static boolean dadosAnimalDisponiveis(@Nullable AnimalState animalState) {
            return animalState != null;
        }

        static boolean dadosCotacaoDisponiveis(@Nullable CotacaoState cotacao) {
            return cotacao != null;
        }

        static boolean dadosPropostaDisponiveis(@Nullable PropostaState proposta) {
            return proposta != null;
        }

        static boolean dadosFechamentoDisponiveis(@Nullable FechamentoState fechamento) {
            return fechamento != null;
        }

        static boolean dadosCorretorDisponiveis(@Nullable CorretorState corretor) {
            return corretor != null;
        }

        static boolean empresaTemNomeValido(@Nullable EmpresaState empresaState) {
            return empresaState != null && empresaState.getNome() != null && !empresaState.getNome().isEmpty();
        }

        static boolean freteCalculadoEstaDisponivel(@Nullable FreteState frete) {
            return frete != null && frete.getValorTotal() != null && frete.getValorTotal().compareTo(BigDecimal.ZERO) > 0;
        }

        static boolean freteManualFoiInformado(@Nullable BigDecimal freteManual) {
            return freteManual != null && freteManual.compareTo(BigDecimal.ZERO) > 0;
        }

        static boolean freteTemIncidenciaDescrita(@NonNull FreteState frete) {
            return frete.getValorParcial() != null;
        }

        static boolean propostaContemFreteDescontado(@NonNull PropostaState proposta) {
            return proposta.isFreteDescontado() && proposta.getFretePorKg() != null;
        }

        static boolean fechamentoTemComissaoCalculada(@NonNull FechamentoState fechamento) {
            return fechamento.isComissaoAplicada() && fechamento.getComissaoPorKg() != null;
        }

        static boolean corretorTemComissaoPorCabeca(@NonNull CorretorState corretor) {
            return corretor.getComissao() != null;
        }
    }

    private static final class Formatter {
        static final String PREFIXO_REAL = "R$ ";
        static final String SUFIXO_CABECAS = " cab.";
        static final String MOEDA_VALOR_ZERO = PREFIXO_REAL + "0,00";
        static final String TEXTO_VALOR_AUSENTE = "-";

        @NonNull
        static String formatarMoedaBrasileira(@Nullable BigDecimal valorMonetario) {
            if (valorMonetario == null) return MOEDA_VALOR_ZERO;
            return PREFIXO_REAL + formatCurrency(valorMonetario);
        }

        static int intSeguroOuZero(@Nullable Integer valor) {
            return valor != null ? valor : 0;
        }

        @NonNull
        static String textoSeguroOuPlaceholder(@Nullable String texto) {
            return (texto != null && !texto.isEmpty()) ? texto : TEXTO_VALOR_AUSENTE;
        }

        @NonNull
        static String formatarDataHoraAtual() {
            return new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("pt", "BR")).format(new Date());
        }
    }

    private static final class Renderer {

        static final float TAMANHO_FONTE_CABECALHO_SECAO = 11f;
        static final float TAMANHO_FONTE_LINHA_NORMAL = 10f;
        static final float TAMANHO_FONTE_LINHA_TOTAL = 12f;
        static final float TAMANHO_FONTE_METADADOS = 10f;

        static final float ALTURA_LINHA_CABECALHO_SECAO = 26f;
        static final float ALTURA_LINHA_NORMAL = 22f;
        static final float ALTURA_LINHA_TOTAL = 26f;

        static final float PESO_COLUNA_LABEL = 2.5f;
        static final float PESO_COLUNA_VALOR = 2.5f;
        static final float PESO_COLUNA_LABEL_TOTAL = 3.5f;
        static final float PESO_COLUNA_VALOR_TOTAL = 1.5f;

        static final float ESPACAMENTO_ENTRE_SECOES = 8f;
        static final float ESPACAMENTO_APOS_TITULO = 6f;
        static final float ESPACAMENTO_APOS_METADADOS = 14f;
        static final float ESPACAMENTO_ANTES_TOTAL = 10f;

        static void adicionarBandaCabecalhoComMetadados(@NonNull PdfGenerator geradorPdf, @NonNull String dataHoraGeracao, @Nullable EmpresaState empresaState) {
            geradorPdf.addBand(new TitleBand("Negociação de Bezerros"));
            geradorPdf.addBand(new SpacerBand(ESPACAMENTO_APOS_TITULO));
            final String nomeFazenda = Validator.empresaTemNomeValido(empresaState) ? "Fazenda: " + empresaState.getNome() : "";
            geradorPdf.addBand(new RowBand(TAMANHO_FONTE_METADADOS, ALTURA_LINHA_NORMAL, new RowBand.Column(nomeFazenda, PESO_COLUNA_LABEL, TextAlignment.LEFT), new RowBand.Column("Data: " + dataHoraGeracao, PESO_COLUNA_VALOR, TextAlignment.RIGHT)));
            geradorPdf.addBand(new SpacerBand(ESPACAMENTO_APOS_METADADOS));
        }

        static void adicionarSecaoEspecificacaoAnimal(@NonNull PdfGenerator geradorPdf, @NonNull AnimalState animalState) {
            adicionarBandaCabecalhoSecao(geradorPdf, "ESPECIFICAÇÃO DO ANIMAL");

            if (animalState.getSexo() != null)
                adicionarLinhaTexto(geradorPdf, "Sexo", animalState.getSexo());

            if (animalState.getCategoria() != null && animalState.getCategoria().getDescricao() != null)
                adicionarLinhaTexto(geradorPdf, "Categoria", animalState.getCategoria().getDescricao());

            if (animalState.getRaca() != null && animalState.getRaca().getDescricao() != null)
                adicionarLinhaTexto(geradorPdf, "Raça", animalState.getRaca().getDescricao());

            if (animalState.getIdade() != null)
                adicionarLinhaTexto(geradorPdf, "Idade/mês", animalState.getIdade().toString());

            adicionarEspacadorEntreSecoes(geradorPdf);
        }

        static void adicionarSecaoDadosCotacao(@NonNull PdfGenerator geradorPdf, @NonNull CotacaoState cotacao) {
            adicionarBandaCabecalhoSecao(geradorPdf, "COTAÇÃO");

            adicionarLinhaTexto(geradorPdf, "Quantidade", FormatHelper.formatInteger(Formatter.intSeguroOuZero(cotacao.getQuantidade())) + Formatter.SUFIXO_CABECAS);

            adicionarLinhaMoeda(geradorPdf, "Valor por kg", cotacao.getValorPorKg());
            adicionarLinhaMoeda(geradorPdf, "Valor por cabeça", cotacao.getValorPorCabeca());
        }

        static void adicionarSecaoValoresProposta(@NonNull PdfGenerator geradorPdf, @NonNull PropostaState proposta) {
            adicionarEspacadorEntreSecoes(geradorPdf);
            adicionarBandaCabecalhoSecao(geradorPdf, "PROPOSTA");

            adicionarLinhaMoeda(geradorPdf, "Valor por kg", proposta.getValorPorKg());
            adicionarLinhaMoeda(geradorPdf, "Valor por cabeça", proposta.getValorPorCabeca());
            adicionarLinhaMoeda(geradorPdf, "Valor total", proposta.getValorTotal());
        }

        static void adicionarSecaoDetalhamentoFrete(@NonNull PdfGenerator geradorPdf, @Nullable FreteState frete, @Nullable BigDecimal freteManual, @Nullable PropostaState proposta) {
            adicionarEspacadorEntreSecoes(geradorPdf);
            adicionarBandaCabecalhoSecao(geradorPdf, "FRETE");

            if (Validator.freteManualFoiInformado(freteManual)) {
                adicionarLinhaMoeda(geradorPdf, "Total frete", freteManual);
                if (proposta != null && Validator.propostaContemFreteDescontado(proposta))
                    adicionarLinhaMoeda(geradorPdf, "Frete por kg", proposta.getFretePorKg());
            } else {
                final FreteState freteValidado = Objects.requireNonNull(frete, "FreteState nulo inesperado após guard freteCalculadoEstaDisponivel");
                if (Validator.freteTemIncidenciaDescrita(freteValidado))
                    adicionarLinhaMoeda(geradorPdf, "Frete por kg", freteValidado.getValorParcial());
                adicionarLinhaMoeda(geradorPdf, "Total frete", freteValidado.getValorTotal());
            }
        }

        static void adicionarSecaoComissaoCorretor(@NonNull PdfGenerator geradorPdf, @NonNull CorretorState corretor, @NonNull CotacaoState cotacao, @Nullable FechamentoState fechamento) {
            adicionarEspacadorEntreSecoes(geradorPdf);
            adicionarBandaCabecalhoSecao(geradorPdf, "CORRETOR");

            adicionarLinhaTexto(geradorPdf, "Nome", Formatter.textoSeguroOuPlaceholder(corretor.getNome()));
            adicionarLinhaMoeda(geradorPdf, "Comissão por cabeça", corretor.getComissao());

            if (fechamento != null && Validator.fechamentoTemComissaoCalculada(fechamento))
                adicionarLinhaMoeda(geradorPdf, "Comissão por kg", fechamento.getComissaoPorKg());

            if (Validator.corretorTemComissaoPorCabeca(corretor)) {
                final BigDecimal totalComissaoCorretor = corretor.getComissao().multiply(BigDecimal.valueOf(Formatter.intSeguroOuZero(cotacao.getQuantidade())));
                adicionarLinhaMoeda(geradorPdf, "Total Comissão", totalComissaoCorretor);
            }
        }

        static void adicionarSecaoValoresFechamento(@NonNull PdfGenerator geradorPdf, @NonNull FechamentoState fechamento) {
            adicionarEspacadorEntreSecoes(geradorPdf);
            adicionarBandaCabecalhoSecao(geradorPdf, "FECHAMENTO");

            adicionarLinhaMoeda(geradorPdf, "Valor por kg", fechamento.getValorPorKg());
            adicionarLinhaMoeda(geradorPdf, "Valor por cabeça", fechamento.getValorPorCabeca());
        }

        static void adicionarBandaTotalGeralNegociacao(@NonNull PdfGenerator geradorPdf, @Nullable BigDecimal valorTotalNegociacao) {
            geradorPdf.addBand(new SpacerBand(ESPACAMENTO_ANTES_TOTAL));
            geradorPdf.addBand(new RowBand(TAMANHO_FONTE_LINHA_TOTAL, ALTURA_LINHA_TOTAL, new RowBand.Column("TOTAL GERAL", PESO_COLUNA_LABEL_TOTAL, TextAlignment.RIGHT), new RowBand.Column(Formatter.formatarMoedaBrasileira(valorTotalNegociacao), PESO_COLUNA_VALOR_TOTAL, TextAlignment.RIGHT)).withBottomLine(false));
        }

        private static void adicionarBandaCabecalhoSecao(@NonNull PdfGenerator geradorPdf, @NonNull String tituloSecao) {
            geradorPdf.addBand(new RowBand(TAMANHO_FONTE_CABECALHO_SECAO, ALTURA_LINHA_CABECALHO_SECAO, new RowBand.Column(tituloSecao, PESO_COLUNA_LABEL, TextAlignment.LEFT), new RowBand.Column("Valor", PESO_COLUNA_VALOR, TextAlignment.RIGHT)).asHeader());
        }

        private static void adicionarLinhaTexto(@NonNull PdfGenerator geradorPdf, @NonNull String labelColuna, @NonNull String valorColuna) {
            geradorPdf.addBand(new RowBand(TAMANHO_FONTE_LINHA_NORMAL, ALTURA_LINHA_NORMAL, new RowBand.Column(labelColuna, PESO_COLUNA_LABEL, TextAlignment.LEFT), new RowBand.Column(valorColuna, PESO_COLUNA_VALOR, TextAlignment.RIGHT)));
        }

        private static void adicionarLinhaMoeda(@NonNull PdfGenerator geradorPdf, @NonNull String labelColuna, @Nullable BigDecimal valorMonetario) {
            adicionarLinhaTexto(geradorPdf, labelColuna, Formatter.formatarMoedaBrasileira(valorMonetario));
        }

        private static void adicionarEspacadorEntreSecoes(@NonNull PdfGenerator geradorPdf) {
            geradorPdf.addBand(new SpacerBand(ESPACAMENTO_ENTRE_SECOES));
        }
    }
}