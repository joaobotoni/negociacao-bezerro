package com.omni.negociacaobezerros.ui.adapters;


import static com.omni.negociacaobezerros.ui.helpers.ViewHelper.setText;
import static com.omni.negociacaobezerros.utils.format.Numbers.formatCurrency;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.omni.negociacaobezerros.databinding.ItemNegociacaoAnimalBinding;
import com.omni.negociacaobezerros.ui.states.NegociacaoAnimalUiState;

import java.util.Locale;

public class NegociacaoAnimalAdapter extends ListAdapter<NegociacaoAnimalUiState, NegociacaoAnimalAdapter.ViewHolder> {
    public interface OnActionsListener {
        void edit(NegociacaoAnimalUiState negociacaoAnimalUiState);
        void remove(int id);
    }

    private final OnActionsListener actionsListener;

    protected NegociacaoAnimalAdapter(@NonNull OnActionsListener onActionsListener) {
        super(new DiffCallback());
        this.actionsListener = onActionsListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemNegociacaoAnimalBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), actionsListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), actionsListener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemNegociacaoAnimalBinding itemNegociacaoAnimalBinding;

        public ViewHolder(@NonNull ItemNegociacaoAnimalBinding itemNegociacaoAnimalBinding, OnActionsListener actionsListener) {
            super(itemNegociacaoAnimalBinding.getRoot());
            this.itemNegociacaoAnimalBinding = itemNegociacaoAnimalBinding;
        }

        void bind(NegociacaoAnimalUiState negociacaoAnimalUiState, OnActionsListener actionsListener) {

            setText(itemNegociacaoAnimalBinding.textViewItemNegociacaoAnimalTitle, String.valueOf(negociacaoAnimalUiState.getPeso()));
            setText(itemNegociacaoAnimalBinding.textViewItemNegociacaoAnimalDescricao, String.format(Locale.getDefault(), "%s. %s",
                            formatCurrency(negociacaoAnimalUiState.getValorPorCabeca()),
                            formatCurrency(negociacaoAnimalUiState.getValorPorQuilo())));
            itemNegociacaoAnimalBinding.imageViewItemNegociacaoAnimalEdit.setOnClickListener(v -> actionsListener.edit(negociacaoAnimalUiState));
            itemNegociacaoAnimalBinding.imageViewItemNegociacaoAnimalRemove.setOnClickListener(v -> actionsListener.remove(negociacaoAnimalUiState.getId()));
        }
    }

    private static class DiffCallback extends DiffUtil.ItemCallback<NegociacaoAnimalUiState> {

        @Override
        public boolean areItemsTheSame(@NonNull NegociacaoAnimalUiState oldItem, @NonNull NegociacaoAnimalUiState newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull NegociacaoAnimalUiState oldItem, @NonNull NegociacaoAnimalUiState newItem) {
            return oldItem.getId() == newItem.getId()
                    && Double.compare(oldItem.getPeso(), newItem.getPeso()) == 0
                    && oldItem.getValorPorQuilo().compareTo(newItem.getValorPorQuilo()) == 0
                    && oldItem.getValorPorCabeca().compareTo(newItem.getValorPorCabeca()) == 0;
        }
    }
}
