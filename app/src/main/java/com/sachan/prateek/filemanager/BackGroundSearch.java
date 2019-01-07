package com.sachan.prateek.filemanager;

import android.os.AsyncTask;

import com.sachan.prateek.filemanager.grid_utils.GridAdapter;
import com.sachan.prateek.filemanager.list_utils.MyRecyclerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BackGroundSearch extends AsyncTask<String, Integer, List<File>> {
    private Data_Manager data_manager;
    private List<File> searchResults;
    private GridAdapter gridAdapter;
    private MyRecyclerAdapter myRecyclerAdapter;

    BackGroundSearch(Data_Manager data_manager, GridAdapter gridAdapter, MyRecyclerAdapter myRecyclerAdapter) {
        this.data_manager = data_manager;
        this.gridAdapter = gridAdapter;
        this.myRecyclerAdapter = myRecyclerAdapter;
    }

    @Override
    protected List<File> doInBackground(String... strings) {
        File file = new File(strings[0]);
        findFile(strings[1], file);
        return searchResults;
    }

    @Override
    protected void onPreExecute() {
        searchResults = new ArrayList<>();
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(List<File> files) {
        data_manager.setSearchResults(files);
        if (MainActivity.gridView)
            gridAdapter.notifyDataSetChanged();
        else
            myRecyclerAdapter.notifyDataSetChanged();
    }


    private void findFile(String str, File loc) {
        File[] files = loc.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                if (file.getName().toLowerCase().contains(str)) {
                    searchResults.add(file);
                    if (searchResults.size() > 250)
                        break;
                }
                findFile(str, file);
            } else if (file.getName().toLowerCase().contains(str)) {
                searchResults.add(file);
                if (searchResults.size() > 250)
                    break;
            }
        }
    }
}
