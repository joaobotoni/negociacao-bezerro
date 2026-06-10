package com.omni.negociacaobezerros.ui.states;

public class RotaUiState {
    private final String cidadeOrigem;
    private final String estadoOrigem;
    private final String cidadeDestino;
    private final String estadoDestino;
    private final double distanciaKm;

    public RotaUiState(String cidadeOrigem, String estadoOrigem, String cidadeDestino, String estadoDestino, double distanciaKm) {
        this.cidadeOrigem = cidadeOrigem;
        this.estadoOrigem = estadoOrigem;
        this.cidadeDestino = cidadeDestino;
        this.estadoDestino = estadoDestino;
        this.distanciaKm = distanciaKm;
    }

    public String getCidadeOrigem() {
        return cidadeOrigem;
    }

    public String getEstadoOrigem() {
        return estadoOrigem;
    }

    public String getCidadeDestino() {
        return cidadeDestino;
    }

    public String getEstadoDestino() {
        return estadoDestino;
    }

    public double getDistanciaKm() {
        return distanciaKm;
    }
}
