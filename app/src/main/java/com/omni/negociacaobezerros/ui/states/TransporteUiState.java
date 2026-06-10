package com.omni.negociacaobezerros.ui.states;

public class TransporteUiState {
    private final String nomeVeiculo;
    private final int quantidadeVeiculos;
    private final int capacidadeCabecas;
    private final double porcentagemOcupada;

    public TransporteUiState(String nomeVeiculo, int quantidadeVeiculos, int capacidadeCabecas, double porcentagemOcupada) {
        this.nomeVeiculo = nomeVeiculo;
        this.quantidadeVeiculos = quantidadeVeiculos;
        this.capacidadeCabecas = capacidadeCabecas;
        this.porcentagemOcupada = porcentagemOcupada;
    }

    public String getNomeVeiculo() {
        return nomeVeiculo;
    }

    public int getQuantidadeVeiculos() {
        return quantidadeVeiculos;
    }

    public int getCapacidadeCabecas() {
        return capacidadeCabecas;
    }

    public double getPorcentagemOcupada() {
        return porcentagemOcupada;
    }
}
