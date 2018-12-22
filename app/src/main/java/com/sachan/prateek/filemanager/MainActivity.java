package com.sachan.prateek.filemanager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    Toolbar toolbar;
    Context context;
    DrawerLayout drawerLayout;
    Data_Manager data_manager;
    File path;
    android.widget.SearchView searchView;
    MyRecyclerAdapter myRecyclerAdapter;
    int sortFlag;

    @Override
    public void onBackPressed() {
        try {
            sortFlag = 0;
            File parent = new File(path.getParent());
            path = parent;
            data_manager.setRecycler(parent, sortFlag);
            myRecyclerAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sortFlag = 0;
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.tool);
        context = MainActivity.this;
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        drawerLayout = findViewById(R.id.drawer);
        NavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                return true;
            }
        });
        recyclerView = findViewById(R.id.letsRecycle);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        data_manager = new Data_Manager();
        path = Environment.getExternalStorageDirectory();
        data_manager.setRecycler(path, sortFlag);
        myRecyclerAdapter = new MyRecyclerAdapter(data_manager);
        recyclerView.setAdapter(myRecyclerAdapter);
        registerForContextMenu(recyclerView);
        recyclerView.addOnItemTouchListener(new Listener_for_Recycler(getApplicationContext(), recyclerView, new Listener_for_Recycler.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                path = data_manager.getFiles(position);
                sortFlag = 0;
                if (path.isDirectory()) {
                    data_manager.setRecycler(path, sortFlag);
                    myRecyclerAdapter.notifyDataSetChanged();
                } else if (path.toString().contains(".txt")) {
                    Intent intent = new Intent();
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                    Uri uri = Uri.fromFile(path);
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "text/plain");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                view.showContextMenu();
            }
        }));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.sortByName:
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.show();

                break;
            case R.id.sortBySize:
                sortFlag = 1;
                data_manager.setRecycler(path, sortFlag);
                myRecyclerAdapter.notifyDataSetChanged();
                break;
            case R.id.sortByDate:
                sortFlag = 1;
                data_manager.setRecycler(path, sortFlag);
                myRecyclerAdapter.notifyDataSetChanged();
                break;
            case R.id.refresh:
                myRecyclerAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        searchView = (android.widget.SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                Data_Manager newDataManager = new Data_Manager();
//                newDataManager.name = new ArrayList<>();
//                newDataManager.date_and_time = new ArrayList<>();
//                newDataManager.files = new File[data_manager.name.size()];
//                for (int i = 0; i < data_manager.name.size(); i++) {
//                    String s = data_manager.getName(i);
//                    if (s.toLowerCase().contains(newText.toLowerCase())) {
//                        newDataManager.name.add(s);
//                        newDataManager.date_and_time.add(data_manager.getDate_and_time(i));
//                        newDataManager.files[i] = data_manager.files[i];
//                    }
//                }
//                data_manager.filterContents(newDataManager);
//                myRecyclerAdapter.notifyDataSetChanged();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

}