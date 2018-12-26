package com.sachan.prateek.filemanager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.provider.DocumentFile;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    static boolean isSelection;
    static boolean isPasteMode;
    static File path;
    static boolean sdCardmode;
    RecyclerView recyclerView;
    Toolbar toolbar;
    Context context;
    DrawerLayout drawerLayout;
    Data_Manager data_manager;
    android.widget.SearchView searchView;
    MyRecyclerAdapter myRecyclerAdapter;
    int sortFlag;
    ActionMode actionMode;
    static File externalSD_root;
    static DocumentFile documentFile;
    static DocumentFile permadDocumentFile;
    boolean isExternalSD_available;

    public static File getCurrentPath() {
        return path;
    }

    @Override
    public void onBackPressed() {
        if (sdCardmode && path.getPath().equals(externalSD_root.getPath())) {
            sdCardmode = false;
            finish();
        } else {
            try {
                sortFlag = 0;
                File parent = new File(path.getParent());
                path = parent;
                data_manager.setRecycler(parent, sortFlag);
                if (sdCardmode)
                    documentFile = documentFile.getParentFile();
                if (isPasteMode)
                    actionMode.setTitle(path.getName());
                myRecyclerAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                super.onBackPressed();
            }
        }
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
                refresh();
                recyclerView.scrollToPosition(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contextual_menu, menu);
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

    void refresh() {
        data_manager.setRecycler(path, sortFlag);
        myRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, 42);

        super.onCreate(savedInstanceState);
        setExternalSD_root();
        sortFlag = 0;
        isSelection = false;
        isPasteMode = false;
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.tool);
        context = MainActivity.this;
        setSupportActionBar(toolbar);
        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Enter your own code here
            }
        });
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        drawerLayout = findViewById(R.id.drawer);
        NavigationView navigationView = findViewById(R.id.navigation);
        navigationView.getMenu().findItem(R.id.internal).setChecked(true);
        if (!isExternalSD_available)
            navigationView.getMenu().findItem(R.id.sd).setVisible(false);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                if (isExternalSD_available) {
                    if (menuItem.getItemId() == R.id.sd && !sdCardmode) {
                        switchToSD();
                        sdCardmode = true;
                    } else if (menuItem.getItemId() == R.id.internal && sdCardmode) {
                        switchToInternal();
                        sdCardmode = false;
                    }

                }

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
                if (!isSelection) {
                    if (isPasteMode)
                        actionMode.setTitle(path.getName());
                    sortFlag = 0;
                    if (path.isDirectory()) {
                        if (sdCardmode)
                            documentFile = documentFile.findFile(data_manager.getName(position));
                        data_manager.setRecycler(path, sortFlag);
                        recyclerView.scrollToPosition(0);
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
                } else {
                    myRecyclerAdapter.toggleSelection(position);
                    if (myRecyclerAdapter.getSelectedItemCount() > 1) {
                        actionMode.getMenu().findItem(R.id.rename).setEnabled(false);
                        actionMode.getMenu().findItem(R.id.properties).setEnabled(false);
                    }
                    if (myRecyclerAdapter.getSelectedItemCount() == 1) {
                        actionMode.getMenu().findItem(R.id.rename).setEnabled(true);
                        actionMode.getMenu().findItem(R.id.properties).setEnabled(true);
                    }

                    actionMode.setTitle(myRecyclerAdapter.getSelectedItemCount() + " Selected");
                    if (myRecyclerAdapter.getSelectedItemCount() == 0) {
                        myRecyclerAdapter.clearSelection();
                        isSelection = false;
                        actionMode.finish();
                    }
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                if (!isSelection) {
                    myRecyclerAdapter.toggleSelection(position);
                    actionMode = startActionMode(new ActionModeCallBack(myRecyclerAdapter, MainActivity.this, data_manager, sortFlag));
                    actionMode.setTitle("1 Seleced");
                    isSelection = true;
                }
            }
        }));
    }

    void setExternalSD_root() {
        File file = new File("/storage");
        File[] temp = file.listFiles();
        File toBe = new File("");
        for (File aTemp : temp) {
            if (aTemp.isDirectory() && aTemp.canRead() && aTemp.listFiles().length > 0) {
                isExternalSD_available = true;
                toBe = aTemp;
            }
            if (isExternalSD_available)
                externalSD_root = toBe;
        }
    }

    void switchToSD() {
        path = externalSD_root;
        data_manager.setRecycler(path, sortFlag);
        myRecyclerAdapter.notifyDataSetChanged();
    }

    void switchToInternal() {
        path = Environment.getExternalStorageDirectory();
        data_manager.setRecycler(path, sortFlag);
        myRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            documentFile = DocumentFile.fromTreeUri(this, uri);
            permadDocumentFile = documentFile;
            grantUriPermission(getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            try {
                DocumentFile file = documentFile.findFile(externalSD_root.getPath() + "/" + "Android");
                Toast.makeText(context, file.getName(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(context, "OOOOOH", Toast.LENGTH_LONG).show();
            }
        }

    }
}