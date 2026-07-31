package com.omni.negociacaobezerros.ui.fragments.layout;

import static com.omni.negociacaobezerros.helpers.NavigationHelper.navigateBackOnToolbar;
import static com.omni.negociacaobezerros.helpers.NavigationHelper.navigateOnClick;
import static com.omni.negociacaobezerros.helpers.RecyclerViewHelper.setupVerticalRecyclerView;
import static com.omni.negociacaobezerros.helpers.ViewHelper.isEmpty;
import static com.omni.negociacaobezerros.helpers.ViewHelper.parseDouble;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.omni.negociacaobezerros.R;
import com.omni.negociacaobezerros.databinding.FragmentNegociacaoAnimalBinding;
import com.omni.negociacaobezerros.ui.adapters.NegociacaoAnimalAdapter;
import com.omni.negociacaobezerros.ui.fragments.dialog.EdicaoItemNegociacaoAnimalFragment;
import com.omni.negociacaobezerros.ui.states.NegociacaoAnimalResumoUiState;
import com.omni.negociacaobezerros.ui.states.NegociacaoAnimalUiState;
import com.omni.negociacaobezerros.ui.states.PrecificacaoBezerroUiState;
import com.omni.negociacaobezerros.ui.viewmodels.CotacaoViewModel;
import com.omni.negociacaobezerros.ui.viewmodels.NegociacaoAnimalViewModel;
import com.omni.negociacaobezerros.ui.viewmodels.NegociacaoViewModel;
import com.omni.negociacaobezerros.utils.format.Decimals;
import com.omni.negociacaobezerros.utils.format.Numbers;

import java.math.BigDecimal;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NegociacaoAnimalFragment extends Fragment {
    private static final String TAG_EDICAO_ITEM = "EdicaoItemNegociacaoAnimal";
    private FragmentNegociacaoAnimalBinding binding;
    private CotacaoViewModel cotacaoViewModel;
    private NegociacaoViewModel negociacaoViewModel;
    private NegociacaoAnimalViewModel negociacaoAnimalViewModel;
    private NegociacaoAnimalAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNegociacaoAnimalBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewModels();
        navigation();
        initAdapter();
        setupListeners();
        observeUiState();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initViewModels() {
        cotacaoViewModel = new ViewModelProvider(requireActivity()).get(CotacaoViewModel.class);
        negociacaoViewModel = new ViewModelProvider(requireActivity()).get(NegociacaoViewModel.class);
        negociacaoAnimalViewModel = new ViewModelProvider(requireActivity()).get(NegociacaoAnimalViewModel.class);
    }

    private void navigation(){
        toFinalizacao();
        back();
    }

    private void toFinalizacao() {
        navigateOnClick(this, R.id.negociacaoAnimalFragment,
                NegociacaoAnimalFragmentDirections.actionNegociacaoAnimalFragmentToFinalizacaoFragment(),
                binding.buttonNegociacaoAnimalSalvar);
    }

    private void back() {
        navigateBackOnToolbar(this, binding.constraintLayoutNegociacaoAnimalToolbar);
    }

    private void initAdapter() {
        adapter = new NegociacaoAnimalAdapter(new NegociacaoAnimalAdapter.OnActionsListener() {
            @Override public void edit(NegociacaoAnimalUiState animal) { abrirEdicaoItem(animal); }
            @Override public void remove(int id) { negociacaoAnimalViewModel.remover(id); }
        });
        setupVerticalRecyclerView(binding.recyclerViewAnimaisPesados, adapter, requireContext());
    }

    private void abrirEdicaoItem(NegociacaoAnimalUiState animal) {
        FragmentManager fm = getChildFragmentManager();
        if (fm.findFragmentByTag(TAG_EDICAO_ITEM) != null) return;
        EdicaoItemNegociacaoAnimalFragment.newInstance(animal.getId(), animal.getPeso()).show(fm, TAG_EDICAO_ITEM);
    }

    private void setupListeners() {
        binding.textInputLayoutCotacaoPeso.setEndIconOnClickListener(v -> onAdicionarAnimalClicked());
    }

    private void onAdicionarAnimalClicked() {
        if (isEmpty(binding.textInputEditTextCotacaoPeso)) return;
        negociacaoAnimalViewModel.adicionar(parseDouble(binding.textInputEditTextCotacaoPeso));
        binding.textInputEditTextCotacaoPeso.setText("");
    }

    private void observeUiState() {
        observeQuantidadeCotacao();
        observeValorKgNegociado();
        observeAnimais();
        observeResumo();
    }

    private void observeQuantidadeCotacao() {
        cotacaoViewModel.getQuantidade().observe(getViewLifecycleOwner(), negociacaoAnimalViewModel::setQuantidadeTotal);
    }

    private void observeValorKgNegociado() {
        negociacaoViewModel.getPropostaState().observe(getViewLifecycleOwner(), this::onPropostaStateChanged);
    }

    private void onPropostaStateChanged(PrecificacaoBezerroUiState proposta) {
        if (proposta == null) return;
        negociacaoAnimalViewModel.setValorKgNegociado(proposta.getValorPorQuilo());
        binding.textViewNegociacaoAnimalDescricao.setText(Decimals.brl().format(proposta.getValorPorQuilo()));
    }

    private void observeAnimais() {
        negociacaoAnimalViewModel.getAnimaisState().observe(getViewLifecycleOwner(), this::onAnimaisStateChanged);
    }

    private void onAnimaisStateChanged(List<NegociacaoAnimalUiState> animais) {
        adapter.submitList(animais);
    }

    private void observeResumo() {
        negociacaoAnimalViewModel.getResumoState().observe(getViewLifecycleOwner(), this::onResumoStateChanged);
    }

    private void onResumoStateChanged(NegociacaoAnimalResumoUiState resumo) {
        if (resumo == null) return;
        bindProgresso(resumo);
        binding.textoValorPeso.setText(Numbers.formatDouble(resumo.getPesoMedioKg()));
        binding.textoValorTotal.setText(Decimals.brl().format(resumo.getValorTotal()));
        binding.buttonNegociacaoAnimalSalvar.setEnabled(resumo.isTodosAnimaisPesados());
    }

    private void bindProgresso(NegociacaoAnimalResumoUiState resumo) {
        binding.textView.setText(getString(R.string.negociacao_animal_progresso_pesados, resumo.getProgresso(), resumo.getQuantidadeTotal()));
        binding.textView2.setText(getString(R.string.negociacao_animal_progresso_percentual, resumo.getPercentual()));
        binding.textView3.setText(getString(R.string.negociacao_animal_progresso_faltam, resumo.getFaltam()));
        binding.linearProgressIndicatorNegociacaoAnimal.setIndeterminate(false);
        binding.linearProgressIndicatorNegociacaoAnimal.setProgress(resumo.getPercentual());
    }
}
