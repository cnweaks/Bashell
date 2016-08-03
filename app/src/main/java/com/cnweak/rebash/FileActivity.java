package com.cnweak.rebash;

import android.app.ActionBar;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.cnweak.rebash.ftpfile.AliOSSFragment;
import com.cnweak.rebash.ftpfile.ExplorerFragment;
import com.cnweak.rebash.ftpfile.LoginFragment;
import com.cnweak.rebash.ftpfile.MyLog;
import com.cnweak.rebash.ftpfile.TaskListFragment;
import android.view.*;

public class FileActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
        
		super.onCreate(savedInstanceState);loadResource();
		AliOSSFragment aliFrag = new AliOSSFragment();aliFrag.init(this);aliFrag.setHasOptionsMenu(true);
		LoginFragment loginFrag = new LoginFragment();loginFrag.init(this);loginFrag.setHasOptionsMenu(true);
		ExplorerFragment remoteFrag = new ExplorerFragment();remoteFrag.init(this, false);remoteFrag.setHasOptionsMenu(true);
		ExplorerFragment localFrag = new ExplorerFragment();localFrag.init(this, true);localFrag.setHasOptionsMenu(true);
		TaskListFragment taskFrag = new TaskListFragment();taskFrag.init(this);taskFrag.setHasOptionsMenu(true);
		final ActionBar bar = getActionBar();bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	    /* 塌陷导航栏主操作栏 */
		bar.setDisplayShowTitleEnabled(false);bar.setDisplayShowHomeEnabled(false);
		bar.addTab(bar.newTab().setText(R.string.Tab_alioss).setTabListener(aliFrag.new AliOSSTabListener()));
		bar.addTab(bar.newTab().setText(R.string.Tab_Login).setTabListener(loginFrag.new LoginTabListener()));
		bar.addTab(bar.newTab().setText(R.string.Tab_Remote).setTabListener(remoteFrag.new ExplorerTabListener()));
		bar.addTab(bar.newTab().setText(R.string.Tab_Local).setTabListener(localFrag.new ExplorerTabListener()));
		bar.addTab(bar.newTab().setText(R.string.Tab_TaskList).setTabListener(taskFrag.new TaskListTabListener()));
	}



	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		//TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		MyLog.d("FileActivity", "onConfigurationChanged");
	}



	/*THE RESOURCE PART*/
	public Drawable mDrawDownload;
	public Drawable mDrawUpload;
	void loadResource(){
		mDrawDownload = getResources().getDrawable(R.drawable.download);
		mDrawUpload = getResources().getDrawable(R.drawable.upload);
	}
	void unloadResource(){
		mDrawDownload = null;
		mDrawUpload = null;
	}
    

    //菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.file_main, menu);
        return true;
    }







//Item选择事件
    @Override
    public boolean onMenuItemSelected(int i,MenuItem item) {
        switch (item.getItemId()) {
            case R.id.file_up: 
                
                break;
            case R.id.file_down:
                
                break;
            case R.id.file_dsc:
                
                break;
            case android.R.id.home:
                finish();
                break;
            default:break;
        }
        return true;
    }

    
    
    
    
    
    
    
    
    
}

	
