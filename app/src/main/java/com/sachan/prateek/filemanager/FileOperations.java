package com.sachan.prateek.filemanager;

import android.content.Context;
import android.support.v4.provider.DocumentFile;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileOperations {
    File file;

    static void delete(File file) throws Exception {
        if (file.isFile()) {
            boolean able = file.delete();
            if (!able)
                throw new Exception();
        } else {
            File[] files = file.listFiles();
            if (files.length == 0) {
                boolean able = file.delete();
                if (!able)
                    throw new Exception();
            }
            for (File file1 : files) {
                if (file1.isDirectory())
                    delete(file1);
                else {
                    file1.delete();
                }
            }
        }
    }

    public static String mime(String URI) {
        String type = null;
        String extention = MimeTypeMap.getFileExtensionFromUrl(URI);
        if (extention != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extention);
        }
        return type;
    }

    public static void copyFolder(File src, File dest)
            throws IOException {

        if (src.isDirectory()) {
            dest = new File(dest, src.getName());
            if (!dest.exists()) {
                dest.mkdirs();
            }

            String files[] = src.list();

            for (String file : files) {
                File srcFile = new File(src, file);
                copyFolder(srcFile, dest);
            }

        } else {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(new File(dest, src.getName()));
            byte[] buffer = new byte[1024];

            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            in.close();
            out.close();
        }
    }

    public void pasteDoc(File source, File destination_Path, Context context) {
        if (source.isFile()) {
            copy(source, destination_Path, context);
        } else {
            ActionModeCallBack.getDocumentFile(destination_Path).createDirectory(source.getName());
            File createdDir = new File(destination_Path, source.getName());
            File[] content = source.listFiles();
            for (int i = 0; i < content.length; i++) {
                try {
                    if (content[i].isFile())
                        copy(content[i], createdDir, context);
                    else
                        pasteDoc(content[i], createdDir, context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean copy(File copy, File directory, Context con) {
        FileInputStream inStream = null;
        OutputStream outStream = null;
        DocumentFile dir = ActionModeCallBack.getDocumentFile(directory);
        String mime = mime(copy.toURI().toString());
        DocumentFile copy1 = dir.createFile(mime, copy.getName());
        try {
            inStream = new FileInputStream(copy);
            outStream = con.getContentResolver().openOutputStream(copy1.getUri());
            byte[] buffer = new byte[inStream.available()];
            int bytesRead;
            while ((bytesRead = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inStream.close();
                outStream.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}