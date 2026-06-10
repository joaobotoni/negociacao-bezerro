package com.omni.negociacaobezerros.ui.adapters;

import static com.omni.negociacaobezerros.ui.helpers.ViewHelper.setText;
import static com.omni.negociacaobezerros.ui.helpers.ViewHelper.setVisible;
import static com.omni.negociacaobezerros.utils.format.Numbers.formatCurrency;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.omni.negociacaobezerros.databinding.ItemCorretorBinding;
import com.omni.negociacaobezerros.ui.states.CorretorUiState;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Objects;

public class CorretorAdapter extends ListAdapter<CorretorUiState, CorretorAdapter.ViewHolder> {
    public interface OnClickListener {
        void onClick(CorretorUiState CorretorUiState);
    }

    private final OnClickListener listener;

    public CorretorAdapter(OnClickListener listener) {
        super(new DiffCallback());
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemCorretorBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemCorretorBinding binding;
        private CorretorUiState item;

        public ViewHolder(@NonNull ItemCorretorBinding binding, OnClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(v -> {
                if (item != null) listener.onClick(item);
            });
        }

        protected void bind(CorretorUiState corretor) {
            this.item = corretor;
            setText(binding.textoNomeCorretor, corretor.getNome());
            setText(binding.textoComissao, String.format(Locale.getDefault(), "R$ %s/%s", formatCurrency(corretor.getComissao()), corretor.getTipoComissao()));
            setVisible(corretor.isSelecionado(), binding.checkImage);
        }
    }


    private static class DiffCallback extends DiffUtil.ItemCallback<CorretorUiState> {

        @Override
        public boolean areItemsTheSame(@NonNull CorretorUiState oldItem, @NonNull CorretorUiState newItem) {
            return Objects.equals(oldItem.getNome(), newItem.getNome());
        }

        @Override
        public boolean areContentsTheSame(@NonNull CorretorUiState oldItem, @NonNull CorretorUiState newItem) {
            return Objects.equals(oldItem.getNome(), newItem.getNome())
                    && oldItem.getComissao().compareTo(newItem.getComissao()) == 0
                    && Objects.equals(oldItem.getTipoComissao(), newItem.getTipoComissao())
                    && oldItem.isSelecionado() == newItem.isSelecionado();
        }
    }
}
