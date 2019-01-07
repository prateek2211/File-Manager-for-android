package com.sachan.prateek.filemanager.grid_utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sachan.prateek.filemanager.Data_Manager;
import com.sachan.prateek.filemanager.R;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class GridAdapter extends RecyclerView.Adapter<GridViewHolder> {
    Context context;
    private Data_Manager dataManager;
    private SparseBooleanArray selectionState;

    public GridAdapter(Data_Manager data_manager, Context context) {
        dataManager = data_manager;
        selectionState = new SparseBooleanArray();
        this.context = context;
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
        new BackImageLoading(i).execute(gridViewHolder);
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

    class BackImageLoading extends AsyncTask<GridViewHolder, GridViewHolder, Object> {
        Bitmap bitmap;
        private int i;

        BackImageLoading(int i) {
            this.i = i;
        }

        @Override
        protected Object doInBackground(GridViewHolder... myViewHolders) {
            String fileType = "";
            URL url = null;
            try {
                url = new URL("file://" + dataManager.getFiles(i).getPath());
                URLConnection connection = url.openConnection();
                fileType = connection.getContentType();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (fileType.contains("image/")) {
                if (dataManager.getFiles(i).length() > 120000) {
                    long imageId = 0;
                    String[] projection = new String[]{
                            MediaStore.Images.Media.DATA,
                            MediaStore.Images.Media._ID
                    };
                    Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
                    cursor.moveToFirst();
                    do {
                        if (cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)).equals(dataManager.getFiles(i).getPath())) {
                            imageId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                            break;
                        }
                    } while (cursor.moveToNext());


                    bitmap = MediaStore.Images.Thumbnails.getThumbnail(
                            context.getContentResolver(), imageId, MediaStore.Images.Thumbnails.MINI_KIND, null);
                }

                if (bitmap == null) {
                    final int THUMBSIZE = 64;
                    bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(dataManager.getFiles(i).getPath()),
                            THUMBSIZE, THUMBSIZE);
                }

                publishProgress(myViewHolders);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(GridViewHolder... values) {

            values[0].imageView.setImageBitmap(bitmap);
        }
    }
}
