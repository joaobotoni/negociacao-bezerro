package com.omni.negociacaobezerros.ui.states;

public class ConexaoUiState {
    private final String site;
    private final String ip;
    private final String porta;
    private final String usuario;
    private final boolean conectado;

    public ConexaoUiState(String site, String ip, String porta, String usuario, boolean conectado) {
        this.site = site;
        this.ip = ip;
        this.porta = porta;
        this.usuario = usuario;
        this.conectado = conectado;
    }

    public String getSite() {
        return site;
    }

    public String getIp() {
        return ip;
    }

    public String getPorta() {
        return porta;
    }

    public String getUsuario() {
        return usuario;
    }

    public boolean isConectado() {
        return conectado;
    }
}
