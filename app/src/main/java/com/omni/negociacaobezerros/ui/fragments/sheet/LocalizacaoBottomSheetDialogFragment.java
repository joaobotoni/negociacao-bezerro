package com.omni.negociacaobezerros.ui.fragments.sheet;

import static com.omni.negociacaobezerros.ui.helpers.AlertHelper.showSnackBarErro;
import static com.omni.negociacaobezerros.ui.helpers.RecyclerViewHelper.setupVerticalRecyclerView;
import static com.omni.negociacaobezerros.ui.helpers.TextWatcherHelper.search;
import static com.omni.negociacaobezerros.ui.helpers.ViewHelper.text;

import android.Manifest;
import android.app.Dialog;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.lifecycle.ViewModelProvider;

import com.omni.negociacaobezerros.R;
import com.omni.negociacaobezerros.databinding.FragmentBottomSheetLocalizacaoBinding;
import com.omni.negociacaobezerros.ui.adapters.LocalizacaoAdapter;
import com.omni.negociacaobezerros.ui.states.LocalizacaoUiState;
import com.omni.negociacaobezerros.ui.viewmodels.LocalizacaoViewModel;
import com.omni.negociacaobezerros.ui.viewmodels.RotaViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LocalizacaoBottomSheetDialogFragment extends BottomSheetDialogFragment {
    public static final String TAG = "LocalizacaoBottomSheet";

    private FragmentBottomSheetLocalizacaoBinding binding;
    private FusedLocationProviderClient fusedClient;
    private LocalizacaoViewModel localizacaoViewModel;
    private RotaViewModel rotaViewModel;
    private LocalizacaoAdapter adapter;
    private TextWatcher consultaWatcher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedClient = LocationServices.getFusedLocationProviderClient(requireActivity());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBottomSheetLocalizacaoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    @RequiresPermission(allOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        iniciarViewModels();
        iniciarRecyclerView();
        configurarInput();
        configurarObservadores();
    }

    @Override
    public void onDestroyView() {
        if (binding != null && consultaWatcher != null) {
            binding.textInputEditText.removeTextChangedListener(consultaWatcher);
        }
        consultaWatcher = null;
        super.onDestroyView();
        binding = null;
    }

    private void iniciarViewModels() {
        localizacaoViewModel = new ViewModelProvider(this).get(LocalizacaoViewModel.class);
        rotaViewModel = new ViewModelProvider(requireActivity()).get(RotaViewModel.class);
    }

    private void iniciarRecyclerView() {
        adapter = new LocalizacaoAdapter(this::aoSelecionarLocalizacao);
        setupVerticalRecyclerView(binding.recyclerView, adapter, requireContext());
    }

    private void configurarInput() {
        consultaWatcher = search(3, this::buscar);
        binding.textInputEditText.addTextChangedListener(consultaWatcher);
    }

    private void configurarObservadores() {
        localizacaoViewModel.getState().observe(getViewLifecycleOwner(), this::atualizarListaLocalizacoes);
        localizacaoViewModel.getErro().observe(getViewLifecycleOwner(), this::tratarErroBusca);
        rotaViewModel.getErro().observe(getViewLifecycleOwner(), this::tratarErroRota);
    }

    private void atualizarListaLocalizacoes(@Nullable LocalizacaoUiState state) {
        adapter.submitList(state != null ? state.getLocalizacoes() : null);
    }

    private void tratarErroBusca(@Nullable Throwable erro) {
        if (erro == null) return;
        adapter.submitList(null);
        showSnackBarErro(requireView(), getString(R.string.erro_busca_endereco));
    }

    private void tratarErroRota(@Nullable Throwable erro) {
        if (erro == null) return;
        showSnackBarErro(requireView(), getString(R.string.erro_busca_endereco));
    }

    private void aoSelecionarLocalizacao(Address origem) {
        rotaViewModel.selecionar(origem, getString(R.string.destino_padrao));
        dismiss();
    }

    @RequiresPermission(allOf = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    })
    private void buscar() {
        if (text(binding.textInputEditText).isEmpty()) return;
        fusedClient.getLastLocation().addOnSuccessListener(this::executarBuscaComLocalizacao);
    }

    private void executarBuscaComLocalizacao(@Nullable Location location) {
        double lat = location != null ? location.getLatitude() : 0;
        double lng = location != null ? location.getLongitude() : 0;
        localizacaoViewModel.buscar(text(binding.textInputEditText), lat, lng);
    }
}