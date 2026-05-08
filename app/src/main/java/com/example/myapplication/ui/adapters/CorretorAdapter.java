package com.example.myapplication.ui.adapters;

import static com.example.myapplication.ui.helpers.FormatHelper.formatCurrency;
import static com.example.myapplication.ui.helpers.ViewHelper.setText;
import static com.example.myapplication.ui.helpers.ViewHelper.setVisible;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.ItemCorretorBinding;
import com.example.myapplication.ui.state.CorretorState;

import java.util.Objects;

public class CorretorAdapter extends ListAdapter<CorretorState, CorretorAdapter.ViewHolder> {
    public interface OnClickListener {
        void onClick(CorretorState corretorState);
    }

    private final OnClickListener listener;

    public CorretorAdapter(OnClickListener listener) {
        super(new DiffCallback());
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemCorretorBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemCorretorBinding binding;

        public ViewHolder(@NonNull ItemCorretorBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        protected void bind(CorretorState item, OnClickListener listener) {
            setText(binding.textoNomeCorretor, item.getNome());
            setText(binding.textoComissao, formatCurrency(item.getComissao()));
            setVisible(item.isSelected(), binding.checkImage);
            binding.getRoot().setOnClickListener(v -> listener.onClick(item));
        }
    }


    private static class DiffCallback extends DiffUtil.ItemCallback<CorretorState> {
        @Override
        public boolean areItemsTheSame(@NonNull CorretorState oldItem, @NonNull CorretorState newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull CorretorState oldItem, @NonNull CorretorState newItem) {
            return oldItem.getId() == newItem.getId()
                    && Objects.equals(oldItem.getNome(), newItem.getNome())
                    && Objects.equals(oldItem.getComissao(), newItem.getComissao())
                    && oldItem.isSelected() == newItem.isSelected();
        }
    }
}
