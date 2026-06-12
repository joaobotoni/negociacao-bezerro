package com.omni.negociacaobezerros.ui.states;

public class EmpresaUiState {

    private final int id;
    private final String nome;
    private final String localizacao;
    private final boolean selecionada;

    public EmpresaUiState(int id, String nome, String localizacao, boolean selecionada) {
        this.id = id;
        this.nome = nome;
        this.localizacao = localizacao;
        this.selecionada = selecionada;
    }

    public int getId() {
        return id;
    }

    public String getNome() { return nome; }
    public String getLocalizacao() { return localizacao; }
    public boolean isSelecionada() { return selecionada; }
}
