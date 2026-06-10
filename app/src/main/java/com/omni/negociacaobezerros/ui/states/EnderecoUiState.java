package com.omni.negociacaobezerros.ui.states;

public class EnderecoUiState {
    private final String cidade;
    private final String estado;
    public EnderecoUiState(String cidade, String estado) {
        this.cidade = cidade;
        this.estado = estado;
    }
    public String getCidade() { return cidade; }
    public String getEstado() { return estado; }
}
