package com.omni.negociacaobezerros.ui.states;

public class EmpresaUiState {
    private final String nome;
    private final String localizacao;
    private final boolean selecionada;

    public EmpresaUiState(String nome, String localizacao, boolean selecionada) {
        this.nome = nome;
        this.localizacao = localizacao;
        this.selecionada = selecionada;
    }

    public String getNome() { return nome; }
    public String getLocalizacao() { return localizacao; }
    public boolean isSelecionada() { return selecionada; }
}
