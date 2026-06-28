package com.omni.negociacaobezerros.ui.adapters;

import static com.omni.negociacaobezerros.helpers.ViewHelper.setText;
import static com.omni.negociacaobezerros.helpers.ViewHelper.setVisible;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.omni.negociacaobezerros.databinding.ItemEmpresaBinding;
import com.omni.negociacaobezerros.ui.states.EmpresaUiState;

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
        return new ViewHolder(ItemEmpresaBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemEmpresaBinding binding;
        private EmpresaUiState item;

        public ViewHolder(@NonNull ItemEmpresaBinding binding, OnClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(v -> {
                if (item != null) listener.onClick(item);
            });
        }

        protected void bind(EmpresaUiState empresa) {
            this.item = empresa;
            setText(binding.textoNomeEmpresa, empresa.getNome());
            setVisible(empresa.isSelecionada(), binding.checkImage);
        }
    }

    private static class DiffCallback extends DiffUtil.ItemCallback<EmpresaUiState> {

        @Override
        public boolean areItemsTheSame(@NonNull EmpresaUiState oldItem, @NonNull EmpresaUiState newItem) {
            return Objects.equals(oldItem.getNome(), newItem.getNome());
        }

        @Override
        public boolean areContentsTheSame(@NonNull EmpresaUiState oldItem, @NonNull EmpresaUiState newItem) {
            return Objects.equals(oldItem.getNome(), newItem.getNome())
                    && Objects.equals(oldItem.getLocalizacao(), newItem.getLocalizacao())
                    && oldItem.isSelecionada() == newItem.isSelecionada();
        }
    }
}
