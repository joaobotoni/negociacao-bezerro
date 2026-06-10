package com.omni.negociacaobezerros.ui.states;

public class NegociacaoUiState {
    private final String nomeEmpresa;
    private final int quantidadeAnimal;

    public NegociacaoUiState(String nomeEmpresa, int quantidadeAnimal) {
        this.nomeEmpresa = nomeEmpresa;
        this.quantidadeAnimal = quantidadeAnimal;
    }

    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    public int getQuantidadeAnimal() {
        return quantidadeAnimal;
    }
}
