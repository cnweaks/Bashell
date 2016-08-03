/*
 *  Copyright (C) 2015, cnweaks, All Rights Reserved
 *
 *  Author:  cnweaks@ecvit.com
 *  
 *  https://www.cnweak.com
 *  
 * 终端Activity
 */
package com.cnweak.rebash;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cnweak.rebash.shell.ShellAndroid;
import android.widget.*;
import android.view.*;
import android.text.method.*;
import java.util.*;
import android.view.View.*;

public class ShellActivity extends Activity {
    private ShellAndroid mShell;
    private boolean mUseMinimum = true;
    private EditText edtCmd;
    private TextView text;
    private Button button;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shell_main);

        edtCmd = (EditText) findViewById(R.id.shellbash);
        text = (TextView)findViewById(R.id.cmd_outram);
        button = (Button)findViewById(R.id.shell_mainButton);
        button.setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View p1)
                {
                    StartRunShell(); 
                }
                
            
        });
        //---- shell initialization ----
        mShell = new ShellAndroid(null);
        String flagFile;
        if (!mUseMinimum){
            flagFile = mShell.initFlag(getApplicationContext());
        }else {
            flagFile = mShell.initFlagMinimum(getApplicationContext());
            Toast.makeText(this, "Use minimum init flag", Toast.LENGTH_SHORT).show();
        }
        mShell.printOutput();
        mShell.setFlagFile(flagFile);
        //---- finish shell initialization ----
      }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {        
	    switch (item.getItemId() ) {
	    case android.R.id.home:
	         finish();
             break;
                case R.id.shell_surun:
                StartRunShell();
               
                break;
                case R.id.shell_check:
                new RootCheckTask(1).execute();  
                break;
                case R.id.shell_purun:
                new RootCheckTask(2).execute();  
                break;
                case R.id.shell_apidoc:
                break;
                
	    default:
	         break;
	    }
return true;
        }
    //菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.shell_menu, menu);
        return true;
    }

private void StartRunShell(){
    
    String cmd = edtCmd.getText().toString();
    if(cmd !=" " && cmd != null){
        new ExecuteTask().execute(cmd);  
    }
    
    text.setText("");
    text.setMovementMethod(ScrollingMovementMethod.getInstance());
    
    edtCmd.requestFocus();  
    
    
}
    private class ExecuteTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String cmd = params[0];
            if (cmd.startsWith("0x")){
                byte[] ascii = new byte[]{Integer.valueOf(cmd.substring(2), 16).byteValue()};
                mShell.exec(false, new String(ascii));
            }else{
                mShell.exec(false, params);
            }
            return mShell.getLastResult();
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            GetHelp(result);
            /*
            if (!TextUtils.isEmpty(result)){
                text.setText(result);
                
            }else{
                text.setText("Empty result");
            }
            */
        }
    }
private void GetHelp(String busyboxls){
    List<String> arry = null;
    String[] kgg = null;
    busyboxls.format(",",kgg);
    if(kgg != null){
        text.setText(kgg[0]+"888888"+kgg[1]); 
    }else{
        text.setText(busyboxls);  
    }
    
}
    public class RootCheckTask extends AsyncTask<Void, Void, Boolean>{

        private final int mTaskType;

        public RootCheckTask(int type) {
            // TODO Auto-generated constructor stub
            mTaskType = type;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO Auto-generated method stub
            if (mTaskType == 1){
                mShell.checkRoot();
            }else if (mTaskType == 2){
                mShell.exitRoot();
            }
            return mShell.hasRoot();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (mTaskType == 1){
                //   txtCheckRoot.setText(result.toString());
            }else if (mTaskType == 2){
                //   txtExitRoot.setText(result.toString());
            }
        }
    }
    
    
    
    
    
    
    
    /*键按下事件*/
    public boolean onKeyDown(int keyCode,KeyEvent event){
        switch(keyCode){
            case KeyEvent.KEYCODE_DPAD_CENTER:
                
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
               
                break;
            case KeyEvent.KEYCODE_ENTER:
                StartRunShell();
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
    /*释放按键事件*/
    public boolean onKeyUp(int keyCode,KeyEvent event){
        switch(keyCode){
            case KeyEvent.KEYCODE_DPAD_CENTER:
                
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                
                break;
        }
        return super.onKeyUp(keyCode, event);
    }
    /*连击事件*/
    public boolean onKeyMultiple(int keyCode,int repeatCount,KeyEvent event){
        return super.onKeyMultiple(keyCode, repeatCount, event);
    }
    /*触笔事件*/
    public boolean onTouchEvent(MotionEvent event){
        int iAction=event.getAction();
        if(iAction==MotionEvent.ACTION_CANCEL||iAction==MotionEvent.ACTION_DOWN||
           iAction==MotionEvent.ACTION_MOVE){
            return false;
        }
        //
        int x=(int)event.getX();
        int y=(int) event.getY();
           return super.onTouchEvent(event);
    }
    
}
