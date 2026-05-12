package com.example.myapplication.utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class DecimalUtil {
    private DecimalUtil() {}

    // Padronização
    public static final Locale LOCALE_BR = new Locale("pt", "BR");
    public static final BigDecimal CEM = new BigDecimal("100");

    // Arredondamentos
    public static final RoundingMode ARREDONDAMENTO_PADRAO = RoundingMode.HALF_EVEN;
    public static final RoundingMode ARREDONDAMENTO_FINANCEIRO = RoundingMode.HALF_UP;
    public static final RoundingMode ARREDONDAMENTO_TRUNCADO = RoundingMode.DOWN;

    // Escala de calculos
    public static final int ESCALA_CALCULO = 15;
    public static final int ESCALA_MONETARIA = 2;
    public static final int ESCALA_PERCENTUAL = 4;
    public static final MathContext CONTEXTO_CALCULO = new MathContext(ESCALA_CALCULO, ARREDONDAMENTO_PADRAO);
    public static final MathContext CONTEXTO_FINANCEIRO = new MathContext(ESCALA_MONETARIA, ARREDONDAMENTO_FINANCEIRO);

    // Formatação Monetaria
    public static final DecimalFormatSymbols SYMBOLS = new DecimalFormatSymbols(LOCALE_BR);
    public static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0.00", SYMBOLS);
    public static final double MAX_VALUE = Double.MAX_VALUE;


}