package com.sachan.prateek.filemanager;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.FileProvider;
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

import static android.support.v4.app.NotificationCompat.Builder;
import static android.support.v4.app.NotificationCompat.PRIORITY_DEFAULT;

public class ActionModeCallBack implements ActionMode.Callback {
    List<File> sources;
    List<DocumentFile> source_doc;
    boolean cut;
    Button b1, b2;
    EditText editText;
    AlertDialog alertDialog1;
    GridAdapter gadapter;
    Handler handler;
    FileOperations fileOperations;
    private MyRecyclerAdapter adapter;
    private Context context;
    private Data_Manager data_manager;
    private int sortFlags;

    ActionModeCallBack(MyRecyclerAdapter adapter, Context context, Data_Manager data_manager, int sortFlags) {
        this.adapter = adapter;
        this.context = context;
        this.data_manager = data_manager;
        this.sortFlags = sortFlags;
        handler = new Handler(context.getMainLooper());
        fileOperations = new FileOperations();
    }

    ActionModeCallBack(GridAdapter adapter, Context context, Data_Manager data_manager, int sortFlags) {
        this.gadapter = adapter;
        this.context = context;
        this.data_manager = data_manager;
        this.sortFlags = sortFlags;
        fileOperations = new FileOperations();
        handler = new Handler(context.getMainLooper());
    }

    static DocumentFile getDocumentFile(File file) {
        String relativePath = file.getPath().substring(MainActivity.externalSD_root.getPath().length() + 1);
        String[] parts = relativePath.split("/");
        DocumentFile document = MainActivity.permadDocumentFile;

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            DocumentFile nextDocument = document.findFile(part);
            if (nextDocument != null) {
                document = nextDocument;
            }
        }
        return document;
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
//                        for (int i = 0; i < adapter.getSelectedItemCount(); i++)
//                            source_doc.add(MainActivity.documentFile.findFile(adapter.getSelectedItemsFile().get(i).getName()));
                    }
                    sources = gadapter.getSelectedItemsFile();
                    gadapter.clearSelection();
                } else {
                    if (MainActivity.sdCardmode) {
//                        for (int i = 0; i < adapter.getSelectedItemCount(); i++)
//                            source_doc.add(MainActivity.documentFile.findFile(adapter.getSelectedItemsFile().get(i).getName()));
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
//                    MainActivity.documentFile = MainActivity.permadDocumentFile;
                }
                mode.getMenuInflater().inflate(R.menu.paste_menu, mode.getMenu());
                break;
            case R.id.copy:
                MainActivity.isPasteMode = true;
                mode.getMenu().clear();
                mode.setTitle(Environment.getExternalStorageDirectory().getPath());
                if (MainActivity.gridView) {
                    if (MainActivity.sdCardmode) {
//                        for (int i = 0; i < adapter.getSelectedItemCount(); i++)
//                            source_doc.add(MainActivity.documentFile.findFile(adapter.getSelectedItemsFile().get(i).getName()));
                    }
                    sources = gadapter.getSelectedItemsFile();
                    gadapter.clearSelection();
                } else {
                    if (MainActivity.sdCardmode) {
//                        for (int i = 0; i < adapter.getSelectedItemCount(); i++)
//                            source_doc.add(MainActivity.documentFile.findFile(adapter.getSelectedItemsFile().get(i).getName()));
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
                    data_manager.setRecycler(MainActivity.getCurrentPath(), sortFlags);
                    if (MainActivity.gridView)
                        gadapter.notifyDataSetChanged();
                    else
                        adapter.notifyDataSetChanged();
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
                    temp = adapter.getSelectedItemCount();
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
                                    if (!MainActivity.sdCardmode && !MainActivity.collections) {
                                        while (files.get(i).exists()) {
                                            try {
                                                FileOperations.delete(files.get(i));
                                            } catch (Exception e) {
                                                Toast.makeText(context, "Sorry, unable to delete the file, don`t have permission", Toast.LENGTH_SHORT).show();
                                                break;
                                            }
                                        }
                                    } else if (MainActivity.collections) {
                                        try {
                                            FileOperations.delete(files.get(i));
                                        } catch (Exception e) {
                                            boolean b = getDocumentFile(sources.get(i)).delete();
                                            if (!b)
                                                Toast.makeText(context, "Sorry, Could not delete the selected file", Toast.LENGTH_SHORT).show();
                                        }
//                                        context.getContentResolver().delete(
//                                                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                                                MediaStore.MediaColumns.DATA + " = ?",
//                                                new String[]{data_manager.getFiles(i).getPath()});
                                        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(files.get(i))));
                                        MediaScannerConnection.scanFile(context, new String[]{files.get(i).getPath()},
                                                null, new MediaScannerConnection.OnScanCompletedListener() {
                                                    public void onScanCompleted(String path, Uri uri) {
                                                        context.getContentResolver()
                                                                .delete(uri, null, null);
                                                    }
                                                });
                                    } else {
//                                        if (MainActivity.searchMode)
//                                            Toast.makeText(context, "Can`t delete file on search mode", Toast.LENGTH_SHORT).show();
//                                        else
//                                            MainActivity.documentFile.findFile(files.get(0).getName()).delete();
                                        getDocumentFile(files.get(i)).delete();
                                    }
                                }
                                if (!MainActivity.collections)
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
                        if (!MainActivity.sdCardmode && !MainActivity.collections) {
                            if (MainActivity.gridView) {
                                boolean jobDone = gadapter.getSelectedItemsFile().get(0).renameTo(new File(MainActivity.getCurrentPath().getPath() + "/" + newName));
                                if (!jobDone)
                                    Toast.makeText(context, "Invalid FileName", Toast.LENGTH_SHORT).show();
                            } else {
                                boolean jobDone = adapter.getSelectedItemsFile().get(0).renameTo(new File(MainActivity.getCurrentPath().getPath() + "/" + newName));
                                if (!jobDone)
                                    Toast.makeText(context, "Invalid FileName", Toast.LENGTH_SHORT).show();
                            }
                        } else if (MainActivity.collections) {
                            final ContentValues contentValues = new ContentValues();
                            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, newName);
                            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(adapter.getSelectedItemsFile().get(0))));
                            MediaScannerConnection.scanFile(context, new String[]{adapter.getSelectedItemsFile().get(0).getPath()},
                                    null, new MediaScannerConnection.OnScanCompletedListener() {
                                        public void onScanCompleted(String path, Uri uri) {
                                            context.getContentResolver()
                                                    .update(uri, contentValues, null, null);
                                        }
                                    });
                        } else {
                            boolean x;
                            if (MainActivity.gridView)
                                x = getDocumentFile(gadapter.getSelectedItemsFile().get(0)).renameTo(newName);
                            else
                                x = getDocumentFile(adapter.getSelectedItemsFile().get(0)).renameTo(newName);
                            if (!x)
                                Toast.makeText(context, "Invalid FileName", Toast.LENGTH_LONG).show();
                        }
                        alertDialog1.cancel();
                        if (MainActivity.gridView)
                            gadapter.clearSelection();
                        else
                            adapter.clearSelection();
                        MainActivity.isPasteMode = false;
                        MainActivity.isSelection = false;
                        if (!MainActivity.collections)
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
                        if (MainActivity.gridView) {
                            gadapter.clearSelection();
                            gadapter.notifyDataSetChanged();
                        } else {
                            adapter.clearSelection();
                            adapter.notifyDataSetChanged();
                        }
                        mode.finish();
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
                MainActivity.collections = false;
                NotificationCompat.Builder builder1;
                NotificationManagerCompat managerCompat;
                builder1 = new Builder(context, MainActivity.CHANNEL_ID);
                builder1.setContentTitle("Copying Files").setSmallIcon(R.mipmap.my_app_icon).setAutoCancel(true).setPriority(PRIORITY_DEFAULT);
                builder1.setContentText("Copying files from " + sources.get(0).getName() + " to " + MainActivity.getCurrentPath().getName());
                builder1.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.copy));
                managerCompat = NotificationManagerCompat.from(context);
                builder1.setContentTitle("Files copied sucessfully");
                builder1.setContentText("100% completed");
                managerCompat.notify(1, builder1.build());
                backGroundCopy(mode);
//                for (int i = 0; i < sources.size(); i++) {
//                    if (sources.get(i).getPath().equals(new File(MainActivity.getCurrentPath(), sources.get(i).getName()).getPath())) {
//                        AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
//                        builder2.setMessage("Files with same name already exists")
//                                .setTitle("Warning")
//                                .setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//                            }
//                        });
//                        AlertDialog alertDialog2 = builder2.create();
//                        alertDialog2.show();
//                        break;
//                    }
//                }
                if (cut) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < sources.size(); i++) {
                                if (!MainActivity.sdCardmode) {
                                    while (sources.get(i).exists()) {
                                        try {
                                            FileOperations.delete(sources.get(i));
                                            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(sources.get(i))));
                                            MediaScannerConnection.scanFile(context, new String[]{sources.get(i).getPath()},
                                                    null, new MediaScannerConnection.OnScanCompletedListener() {
                                                        public void onScanCompleted(String path, Uri uri) {
                                                            context.getContentResolver()
                                                                    .delete(uri, null, null);
                                                        }
                                                    });
                                        } catch (Exception e) {
                                            boolean b = getDocumentFile(sources.get(i)).delete();
                                            if (!b)
                                                Toast.makeText(context, "Sorry, Could not delete the selected file", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } else {
                                    getDocumentFile(sources.get(i)).delete();
                                }
                            }
                            handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    mode.finish();
                                    data_manager.setRecycler(MainActivity.getCurrentPath(), sortFlags);
                                    if (MainActivity.gridView)
                                        gadapter.notifyDataSetChanged();
                                    else
                                        adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }).start();
                }
                mode.finish();
                if (MainActivity.sdCardmode) {
                    data_manager.setRecycler(MainActivity.getCurrentPath(), sortFlags);
                    if (MainActivity.gridView)
                        gadapter.notifyDataSetChanged();
                    else
                        adapter.notifyDataSetChanged();
                }
//                data_manager.setRecycler(MainActivity.getCurrentPath(), sortFlags);
//                if (MainActivity.gridView)
//                    gadapter.notifyDataSetChanged();
//                else
//                    adapter.notifyDataSetChanged();
                cut = false;
                break;
            case R.id.share:
                ArrayList<Uri> uris = new ArrayList<>();
                boolean possib = true;
                if (MainActivity.gridView) {
                    for (int i = 0; i < gadapter.getSelectedItemsFile().size(); i++) {
                        if (gadapter.getSelectedItemsFile().get(i).isDirectory()) {
                            Toast.makeText(context, "Sorry couldn`t share directories", Toast.LENGTH_SHORT).show();
                            possib = false;
                            break;
                        }
                        uris.add(FileProvider.getUriForFile(context, "com.sachan.prateek", gadapter.getSelectedItemsFile().get(i)));
                    }
                } else {
                    for (int i = 0; i < adapter.getSelectedItemsFile().size(); i++) {
                        if (adapter.getSelectedItemsFile().get(i).isDirectory()) {
                            Toast.makeText(context, "Sorry couldn`t share directories", Toast.LENGTH_SHORT).show();
                            possib = false;
                            break;
                        }
                        uris.add(FileProvider.getUriForFile(context, "com.sachan.prateek", adapter.getSelectedItemsFile().get(i)));
                    }
                }
                if (possib) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                        intent.setType("image/*");
                        context.startActivity(Intent.createChooser(intent, "Send Via"));
                    } catch (Exception e) {
                    }
                }
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

//    class BackgroundPaste extends AsyncTask<Object, Integer, Object> {
//        long totalSize;
//        NotificationCompat.Builder builder;
//        NotificationManagerCompat managerCompat;
//
//        BackgroundPaste() {
//            for (int i = 0; i < sources.size(); i++) {
//                totalSize += File_Properties.getFileFolderSize(sources.get(i));
//            }
//            builder = new Builder(context, MainActivity.CHANNEL_ID);
//            builder.setContentTitle("Copying Files").setSmallIcon(R.mipmap.my_app_icon).setAutoCancel(true).setPriority(PRIORITY_DEFAULT);
//            builder.setContentText("Copying files from " + sources.get(0).getName() + " to " + MainActivity.getCurrentPath().getName());
//            builder.setProgress(100, 0, false);
//            managerCompat = NotificationManagerCompat.from(context);
//            managerCompat.notify(1, builder.build());
//

//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            Toast.makeText(context, "FJILgh", Toast.LENGTH_SHORT).show();
//        }


//        @Override
//        protected Object doInBackground(Object... objects) {
//
//            for (int i = 0; i < sources.size(); i++) {
//                long sizedone = 0;
//                try {
//                    new FileOperations().copyFile(sources.get(i), MainActivity.getCurrentPath(), sources.get(i).getName());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                if (totalSize != 0)
//                    publishProgress((int) (sizedone / totalSize) * 100);
//            }
//            return null;
//        }
//
//        @Override
//        protected void onProgressUpdate(Integer... values) {
//            builder.setProgress(100, values[0], false);
//            managerCompat.notify(1, builder.build());
//        }


    //        @Override
//        protected void onPostExecute(Object o) {
//            NotificationCompat.Builder builder;
//            NotificationManagerCompat managerCompat;
//            builder = new Builder(context, MainActivity.CHANNEL_ID);
//            builder.setContentTitle("Copying Files").setSmallIcon(R.mipmap.my_app_icon).setAutoCancel(true).setPriority(PRIORITY_DEFAULT);
//            builder.setContentText("Copying files from " + sources.get(0).getName() + " to " + MainActivity.getCurrentPath().getName());
//            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.copy));
//            builder.setProgress(100, 0, false);
//            managerCompat = NotificationManagerCompat.from(context);
//            managerCompat.notify(1, builder.build());
//            builder.setContentTitle("Files copied sucessfully");
//            builder.setContentText("100% completed");
//            managerCompat.notify(1, builder.build());
//            data_manager.setRecycler(MainActivity.getCurrentPath(), sortFlags);
//            if (MainActivity.gridView)
//                gadapter.notifyDataSetChanged();
//            else
//                adapter.notifyDataSetChanged();
//            cut = false;
//        }
//    }
    void backGroundCopy(final ActionMode mode) {
        Thread myThread = new Thread(new Runnable() {
            @Override
            public void run() {

                for (int i = 0; i < sources.size(); i++) {
                    if (!MainActivity.sdCardmode) {
                        try {
                            FileOperations.copyFolder(sources.get(i), MainActivity.getCurrentPath());
                        } catch (IOException e) {
                            if (MainActivity.isExternalSD_available)
                                fileOperations.pasteDoc(sources.get(i), MainActivity.getCurrentPath(), context);
                            e.printStackTrace();
                        }
                    } else {
                        if (MainActivity.sdCardmode)
                            fileOperations.pasteDoc(sources.get(i), MainActivity.getCurrentPath(), context);
                    }
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mode.finish();
                        data_manager.setRecycler(MainActivity.getCurrentPath(), sortFlags);
                        if (MainActivity.gridView)
                            gadapter.notifyDataSetChanged();
                        else
                            adapter.notifyDataSetChanged();
                    }
                });

            }
        });
        myThread.start();
    }
}