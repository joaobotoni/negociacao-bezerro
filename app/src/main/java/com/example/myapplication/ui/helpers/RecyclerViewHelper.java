package com.example.myapplication.ui.helpers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewHelper {

    private RecyclerViewHelper(){
        throw new AssertionError("RecyclerViewHelper é uma classe utilitária e não deve ser instanciada.");
    }
    public static void setupHorizontalRecyclerView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.Adapter<?> adapter, @NonNull Context context) {
        setupRecyclerView(recyclerView, adapter, context, LinearLayoutManager.HORIZONTAL);
    }

    public static void setupVerticalRecyclerView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.Adapter<?> adapter, @NonNull Context context) {
        setupRecyclerView(recyclerView, adapter, context, LinearLayoutManager.VERTICAL);
    }

    private static void setupRecyclerView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.Adapter<?> adapter, @NonNull Context context, int orientation) {
        recyclerView.setLayoutManager(new LinearLayoutManager(context, orientation, false));
        recyclerView.setAdapter(adapter);
    }
}
