package com.omni.negociacaobezerros.ui.adapters;

import static com.omni.negociacaobezerros.ui.helpers.ViewHelper.setText;
import static com.omni.negociacaobezerros.ui.helpers.ViewHelper.setVisible;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.omni.negociacaobezerros.databinding.ItemCategoriaBinding;
import com.omni.negociacaobezerros.ui.states.CategoriaUiState;

import java.util.Objects;

public class CategoriaAdapter extends ListAdapter<CategoriaUiState, CategoriaAdapter.ViewHolder> {
    public interface OnClickListener {
        void onClick(CategoriaUiState categoria);
    }

    private final OnClickListener listener;

    public CategoriaAdapter(OnClickListener listener) {
        super(new DiffCallback());
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemCategoriaBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemCategoriaBinding binding;
        private CategoriaUiState item;

        public ViewHolder(@NonNull ItemCategoriaBinding binding, OnClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(v -> {
                if (item != null) listener.onClick(item);
            });
        }

        protected void bind(CategoriaUiState categoria) {
            this.item = categoria;
            setText(binding.textoNomeCategoria, categoria.getOpcao());
            setVisible(categoria.isSelecionada(), binding.imageViewCategoriaCheck);
        }
    }

    private static class DiffCallback extends DiffUtil.ItemCallback<CategoriaUiState> {

        @Override
        public boolean areItemsTheSame(@NonNull CategoriaUiState oldItem, @NonNull CategoriaUiState newItem) {
            return Objects.equals(oldItem.getOpcao(), newItem.getOpcao());
        }

        @Override
        public boolean areContentsTheSame(@NonNull CategoriaUiState oldItem, @NonNull CategoriaUiState newItem) {
            return Objects.equals(oldItem.getOpcao(), newItem.getOpcao())
                    && oldItem.isSelecionada() == newItem.isSelecionada();
        }
    }
}