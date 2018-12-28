package com.sachan.prateek.filemanager.grid_utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sachan.prateek.filemanager.Data_Manager;
import com.sachan.prateek.filemanager.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GridAdapter extends RecyclerView.Adapter<GridViewHolder> {
    private Data_Manager dataManager;
    private SparseBooleanArray selectionState;

    public GridAdapter(Data_Manager data_manager) {
        dataManager = data_manager;
        selectionState = new SparseBooleanArray();
    }

    @NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_grid, viewGroup, false);
        return new GridViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GridViewHolder gridViewHolder, int i) {
        if (dataManager.getName(i).length() > 12)
            gridViewHolder.textView.setText(dataManager.getName(i).substring(0, 11));
        else
            gridViewHolder.textView.setText(dataManager.getName(i));
        gridViewHolder.imageView.setImageResource(dataManager.getIconId(i));
        gridViewHolder.itemView.setActivated(selectionState.get(i, false));
    }

    @Override
    public int getItemCount() {
        return dataManager.name.size();
    }

    public void toggleSelection(int position) {
        if (selectionState.get(position, false)) {
            selectionState.delete(position);
        } else {
            selectionState.put(position, true);
        }
        notifyItemChanged(position);
    }

    public void clearSelection() {
        selectionState.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectionState.size();
    }

    public List<File> getSelectedItemsFile() {
        List<File> list = new ArrayList<>();
        for (int i = 0; i < selectionState.size(); i++) {
            list.add(dataManager.getFiles(selectionState.keyAt(i)));
        }
        return list;
    }
}
