package com.omni.negociacaobezerros.ui.viewmodels;

import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.omni.negociacaobezerros.data.source.network.gespec.GespecAcessoService;
import com.omni.negociacaobezerros.helpers.TaskHelper;
import com.omni.negociacaobezerros.ui.states.ConexaoUiState;

import java.io.IOException;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Response;

@HiltViewModel
public class ConexaoViewModel extends ViewModel {
    private static final String KEY_SITE = "site";
    private static final String KEY_IP = "ip";
    private static final String KEY_PORT = "port";
    private static final String KEY_USER = "user";
    private static final String CONTENT_TYPE = "application/json";
    private final SharedPreferences preferences;
    private final GespecAcessoService service;
    private final TaskHelper taskHelper;

    private final MutableLiveData<ConexaoUiState> uiState = new MutableLiveData<>(null);
    private final MutableLiveData<Throwable> erro = new MutableLiveData<>(null);

    @Inject
    public ConexaoViewModel(SharedPreferences preferences, GespecAcessoService service, TaskHelper taskHelper) {
        this.preferences = preferences;
        this.service = service;
        this.taskHelper = taskHelper;
    }

    public LiveData<ConexaoUiState> getUiState() { return uiState; }
    public LiveData<Throwable> getErro() { return erro; }

    public void carregar() {
        uiState.setValue(new ConexaoUiState(
                preferences.getString(KEY_SITE, ""),
                preferences.getString(KEY_IP, ""),
                preferences.getString(KEY_PORT, ""),
                preferences.getString(KEY_USER, ""),
                false));
    }

    public void salvarETestar(String site, String ip, String porta, String usuario) {
        salvarPreferencias(site, ip, porta, usuario);
        taskHelper.execute(() -> testarConexao(site, usuario), resposta -> onConexaoTestada(site, ip, porta, usuario), erro::setValue);
    }

    private void salvarPreferencias(String site, String ip, String porta, String usuario) {
        preferences.edit()
                .putString(KEY_SITE, site)
                .putString(KEY_IP, ip)
                .putString(KEY_PORT, porta)
                .putString(KEY_USER, usuario)
                .apply();
    }

    private String testarConexao(String site, String usuario) throws IOException {
        Response<String> resposta = service.sync("", CONTENT_TYPE, site, usuario).execute();
        if (!resposta.isSuccessful()) throw new IOException("HTTP " + resposta.code());
        return resposta.body();
    }

    private void onConexaoTestada(String site, String ip, String porta, String usuario) {
        uiState.setValue(new ConexaoUiState(site, ip, porta, usuario, true));
    }
}
