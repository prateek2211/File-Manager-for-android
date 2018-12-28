package com.sachan.prateek.filemanager.list_utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sachan.prateek.filemanager.R;

class MyViewHolder extends RecyclerView.ViewHolder {
    ImageView imageView;
    TextView time, date, title;

    MyViewHolder(@NonNull View itemView) {
        super(itemView);
        date = itemView.findViewById(R.id.date);
        time = itemView.findViewById(R.id.time);
        title = itemView.findViewById(R.id.title);
        imageView = itemView.findViewById(R.id.icon_file);
    }
}
