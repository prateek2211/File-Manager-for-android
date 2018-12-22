package com.sachan.prateek.filemanager;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
