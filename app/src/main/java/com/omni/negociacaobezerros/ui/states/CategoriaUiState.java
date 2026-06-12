package com.omni.negociacaobezerros.ui.states;

public class CategoriaUiState {

    private final int id;
    private final String opcao;
    private final boolean selecionada;


    public CategoriaUiState(int id, String opcao, boolean selecionada) {
        this.id = id;
        this.opcao = opcao;
        this.selecionada = selecionada;
    }

    public int getId() {
        return id;
    }

    public String getOpcao() {
        return opcao;
    }

    public boolean isSelecionada() {
        return selecionada;
    }
}
