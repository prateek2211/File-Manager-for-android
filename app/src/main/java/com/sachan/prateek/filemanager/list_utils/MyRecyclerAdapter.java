package com.sachan.prateek.filemanager.list_utils;

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

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private Data_Manager dataManager;
    private SparseBooleanArray selectionState;

    public MyRecyclerAdapter(Data_Manager data_manager) {
        dataManager = data_manager;
        selectionState = new SparseBooleanArray();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.viewholder_recycler, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
//        String s = dataManager.files[i].getAbsolutePath();
//        if (!dataManager.files[i].isDirectory() && s.contains(".png")) {
//            try {
//                FileInputStream inputStream = new FileInputStream(dataManager.getFiles(i));
//
//                byte[] imageData = new byte[inputStream.available()];
//                while (inputStream.read() != -1)
//                    inputStream.read(imageData);
//                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
//                myViewHolder.imageView.setImageBitmap
//                        (Bitmap.createScaledBitmap
//                                (bitmap, myViewHolder.imageView.getWidth(), myViewHolder.imageView.getHeight(), false));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } else {
            myViewHolder.imageView.setImageResource(dataManager.getIconId(i));
//        }
        myViewHolder.title.setText(dataManager.getName(i));
        myViewHolder.date.setText(dataManager.getDate_and_time(i));
        myViewHolder.itemView.setActivated(selectionState.get(i, false));
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