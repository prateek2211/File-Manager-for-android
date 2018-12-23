package com.sachan.prateek.filemanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.util.List;

public class ActionModeCallBack implements ActionMode.Callback {
    private MyRecyclerAdapter adapter;
    private Context context;
    private Data_Manager data_manager;
    private File path;
    private int sortFlags;


    ActionModeCallBack(MyRecyclerAdapter adapter, Context context, Data_Manager data_manager, File path, int sortFlags) {
        this.adapter = adapter;
        this.context = context;
        this.data_manager = data_manager;
        this.path = path;
        this.sortFlags = sortFlags;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.contextual_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cut:
                break;
            case R.id.copy:
                break;
            case R.id.delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure that you want to delete these " + adapter.getSelectedItemCount() + " items? You can`t recover these items later")
                        .setTitle("Warning")
                        .setCancelable(false)
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                List<File> files = adapter.getSelectedItemsFile();
                                for (int i = 0; i < files.size(); i++) {
                                    files.get(i).delete();
                                    files.remove(i);
                                }
                                data_manager.setRecycler(path, sortFlags);
                                adapter.notifyDataSetChanged();
                                mode.finish();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                break;
            case R.id.rename:
                break;
            case R.id.properties:
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.properties_dialog);
                dialog.show();
                break;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        adapter.clearSelection();
        MainActivity.isSelection = false;
    }
}