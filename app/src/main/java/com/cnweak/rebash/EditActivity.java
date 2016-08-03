package com.cnweak.rebash;

/*
 *  Copyright (C) 2015, cnweaks, All Rights Reserved
 *
 *  Author:  cnweaks@ecvit.com
 *
 *  https://www.cnweak.com
 *
 * 编辑页Activity
 */

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.Toast;

import com.cnweak.rebash.data.BashDataBase;
import com.cnweak.rebash.edit.MDWriter;

import java.util.Calendar;
import android.widget.*;

public class EditActivity extends Activity {
    private BashDataBase.Note mNote = new BashDataBase.Note();
    private MDWriter mMDWriter;
    private LinearLayout inputlayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
       inputlayout = (LinearLayout)findViewById(R.id.input_layoutlin);
        inputlayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if(bottom > oldBottom){
                    System.out.println("状态已改变");
                   
                    //inputlayout.addView(tx);
                    addView(v.getContext());
                }
                if(bottom < oldBottom){
                    System.out.println("状态已改变");
                    TableLayout table = (TableLayout)findViewById(R.id.inpput_table);
                    inputlayout.removeView(table);
                }

            }
        });
        EditText edittext = (EditText)findViewById(R.id.NoteEditText);
        mMDWriter = new MDWriter(edittext);
        mNote.key = getIntent().getLongExtra("NoteId",-1);
        if(mNote.key!=-1) {
            BashDataBase.Note note = BashDataBase.getInstance().get(mNote.key);
            if(note!=null) {
                mMDWriter.setContent(note.content);
                mNote = note;
            }
            else {
                mNote.key=-1;
            }
        }

    }
    public void onItemMenu(int p3){
        String[] languages = getResources().getStringArray(R.array.tools);
        Toast.makeText(EditActivity.this, "你点击的是:"+languages[p3], Toast.LENGTH_SHORT).show();



    }
    // TODO 动态添加布局(xml方式)
    private View addView(Context context) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.keyboard, null);
        view.setLayoutParams(p);

        return view;

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        onSaveNote();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId() ) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_display:
                onSaveNote();
                Intent intent = new Intent(this,ViewActivity.class);
                intent.putExtra("Content",mMDWriter.getContent());
                startActivity(intent);
                return true;
            default:
                break;
        }
        return true;






    }

    public void onClickHeader(View v) {
        mMDWriter.setAsHeader();
    }

    public void onClickCenter(View v) {
        mMDWriter.setAsCenter();
    }

    public void onClickList(View v) {
        mMDWriter.setAsList();
    }

    public void onClickBold(View v) {
        mMDWriter.setAsBold();
    }

    public void onClickQuote(View v) {
        mMDWriter.setAsQuote();
    }

    public void onSaveNote() {
        mNote.title = mMDWriter.getTitle();
        mNote.content = mMDWriter.getContent();
        if(mNote.key==-1) {
            if(!"".equals(mNote.content)) {
                mNote.date = Calendar.getInstance().getTimeInMillis();
                BashDataBase.getInstance().insert(mNote);
            }
        }
        else {
            BashDataBase.getInstance().update(mNote);
        }
    }
}
