package com.omni.negociacaobezerros.ui.fragments;

import static com.omni.negociacaobezerros.ui.helpers.AlertHelper.showSnackBarErro;
import static com.omni.negociacaobezerros.ui.helpers.FileHelper.compartilhar;
import static com.omni.negociacaobezerros.ui.helpers.FileHelper.salvar;
import static com.omni.negociacaobezerros.ui.helpers.ViewHelper.isNotNull;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.omni.negociacaobezerros.databinding.FragmentSucessBinding;
import com.omni.negociacaobezerros.ui.helpers.AlertHelper;
import com.omni.negociacaobezerros.ui.helpers.NavigationHelper;
import com.omni.negociacaobezerros.ui.reports.PdfNegociacaoBuilder;
import com.omni.negociacaobezerros.ui.state.animal.AnimalState;
import com.omni.negociacaobezerros.ui.state.empresa.CorretorState;
import com.omni.negociacaobezerros.ui.state.empresa.EmpresaState;
import com.omni.negociacaobezerros.ui.state.frete.FreteState;
import com.omni.negociacaobezerros.ui.state.negociacao.NegociacaoState;
import com.omni.negociacaobezerros.ui.viewmodel.AnimalViewModel;
import com.omni.negociacaobezerros.ui.viewmodel.CorretorViewModel;
import com.omni.negociacaobezerros.ui.viewmodel.EmpresaViewModel;
import com.omni.negociacaobezerros.ui.viewmodel.NegociacaoViewModel;
import com.omni.negociacaobezerros.ui.viewmodel.PrecificacaoFreteViewModel;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SucessoFragment extends Fragment {
    private static final String MIME_TYPE = "application/pdf";
    private static final String TITLE_PDF = "negociacao_bezerro";
    private FragmentSucessBinding binding;
    private NegociacaoViewModel negociacaoViewModel;
    private PrecificacaoFreteViewModel freteViewModel;
    private EmpresaViewModel empresaViewModel;
    private AnimalViewModel animalViewModel;
    private CorretorViewModel corretorViewModel;
    private NegociacaoState negociacaoAtual;
    private FreteState freteAtual;
    private EmpresaState empresaAtual;
    private CorretorState corretorAtual;
    private AnimalState especificacoesAtual;
    private float freteManualAtual;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSucessBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        iniciar();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void iniciar() {
        extrairArgumentosDeNavegacao();
        configurarEventosDeClique();
        inicializarViewModels();
        iniciarAnimacaoEntrada();
        observarEstadosDasViewModels();
    }

    private void extrairArgumentosDeNavegacao() {
        SucessoFragmentArgs args = SucessoFragmentArgs.fromBundle(requireArguments());
        freteManualAtual = args.getValorFrete();
    }

    private void inicializarViewModels() {
        empresaViewModel = new ViewModelProvider(requireActivity()).get(EmpresaViewModel.class);
        animalViewModel = new ViewModelProvider(requireActivity()).get(AnimalViewModel.class);
        negociacaoViewModel = new ViewModelProvider(requireActivity()).get(NegociacaoViewModel.class);
        freteViewModel = new ViewModelProvider(requireActivity()).get(PrecificacaoFreteViewModel.class);
        corretorViewModel = new ViewModelProvider(requireActivity()).get(CorretorViewModel.class);
    }

    private void observarEstadosDasViewModels() {
        observarNegocicao();
        observarFrete();
        observarAnimal();
        observarEmpresa();
        observarCorretor();
    }

    private void observarNegocicao() {
        negociacaoViewModel.getState().observe(getViewLifecycleOwner(), this::atualizarNegociacao);
    }

    private void observarFrete() {
        freteViewModel.getState().observe(getViewLifecycleOwner(), this::atualizarFrete);
    }

    private void observarAnimal() {
        animalViewModel.getAnimalState().observe(getViewLifecycleOwner(), this::atualizarAnimal);
    }

    private void observarEmpresa() {
        empresaViewModel.getSelecionada().observe(getViewLifecycleOwner(), this::atualizarEmpresa);
    }

    private void observarCorretor() {
        corretorViewModel.getSelecionado().observe(getViewLifecycleOwner(), this::atualizarCorretor);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void configurarEventosDeClique() {
        binding.botaoConcluir.setOnClickListener(v -> onCliqueFinalizar());
        binding.botaoGerarRelatorioEFinalizar.setOnClickListener(v -> onCliqueFinalizarECompartilhar());
        binding.toolbar.setOnClickListener(v -> onCliqueVoltar());
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void onCliqueFinalizar() {
        if (isFreteDisponivel(freteAtual) || isFreteManualDisponivel()) {
            onFinalizar();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void onCliqueFinalizarECompartilhar() {
        if (isFreteDisponivel(freteAtual) || isFreteManualDisponivel()) {
            onFinalizarECompartilhar();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void onFinalizar() {
        if (isFreteManual()) {
            finalizar(empresaAtual, corretorAtual, especificacoesAtual, negociacaoAtual, BigDecimal.valueOf(freteManualAtual));
        }
        finalizar(empresaAtual, corretorAtual, especificacoesAtual, negociacaoAtual, freteAtual);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void onFinalizarECompartilhar() {
        if (isFreteManual()) {
            finalizarECompartilhar(empresaAtual, corretorAtual, especificacoesAtual, negociacaoAtual, BigDecimal.valueOf(freteManualAtual));
        }
        finalizarECompartilhar(empresaAtual, corretorAtual, especificacoesAtual, negociacaoAtual, freteAtual);
    }

    private void onCliqueVoltar() {
        NavigationHelper.voltar(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void finalizar(EmpresaState empresa, CorretorState corretor, AnimalState animal, NegociacaoState negociacao, FreteState frete) {
        try {
            salvar(requireActivity(), gerarRelatorioComEstadoDoFrete(empresa, corretor, animal, negociacao, frete), MIME_TYPE);
        } catch (IOException e) {
            showSnackBarErro(requireView(), e.getMessage());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void finalizar(EmpresaState empresa, CorretorState corretor, AnimalState animal, NegociacaoState negociacao, BigDecimal frete) {
        try {
            salvar(requireActivity(), gerarRelatorioComIncidenciaDofrete(empresa, corretor, animal, negociacao, frete), MIME_TYPE);
        } catch (IOException e) {
            showSnackBarErro(requireView(), e.getMessage());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void finalizarECompartilhar(EmpresaState empresa, CorretorState corretor, AnimalState animal, NegociacaoState negociacao, FreteState frete) {
        try {
            salvar(requireActivity(), gerarRelatorioComEstadoDoFrete(empresa, corretor, animal, negociacao, frete), MIME_TYPE);
            compartilhar(requireActivity(), gerarRelatorioComEstadoDoFrete(empresa, corretor, animal, negociacao, frete), MIME_TYPE, TITLE_PDF);
        } catch (IOException e) {
            showSnackBarErro(requireView(), e.getMessage());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void finalizarECompartilhar(EmpresaState empresa, CorretorState corretor, AnimalState animal, NegociacaoState negociacao, BigDecimal frete) {
        try {
            salvar(requireActivity(), gerarRelatorioComIncidenciaDofrete(empresa, corretor, animal, negociacao, frete), MIME_TYPE);
            compartilhar(requireActivity(), gerarRelatorioComIncidenciaDofrete(empresa, corretor, animal, negociacao, frete), MIME_TYPE, TITLE_PDF);
        } catch (IOException e) {
            showSnackBarErro(requireView(), e.getMessage());
        }
    }

    private File gerarRelatorioComEstadoDoFrete(EmpresaState empresa, CorretorState corretor, AnimalState animal, NegociacaoState negociacao, FreteState frete) throws IOException {
        return PdfNegociacaoBuilder.gerarRelatorio(requireContext(), empresa, corretor, animal, negociacao.getCotacao(), negociacao.getProposta(), negociacao.getFechamento(), frete);
    }

    private File gerarRelatorioComIncidenciaDofrete(EmpresaState empresa, CorretorState corretor, AnimalState animal, NegociacaoState negociacao, BigDecimal frete) throws IOException {
        return PdfNegociacaoBuilder.gerarRelatorio(requireContext(), empresa, corretor, animal, negociacao.getCotacao(), negociacao.getProposta(), negociacao.getFechamento(), frete);
    }

    private void atualizarEmpresa(@Nullable EmpresaState empresa) {
        empresaAtual = empresa;
    }

    private void atualizarAnimal(@Nullable AnimalState animal) {
        especificacoesAtual = animal;
    }

    private void atualizarNegociacao(@Nullable NegociacaoState negociacao) {
        negociacaoAtual = negociacao;
    }
    private void atualizarFrete(@Nullable FreteState frete) {
        freteAtual = frete;
    }

    private void atualizarCorretor(@Nullable CorretorState corretor) {
        corretorAtual = corretor;
    }

    private boolean isFreteManual() {
        return freteManualAtual > 0.0;
    }

    private boolean isFreteManualDisponivel() {
        return isFreteManual() && !isFreteDisponivel(freteAtual);
    }

    private boolean isFreteDisponivel(FreteState freteState) {
        return isNotNull(freteState);
    }

    private void concluirNegociacao() {
        AlertHelper.showSnackBarSucessoTop(requireView(), "Negociação Concluida com sucesso");
    }

    private void iniciarAnimacaoEntrada() {
        binding.cardIconeSucess.setScaleX(0f);
        binding.cardIconeSucess.setScaleY(0f);
        binding.haloMaior.setScaleX(0f);
        binding.haloMaior.setScaleY(0f);
        binding.haloMenor.setScaleX(0f);
        binding.haloMenor.setScaleY(0f);

        binding.textoTituloSucess.setAlpha(0f);
        binding.textoTituloSucess.setTranslationY(40f);
        binding.textoDescricaoSucess.setAlpha(0f);
        binding.textoDescricaoSucess.setTranslationY(40f);

        binding.cardIconeSucess.animate()
                .scaleX(1f).scaleY(1f)
                .setDuration(600)
                .setInterpolator(new OvershootInterpolator(1.6f))
                .withEndAction(this::iniciarLoopsInfinitos)
                .start();

        binding.haloMaior.animate().scaleX(1f).scaleY(1f).setDuration(800).setStartDelay(100).start();
        binding.haloMenor.animate().scaleX(1f).scaleY(1f).setDuration(700).setStartDelay(50).start();

        binding.textoTituloSucess.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .setStartDelay(300)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        binding.textoDescricaoSucess.animate()
                .alpha(0.7f)
                .translationY(0f)
                .setDuration(500)
                .setStartDelay(450)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    private void iniciarLoopsInfinitos() {
        aplicarEfeitoPulso(binding.cardIconeSucess, 1.05f, 1200, 0);
        aplicarEfeitoPulso(binding.haloMenor, 1.08f, 1500, 150);
        aplicarEfeitoPulso(binding.haloMaior, 1.12f, 2000, 300);
    }

    private void aplicarEfeitoPulso(View view, float escalaFinal, int duracao, int delay) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, escalaFinal);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, escalaFinal);

        scaleX.setRepeatCount(ValueAnimator.INFINITE);
        scaleX.setRepeatMode(ValueAnimator.REVERSE);
        scaleY.setRepeatCount(ValueAnimator.INFINITE);
        scaleY.setRepeatMode(ValueAnimator.REVERSE);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleX, scaleY);
        set.setDuration(duracao);
        set.setStartDelay(delay);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}