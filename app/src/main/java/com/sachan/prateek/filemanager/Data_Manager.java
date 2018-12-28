package com.sachan.prateek.filemanager;

import android.annotation.SuppressLint;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Data_Manager {
    public List<String> name;
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
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
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
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
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
        if (files[position].isDirectory())
            return R.drawable.foldericon;
        else if (s.contains(".apk"))
            return R.drawable.apk;
        else if (s.contains(".docx") || s.contains(".txt"))
            return R.drawable.docx;
        else if (s.contains(".mp4") || s.contains(".mp3"))
            return R.drawable.mp_three;
        else if (s.contains(".pdf"))
            return R.drawable.pdf;
        else if (s.contains(".ppt"))
            return R.drawable.ppt;
        else if (s.contains(".xls"))
            return R.drawable.xls;

        return R.drawable.my;
    }

    //    void filterContents(Data_Manager newDataManager){
//        date_and_time=new ArrayList<>();
//        name=new ArrayList<>();
//        files=newDataManager.files;
//        for (int i=0;i<newDataManager.name.size();i++){
//            files[i]=new File("");
//            name.add(newDataManager.getName(i));
//            date_and_time.add(newDataManager.getDate_and_time(i));
//            files[i]=newDataManager.files[i];
//        }
//    }
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
}