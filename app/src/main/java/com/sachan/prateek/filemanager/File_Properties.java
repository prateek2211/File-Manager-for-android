package com.sachan.prateek.filemanager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;

public class File_Properties {
    TextView ftype;
    TextView last_Modified;
    TextView path;
    TextView fsize;
    TextView writable;
    AlertDialog alertDialog;

    public File_Properties(AlertDialog alertDialog) {
        this.alertDialog = alertDialog;
    }

    public static long getFileFolderSize(File dir) {
        long size = 0;
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isFile()) {
                    size += file.length();
                } else
                    size += getFileFolderSize(file);
            }
        } else if (dir.isFile()) {
            size += dir.length();
        }
        return size;
    }

    void setProperties(File file) {
        setVars();
        if (file.isDirectory())
            ftype.setText("Folder");
        else
            ftype.setText("File");
        path.setText(file.getPath());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        last_Modified.setText(dateFormat.format(file.lastModified()));
        if (file.canWrite())
            writable.setText("Yes");
        else
            writable.setText("No");
        long size = 0;
        size = getFileFolderSize(file);

        double sizeMB = (double) size / 1024 / 1024;
        String s = " MB";
        if (sizeMB < 1) {
            sizeMB = (double) size / 1024;
            s = " KB";
        }
        fsize.setText(sizeMB + s);
    }

    private void setVars() {
        ftype = alertDialog.findViewById(R.id.type);
        path = alertDialog.findViewById(R.id.path);
        fsize = alertDialog.findViewById(R.id.size);
        last_Modified = alertDialog.findViewById(R.id.last_modified);
        writable = alertDialog.findViewById(R.id.writable);
    }

}
