package com.omni.negociacaobezerros.data.repositories;

import android.location.Address;
import android.location.Geocoder;

import com.omni.negociacaobezerros.data.models.Rota;
import com.omni.negociacaobezerros.data.source.network.google.RoutesService;
import com.omni.negociacaobezerros.data.source.network.models.NetworkRoutes;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class LocalizacaoRepository {
    private static final int MAX_RESULTADOS = 10;
    private static final int RESULTADO_UNICO = 1;
    private static final double METROS_POR_QUILOMETRO = 1000.0;
    private final Geocoder geocoder;
    private final RoutesService service;
    @Inject
    public LocalizacaoRepository(Geocoder geocoder, RoutesService service) {
        this.geocoder = geocoder;
        this.service = service;
    }

    public Optional<String> getCodigoPais(double latitude, double longitude) throws IOException {
        return geocodificarCoordenadas(latitude, longitude)
                .stream()
                .findFirst()
                .map(Address::getCountryCode);
    }

    public List<Address> findEnderecos(String consulta, String codigoPais) throws IOException {
        return geocodificarNome(consulta, MAX_RESULTADOS)
                .stream()
                .filter(endereco -> isDoMesmoPais(endereco, codigoPais))
                .collect(Collectors.toList());
    }

    public Optional<Address> findEndereco(String nomeLocal) throws IOException {
        return geocodificarNome(nomeLocal, RESULTADO_UNICO)
                .stream()
                .findFirst();
    }

    public Rota calcularRota(Address origem, Address destino) {
        NetworkRoutes.Response response = fetchRota(origem, destino);
        double distanciaKm = parseDistanciaKm(response);
        return new Rota(
                getCidade(origem), getEstado(origem),
                getCidade(destino), getEstado(destino),
                distanciaKm
        );
    }

    private List<Address> geocodificarCoordenadas(double latitude, double longitude) throws IOException {
        return Optional.ofNullable(geocoder.getFromLocation(latitude, longitude, RESULTADO_UNICO))
                .orElseGet(Collections::emptyList);
    }

    private List<Address> geocodificarNome(String nomeLocal, int maxResultados) throws IOException {
        return Optional.ofNullable(geocoder.getFromLocationName(nomeLocal, maxResultados))
                .orElseGet(Collections::emptyList);
    }

    private boolean isDoMesmoPais(Address endereco, String codigoPais) {
        return codigoPais == null || (endereco.getCountryCode() != null
                && endereco.getCountryCode().equalsIgnoreCase(codigoPais));
    }

    private NetworkRoutes.Response fetchRota(Address origem, Address destino) {
        try {
            return service.computeRoutes(request(origem, destino)).execute().body();
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Falha ao buscar rota de '%s' para '%s'.", getCidade(origem), getCidade(destino)), e);
        }
    }

    private double parseDistanciaKm(NetworkRoutes.Response response) {
        return response.routes.get(0).distanceMeters / METROS_POR_QUILOMETRO;
    }

    private NetworkRoutes.Request request(Address origem, Address destino) {
        return new NetworkRoutes.Request(origin(origem), destination(destino));
    }

    private NetworkRoutes.Request.Origin origin(Address origem) {
        return new NetworkRoutes.Request.Origin(location(origem));
    }

    private NetworkRoutes.Request.Destination destination(Address destino) {
        return new NetworkRoutes.Request.Destination(location(destino));
    }

    private NetworkRoutes.Request.Location location(Address endereco) {
        return new NetworkRoutes.Request.Location(latLng(endereco));
    }

    private NetworkRoutes.Request.LatLng latLng(Address endereco) {
        return new NetworkRoutes.Request.LatLng(endereco.getLatitude(), endereco.getLongitude());
    }

    private String getCidade(Address endereco) {
        if (endereco.getLocality() != null) return endereco.getLocality();
        if (endereco.getSubAdminArea() != null) return endereco.getSubAdminArea();
        return endereco.getAddressLine(0);
    }

    private String getEstado(Address endereco) {
        return endereco.getAdminArea() != null ? endereco.getAdminArea() : "";
    }
}