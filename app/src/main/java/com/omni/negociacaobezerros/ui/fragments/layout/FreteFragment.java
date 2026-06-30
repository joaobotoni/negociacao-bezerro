package com.omni.negociacaobezerros.ui.fragments.layout;

import static com.omni.negociacaobezerros.helpers.NavigationHelper.navigateBackOnToolbar;
import static com.omni.negociacaobezerros.helpers.NavigationHelper.navigateOnClick;
import static com.omni.negociacaobezerros.helpers.RecyclerViewHelper.setupHorizontalRecyclerView;
import static com.omni.negociacaobezerros.helpers.ViewHelper.isEmpty;
import static com.omni.negociacaobezerros.helpers.ViewHelper.parseDecimal;
import static com.omni.negociacaobezerros.helpers.ViewHelper.setVisible;

import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.omni.negociacaobezerros.R;
import com.omni.negociacaobezerros.databinding.FragmentFreteBinding;
import com.omni.negociacaobezerros.helpers.AlertHelper;
import com.omni.negociacaobezerros.helpers.PermissionHelper;
import com.omni.negociacaobezerros.helpers.TextWatcherHelper;
import com.omni.negociacaobezerros.ui.adapters.TransporteAdapter;
import com.omni.negociacaobezerros.ui.fragments.sheet.CategoriaBottomSheetDialogFragment;
import com.omni.negociacaobezerros.ui.fragments.sheet.LocalizacaoBottomSheetDialogFragment;
import com.omni.negociacaobezerros.ui.states.CategoriaUiState;
import com.omni.negociacaobezerros.ui.states.FreteUiState;
import com.omni.negociacaobezerros.ui.states.RotaUiState;
import com.omni.negociacaobezerros.ui.states.TransporteUiState;
import com.omni.negociacaobezerros.ui.viewmodels.CategoriaViewModel;
import com.omni.negociacaobezerros.ui.viewmodels.CotacaoViewModel;
import com.omni.negociacaobezerros.ui.viewmodels.FreteViewModel;
import com.omni.negociacaobezerros.ui.viewmodels.RotaViewModel;
import com.omni.negociacaobezerros.utils.format.Numbers;

import java.math.BigDecimal;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FreteFragment extends Fragment {
    private static final String TAG_BOTTOM_SHEET_CATEGORIA = "CategoriaBottomSheet";
    private static final String TAG_BOTTOM_SHEET_LOCALIZACAO = LocalizacaoBottomSheetDialogFragment.TAG;
    private static final String[] PERMISSOES_LOCALIZACAO = {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    private FragmentFreteBinding binding;
    private CategoriaViewModel categoriaViewModel;
    private RotaViewModel rotaViewModel;
    private FreteViewModel freteViewModel;
    private CotacaoViewModel cotacaoViewModel;
    private TransporteAdapter transporteAdapter;
    private ActivityResultLauncher<String[]> permissaoLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissaoLauncher = PermissionHelper.register(this, this::onPermissaoResultado);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFreteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewModels();
        navigation();
        initTransporteAdapter();
        setupListeners();
        observeUiState();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initViewModels() {
        categoriaViewModel = new ViewModelProvider(requireActivity()).get(CategoriaViewModel.class);
        rotaViewModel = new ViewModelProvider(requireActivity()).get(RotaViewModel.class);
        freteViewModel = new ViewModelProvider(requireActivity()).get(FreteViewModel.class);
        cotacaoViewModel = new ViewModelProvider(requireActivity()).get(CotacaoViewModel.class);
    }

    private void navigation() {
        toNegociacao();
        back();
    }

    private void toNegociacao() {
        navigateOnClick(this, R.id.freteFragment,
                FreteFragmentDirections.actionFreteFragmentToNegociacaoFragment(),
                binding.buttonFreteFinalizar);
    }

    private void back() {
        navigateBackOnToolbar(this, binding.constraintLayoutFreteToolbar);
    }

    private void initTransporteAdapter() {
        transporteAdapter = new TransporteAdapter();
        setupHorizontalRecyclerView(binding.listTransport, transporteAdapter, requireContext());
    }

    private void setupListeners() {
        binding.cardViewFreteSelecionarCategoria.setOnClickListener(v -> abrirCategoriaBottomSheet());
        binding.cardViewFreteExplorarRota.setOnClickListener(v -> onExplorarRotaClicked());
        binding.textInputEditTextFreteDistancia.addTextChangedListener(TextWatcherHelper.simple(this::onDistanciaManualAlterada));
    }

    private void abrirCategoriaBottomSheet() {
        FragmentManager fm = getChildFragmentManager();
        if (fm.findFragmentByTag(TAG_BOTTOM_SHEET_CATEGORIA) != null) return;
        new CategoriaBottomSheetDialogFragment().show(fm, TAG_BOTTOM_SHEET_CATEGORIA);
    }

    private void onExplorarRotaClicked() {
        if (hasPermissaoLocalizacao()) {
            abrirLocalizacaoBottomSheet();
            return;
        }
        PermissionHelper.request(requireContext(), permissaoLauncher, PERMISSOES_LOCALIZACAO);
    }

    private boolean hasPermissaoLocalizacao() {
        return PermissionHelper.hasPermissions(requireContext(), PERMISSOES_LOCALIZACAO);
    }

    private void onPermissaoResultado(boolean concedida, Object resultado) {
        if (!concedida) return;
        abrirLocalizacaoBottomSheet();
    }

    private void abrirLocalizacaoBottomSheet() {
        FragmentManager fm = getChildFragmentManager();
        if (fm.findFragmentByTag(TAG_BOTTOM_SHEET_LOCALIZACAO) != null) return;
        new LocalizacaoBottomSheetDialogFragment().show(fm, TAG_BOTTOM_SHEET_LOCALIZACAO);
    }

    private void onDistanciaManualAlterada() {
        if (isEmpty(binding.textInputEditTextFreteDistancia)) return;
        freteViewModel.setDistanciaKm(lerDistanciaManual());
    }

    private double lerDistanciaManual() {
        return parseDecimal(binding.textInputEditTextFreteDistancia).doubleValue();
    }

    private void observeUiState() {
        observeCategoriaSelecionada();
        observeCotacao();
        observeRota();
        observeTransportes();
        observeFreteState();
        observeFreteCalculado();
        observeErro();
    }

    private void observeCategoriaSelecionada() {
        categoriaViewModel.getSelecionada().observe(getViewLifecycleOwner(), this::onCategoriaSelecionadaChanged);
    }

    private void onCategoriaSelecionadaChanged(CategoriaUiState categoria) {
        binding.textViewFreteDescricaoCategoria.setText(isCategoriaSelecionada(categoria)
                ? categoria.getOpcao() : getString(R.string.frete_card__descricao_selecao_categoria_animal));
        freteViewModel.setCategoria(isCategoriaSelecionada(categoria) ? (long) categoria.getId() : null);
    }

    private boolean isCategoriaSelecionada(CategoriaUiState categoria) {
        return categoria != null;
    }

    private void observeCotacao() {
        cotacaoViewModel.getQuantidade().observe(getViewLifecycleOwner(), freteViewModel::setQuantidade);
        cotacaoViewModel.getPeso().observe(getViewLifecycleOwner(), freteViewModel::setPesoMedio);
    }

    private void observeRota() {
        rotaViewModel.getState().observe(getViewLifecycleOwner(), this::onRotaCalculada);
    }

    private void onRotaCalculada(RotaUiState rota) {
        setVisible(isRotaCalculada(rota), binding.containerRota);
        if (!isRotaCalculada(rota)) return;
        bindRotaCard(rota);
        if (isEmpty(binding.textInputEditTextFreteDistancia)) freteViewModel.setDistanciaKm(rota.getDistanciaKm());
    }

    private boolean isRotaCalculada(RotaUiState rota) {
        return rota != null;
    }

    private void bindRotaCard(RotaUiState rota) {
        binding.textoCidadeOrigem.setText(rota.getCidadeOrigem());
        binding.textoEstadoOrigem.setText(rota.getEstadoOrigem());
        binding.textoCidadeDestino.setText(rota.getCidadeDestino());
        binding.textoEstadoDestino.setText(rota.getEstadoDestino());
        binding.textoValorDistancia.setText(Numbers.formatDouble(rota.getDistanciaKm()));
    }

    private void observeTransportes() {
        freteViewModel.getTransportesState().observe(getViewLifecycleOwner(), this::onTransportesStateChanged);
    }

    private void onTransportesStateChanged(List<TransporteUiState> transportes) {
        setVisible(!isEmpty(transportes), binding.containerTransporte);
        transporteAdapter.submitList(transportes);
    }

    private void observeFreteState() {
        freteViewModel.getFreteState().observe(getViewLifecycleOwner(), this::onFreteStateChanged);
    }

    private void onFreteStateChanged(FreteUiState state) {
        setVisible(state != null, binding.containerResumoValores);
        if (state == null) return;
        binding.textoValorPrincipal.setText(Numbers.formatCurrency(state.getValorTotal()));
        binding.textoValorSecundario.setText(Numbers.formatCurrency(state.getValorPorKg()));
    }

    private void observeFreteCalculado() {
        freteViewModel.getFreteCalculado().observe(getViewLifecycleOwner(), this::onFreteCalculadoChanged);
    }

    private void onFreteCalculadoChanged(Boolean calculado) {
        binding.buttonFreteFinalizar.setEnabled(Boolean.TRUE.equals(calculado));
    }

    private void observeErro() {
        freteViewModel.getErro().observe(getViewLifecycleOwner(), this::onErroChanged);
    }

    private void onErroChanged(Throwable erro) {
        if (erro == null) return;
        AlertHelper.showSnackBarErro(binding.getRoot(), erro.getMessage());
    }
}
