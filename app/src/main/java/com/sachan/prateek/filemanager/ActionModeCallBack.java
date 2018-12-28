package com.sachan.prateek.filemanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.provider.DocumentFile;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sachan.prateek.filemanager.grid_utils.GridAdapter;
import com.sachan.prateek.filemanager.list_utils.MyRecyclerAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ActionModeCallBack implements ActionMode.Callback {
    List<File> sources;
    List<DocumentFile> source_doc;
    boolean cut;
    Button b1, b2;
    EditText editText;
    AlertDialog alertDialog1;
    GridAdapter gadapter;
    private MyRecyclerAdapter adapter;
    private Context context;
    private Data_Manager data_manager;
    private int sortFlags;

    ActionModeCallBack(MyRecyclerAdapter adapter, Context context, Data_Manager data_manager, int sortFlags) {
        this.adapter = adapter;
        this.context = context;
        this.data_manager = data_manager;
        this.sortFlags = sortFlags;
    }

    ActionModeCallBack(GridAdapter adapter, Context context, Data_Manager data_manager, int sortFlags) {
        this.gadapter = adapter;
        this.context = context;
        this.data_manager = data_manager;
        this.sortFlags = sortFlags;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.contextual_menu, menu);
        source_doc = new ArrayList<>();
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
                cut = true;
                MainActivity.isPasteMode = true;
                mode.getMenu().clear();
                mode.setTitle(Environment.getExternalStorageDirectory().getPath());
                if (MainActivity.gridView) {
                    if (MainActivity.sdCardmode) {
                        for (int i = 0; i < adapter.getSelectedItemCount(); i++)
                            source_doc.add(MainActivity.documentFile.findFile(adapter.getSelectedItemsFile().get(i).getName()));
                    }
                    sources = gadapter.getSelectedItemsFile();
                    gadapter.clearSelection();
                } else {
                    if (MainActivity.sdCardmode) {
                        for (int i = 0; i < adapter.getSelectedItemCount(); i++)
                            source_doc.add(MainActivity.documentFile.findFile(adapter.getSelectedItemsFile().get(i).getName()));
                    }
                    sources = adapter.getSelectedItemsFile();
                    adapter.clearSelection();
                }
                MainActivity.isSelection = false;
                if (!MainActivity.sdCardmode) {
                    MainActivity.path = Environment.getExternalStorageDirectory();
                    data_manager.setRecycler(MainActivity.getCurrentPath(), sortFlags);
                    if (MainActivity.gridView)
                        gadapter.notifyDataSetChanged();
                    else
                        adapter.notifyDataSetChanged();
                } else {
                    MainActivity.path = MainActivity.externalSD_root;
                    MainActivity.documentFile = MainActivity.permadDocumentFile;
                }
                mode.getMenuInflater().inflate(R.menu.paste_menu, mode.getMenu());
                break;
            case R.id.copy:
                MainActivity.isPasteMode = true;
                mode.getMenu().clear();
                mode.setTitle(Environment.getExternalStorageDirectory().getPath());
                if (MainActivity.gridView) {
                    if (MainActivity.sdCardmode) {
                        for (int i = 0; i < adapter.getSelectedItemCount(); i++)
                            source_doc.add(MainActivity.documentFile.findFile(adapter.getSelectedItemsFile().get(i).getName()));
                    }
                    sources = gadapter.getSelectedItemsFile();
                    gadapter.clearSelection();
                } else {
                    if (MainActivity.sdCardmode) {
                        for (int i = 0; i < adapter.getSelectedItemCount(); i++)
                            source_doc.add(MainActivity.documentFile.findFile(adapter.getSelectedItemsFile().get(i).getName()));
                    }
                    sources = adapter.getSelectedItemsFile();
                    adapter.clearSelection();
                }
                MainActivity.isSelection = false;
                if (!MainActivity.sdCardmode) {
                    MainActivity.path = Environment.getExternalStorageDirectory();
                    data_manager.setRecycler(MainActivity.getCurrentPath(), sortFlags);
                    if (MainActivity.gridView)
                        gadapter.notifyDataSetChanged();
                    else
                        adapter.notifyDataSetChanged();
                } else {
                    MainActivity.path = MainActivity.externalSD_root;
                    MainActivity.documentFile = MainActivity.permadDocumentFile;
                }
                mode.getMenuInflater().inflate(R.menu.paste_menu, mode.getMenu());
                break;
            case R.id.delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                int temp = 0;
                if (MainActivity.gridView)
                    temp = gadapter.getSelectedItemCount();
                else
                    adapter.getSelectedItemCount();
                builder.setMessage("Are you sure that you want to delete these " + temp + " items? You can`t recover these items later")
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
                                List<File> files;
                                if (MainActivity.gridView)
                                    files = gadapter.getSelectedItemsFile();
                                else
                                    files = adapter.getSelectedItemsFile();
                                for (int i = 0; i < files.size(); i++) {
                                    if (!MainActivity.sdCardmode) {
                                        while (files.get(i).exists()) {
                                            try {
                                                FileOperations.delete(files.get(i));
                                            } catch (Exception e) {
                                                Toast.makeText(context, "Sorry, unable to delete the file, don`t have permission", Toast.LENGTH_SHORT).show();
                                                break;
                                            }
                                        }
                                    } else {
                                        MainActivity.documentFile.findFile(files.get(0).getName()).delete();

                                    }
                                    files.remove(i);
                                }
                                data_manager.setRecycler(MainActivity.getCurrentPath(), sortFlags);
                                if (MainActivity.gridView)
                                    gadapter.notifyDataSetChanged();
                                else
                                    adapter.notifyDataSetChanged();
                                mode.finish();
                            }
                        });
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();
                break;
            case R.id.rename:
                AlertDialog.Builder bobTheBuilder = new AlertDialog.Builder(context);
                bobTheBuilder.setView(R.layout.rename_dialog).setTitle("Rename").setCancelable(false);
                alertDialog1 = bobTheBuilder.create();
                alertDialog1.show();
                editText = alertDialog1.findViewById(R.id.renameText);
                if (MainActivity.gridView)
                    editText.setText(gadapter.getSelectedItemsFile().get(0).getName());
                else
                    editText.setText(adapter.getSelectedItemsFile().get(0).getName());
                editText.selectAll();
                b1 = alertDialog1.findViewById(R.id.cancel);
                b2 = alertDialog1.findViewById(R.id.ok);
                b2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newName = editText.getText().toString();
                        if (!MainActivity.sdCardmode) {
                            if (MainActivity.gridView) {
                                boolean jobDone = gadapter.getSelectedItemsFile().get(0).renameTo(new File(MainActivity.getCurrentPath().getPath() + "/" + newName));
                                if (!jobDone)
                                    Toast.makeText(context, "Invalid FileName", Toast.LENGTH_SHORT).show();
                            } else {
                                boolean jobDone = adapter.getSelectedItemsFile().get(0).renameTo(new File(MainActivity.getCurrentPath().getPath() + "/" + newName));
                                if (!jobDone)
                                    Toast.makeText(context, "Invalid FileName", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            boolean x = MainActivity.documentFile.findFile(adapter.getSelectedItemsFile().get(0).getName()).renameTo(newName);
                            if (!x)
                                Toast.makeText(context, "Sorry unable to process the request", Toast.LENGTH_LONG).show();
                        }
                        alertDialog1.cancel();
                        if (MainActivity.gridView)
                            gadapter.clearSelection();
                        else
                            adapter.clearSelection();
                        MainActivity.isPasteMode = false;
                        MainActivity.isSelection = false;
                        data_manager.setRecycler(MainActivity.getCurrentPath(), sortFlags);
                        if (MainActivity.gridView)
                            gadapter.notifyDataSetChanged();
                        else
                            adapter.notifyDataSetChanged();
                        mode.finish();

                    }
                });
                b1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog1.cancel();
                    }
                });
                break;
            case R.id.properties:
                AlertDialog.Builder propBuilder = new AlertDialog.Builder(context);
                propBuilder.setView(R.layout.properties_dialog);
                propBuilder.setTitle("Properties").setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = propBuilder.create();
                dialog.show();
                if (MainActivity.gridView)
                    new File_Properties(dialog).setProperties(gadapter.getSelectedItemsFile().get(0));
                else
                    new File_Properties(dialog).setProperties(adapter.getSelectedItemsFile().get(0));
                break;
            case R.id.paste:
                for (int i = 0; i < sources.size(); i++) {
                    if (sources.get(i).getPath().equals(new File(MainActivity.getCurrentPath(), sources.get(i).getName()).getPath())) {
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                        builder2.setMessage("Files with same name already exists")
                                .setTitle("Warning")
                                .setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alertDialog2 = builder2.create();
                        alertDialog2.show();

                        break;
                    } else {
                        try {
                            if (MainActivity.sdCardmode) {
                                FileOperations.pasteDoc(source_doc.get(i), MainActivity.documentFile);
                            }
                            new FileOperations().copyFile(sources.get(i), MainActivity.getCurrentPath(), sources.get(i).getName());
                        } catch (IOException e) {
                        }
                        if (!MainActivity.sdCardmode) {
                            if (cut) {
                                while (sources.get(i).exists()) {
                                    try {
                                        FileOperations.delete(sources.get(i));
                                    } catch (Exception e) {
                                        Toast.makeText(context, "Sorry, unable to delete the file, don`t have permission", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        } else {
                            source_doc.get(i).delete();
                        }
                    }
                }
                mode.finish();
                data_manager.setRecycler(MainActivity.getCurrentPath(), sortFlags);
                if (MainActivity.gridView)
                    gadapter.notifyDataSetChanged();
                else
                    adapter.notifyDataSetChanged();
                cut = false;
                break;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        if (MainActivity.gridView)
            gadapter.clearSelection();
        else
            adapter.clearSelection();
        MainActivity.isPasteMode = false;
        MainActivity.isSelection = false;
        cut = false;
    }
}