package com.omni.negociacaobezerros.ui.states;

public class RegistroNegociacaoUiState {

    private final String nomeEmpresa;
    private final String localizacao;

    public RegistroNegociacaoUiState(String nomeEmpresa, String localizacao) {
        this.nomeEmpresa = nomeEmpresa;
        this.localizacao = localizacao;
    }
    public String getNomeEmpresa() { return nomeEmpresa; }
    public String getLocalizacao() { return localizacao; }
}
