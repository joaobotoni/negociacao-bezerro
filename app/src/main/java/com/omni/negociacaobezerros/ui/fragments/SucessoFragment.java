package com.omni.negociacaobezerros.ui.fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.omni.negociacaobezerros.databinding.FragmentSucessBinding;
import com.omni.negociacaobezerros.ui.helpers.AlertHelper;
import com.omni.negociacaobezerros.ui.reports.PdfNegociacaoBuilder;
import com.omni.negociacaobezerros.ui.state.frete.FreteState;
import com.omni.negociacaobezerros.ui.state.negociacao.NegociacaoState;
import com.omni.negociacaobezerros.ui.viewmodel.NegociacaoViewModel;
import com.omni.negociacaobezerros.ui.viewmodel.PrecificacaoFreteViewModel;

import java.io.IOException;
import java.math.BigDecimal;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SucessoFragment extends Fragment {
    private FragmentSucessBinding binding;
    private NegociacaoViewModel negociacaoViewModel;
    private PrecificacaoFreteViewModel freteViewModel;
    private NegociacaoState negociacaoAtual;
    private FreteState freteAtual;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSucessBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        configurarEventos();
        inicializarViewModels();
        iniciarAnimacaoEntrada();
    }

    private void inicializarViewModels() {
        negociacaoViewModel = new ViewModelProvider(requireActivity()).get(NegociacaoViewModel.class);
        freteViewModel = new ViewModelProvider(requireActivity()).get(PrecificacaoFreteViewModel.class);
    }

    private void observarEstadosDasViewModels() {

    }

    private void observarNegocicao() {
        negociacaoViewModel.getState().observe(getViewLifecycleOwner(), this::atualizarNegociacao);
        freteViewModel.getState().observe(getViewLifecycleOwner(), this::atualizarFrete);
    }


    private void finalizar(NegociacaoState negociacao, FreteState frete) {
        try {
            PdfNegociacaoBuilder.gerarRelatorio(
                    requireContext(),
                    negociacao.getCotacao(),
                    negociacao.getProposta(),
                    negociacao.getFechamento(),
                    frete);
        } catch (IOException e) {
            AlertHelper.showSnackBarErro(requireView(), "Erro ao salvar o relatorio");
        }
    }

    private void finalizar(NegociacaoState negociacao, BigDecimal frete) {
        try {
            PdfNegociacaoBuilder.gerarRelatorio(
                    requireContext(),
                    negociacao.getCotacao(),
                    negociacao.getProposta(),
                    negociacao.getFechamento(),
                    frete);
        } catch (IOException e) {
            AlertHelper.showSnackBarErro(requireView(), "Erro ao salvar o relatorio");
        }
    }

    private void atualizarNegociacao(@Nullable NegociacaoState negociacao) {
        negociacaoAtual = negociacao;
    }

    private void atualizarFrete(@Nullable FreteState frete) {
        freteAtual = frete;
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

    private void configurarEventos() {
        binding.botaoConcluir.setOnClickListener(v -> {
        });
        binding.botaoGerarRelatorioEFinalizar.setOnClickListener(v -> {
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}