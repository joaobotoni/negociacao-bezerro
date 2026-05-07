package com.example.myapplication.ui.adapters;

import static com.example.myapplication.ui.helpers.ViewHelper.setText;
import static com.example.myapplication.ui.helpers.ViewHelper.setVisible;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.ItemEmpresaBinding;
import com.example.myapplication.ui.state.EmpresaUiState;

import java.util.Objects;

public class EmpresaAdapter extends ListAdapter<EmpresaUiState, EmpresaAdapter.ViewHolder> {

    public interface OnClickListener {
        void onClick(EmpresaUiState empresaUiState);
    }

    private final OnClickListener clickListener;

    public EmpresaAdapter(OnClickListener clickListener) {
        super(new DiffCallback());
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemEmpresaBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), clickListener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemEmpresaBinding binding;

        public ViewHolder(@NonNull ItemEmpresaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        protected void bind(EmpresaUiState item, OnClickListener listener) {
            setText(binding.textoNomeEmpresa, item.getNome());
            setVisible(item.isSelected(), binding.checkImage);
            binding.getRoot().setOnClickListener(v -> listener.onClick(item));
        }
    }

    private static class DiffCallback extends DiffUtil.ItemCallback<EmpresaUiState> {

        @Override
        public boolean areItemsTheSame(@NonNull EmpresaUiState oldItem, @NonNull EmpresaUiState newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull EmpresaUiState oldItem, @NonNull EmpresaUiState newItem) {
            return oldItem.getId() == newItem.getId()
                    && Objects.equals(oldItem.getNome(), newItem.getNome())
                    && oldItem.isSelected() == newItem.isSelected();
        }
    }
}
