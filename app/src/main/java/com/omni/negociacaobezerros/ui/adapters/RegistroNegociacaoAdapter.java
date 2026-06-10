package com.omni.negociacaobezerros.ui.adapters;

import static com.omni.negociacaobezerros.ui.helpers.ViewHelper.setText;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.omni.negociacaobezerros.databinding.ItemRegistroNegociacaoBinding;
import com.omni.negociacaobezerros.ui.states.RegistroNegociacaoUiState;

import java.util.Objects;

public class RegistroNegociacaoAdapter extends ListAdapter<RegistroNegociacaoUiState, RegistroNegociacaoAdapter.ViewHolder> {
    protected RegistroNegociacaoAdapter() {
        super(new DiffCallback());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemRegistroNegociacaoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemRegistroNegociacaoBinding itemRegistroNegociacaoBinding;

        public ViewHolder(@NonNull ItemRegistroNegociacaoBinding itemRegistroNegociacaoBinding) {
            super(itemRegistroNegociacaoBinding.getRoot());
            this.itemRegistroNegociacaoBinding = itemRegistroNegociacaoBinding;
        }

        void bind(RegistroNegociacaoUiState registroNegociacaoUiState) {
           setText(itemRegistroNegociacaoBinding.textViewItemEmpresaNome, registroNegociacaoUiState.getNomeEmpresa());
           setText(itemRegistroNegociacaoBinding.textViewItemEmpresaLocalizacao, registroNegociacaoUiState.getLocalizacao());
        }
    }

    private static class DiffCallback extends DiffUtil.ItemCallback<RegistroNegociacaoUiState> {

        @Override
        public boolean areItemsTheSame(@NonNull RegistroNegociacaoUiState oldItem, @NonNull RegistroNegociacaoUiState newItem) {
            return Objects.equals(oldItem.getNomeEmpresa(), newItem.getNomeEmpresa());
        }

        @Override
        public boolean areContentsTheSame(@NonNull RegistroNegociacaoUiState oldItem, @NonNull RegistroNegociacaoUiState newItem) {
            return Objects.equals(oldItem.getNomeEmpresa(), newItem.getNomeEmpresa()) &&
                    Objects.equals(oldItem.getLocalizacao(), newItem.getLocalizacao());
        }
    }
}
