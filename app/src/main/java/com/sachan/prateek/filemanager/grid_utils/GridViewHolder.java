package com.sachan.prateek.filemanager.grid_utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sachan.prateek.filemanager.R;

class GridViewHolder extends RecyclerView.ViewHolder {
    TextView textView;
    ImageView imageView;

    public GridViewHolder(@NonNull View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.text_grid);
        imageView = itemView.findViewById(R.id.icon_grid);
    }
}
