/*
 *  Copyright (C) 2015, cnweaks, All Rights Reserved
 *
 *  Author:  cnweaks@ecvit.com
 *  
 *  https://www.cnweak.com
 *  
 * 关于栏目Activity
 */
package com.cnweak.rebash;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.cnweak.rebash.edit.MDReader;
public class AboutActivity extends Activity {
    
    private TextView mTextView;
    private MDReader mMDReader;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    getActionBar().setDisplayHomeAsUpEnabled(true);  
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);               
        mTextView = (TextView)findViewById(R.id.DisplayTextView);
        mMDReader = new MDReader(getAboutAuthor());        
        mTextView.setTextKeepState(mMDReader.getFormattedContent(),BufferType.SPANNABLE);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {        
	    switch (item.getItemId() ) {
	    case android.R.id.home:
	         finish();
	    default:
	         break;
	    }
	    return true;
	}
	
	protected String getVersionDescription() {        
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName + " for Android";
        } 
        catch (NameNotFoundException e) {            
            e.printStackTrace();
        }
        return "Unknow";
    }
	
	protected String getAboutAuthor() {
	    StringBuilder builder = new StringBuilder();
        builder.append("# **关于软件:**\n\n");
        builder.append("- 版本号: " + getVersionDescription() + "\n\n");
        builder.append("# **关于作者:**\n\n");
        builder.append("### 卢俊\n\n");
        builder.append("- 联系方式: cnweaks@cnweak.com \n\n");
        builder.append("- 网站: http://www.cnweak.com \n\n");        
        return builder.toString();
	}
}
