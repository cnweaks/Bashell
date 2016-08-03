/*
 *  Copyright (C) 2015, cnweaks, All Rights Reserved
 *
 *  Author:  cnweaks@ecvit.com
 *  
 *  https://www.cnweak.com
 *  
 * 首页Activity
 */
package com.cnweak.rebash;
import android.app.*;
import android.content.*;
import android.os.*;
import android.preference.*;
import android.view.*;
import android.view.ContextMenu.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.cnweak.rebash.data.*;
import com.cnweak.rebash.data.BashDataBase.*;
import com.cnweak.rebash.edit.*;
import java.util.*;
public class CnweakActivity extends Activity implements OnItemClickListener {

    public static final String CONFIG_FIRST_START = "isFirstStart";
    private static final int REQUEST_CODE_ADD  = 0;
    private static final int REQUEST_CODE_EDIT = 1;
    private ListView mNoteListView;
    private NoteAdapter mNoteAdapter;
    private int mSelectedPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BashDataBase.getInstance().open(this);
        onCheckFirstStart();
        mNoteListView = (ListView)findViewById(R.id.NoteListView);
        mNoteAdapter = new NoteAdapter(this);
        mNoteListView.setAdapter(mNoteAdapter);
        registerForContextMenu(mNoteListView);
        OnItemLongClickListener longListener = new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view,int position, long id) {
                mSelectedPosition = position;
                mNoteListView.showContextMenu();
                return true;

            }
       };
       mNoteListView.setOnItemLongClickListener(longListener);
       mNoteListView.setOnItemClickListener(this);
    }

    @Override
    protected void onDestroy() {
        BashDataBase.getInstance().close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu,View v,ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.data_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Intent intent = new Intent(this,EditActivity.class);
                startActivityForResult(intent,REQUEST_CODE_ADD);
                break;
            case R.id.action_file:
                startActivity(new Intent(this, FileActivity.class));
                break;
            case R.id.action_cmd:
                startActivity(new Intent(this, ShellActivity.class));
                break;
			case R.id.action_oss:
				//startActivity(new Intent(this,FtpBrowserActivity2.class));
				break;
			case R.id.action_html:
				startActivity(new Intent(this,HtmlViewActivity.class));
				break;
			case R.id.action_Xml:
                startActivity(new Intent(this, HexActivity.class));
                break;
			case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
			case R.id.action_exit:
            	finish();
				break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
           case R.id.DataDelete:
           if(mSelectedPosition != -1) {
            BashDataBase.getInstance().delete(mSelectedPosition);
            mNoteAdapter.notifyDataSetChanged();
            }
            return true;
            case R.id.DataClear:
            BashDataBase.getInstance().clear();
            mNoteAdapter.notifyDataSetChanged();
            return true;
            default:
        return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
        Intent intent = new Intent(this,EditActivity.class);
        intent.putExtra("NoteId", id);
        startActivityForResult(intent, REQUEST_CODE_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_CODE_ADD||requestCode==REQUEST_CODE_EDIT) {
            mNoteAdapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void onCheckFirstStart() {
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(!mSharedPreferences.getBoolean(CONFIG_FIRST_START,true)) {
            return;
        }
        Note note = new Note();
        note.title = "Markdown功能介绍";
        note.content = "# Markdown功能介绍\n\n";
        note.date = Calendar.getInstance().getTimeInMillis();
        BashDataBase.getInstance().insert(note);
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putBoolean(CONFIG_FIRST_START,false);
        edit.apply();
    }

}
