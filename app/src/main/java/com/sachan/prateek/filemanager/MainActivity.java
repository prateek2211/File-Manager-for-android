package com.sachan.prateek.filemanager;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.sachan.prateek.filemanager.grid_utils.GridAdapter;
import com.sachan.prateek.filemanager.list_utils.MyRecyclerAdapter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {
    static final String CHANNEL_ID = "default";
    static final String CHANNEL_NAME = "CopyPaste";
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
    static int whichCollection;
    static boolean collections;
    static long imagesSize;
    static long sdimagesSize;
    static long audioSize;
    static long sdaudioSize;
    static long videoSize;
    static long sdvideoSize;
    static long docsSize;
    static long sddocsSize;
    static boolean isExternalSD_available;
    static boolean favourites;
    NavigationView navigationView;
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
    private Menu menu;
    private Uri uri;

    public static File getCurrentPath() {
        return path;
    }

    @Override
    public void onBackPressed() {
        if (collections) {
            collections = false;
            finish();
        }
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
                if (collections || favourites)
                    data_manager.sortCollectionsByName();
                else
                    sortFlag = 1;
                refresh();
                break;
            case R.id.sortBySize:
                if (collections || favourites)
                    data_manager.sortCollectionsBySize();
                else
                    sortFlag = 3;
                refresh();
                break;
            case R.id.sortByDate:
                if (collections || favourites)
                    data_manager.sortCollectionsByDate();
                else
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
            case R.id.stats:
                startActivity(new Intent(MainActivity.this, Main2Activity.class));
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
        if (!collections && !favourites)
            data_manager.setRecycler(path, sortFlag);
        if (gridView)
            adapter.notifyDataSetChanged();
        else
            myRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNotificationChannel();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 420);
        }
        setExternalSD_root();
        if (isExternalSD_available) {
            SharedPreferences prefs = getSharedPreferences(prefsName, 0);
            String uriString = prefs.getString("treeuri", "0");
            if (uriString.equals("0")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Attention!!!").setMessage("Sd Card Detected. Please Select sd card root from storage access framework to give permissions to function properly ");
                builder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                        startActivityForResult(intent, 42);
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } else {
                uri = Uri.parse(uriString);
                documentFile = DocumentFile.fromTreeUri(this, uri);
                permadDocumentFile = documentFile;
            }
        }
        isSelection = false;
        isPasteMode = false;
        setContentView(R.layout.activity_main);
        new BackgroundSizeCalculation().start();
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
                editText.setHint("Please Enter Folder Name");
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
        navigationView = findViewById(R.id.navigation);
        navigationView.getMenu().findItem(R.id.internal).setChecked(true);
        if (!isExternalSD_available)
            navigationView.getMenu().findItem(R.id.sd).setVisible(false);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                if (isExternalSD_available) {
                    if (menuItem.getItemId() == R.id.sd) {
                        favourites = false;
                        collections = false;
                        switchToSD();
                        sdCardmode = true;
                    } else if (menuItem.getItemId() == R.id.internal) {
                        collections = false;
                        favourites = false;
                        switchToInternal();
                        sdCardmode = false;
                    }
                }
                if (menuItem.getItemId() == R.id.internal) {
                    favourites = false;
                    collections = false;
                    switchToInternal();
                    sdCardmode = false;
                }
                if (menuItem.getItemId() == R.id.pictures) {
                    favourites = false;
                    collections = false;
                    path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    refresh();
                }
                if (menuItem.getItemId() == R.id.music) {
                    favourites = false;
                    collections = false;
                    path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
                    refresh();
                }
                if (menuItem.getItemId() == R.id.downloads) {
                    favourites = false;
                    collections = false;
                    path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    refresh();
                }
                if (menuItem.getItemId() == R.id.movies) {
                    favourites = false;
                    collections = false;
                    path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
                    refresh();
                }
                if (menuItem.getItemId() == R.id.document) {
                    favourites = false;
                    collections = false;
                    path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
                    refresh();
                }
                if (menuItem.getItemId() == R.id.images) {
                    favourites = false;
                    collections = true;
                    sdCardmode = false;
                    whichCollection = 1;
                    data_manager.setImagesData(context);
                    if (gridView)
                        adapter.notifyDataSetChanged();
                    else
                        myRecyclerAdapter.notifyDataSetChanged();
                }
                if (menuItem.getItemId() == R.id.audio) {
                    favourites = false;
                    sdCardmode = false;
                    collections = true;
                    whichCollection = 2;
                    data_manager.setAudio(context);
                    if (gridView)
                        adapter.notifyDataSetChanged();
                    else
                        myRecyclerAdapter.notifyDataSetChanged();
                }
                if (menuItem.getItemId() == R.id.docs) {
                    favourites = false;
                    sdCardmode = false;
                    collections = true;
                    whichCollection = 3;
                    data_manager.setDocs(context);
                    if (gridView)
                        adapter.notifyDataSetChanged();
                    else
                        myRecyclerAdapter.notifyDataSetChanged();
                }
                if (menuItem.getItemId() == R.id.favourites) {
                    data_manager.setFavourites(context);
                    favourites = true;
                    collections = false;
                    if (gridView)
                        adapter.notifyDataSetChanged();
                    else
                        myRecyclerAdapter.notifyDataSetChanged();
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
        myRecyclerAdapter = new MyRecyclerAdapter(data_manager, context);
        adapter = new GridAdapter(data_manager, context);
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

                if (!isSelection) {
                    path = data_manager.getFiles(position);
                    if (favourites) {
                        favourites = false;
                        navigationView.setCheckedItem(R.id.internal);
                    }
                    if (isPasteMode)
                        actionMode.setTitle(path.getName());
                    if (path.isDirectory()) {
                        if (collections)
                            collections = false;
                        if (sdCardmode)
                            documentFile = documentFile.findFile(data_manager.getName(position));
                        data_manager.setRecycler(path, sortFlag);
                        recyclerView.scrollToPosition(0);
                        if (gridView)
                            adapter.notifyDataSetChanged();
                        else
                            myRecyclerAdapter.notifyDataSetChanged();
                    } else {
                        String fileType = "";
                        URL url = null;
                        try {
                            url = new URL("file://" + path.getPath());
                            URLConnection connection = url.openConnection();
                            fileType = connection.getContentType();
                            Intent intent = new Intent();
                            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                            StrictMode.setVmPolicy(builder.build());
                            Uri uri = Uri.fromFile(path);
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setDataAndType(uri, fileType);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            try {
                                context.startActivity(intent);
                            } catch (ActivityNotFoundException e) {
                                createOpenAs(path);
                            }
                        } catch (IOException e) {
                            Toast.makeText(context, "Couldn`t open the specified file", Toast.LENGTH_SHORT).show();
                        }
                        if (!collections)
                            path = path.getParentFile();
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
        refresh();
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

    void createOpenAs(File file) {
        AlertDialog.Builder bobTheBuilder = new AlertDialog.Builder(context);
        bobTheBuilder.setView(R.layout.open_as).setTitle("Open As");
        final AlertDialog alertDialog1 = bobTheBuilder.create();
        alertDialog1.show();
        ListView listView = alertDialog1.findViewById(R.id.list);
        listView.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, new String[]{
                "Text", "Audio", "Video", "Image"}));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.fromFile(path);
                switch (position) {
                    case 0:
                        intent.setDataAndType(uri, "text/plain");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        break;
                    case 1:
                        intent.setDataAndType(uri, "audio/wav");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        break;
                    case 2:
                        intent.setDataAndType(uri, "video/*");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        break;
                    case 3:
                        intent.setDataAndType(uri, "image/jpeg");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        break;
                }
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, "No App found to open requested file type", Toast.LENGTH_SHORT).show();
                }
                alertDialog1.cancel();
            }
        });
    }

    void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel;
            notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("This channel notifies about current copy and paste task");
            NotificationManager manager;
            manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(notificationChannel);
            }
        }
    }

    class BackgroundSizeCalculation extends Thread {
        @Override
        public void run() {
            Cursor cursor;
            String[] proj = new String[]{MediaStore.Images.Media.SIZE
                    , MediaStore.Images.Media.DATA};
            cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj, null, null, null);
            cursor.moveToFirst();
            do {
                if (cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)).contains(Environment.getExternalStorageDirectory().getPath()))
                    imagesSize += Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)));
                else
                    sdimagesSize += Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)));
            } while (cursor.moveToNext());
            String[] proj_audio = new String[]{MediaStore.Audio.Media.SIZE
                    , MediaStore.Audio.Media.DATA};
            cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj_audio, null, null, null);
            cursor.moveToFirst();
            if (cursor.getCount() != 0) {
                do {
                    if (cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)).contains(Environment.getExternalStorageDirectory().getPath()))
                        audioSize += Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)));
                    else
                        sdaudioSize += Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)));
                } while (cursor.moveToNext());
            }
            String[] proj_video = new String[]{MediaStore.Video.Media.SIZE
                    , MediaStore.Video.Media.DATA};
            cursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, proj_video, null, null, null);
            cursor.moveToFirst();
            if (cursor.getCount() != 0) {
                do {
                    if (cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)).contains(Environment.getExternalStorageDirectory().getPath()))
                        videoSize += Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)));
                    else
                        sdvideoSize += Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)));
                } while (cursor.moveToNext());
            }


            String pdf = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf");
            String doc = MimeTypeMap.getSingleton().getMimeTypeFromExtension("doc");
            String docx = MimeTypeMap.getSingleton().getMimeTypeFromExtension("docx");
            String xls = MimeTypeMap.getSingleton().getMimeTypeFromExtension("xls");
            String xlsx = MimeTypeMap.getSingleton().getMimeTypeFromExtension("xlsx");
            String ppt = MimeTypeMap.getSingleton().getMimeTypeFromExtension("ppt");
            String pptx = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pptx");
            String txt = MimeTypeMap.getSingleton().getMimeTypeFromExtension("txt");
            String rtx = MimeTypeMap.getSingleton().getMimeTypeFromExtension("rtx");
            String rtf = MimeTypeMap.getSingleton().getMimeTypeFromExtension("rtf");
            String html = MimeTypeMap.getSingleton().getMimeTypeFromExtension("html");

            Uri table = MediaStore.Files.getContentUri("external");
            String[] column = {MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.SIZE};
            String where = MediaStore.Files.FileColumns.MIME_TYPE + "=?"
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
            String[] args = new String[]{pdf, doc, docx, xls, xlsx, ppt, pptx, txt, rtx, rtf, html};

            cursor = getContentResolver().query(table, column, where, args, null);
            cursor.moveToFirst();
            do {
                if (cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)).contains(Environment.getExternalStorageDirectory().getPath()))
                    docsSize += Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)));
                else
                    sddocsSize += Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)));
            } while (cursor.moveToNext());
        }
    }
}