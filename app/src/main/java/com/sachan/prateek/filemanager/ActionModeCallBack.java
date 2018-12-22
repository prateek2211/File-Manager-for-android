package com.sachan.prateek.filemanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

public class ActionModeCallBack extends AppCompatActivity implements ActionMode.Callback {
    MyRecyclerAdapter adapter;
    private Context context;

    public ActionModeCallBack(MyRecyclerAdapter adapter,Context context) {
        this.adapter = adapter;
        this.context=context;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.contextual_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cut:
                break;
            case R.id.copy:
                break;
            case R.id.delete:
                break;
            case R.id.rename:
                break;
            case R.id.properties:
                Dialog dialog=new Dialog(context);
                dialog.setContentView(R.layout.properties_dialog);
                dialog.show();
                break;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        adapter.clearSelection();
        MainActivity.isSelection = false;
    }
}
