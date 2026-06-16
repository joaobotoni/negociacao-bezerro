package com.omni.negociacaobezerros.di.network.gespec;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

@Singleton
public class GespecInterceptor implements Interceptor {
    private static final String HEADER_USER_NAME = "X-User-Name";
    private final GespecServerProvider server;
    @Inject
    public GespecInterceptor(@NonNull GespecServerProvider server) {
        this.server = server;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        requireInitialized();
        return chain.proceed(apply(chain.request()));
    }

    private void requireInitialized() throws IOException {
        if (!server.isInitialized()) throw notInitialized();
    }

    @NonNull
    private Request apply(@NonNull Request request) throws IOException {
        return request.newBuilder()
                .url(rewriteUrl(request.url()))
                .header(HEADER_USER_NAME, username())
                .build();
    }

    @NonNull
    private HttpUrl rewriteUrl(@NonNull HttpUrl url) throws IOException {
        return build(url.newBuilder(), host(), port());
    }

    @NonNull
    private static HttpUrl build(@NonNull HttpUrl.Builder builder, @NonNull String host, int port) throws IOException {
        try {
            return builder.host(host).port(port).build();
        } catch (IllegalArgumentException e) {
            throw invalidEndpoint(host, port, e);
        }
    }

    @NonNull
    private String host() throws IOException {
        return require(server.address(), "endereço");
    }

    private int port() throws IOException {
        return parsePort(require(server.port(), "porta"));
    }

    @NonNull
    private String username() throws IOException {
        return require(server.username(), "utilizador");
    }

    private static int parsePort(@NonNull String port) throws IOException {
        try {
            return Integer.parseInt(port);
        } catch (NumberFormatException e) {
            throw invalidPort(port, e);
        }
    }

    @NonNull
    private static String require(@Nullable String value, @NonNull String field) throws IOException {
        if (!isFilled(value)) throw incomplete(field);
        return value.trim();
    }

    private static boolean isFilled(@Nullable String value) {
        return value != null && !value.trim().isEmpty();
    }

    @NonNull
    private static IOException notInitialized() {
        return new IOException("Rede Gespec não configurada. Defina endereço, porta e utilizador antes de efetuar requisições.");
    }

    @NonNull
    private static IOException incomplete(@NonNull String field) {
        return new IOException("Configuração Gespec incompleta: " + field + " ausente.");
    }

    @NonNull
    private static IOException invalidPort(@NonNull String port, @NonNull Exception cause) {
        return new IOException("Porta Gespec inválida: " + port, cause);
    }

    @NonNull
    private static IOException invalidEndpoint(@NonNull String host, int port, @NonNull Exception cause) {
        return new IOException("Endereço ou porta Gespec inválidos (host=" + host + ", port=" + port + ").", cause);
    }
}