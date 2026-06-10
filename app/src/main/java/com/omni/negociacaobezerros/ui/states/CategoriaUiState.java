package com.omni.negociacaobezerros.ui.states;

public class CategoriaUiState {
    private final String opcao;
    private final boolean selecionada;

    public CategoriaUiState(String opcao, boolean selecionada) {
        this.opcao = opcao;
        this.selecionada = selecionada;
    }

    public String getOpcao() { return opcao; }
    public boolean isSelecionada() { return selecionada; }
}
