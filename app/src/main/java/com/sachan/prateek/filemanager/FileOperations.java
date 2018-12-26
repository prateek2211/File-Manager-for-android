package com.sachan.prateek.filemanager;

import android.support.v4.provider.DocumentFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileOperations {
    File file;

    public static void pasteDoc(DocumentFile source, DocumentFile destination_Path) {

    }

    static void delete(File file) throws Exception {
        if (file.isFile()) {
            file.delete();
        } else {
            File[] files = file.listFiles();
            if (files.length == 0) {
                boolean able = file.delete();
                if (!able)
                    throw new Exception();
            }
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory())
                    delete(files[i]);
                else {
                    files[i].delete();
                }
            }
        }
    }

    void copyFile(File source, File destination, String name) throws IOException {
        if (source.isDirectory()) {
            copyDirectoryRecursively(source, destination, name);
        } else {
            FileInputStream fileInputStream = new FileInputStream(source);
            byte[] arr = new byte[fileInputStream.available()];
            fileInputStream.read(arr);
            File output = new File(destination, name);
            FileOutputStream fileOutputStream = new FileOutputStream(output);
            fileOutputStream.write(arr);
        }
    }

    private void copyDirectoryRecursively(File source, File destination, String name) throws IOException {
        File output = new File(destination, name);
        output.mkdirs();
        File[] contents = source.listFiles();
        for (int i = 0; i < contents.length; i++) {
            copyFile(contents[i], output, contents[i].getName());
        }

    }
}