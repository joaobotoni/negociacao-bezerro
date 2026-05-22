package com.omni.negociacaobezerros.ui.reports;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.omni.negociacaobezerros.ui.helpers.FormatHelper;
import com.omni.negociacaobezerros.ui.state.frete.FreteState;
import com.omni.negociacaobezerros.ui.state.frete.StatusFrete;
import com.omni.negociacaobezerros.ui.state.negociacao.CotacaoState;
import com.omni.negociacaobezerros.ui.state.negociacao.FechamentoState;
import com.omni.negociacaobezerros.ui.state.negociacao.PropostaState;
import com.omni.negociacaobezerros.utils.pdf.PdfGenerator;
import com.omni.negociacaobezerros.utils.pdf.PdfPageConfig;
import com.omni.negociacaobezerros.utils.pdf.TextAlignment;
import com.omni.negociacaobezerros.utils.pdf.bands.FooterBand;
import com.omni.negociacaobezerros.utils.pdf.bands.RowBand;
import com.omni.negociacaobezerros.utils.pdf.bands.SpacerBand;
import com.omni.negociacaobezerros.utils.pdf.bands.TextBand;
import com.omni.negociacaobezerros.utils.pdf.bands.TitleBand;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PdfNegociacaoBuilder {

    private PdfNegociacaoBuilder() {
    }

    @NonNull
    public static File gerarRelatorio(
            @NonNull Context context,
            @Nullable CotacaoState cotacao,
            @Nullable PropostaState proposta,
            @Nullable FechamentoState fechamento,
            @Nullable FreteState frete
    ) throws IOException {
        return gerarRelatorioInterno(context, cotacao, proposta, fechamento, frete, null);
    }

    @NonNull
    public static File gerarRelatorio(
            @NonNull Context context,
            @Nullable CotacaoState cotacao,
            @Nullable PropostaState proposta,
            @Nullable FechamentoState fechamento,
            @NonNull BigDecimal freteManual
    ) throws IOException {
        return gerarRelatorioInterno(context, cotacao, proposta, fechamento, null, freteManual);
    }

    @NonNull
    private static File gerarRelatorioInterno(
            @NonNull Context context,
            @Nullable CotacaoState cotacao,
            @Nullable PropostaState proposta,
            @Nullable FechamentoState fechamento,
            @Nullable FreteState frete,
            @Nullable BigDecimal freteManual
    ) throws IOException {

        String dataGeracao = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("pt", "BR")).format(new Date());

        boolean temFreteState = frete != null && frete.getValorTotal() != null && frete.getValorTotal().compareTo(BigDecimal.ZERO) > 0;
        boolean temFreteManual = freteManual != null && freteManual.compareTo(BigDecimal.ZERO) > 0;
        boolean temFrete = temFreteState || temFreteManual;
        boolean temProposta = proposta != null;
        boolean temFechamento = fechamento != null;

        PdfGenerator generator = new PdfGenerator(PdfPageConfig.a4Portrait());
        generator.setFooter(new FooterBand("Flow — Negociação  •  " + dataGeracao));

        generator.addBand(new TitleBand("Negociação de Bezerros"));
        generator.addBand(new SpacerBand(10f));
        generator.addBand(new TextBand("Data: " + dataGeracao, 10f, TextAlignment.LEFT));
        generator.addBand(new SpacerBand(14f));

        if (cotacao != null) {
            generator.addBand(new RowBand(11f, 26f,
                    new RowBand.Column("COTAÇÃO", 2.5f, TextAlignment.LEFT),
                    new RowBand.Column("Valor", 2.5f, TextAlignment.RIGHT)
            ).asHeader());

            generator.addBand(new RowBand(10f, 22f,
                    new RowBand.Column("Quantidade", 2.5f, TextAlignment.LEFT),
                    new RowBand.Column(FormatHelper.formatInteger(cotacao.getQuantidade()) + " cab.", 2.5f, TextAlignment.RIGHT)
            ));
            generator.addBand(new RowBand(10f, 22f,
                    new RowBand.Column("Valor por kg", 2.5f, TextAlignment.LEFT),
                    new RowBand.Column("R$ " + FormatHelper.formatCurrency(cotacao.getValorPorKg()), 2.5f, TextAlignment.RIGHT)
            ));
            generator.addBand(new RowBand(10f, 22f,
                    new RowBand.Column("Valor por cabeça", 2.5f, TextAlignment.LEFT),
                    new RowBand.Column("R$ " + FormatHelper.formatCurrency(cotacao.getValorPorCabeca()), 2.5f, TextAlignment.RIGHT)
            ));
        }

        if (temProposta) {
            generator.addBand(new SpacerBand(8f));

            generator.addBand(new RowBand(11f, 26f,
                    new RowBand.Column("PROPOSTA", 2.5f, TextAlignment.LEFT),
                    new RowBand.Column("Valor", 2.5f, TextAlignment.RIGHT)
            ).asHeader());

            generator.addBand(new RowBand(10f, 22f,
                    new RowBand.Column("Valor por kg", 2.5f, TextAlignment.LEFT),
                    new RowBand.Column("R$ " + FormatHelper.formatCurrency(proposta.getValorPorKg()), 2.5f, TextAlignment.RIGHT)
            ));
            generator.addBand(new RowBand(10f, 22f,
                    new RowBand.Column("Valor por cabeça", 2.5f, TextAlignment.LEFT),
                    new RowBand.Column("R$ " + FormatHelper.formatCurrency(proposta.getValorPorCabeca()), 2.5f, TextAlignment.RIGHT)
            ));
            generator.addBand(new RowBand(10f, 22f,
                    new RowBand.Column("Valor total", 2.5f, TextAlignment.LEFT),
                    new RowBand.Column("R$ " + FormatHelper.formatCurrency(proposta.getValorTotal()), 2.5f, TextAlignment.RIGHT)
            ));

            if (proposta.isFreteDescontado() && proposta.getFretePorKg() != null) {
                generator.addBand(new RowBand(10f, 22f,
                        new RowBand.Column("Frete por kg (descontado)", 2.5f, TextAlignment.LEFT),
                        new RowBand.Column("R$ " + FormatHelper.formatCurrency(proposta.getFretePorKg()), 2.5f, TextAlignment.RIGHT)
                ));
                generator.addBand(new RowBand(10f, 22f,
                        new RowBand.Column("Status frete", 2.5f, TextAlignment.LEFT),
                        new RowBand.Column(proposta.getFreteState().name(), 2.5f, TextAlignment.RIGHT)
                ));
            }
        }

        if (temFrete) {
            generator.addBand(new SpacerBand(8f));

            generator.addBand(new RowBand(11f, 26f,
                    new RowBand.Column("FRETE", 2.5f, TextAlignment.LEFT),
                    new RowBand.Column("Valor", 2.5f, TextAlignment.RIGHT)
            ).asHeader());

            if (temFreteManual) {
                generator.addBand(new RowBand(10f, 22f,
                        new RowBand.Column("Total frete (manual)", 2.5f, TextAlignment.LEFT),
                        new RowBand.Column("R$ " + FormatHelper.formatCurrency(freteManual), 2.5f, TextAlignment.RIGHT)
                ));
            } else {
                generator.addBand(new RowBand(10f, 22f,
                        new RowBand.Column("Total frete", 2.5f, TextAlignment.LEFT),
                        new RowBand.Column("R$ " + FormatHelper.formatCurrency(frete.getValorTotal()), 2.5f, TextAlignment.RIGHT)
                ));

                if (frete.getValorParcial() != null && frete.getValorParcial().compareTo(BigDecimal.ZERO) > 0) {
                    generator.addBand(new RowBand(10f, 22f,
                            new RowBand.Column("Valor parcial frete", 2.5f, TextAlignment.LEFT),
                            new RowBand.Column("R$ " + FormatHelper.formatCurrency(frete.getValorParcial()), 2.5f, TextAlignment.RIGHT)
                    ));
                }
            }
        }

        if (temFechamento) {
            generator.addBand(new SpacerBand(8f));

            generator.addBand(new RowBand(11f, 26f,
                    new RowBand.Column("FECHAMENTO", 2.5f, TextAlignment.LEFT),
                    new RowBand.Column("Valor", 2.5f, TextAlignment.RIGHT)
            ).asHeader());

            generator.addBand(new RowBand(10f, 22f,
                    new RowBand.Column("Valor por kg", 2.5f, TextAlignment.LEFT),
                    new RowBand.Column("R$ " + FormatHelper.formatCurrency(fechamento.getValorPorKg()), 2.5f, TextAlignment.RIGHT)
            ));
            generator.addBand(new RowBand(10f, 22f,
                    new RowBand.Column("Valor por cabeça", 2.5f, TextAlignment.LEFT),
                    new RowBand.Column("R$ " + FormatHelper.formatCurrency(fechamento.getValorPorCabeca()), 2.5f, TextAlignment.RIGHT)
            ));

            if (fechamento.isComissaoAplicada() && fechamento.getComissaoPorKg() != null) {
                generator.addBand(new RowBand(10f, 22f,
                        new RowBand.Column("Comissão por kg", 2.5f, TextAlignment.LEFT),
                        new RowBand.Column("R$ " + FormatHelper.formatCurrency(fechamento.getComissaoPorKg()), 2.5f, TextAlignment.RIGHT)
                ));
            }

            generator.addBand(new SpacerBand(10f));
            generator.addBand(new RowBand(12f, 26f,
                    new RowBand.Column("TOTAL GERAL", 3.5f, TextAlignment.RIGHT),
                    new RowBand.Column("R$ " + FormatHelper.formatCurrency(fechamento.getValorTotal()), 1.5f, TextAlignment.RIGHT)
            ));

        } else if (temProposta) {
            generator.addBand(new SpacerBand(10f));
            generator.addBand(new RowBand(12f, 26f,
                    new RowBand.Column("TOTAL GERAL", 3.5f, TextAlignment.RIGHT),
                    new RowBand.Column("R$ " + FormatHelper.formatCurrency(proposta.getValorTotal()), 1.5f, TextAlignment.RIGHT)
            ));
        }

        String nomeArquivo = "negociacao_" + System.currentTimeMillis() + ".pdf";
        return generator.generate(context, nomeArquivo);
    }
}
