package com.sachan.prateek.filemanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sachan.prateek.filemanager.grid_utils.GridAdapter;
import com.sachan.prateek.filemanager.list_utils.MyRecyclerAdapter;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final String prefsName = "mysharedpref";
    static boolean isSelection;
    static boolean isPasteMode;
    static File path;
    static boolean sdCardmode;
    static File externalSD_root;
    static DocumentFile documentFile;
    static DocumentFile permadDocumentFile;
    static boolean gridView;
    static boolean searchMode;
    static int sortFlag;
    EditText editText;
    RecyclerView recyclerView;
    Toolbar toolbar;
    Context context;
    DrawerLayout drawerLayout;
    Data_Manager data_manager;
    android.widget.SearchView searchView;
    MyRecyclerAdapter myRecyclerAdapter;
    GridAdapter adapter;
    ActionMode actionMode;
    boolean isExternalSD_available;
    private Menu menu;
    private Uri uri;


    public static File getCurrentPath() {
        return path;
    }

    @Override
    public void onBackPressed() {
        if (searchMode) {
            searchMode = false;
            searchView.onActionViewCollapsed();
            menu.findItem(R.id.refresh).setVisible(true);
            menu.findItem(R.id.sortby).setVisible(true);
            menu.findItem(R.id.stats).setVisible(true);
            refresh();
        } else {
            try {
                if (sdCardmode && path.getPath().equals(externalSD_root.getPath())) {
                    sdCardmode = false;
                    finish();
                } else if (path.getPath().equals(Environment.getExternalStorageDirectory().getPath())) {
                    finish();
                } else {
                    File parent = new File(path.getParent());
                    path = parent;
                    data_manager.setRecycler(parent, sortFlag);
                    if (sdCardmode)
                        documentFile = documentFile.getParentFile();
                    if (isPasteMode)
                        actionMode.setTitle(path.getName());
                    if (gridView)
                        adapter.notifyDataSetChanged();
                    else
                        myRecyclerAdapter.notifyDataSetChanged();

                }
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
                sortFlag = 1;
                refresh();
                break;
            case R.id.sortBySize:
                sortFlag = 3;
                refresh();
                break;
            case R.id.sortByDate:
                sortFlag = 2;
                refresh();
                break;
            case R.id.refresh:
                refresh();
                recyclerView.scrollToPosition(0);
                break;
            case R.id.view:
                if (!gridView) {
                    recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
                    recyclerView.setAdapter(adapter);
                    gridView = true;
                    item.setIcon(R.drawable.list);
                } else {
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                    recyclerView.setAdapter(myRecyclerAdapter);
                    gridView = false;
                    item.setIcon(R.drawable.grid);
                }
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
    public boolean onCreateOptionsMenu(final Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        if (gridView)
            menu.findItem(R.id.view).setIcon(R.drawable.list);
        MenuItem menuItem = menu.findItem(R.id.search);
        searchView = (android.widget.SearchView) menuItem.getActionView();
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.findItem(R.id.refresh).setVisible(false);
                menu.findItem(R.id.sortby).setVisible(false);
                menu.findItem(R.id.stats).setVisible(false);
                searchMode = true;
                data_manager.setRecycler(new File(""), sortFlag);
                if (gridView)
                    adapter.notifyDataSetChanged();
                else
                    myRecyclerAdapter.notifyDataSetChanged();
            }
        });
        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.equals("")) {
                    BackGroundSearch search = new BackGroundSearch(data_manager, adapter, myRecyclerAdapter);
                    search.execute(path.getPath(), newText.toLowerCase());
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    void refresh() {
        data_manager.setRecycler(path, sortFlag);
        if (gridView)
            adapter.notifyDataSetChanged();
        else
            myRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Display display = getWindowManager().getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        int width = size.x


        super.onCreate(savedInstanceState);
        setExternalSD_root();
        if (isExternalSD_available) {
            SharedPreferences prefs = getSharedPreferences(prefsName, 0);
            String uriString = prefs.getString("treeuri", "0");
            if (uriString.equals("0")) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                startActivityForResult(intent, 42);
            } else {
                uri = Uri.parse(uriString);
                documentFile = DocumentFile.fromTreeUri(this, uri);
                permadDocumentFile = documentFile;
            }
        }
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
                AlertDialog.Builder bobTheBuilder = new AlertDialog.Builder(context);
                bobTheBuilder.setView(R.layout.rename_dialog).setTitle("Create New Folder");
                final AlertDialog alertDialog1 = bobTheBuilder.create();
                alertDialog1.show();
                editText = alertDialog1.findViewById(R.id.renameText);
                Button ok = alertDialog1.findViewById(R.id.ok);
                Button cancel = alertDialog1.findViewById(R.id.cancel);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!sdCardmode) {
                            String s = editText.getText().toString();
                            boolean done = new File(path, s).mkdirs();
                            if (!done)
                                Toast.makeText(context, "Sorry, could not create the Folder", Toast.LENGTH_SHORT).show();
                        } else {
                            String s = editText.getText().toString();
                            documentFile.createDirectory(s);
                        }
                        alertDialog1.cancel();
                        refresh();
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog1.cancel();
                    }
                });
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
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        data_manager = new Data_Manager();

        path = Environment.getExternalStorageDirectory();
        data_manager.setRecycler(path, sortFlag);
        myRecyclerAdapter = new MyRecyclerAdapter(data_manager);
        adapter = new GridAdapter(data_manager);
        if (gridView) {
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        } else {
            recyclerView.setAdapter(myRecyclerAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
        registerForContextMenu(recyclerView);
        recyclerView.addOnItemTouchListener(new Listener_for_Recycler(getApplicationContext(), recyclerView, new Listener_for_Recycler.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                path = data_manager.getFiles(position);
                if (!isSelection) {
                    if (isPasteMode)
                        actionMode.setTitle(path.getName());
                    if (path.isDirectory()) {
                        if (sdCardmode)
                            documentFile = documentFile.findFile(data_manager.getName(position));
                        data_manager.setRecycler(path, sortFlag);
                        recyclerView.scrollToPosition(0);
                        if (gridView)
                            adapter.notifyDataSetChanged();
                        else
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
                    if (gridView) {
                        adapter.toggleSelection(position);
                        if (adapter.getSelectedItemCount() > 1) {
                            actionMode.getMenu().findItem(R.id.rename).setEnabled(false);
                            actionMode.getMenu().findItem(R.id.properties).setEnabled(false);
                        }
                        if (adapter.getSelectedItemCount() == 1) {
                            actionMode.getMenu().findItem(R.id.rename).setEnabled(true);
                            actionMode.getMenu().findItem(R.id.properties).setEnabled(true);
                        }

                        actionMode.setTitle(adapter.getSelectedItemCount() + " Selected");
                        if (adapter.getSelectedItemCount() == 0) {
                            adapter.clearSelection();
                            isSelection = false;
                            actionMode.finish();
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
            }

            @Override
            public void onLongClick(View view, int position) {
                if (!isSelection) {
                    if (gridView) {
                        adapter.toggleSelection(position);
                        actionMode = startActionMode(new ActionModeCallBack(adapter, MainActivity.this, data_manager, sortFlag));
                        actionMode.setTitle("1 Selected");
                        isSelection = true;
                    } else {
                        myRecyclerAdapter.toggleSelection(position);
                        actionMode = startActionMode(new ActionModeCallBack(myRecyclerAdapter, MainActivity.this, data_manager, sortFlag));
                        actionMode.setTitle("1 Selected");
                        isSelection = true;
                    }
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
        if (gridView)
            adapter.notifyDataSetChanged();
        else
            myRecyclerAdapter.notifyDataSetChanged();

    }

    void switchToInternal() {
        path = Environment.getExternalStorageDirectory();
        data_manager.setRecycler(path, sortFlag);
        if (gridView)
            adapter.notifyDataSetChanged();
        else
            myRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == 42) {
            uri = data.getData();
            documentFile = DocumentFile.fromTreeUri(this, uri);
            permadDocumentFile = documentFile;
            grantUriPermission(getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            SharedPreferences.Editor editor = getSharedPreferences(prefsName, 0).edit();
            editor.putString("treeuri", uri.toString());
            editor.apply();
            try {
                DocumentFile file = documentFile.findFile(externalSD_root.getPath() + "/" + "Android");
            } catch (Exception e) {
                Toast.makeText(context, "Something went unexpected", Toast.LENGTH_LONG).show();
            }
        }

    }
}