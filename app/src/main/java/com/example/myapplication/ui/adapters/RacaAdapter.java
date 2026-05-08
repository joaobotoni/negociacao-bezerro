package com.example.myapplication.ui.adapters;


import static com.example.myapplication.ui.helpers.ViewHelper.setText;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.ItemRacaBinding;
import com.example.myapplication.ui.state.RacaState;

import java.util.Objects;

public class RacaAdapter extends ListAdapter<RacaState, RacaAdapter.ViewHolder> {
    public interface OnClickListener {
        void onClick(RacaState categoria);
    }

    private final OnClickListener listener;

    public RacaAdapter(OnClickListener listener) {
        super(new DiffCallback());
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemRacaBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemRacaBinding binding;

        public ViewHolder(ItemRacaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        protected void bind(RacaState estado, OnClickListener listener) {
            setText(binding.textoOpcao,estado.getDescricao());
            binding.cardOpcao.setChecked(estado.isSelected());
            binding.cardOpcao.setOnClickListener(v -> listener.onClick(estado));
        }
    }

    private static class DiffCallback extends DiffUtil.ItemCallback<RacaState> {
        @Override
        public boolean areItemsTheSame(@NonNull RacaState oldItem, @NonNull RacaState newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull RacaState oldItem, @NonNull RacaState newItem) {
            return oldItem.getId() == newItem.getId()
                    && Objects.equals(oldItem.getDescricao(), newItem.getDescricao())
                    && oldItem.isSelected() == newItem.isSelected();
        }
    }
}