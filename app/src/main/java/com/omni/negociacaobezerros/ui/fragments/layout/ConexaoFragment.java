package com.omni.negociacaobezerros.ui.fragments.layout;

import static com.omni.negociacaobezerros.helpers.NavigationHelper.navigateBackOnToolbar;
import static com.omni.negociacaobezerros.helpers.NavigationHelper.navigateOnMenuItem;
import static com.omni.negociacaobezerros.helpers.ViewHelper.isNotEmpty;
import static com.omni.negociacaobezerros.helpers.ViewHelper.setTextSafely;
import static com.omni.negociacaobezerros.helpers.ViewHelper.text;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.omni.negociacaobezerros.R;
import com.omni.negociacaobezerros.databinding.FragmentConexaoBinding;
import com.omni.negociacaobezerros.helpers.AlertHelper;
import com.omni.negociacaobezerros.helpers.TextWatcherHelper;
import com.omni.negociacaobezerros.ui.states.ConexaoUiState;
import com.omni.negociacaobezerros.ui.viewmodels.ConexaoViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ConexaoFragment extends Fragment {
    private FragmentConexaoBinding binding;
    private ConexaoViewModel conexaoViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentConexaoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewModel();
        navigation();
        setupListeners();
        observeUiState();
        conexaoViewModel.carregar();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initViewModel() {
        conexaoViewModel = new ViewModelProvider(requireActivity()).get(ConexaoViewModel.class);
    }

    private void navigation() {
        toSincronizacao();
        back();
    }

    private void toSincronizacao() {
        navigateOnMenuItem(this, R.id.conexaoFragment,
                ConexaoFragmentDirections.actionConexaoFragmentToSincronizacaoFragment(),
                binding.constraintLayoutConexaoToolbar, R.id.menu_conexao_sincronizacao);
    }

    private void back() {
       navigateBackOnToolbar(this, binding.constraintLayoutConexaoToolbar);
    }

    private void setupListeners() {
        Runnable atualizarBotao = this::onCamposAlterados;
        binding.textInputEditTextConexaoSite.addTextChangedListener(TextWatcherHelper.simple(atualizarBotao));
        binding.textInputEditTextConexaoIp.addTextChangedListener(TextWatcherHelper.simple(atualizarBotao));
        binding.textInputEditTextConexaoPorta.addTextChangedListener(TextWatcherHelper.simple(atualizarBotao));
        binding.textInputEditTextConexaoUsuario.addTextChangedListener(TextWatcherHelper.simple(atualizarBotao));
        binding.buttonConexaoProximo.setOnClickListener(v -> onConectarClicked());
    }

    private void onCamposAlterados() {
        binding.buttonConexaoProximo.setEnabled(isFormularioPreenchido());
    }

    private boolean isFormularioPreenchido() {
        return isNotEmpty(binding.textInputEditTextConexaoSite)
                && isNotEmpty(binding.textInputEditTextConexaoIp)
                && isNotEmpty(binding.textInputEditTextConexaoPorta)
                && isNotEmpty(binding.textInputEditTextConexaoUsuario);
    }

    private void onConectarClicked() {
        conexaoViewModel.salvarETestar(
                text(binding.textInputEditTextConexaoSite),
                text(binding.textInputEditTextConexaoIp),
                text(binding.textInputEditTextConexaoPorta),
                text(binding.textInputEditTextConexaoUsuario));
    }

    private void observeUiState() {
        conexaoViewModel.getUiState().observe(getViewLifecycleOwner(), this::onUiStateChanged);
        conexaoViewModel.getErro().observe(getViewLifecycleOwner(), this::onErroChanged);
    }

    private void onUiStateChanged(ConexaoUiState state) {
        if (state == null) return;
        bindCampos(state);
        if (isConectado(state)) showSucessoConexao();
    }

    private boolean isConectado(ConexaoUiState state) {
        return state.isConectado();
    }

    private void bindCampos(ConexaoUiState state) {
        setTextSafely(binding.textInputEditTextConexaoSite, state.getSite());
        setTextSafely(binding.textInputEditTextConexaoIp, state.getIp());
        setTextSafely(binding.textInputEditTextConexaoPorta, state.getPorta());
        setTextSafely(binding.textInputEditTextConexaoUsuario, state.getUsuario());
        binding.buttonConexaoProximo.setEnabled(isFormularioPreenchido());
    }

    private void showSucessoConexao() {
        AlertHelper.showSnackBarSucesso(binding.getRoot(), getString(R.string.conexao_sucesso));
    }

    private void onErroChanged(Throwable erro) {
        if (erro == null) return;
        AlertHelper.showSnackBarErro(binding.getRoot(), erro.getMessage());
    }
}
