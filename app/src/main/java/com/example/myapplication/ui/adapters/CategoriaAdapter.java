package com.example.myapplication.ui.adapters;


import static com.example.myapplication.ui.helpers.ViewHelper.setText;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.ItemCategoriaBinding;
import com.example.myapplication.ui.state.CategoriaUiState;

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
        return new ViewHolder(ItemCategoriaBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemCategoriaBinding binding;

        public ViewHolder(ItemCategoriaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        protected void bind(CategoriaUiState estado, OnClickListener listener) {
            setText(binding.textoOpcao,estado.getDescricao());
            binding.cardOpcao.setChecked(estado.isSelected());
            binding.cardOpcao.setOnClickListener(v -> listener.onClick(estado));
        }
    }

    private static class DiffCallback extends DiffUtil.ItemCallback<CategoriaUiState> {
        @Override
        public boolean areItemsTheSame(@NonNull CategoriaUiState oldItem, @NonNull CategoriaUiState newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull CategoriaUiState oldItem, @NonNull CategoriaUiState newItem) {
            return oldItem.getId() == newItem.getId()
                    && Objects.equals(oldItem.getDescricao(), newItem.getDescricao())
                    && oldItem.isSelected() == newItem.isSelected();
        }
    }
}