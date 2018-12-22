package com.sachan.prateek.filemanager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private Data_Manager dataManager;

    MyRecyclerAdapter(Data_Manager data_manager) {
        dataManager = data_manager;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.viewholder_recycler, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        String s = dataManager.files[i].getAbsolutePath();
        if (!dataManager.files[i].isDirectory()&&s.contains(".png")) {
                try {
                    FileInputStream inputStream = new FileInputStream(dataManager.getFiles(i));

                    byte[] imageData = new byte[inputStream.available()];
                    while (inputStream.read() != -1)
                        inputStream.read(imageData);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                    myViewHolder.imageView.setImageBitmap
                            (Bitmap.createScaledBitmap
                                    (bitmap, myViewHolder.imageView.getWidth(),myViewHolder.imageView.getHeight(), false));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        else {
        myViewHolder.imageView.setImageResource(dataManager.getIconId(i));
        }
        myViewHolder.title.setText(dataManager.getName(i));
        myViewHolder.date.setText(dataManager.getDate_and_time(i));
    }


    @Override
    public int getItemCount() {
        return dataManager.name.size();
    }
}