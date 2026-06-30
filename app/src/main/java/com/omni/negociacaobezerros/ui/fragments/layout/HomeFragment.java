package com.omni.negociacaobezerros.ui.fragments.layout;

import static com.omni.negociacaobezerros.helpers.NavigationHelper.navigate;
import static com.omni.negociacaobezerros.helpers.NavigationHelper.navigateBackOnToolbar;
import static com.omni.negociacaobezerros.helpers.NavigationHelper.navigateOnClick;
import static com.omni.negociacaobezerros.helpers.NavigationHelper.setupMenuItems;
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
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.omni.negociacaobezerros.R;
import com.omni.negociacaobezerros.databinding.FragmentHomeBinding;
import com.omni.negociacaobezerros.ui.adapters.RegistroNegociacaoAdapter;
import com.omni.negociacaobezerros.ui.fragments.sheet.EmpresaBottomSheetDialogFragment;
import com.omni.negociacaobezerros.ui.states.EmpresaUiState;
import com.omni.negociacaobezerros.ui.states.RegistroNegociacaoUiState;
import com.omni.negociacaobezerros.ui.viewmodels.EmpresaViewModel;
import com.omni.negociacaobezerros.ui.viewmodels.NegociacaoGadoViewModel;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private EmpresaViewModel empresaViewModel;
    private NegociacaoGadoViewModel negociacaoGadoViewModel;
    private RegistroNegociacaoAdapter historicoAdapter;

    private static final String TAG_BOTTOM_SHEET_EMPRESA = "EmpresaBottomSheet";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inicializar();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        negociacaoGadoViewModel.carregar();
    }

    private void inicializar(){
        initViewModel();
        navigation();
        initHistoricoAdapter();
        configurarEventosDeClique();
        observeEmpresaSelecionada();
        observeHistorico();
    }

    private void initViewModel() {
        empresaViewModel = new ViewModelProvider(requireActivity()).get(EmpresaViewModel.class);
        negociacaoGadoViewModel = new ViewModelProvider(requireActivity()).get(NegociacaoGadoViewModel.class);
    }

    private void initHistoricoAdapter() {
        historicoAdapter = new RegistroNegociacaoAdapter();
        setupVerticalRecyclerView(binding.recyclerViewHomeHistorico, historicoAdapter, requireContext());
    }

    private void observeHistorico() {
        negociacaoGadoViewModel.getHistorico().observe(getViewLifecycleOwner(), this::onHistoricoChanged);
    }

    private void onHistoricoChanged(List<RegistroNegociacaoUiState> historico) {
        historicoAdapter.submitList(historico);
        setVisible(!isEmpty(historico), binding.recyclerViewHomeHistorico);
        setVisible(isEmpty(historico), binding, R.id.layout_home_empty_state);
    }

    private void observeEmpresaSelecionada() {
        empresaViewModel.getSelecionada().observe(getViewLifecycleOwner(), this::onEmpresaSelecionadaChanged);
    }

    private void onEmpresaSelecionadaChanged(EmpresaUiState empresa) {
        bindEmpresaCard(empresa);
        binding.buttonHomeProximo.setEnabled(isEmpresaSelecionada(empresa));
    }

    private boolean isEmpresaSelecionada(EmpresaUiState empresa) {
        return empresa != null;
    }

    private void bindEmpresaCard(EmpresaUiState empresa) {
        if (isEmpresaSelecionada(empresa)) {
            binding.textViewHomeNomeEmpresa.setText(empresa.getNome());
            binding.textViewHomeDescricaoEmpresa.setText(empresa.getLocalizacao());
            return;
        }
        binding.textViewHomeNomeEmpresa.setText(R.string.home_title_selecionar_empresa);
        binding.textViewHomeDescricaoEmpresa.setText(R.string.home_desc_selecionar_empresa);
    }

    private void navigation() {
        toMenu();
        toCotacao();
        back();
    }

    private void configurarEventosDeClique() {
        binding.cardViewHomeSelecionarEmpresa.setOnClickListener(v -> onCliqueEmpresa());
    }

    private void toMenu() {
        setupMenuItems(binding.constraintLayoutHomeToolbar, itemId -> {
            if (itemId == R.id.menu_home_sincronizacao) {
                navigate(this, R.id.homeFragment, HomeFragmentDirections.actionHomeFragmentToSincronizacaoFragment());
                return true;
            }
            if (itemId == R.id.menu_home_conexao) {
                navigate(this, R.id.homeFragment, HomeFragmentDirections.actionHomeFragmentToConexaoFragment());
                return true;
            }
            return false;
        });
    }

    private void toCotacao() {
        navigateOnClick(this, R.id.homeFragment,
                HomeFragmentDirections.actionHomeFragmentToCotacaoFragment(), binding.buttonHomeProximo);
    }

    private void back() {
       navigateBackOnToolbar(this, binding.constraintLayoutHomeToolbar);
    }

    private void onCliqueEmpresa() {
        exibirBottomSheetEmpresa();
    }

    private void exibirBottomSheetEmpresa() {
        FragmentManager fm = getChildFragmentManager();
        if (isBottomSheetEmpresaVisivel(fm)) return;
        new EmpresaBottomSheetDialogFragment().show(fm, TAG_BOTTOM_SHEET_EMPRESA);
    }

    private boolean isBottomSheetEmpresaVisivel(@NonNull FragmentManager fm) {
        return fm.findFragmentByTag(TAG_BOTTOM_SHEET_EMPRESA) != null;
    }
}
