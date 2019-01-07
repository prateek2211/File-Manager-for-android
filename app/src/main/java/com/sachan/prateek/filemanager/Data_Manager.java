package com.sachan.prateek.filemanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Data_Manager {
    public List<String> name;
    Context mContext;
    private File[] files;
    private List<String> date_and_time;

    void setRecycler(File path, int sortFlags) {
        date_and_time = new ArrayList<>();
        name = new ArrayList<>();
        files = path.listFiles();
        if (files == null) {
            files = new File[0];
        }
        if (sortFlags == 1)
            sortByName(files);
        else if (sortFlags == 2)
            sortByDate(files);
        else if (sortFlags == 3)
            sortBySize(files);
        else if (sortFlags == -1)
            sortByNameReverse(files);
        else if (sortFlags == -2)
            sortByDateReverse(files);
        else if (sortFlags == -3)
            sortBySizeReverse(files);
        for (File file : files) {
            name.add(file.getName());
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy | HH:mm:ss");
            date_and_time.add(dateFormat.format(file.lastModified()));
        }
    }

    void setSearchResults(List<File> list) {
        date_and_time = new ArrayList<>();
        name = new ArrayList<>();
        files = new File[list.size()];
        for (int i = 0; i < list.size(); i++) {
            files[i] = list.get(i);
            name.add(files[i].getName());
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy | HH:mm:ss");
            date_and_time.add(dateFormat.format(files[i].lastModified()));
        }
    }

    public File getFiles(int position) {
        return files[position];
    }

    public String getName(int position) {
        return name.get(position);
    }

    public String getDate_and_time(int position) {
        return date_and_time.get(position);
    }

    public int getIconId(int position) {
        String s = files[position].getAbsolutePath();
        String fileType = "";
        URL url = null;
        try {
            url = new URL("file://" + files[position].getPath());
            URLConnection connection = url.openConnection();
            fileType = connection.getContentType();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (files[position].isDirectory())
            return R.drawable.foldericon;
        else if (s.contains(".apk"))
            return R.drawable.apk;
        else if (s.contains(".docx") || s.contains(".txt"))
            return R.drawable.docx;
        else if (fileType.contains("audio/"))
            return R.drawable.audio;
        else if (fileType.contains("video/"))
            return R.drawable.video;
        else if (fileType.contains("image/"))
            return R.drawable.image;
        else if (s.contains(".pdf"))
            return R.drawable.pdf;
        else if (s.contains(".ppt"))
            return R.drawable.ppt;
        else if (s.contains(".xls"))
            return R.drawable.xls;

        return R.drawable.my;
    }

    public void sortByName(File[] fileCmp) {
        Arrays.sort(fileCmp, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
            }
        });
    }

    public void sortByNameReverse(File[] fileCmp) {
        Arrays.sort(fileCmp, Collections.<File>reverseOrder(new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }
        }));
    }

    public void sortByDate(File[] fileCmp) {
        Arrays.sort(fileCmp, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return Long.valueOf(o1.lastModified()).compareTo(o2.lastModified());
            }
        });
    }

    void sortByDateReverse(File[] fileCmp) {
        Arrays.sort(fileCmp, Collections.<File>reverseOrder(new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return Long.valueOf(o1.lastModified()).compareTo(o2.lastModified());
            }
        }));
    }

    void sortBySize(File[] fileCmp) {
        Arrays.sort(fileCmp, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return Long.valueOf(o1.length()).compareTo(o2.length());
            }
        });
    }

    void sortBySizeReverse(File[] fileCmp) {
        Arrays.sort(fileCmp, Collections.<File>reverseOrder(new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return Long.valueOf(o1.length()).compareTo(o2.length());
            }
        }));
    }

    void setImagesData(Context mContext) {
        this.mContext = mContext;
        date_and_time = new ArrayList<>();
        name = new ArrayList<>();
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA
        };

        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        CursorLoader loader = new CursorLoader(mContext, images, projection, null, null, null);
        Cursor cursor = loader.loadInBackground();
        cursor.moveToFirst();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        if (cursor != null) {
            files = new File[cursor.getCount()];
        }
        int i = 0;
        do {
            files[i] = new File(cursor.getString(column_index));
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy | HH:mm:ss");
            date_and_time.add(dateFormat.format(files[i].lastModified()));
            name.add(files[i].getName());
            i++;
        } while (cursor.moveToNext());
    }

    void setAudio(Context context) {
        this.mContext = context;
        date_and_time = new ArrayList<>();
        name = new ArrayList<>();
        Uri audioUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{
                MediaStore.Audio.Media.DATA
        };
        Cursor cursor = context.getContentResolver().query(audioUri, projection, null, null, null);
        files = new File[cursor.getCount()];
        cursor.moveToFirst();
        int i = 0;
        if (cursor.getCount() != 0) {
            do {
                files[i] = new File(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy | HH:mm:ss");
                date_and_time.add(dateFormat.format(files[i].lastModified()));
                name.add(files[i].getName());
                i++;
            } while (cursor.moveToNext());
        } else {
            Toast.makeText(context, "No Audio Files Present", Toast.LENGTH_LONG).show();
        }
    }

    void setDocs(Context context) {
        mContext = context;
        date_and_time = new ArrayList<>();
        name = new ArrayList<>();
        String pdf = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf");
        String doc = MimeTypeMap.getSingleton().getMimeTypeFromExtension("doc");
        String docx = MimeTypeMap.getSingleton().getMimeTypeFromExtension("docx");
        String xls = MimeTypeMap.getSingleton().getMimeTypeFromExtension("xls");
        String xlsx = MimeTypeMap.getSingleton().getMimeTypeFromExtension("xlsx");
        String ppt = MimeTypeMap.getSingleton().getMimeTypeFromExtension("ppt");
        String pptx = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pptx");
        String txt = MimeTypeMap.getSingleton().getMimeTypeFromExtension("txt");
        String html = MimeTypeMap.getSingleton().getMimeTypeFromExtension("html");
        Uri uri = MediaStore.Files.getContentUri("external");
        String[] projection = {MediaStore.Files.FileColumns.DATA};
        String selection = MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        String[] args = new String[]{pdf, doc, docx, xls, xlsx, ppt, pptx, txt, html};

        Cursor cursor = mContext.getContentResolver().query(uri, projection, selection, args, null);
        files = new File[cursor.getCount()];
        cursor.moveToFirst();
        int i = 0;
        if (cursor.getCount() != 0) {
            do {
                files[i] = new File(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)));
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy | HH:mm:ss");
                date_and_time.add(dateFormat.format(files[i].lastModified()));
                name.add(files[i].getName());
                i++;
            } while (cursor.moveToNext());
        } else {
            Toast.makeText(context, "No Document Files Present", Toast.LENGTH_LONG).show();
        }
    }

    void sortCollectionsBySize() {
        sortBySize(files);
        date_and_time = new ArrayList<>();
        name = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            name.add(files[i].getName());
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy | HH:mm:ss");
            date_and_time.add(dateFormat.format(files[i].lastModified()));
        }

    }

    void sortCollectionsByName() {
        sortByName(files);
        date_and_time = new ArrayList<>();
        name = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            name.add(files[i].getName());
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy | HH:mm:ss");
            date_and_time.add(dateFormat.format(files[i].lastModified()));
        }

    }

    void sortCollectionsByDate() {
        sortByDate(files);
        date_and_time = new ArrayList<>();
        name = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            name.add(files[i].getName());
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy | HH:mm:ss");
            date_and_time.add(dateFormat.format(files[i].lastModified()));
        }

    }
//    public static DocumentFile getDocumentFile(final File file) {
//        String baseFolder = getExtSdCardFolder(file);
//        String relativePath = null;
//
//        if (baseFolder == null) {
//            return null;
//        }
//
//        try {
//            String fullPath = file.getCanonicalPath();
//            relativePath = fullPath.substring(baseFolder.length() + 1);
//        } catch (IOException e) {
//            Logger.log(e.getMessage());
//            return null;
//        }
//        Uri treeUri = Common.getInstance().getContentResolver().getPersistedUriPermissions().get(0).getUri();
//
//        if (treeUri == null) {
//            return null;
//        }
//
//        // start with root of SD card and then parse through document tree.
//        DocumentFile document = DocumentFile.fromTreeUri(Common.getInstance(), treeUri);
//
//        String[] parts = relativePath.split("\\/");
//
//        for (String part : parts) {
//            DocumentFile nextDocument = document.findFile(part);
//            if (nextDocument != null) {
//                document = nextDocument;
//            }
//        }
//
//        return document;
//    }
//
//
//    public static String getExtSdCardFolder(File file) {
//        String[] extSdPaths = getExtSdCardPaths();
//        try {
//            for (int i = 0; i < extSdPaths.length; i++) {
//                if (file.getCanonicalPath().startsWith(extSdPaths[i])) {
//                    return extSdPaths[i];
//                }
//            }
//        } catch (IOException e) {
//            return null;
//        }
//        return null;
//    }
//
//    @TargetApi(Build.VERSION_CODES.KITKAT)
//    public static String[] getExtSdCardPaths() {
//        List<String> paths = new ArrayList<>();
//        for (File file : Common.getInstance().getExternalFilesDirs("external")) {
//
//            if (file != null && !file.equals(Common.getInstance().getExternalFilesDir("external"))) {
//                int index = file.getAbsolutePath().lastIndexOf("/Android/data");
//                if (index < 0) {
//                    Log.w("asd", "Unexpected external file dir: " + file.getAbsolutePath());
//                } else {
//                    String path = file.getAbsolutePath().substring(0, index);
//                    try {
//                        path = new File(path).getCanonicalPath();
//                    } catch (IOException e) {
//                        // Keep non-canonical path.
//                    }
//                    paths.add(path);
//                }
//            }
//        }
//        return paths.toArray(new String[paths.size()]);

}