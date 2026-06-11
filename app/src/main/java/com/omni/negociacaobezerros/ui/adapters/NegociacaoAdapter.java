package com.omni.negociacaobezerros.ui.adapters;

import static com.omni.negociacaobezerros.ui.helpers.ViewHelper.setText;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.omni.negociacaobezerros.databinding.ItemNegociacaoBinding;
import com.omni.negociacaobezerros.ui.states.NegociacaoUiState;

import java.util.Locale;
import java.util.Objects;
public class NegociacaoAdapter extends ListAdapter<NegociacaoUiState, NegociacaoAdapter.ViewHolder> {

    private NegociacaoAdapter() {
        super(new DiffCallback());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemNegociacaoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemNegociacaoBinding binding;
        public ViewHolder(@NonNull ItemNegociacaoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(NegociacaoUiState negociacaoUiState){
          setText(binding.textViewItemNegociacaoDescricao, String.format(Locale.getDefault(), "%s. %s", negociacaoUiState.getNomeEmpresa(),
                  negociacaoUiState.getQuantidadeAnimal()));
        }
    }

    private static class DiffCallback extends DiffUtil.ItemCallback<NegociacaoUiState> {

        @Override
        public boolean areItemsTheSame(@NonNull NegociacaoUiState oldItem, @NonNull NegociacaoUiState newItem) {
            return Objects.equals(oldItem.getNomeEmpresa(), newItem.getNomeEmpresa());
        }

        @Override
        public boolean areContentsTheSame(@NonNull NegociacaoUiState oldItem, @NonNull NegociacaoUiState newItem) {
            return Objects.equals(oldItem.getNomeEmpresa(), newItem.getNomeEmpresa()) &&
                    oldItem.getQuantidadeAnimal() == newItem.getQuantidadeAnimal();
        }
    }
}
