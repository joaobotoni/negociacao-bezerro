package com.omni.negociacaobezerros.ui.fragments.layout;

import static com.omni.negociacaobezerros.helpers.NavigationHelper.navigateBackOnToolbar;
import static com.omni.negociacaobezerros.helpers.NavigationHelper.navigateOnClick;
import static com.omni.negociacaobezerros.helpers.RecyclerViewHelper.setupVerticalRecyclerView;
import static com.omni.negociacaobezerros.helpers.ViewHelper.isEmpty;
import static com.omni.negociacaobezerros.helpers.ViewHelper.setVisible;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.omni.negociacaobezerros.R;
import com.omni.negociacaobezerros.databinding.FragmentSincronizacaoBinding;
import com.omni.negociacaobezerros.helpers.AlertHelper;
import com.omni.negociacaobezerros.ui.adapters.NegociacaoAdapter;
import com.omni.negociacaobezerros.ui.states.NegociacaoUiState;
import com.omni.negociacaobezerros.ui.viewmodels.SincronizacaoViewModel;
import com.omni.negociacaobezerros.utils.format.Numbers;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SincronizacaoFragment extends Fragment {

    private FragmentSincronizacaoBinding binding;
    private SincronizacaoViewModel sincronizacaoViewModel;
    private NegociacaoAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSincronizacaoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewModel();
        navigation();
        initAdapter();
        setupListeners();
        observeUiState();
        sincronizacaoViewModel.carregar();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initViewModel() {
        sincronizacaoViewModel = new ViewModelProvider(requireActivity()).get(SincronizacaoViewModel.class);
    }

    private void navigation() {
        toConexao();
        back();
    }

    private void toConexao() {
        navigateOnClick(this, R.id.sincronizacaoFragment,
                SincronizacaoFragmentDirections.actionSincronizacaoFragmentToConexaoFragment(), binding.cardViewSincronizacao);
    }

    private void back() {
       navigateBackOnToolbar(this, binding.constraintLayoutSincronizacaoToolbar);
    }

    private void initAdapter() {
        adapter = new NegociacaoAdapter();
        setupVerticalRecyclerView(binding.recyclerViewSincronizacaoNegociacoes, adapter, requireContext());
    }

    private void setupListeners() {
        binding.buttonSincronizacaoProximo.setOnClickListener(v -> onSincronizarClicked());
    }

    private void onSincronizarClicked() {
        sincronizacaoViewModel.sincronizar();
    }

    private void observeUiState() {
        sincronizacaoViewModel.getNegociacoesState().observe(getViewLifecycleOwner(), this::onNegociacoesStateChanged);
        sincronizacaoViewModel.getSincronizando().observe(getViewLifecycleOwner(), this::onSincronizandoChanged);
        sincronizacaoViewModel.getErro().observe(getViewLifecycleOwner(), this::onErroChanged);
    }

    private void onNegociacoesStateChanged(List<NegociacaoUiState> negociacoes) {
        adapter.submitList(negociacoes);
        setVisible(!isEmpty(negociacoes), binding.recyclerViewSincronizacaoNegociacoes, binding.textViewSincronizacaoLabelItemsParaEnvio);
        setVisible(isEmpty(negociacoes), binding, R.id.layout_sincronizacao_empty_state);
        binding.textViewSincronizacaoLabelItemsParaEnvio.setText(getString(R.string.sincronizacao_label_items_para_envio,
                Numbers.formatInteger(negociacoes != null ? negociacoes.size() : 0)));
        binding.buttonSincronizacaoProximo.setEnabled(!isEmpty(negociacoes));
    }

    private void onSincronizandoChanged(Boolean sincronizando) {
        binding.buttonSincronizacaoProximo.setEnabled(!Boolean.TRUE.equals(sincronizando) && !isEmpty(adapter.getCurrentList()));
    }

    private void onErroChanged(Throwable erro) {
        if (erro == null) return;
        AlertHelper.showSnackBarErro(binding.getRoot(), erro.getMessage());
    }
}
