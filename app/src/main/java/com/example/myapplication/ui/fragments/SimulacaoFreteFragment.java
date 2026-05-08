package com.example.myapplication.ui.fragments;

import static com.example.myapplication.ui.helpers.AlertHelper.showDialog;
import static com.example.myapplication.ui.helpers.FormatHelper.formatCurrency;
import static com.example.myapplication.ui.helpers.PermissionHelper.hasPermissions;
import static com.example.myapplication.ui.helpers.PermissionHelper.register;
import static com.example.myapplication.ui.helpers.PermissionHelper.request;
import static com.example.myapplication.ui.helpers.TextWatcherHelper.SimpleTextWatcher;
import static com.example.myapplication.ui.helpers.ViewHelper.clearText;
import static com.example.myapplication.ui.helpers.ViewHelper.getDouble;
import static com.example.myapplication.ui.helpers.ViewHelper.isEmpty;
import static com.example.myapplication.ui.helpers.ViewHelper.isNotEmpty;
import static com.example.myapplication.ui.helpers.ViewHelper.orElse;
import static com.example.myapplication.ui.helpers.ViewHelper.setText;
import static com.example.myapplication.ui.helpers.ViewHelper.setVisible;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myapplication.R;
import com.example.myapplication.data.models.Transporte;
import com.example.myapplication.databinding.FragmentSimulacaoFreteBinding;
import com.example.myapplication.ui.adapters.TransporteAdapter;
import com.example.myapplication.ui.state.CategoriaState;
import com.example.myapplication.ui.state.PrecificacaoFreteState;
import com.example.myapplication.ui.state.RotaState;
import com.example.myapplication.ui.state.TransporteState;
import com.example.myapplication.ui.viewmodel.CategoriaViewModel;
import com.example.myapplication.ui.viewmodel.PrecificacaoFreteViewModel;
import com.example.myapplication.ui.viewmodel.RotaViewModel;
import com.example.myapplication.ui.viewmodel.TransporteViewModel;
import com.example.myapplication.utils.mappers.domain.TransporteMapper;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SimulacaoFreteFragment extends Fragment {

    static final String CHAVE_RESULTADO_FRETE = "resultado_selecao_frete";
    static final String EXTRA_VALOR_FRETE = "valor_frete";

    private static final String[] PERMISSOES_LOCALIZACAO = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    @Inject
    TransporteMapper transporteMapper;
    private FragmentSimulacaoFreteBinding binding;
    private TransporteAdapter transporteAdapter;
    private PrecificacaoFreteViewModel precificacaoFreteViewModel;
    private RotaViewModel rotaViewModel;
    private TransporteViewModel transporteViewModel;
    private CategoriaViewModel categoriaViewModel;
    private TextWatcher distanciaManualWatcher;
    private ActivityResultLauncher<String[]> solicitacaoDePermissaoLauncher;
    private int cargaTotalDoLote;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registrarLauncherDePermissao();
        registrarCallbackDeBotaoVoltar();
    }

    @Override
    public void onStart() {
        super.onStart();
        solicitarPermissoesDeLocalizacaoSeNecessario();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSimulacaoFreteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        extrairArgumentos();
        inicializarViewModels();
        configurarInterface();
        observarEstadosDosViewModels();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        restaurarDistanciaManualSalva();
        atualizarVisibilidadeDosContainers();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void extrairArgumentos() {
        cargaTotalDoLote = SimulacaoFreteFragmentArgs.fromBundle(requireArguments()).getCargaTotal();
    }

    private void inicializarViewModels() {
        rotaViewModel = new ViewModelProvider(requireActivity()).get(RotaViewModel.class);
        transporteViewModel = new ViewModelProvider(requireActivity()).get(TransporteViewModel.class);
        precificacaoFreteViewModel = new ViewModelProvider(requireActivity()).get(PrecificacaoFreteViewModel.class);
        categoriaViewModel = new ViewModelProvider(requireActivity()).get(CategoriaViewModel.class);
    }

    private void configurarInterface() {
        configurarAdapterDeTransporte();
        configurarWatcherDeDistanciaManual();
        configurarListenersDeBotoes();
    }

    private void configurarAdapterDeTransporte() {
        transporteAdapter = new TransporteAdapter();
        binding.listTransport.setAdapter(transporteAdapter);
    }

    private void configurarWatcherDeDistanciaManual() {
        distanciaManualWatcher = SimpleTextWatcher(this::aoAlterarDistanciaManual);
        binding.entradaTextoDistancia.addTextChangedListener(distanciaManualWatcher);
    }

    private void configurarListenersDeBotoes() {
        binding.cartaoExplorarRota.setOnClickListener(v -> abrirBuscaDeLocalizacao());
        binding.toolbar.setOnClickListener(v -> navigateBack());
        binding.botaoContinuar.setOnClickListener(v -> confirmarFreteERetornarResultado());
    }


    private void observarEstadosDosViewModels() {
        observarEstadoDaRota();
        observarEstadoDeTransporte();
        observarCategoriaSelecionada();
        observarResumoDoFrete();
    }

    private void observarEstadoDaRota() {
        rotaViewModel.getState().observe(getViewLifecycleOwner(), this::aoAtualizarRota);
    }

    private void observarEstadoDeTransporte() {
        transporteViewModel.getState().observe(getViewLifecycleOwner(), this::aoAtualizarListaDeTransportes);
    }

    private void observarCategoriaSelecionada() {
        categoriaViewModel.getCategoriaSelecionada().observe(getViewLifecycleOwner(), this::aoAtualizarCategoria);
    }

    private void observarResumoDoFrete() {
        precificacaoFreteViewModel.getState().observe(getViewLifecycleOwner(), this::aoAtualizarResumoDoFrete);
    }

    private void aoAtualizarRota(RotaState rota) {
        if (distanciaManualTemPrioridadeSobreRota(rota)) {
            rotaViewModel.limpar();
            return;
        }
        if (isNotEmpty(rota)) limparDistanciaManual();
        sincronizarEstado();
    }

    private boolean distanciaManualTemPrioridadeSobreRota(RotaState rota) {
        return isNotEmpty(rota) && isDistanciaManualPreenchida();
    }

    private void aoAtualizarListaDeTransportes(List<TransporteState> transportes) {
        transporteAdapter.submitList(isNotEmpty(transportes) ? transportes : Collections.emptyList());
        sincronizarEstado();
    }

    private void aoAtualizarCategoria(CategoriaState categoria) {
        if (categoria == null) return;
        transporteViewModel.recomendar(categoria.getId(), cargaTotalDoLote);
    }

    private void aoAlterarDistanciaManual() {
        double distancia = lerDistanciaManual();
        precificacaoFreteViewModel.setDistancia(distancia);
        if (isNotEmpty(distancia)) rotaViewModel.limpar();
        sincronizarEstado();
    }

    private void aoAtualizarResumoDoFrete(PrecificacaoFreteState estado) {
        atualizarVisibilidadeDoResumo(estado);
        if (isEmpty(estado)) return;
        exibirValoresDoResumo(estado);
    }

    private void atualizarVisibilidadeDoResumo(PrecificacaoFreteState estado) {
        setVisible(isNotEmpty(estado), binding.containerResumoValores);
    }

    private void exibirValoresDoResumo(@NonNull PrecificacaoFreteState estado) {
        setText(binding.textoValorPrincipal, formatCurrency(estado.getValorTotal()));
        setText(binding.textoValorSecundario, formatCurrency(estado.getValorParcial()));
    }

    private void sincronizarEstado() {
        atualizarVisibilidadeDosContainers();
        recalcularFrete();
    }

    private void atualizarVisibilidadeDosContainers() {
        exibirContainerDeRota();
        exibirContainerDeTransporte();
        if (isRotaDefinida()) exibirDadosDaRota(rotaViewModel.getState().getValue());
    }

    private void exibirContainerDeRota() {
        setVisible(isRotaDefinida() && !isDistanciaManualPreenchida(), binding.containerRota);
    }

    private void exibirContainerDeTransporte() {
        setVisible(existeDistanciaAtiva() && existemTransportesDisponiveis(), binding.containerTransporte);
    }

    private boolean existeDistanciaAtiva() {
        return isRotaDefinida() || isDistanciaManualPreenchida();
    }

    private void exibirDadosDaRota(RotaState rota) {
        exibirOrigem(rota);
        exibirDestino(rota);
        exibirDistanciaDaRota(rota);
    }

    private void exibirOrigem(@NonNull RotaState rota) {
        setText(binding.textoCidadeOrigem, rota.getCidadeOrigem());
        setText(binding.textoEstadoOrigem, rota.getEstadoOrigem());
    }

    private void exibirDestino(@NonNull RotaState rota) {
        setText(binding.textoCidadeDestino, rota.getCidadeDestino());
        setText(binding.textoEstadoDestino, rota.getEstadoDestino());
    }

    private void exibirDistanciaDaRota(@NonNull RotaState rota) {
        setText(binding.textoValorDistancia, String.valueOf(rota.getDistancia()));
    }
    private void recalcularFrete() {
        if (dadosInsuficientesParaCalcular()) {
            limparResultadoDaSimulacao();
            return;
        }
        calcularFrete();
    }

    private boolean dadosInsuficientesParaCalcular() {
        return isEmpty(resolverDistanciaAtiva()) || !existemTransportesDisponiveis();
    }

    private void calcularFrete() {
        precificacaoFreteViewModel.calcularFrete(
                mapearTransportesDisponiveis(),
                resolverDistanciaAtiva(),
                cargaTotalDoLote);
    }

    private List<Transporte> mapearTransportesDisponiveis() {
        return transporteMapper.mapTo(orElse(transporteViewModel.getState().getValue(), Collections.emptyList()));
    }

    private void limparResultadoDaSimulacao() {
        precificacaoFreteViewModel.limpar();
        ocultarContainerDeResumo();
    }

    private void ocultarContainerDeResumo() {
        setVisible(false, binding.containerResumoValores);
    }


    private void confirmarFreteERetornarResultado() {
        if (simulacaoSemValorCalculado()) return;
        retornarValorDoFreteParaTelaPrecedente(resolverValorTotalCalculado());
        navigateBack();
    }

    private boolean simulacaoSemValorCalculado() {
        PrecificacaoFreteState estado = precificacaoFreteViewModel.getState().getValue();
        return isEmpty(estado) || isEmpty(estado.getValorTotal());
    }

    private BigDecimal resolverValorTotalCalculado() {
        PrecificacaoFreteState estado = precificacaoFreteViewModel.getState().getValue();
        if(estado == null) return BigDecimal.ZERO;
        return orElse(estado.getValorTotal(), BigDecimal.ZERO);
    }

    private void retornarValorDoFreteParaTelaPrecedente(BigDecimal valorTotal) {
        Bundle resultado = new Bundle();
        resultado.putString(EXTRA_VALOR_FRETE, formatCurrency(valorTotal));
        getParentFragmentManager().setFragmentResult(CHAVE_RESULTADO_FRETE, resultado);
    }

    private void restaurarDistanciaManualSalva() {
        if (isDistanciaManualPreenchida()) return;
        double distanciaSalva = lerDistanciaSalvaNoViewModel();
        if (distanciaSalva > 0) setText(binding.entradaTextoDistancia, String.valueOf(distanciaSalva));
    }

    private double lerDistanciaSalvaNoViewModel() {
        return orElse(precificacaoFreteViewModel.getDistancia().getValue(), 0.0);
    }

    private void limparDistanciaManual() {
        if (isEmpty(lerDistanciaManual())) return;
        removerWatcher();
        limparCampoDeDistancia();
        adicionarWatcher();
    }

    private void removerWatcher() {
        binding.entradaTextoDistancia.removeTextChangedListener(distanciaManualWatcher);
    }

    private void limparCampoDeDistancia() {
        clearText(binding.entradaTextoDistancia);
        precificacaoFreteViewModel.setDistancia(0);
    }

    private void adicionarWatcher() {
        binding.entradaTextoDistancia.addTextChangedListener(distanciaManualWatcher);
    }

    private double resolverDistanciaAtiva() {
        double distanciaManual = lerDistanciaManual();
        if (isNotEmpty(distanciaManual)) return distanciaManual;
        return lerDistanciaDefinidaPelaRota();
    }

    private double lerDistanciaManual() {
        return getDouble(binding.entradaTextoDistancia);
    }

    private double lerDistanciaDefinidaPelaRota() {
        RotaState rota = rotaViewModel.getState().getValue();
        return isNotEmpty(rota) ? rota.getDistancia() : 0.0;
    }

    private void registrarLauncherDePermissao() {
        solicitacaoDePermissaoLauncher = register(this, (concedida, result) -> {
            if (!concedida) exibirDialogoExplicandoNecessidadeDePermissao();
        });
    }

    private void solicitarPermissoesDeLocalizacaoSeNecessario() {
        if (permissoesDeLocalizacaoJaConcedidas()) return;
        request(requireContext(), solicitacaoDePermissaoLauncher, PERMISSOES_LOCALIZACAO);
    }

    private boolean permissoesDeLocalizacaoJaConcedidas() {
        return hasPermissions(requireContext(), PERMISSOES_LOCALIZACAO);
    }

    private void exibirDialogoExplicandoNecessidadeDePermissao() {
        showDialog(
                requireContext(),
                getString(R.string.dialogo_titulo_permissao_localizacao),
                getString(R.string.dialogo_mensagem_permissao_localizacao),
                (dialogo, qual) -> abrirConfiguracoesDoPeloApp(),
                (dialogo, qual) -> encerrarActivity()
        );
    }

    private void abrirConfiguracoesDoPeloApp() {
        startActivity(new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", requireContext().getPackageName(), null)));
    }

    private void encerrarActivity() {
        requireActivity().finish();
    }

    private void registrarCallbackDeBotaoVoltar() {
        requireActivity().getOnBackPressedDispatcher().
                addCallback(this, new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        navigateBack();
                    }
                });
    }

    private void abrirBuscaDeLocalizacao() {
        new BuscaLocalizacaoBottomSheetDialogFragment().show(getChildFragmentManager(), null);
    }
    private void navigateBack() {NavHostFragment.findNavController(this).popBackStack();}
    private boolean isRotaDefinida() {return isNotEmpty(rotaViewModel.getState().getValue());}
    private boolean existemTransportesDisponiveis() {return isNotEmpty(transporteViewModel.getState().getValue());}
    private boolean isDistanciaManualPreenchida() {
        return isNotEmpty(lerDistanciaManual());
    }
}