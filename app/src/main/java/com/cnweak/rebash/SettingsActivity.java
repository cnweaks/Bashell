package com.cnweak.rebash;

/*
 *  Copyright (C) 2015, cnweaks, All Rights Reserved
 *
 *  Author:  cnweaks@ecvit.com
 *  
 *  https://www.cnweak.com
 *  
 * 设置Activity
 本Activity通过PreferenceActivity加载
 */
import android.content.*;
import android.os.*;
import android.preference.*;
import android.view.*;
import com.cnweak.rebash.edit.*;
import com.cnweak.rebash.edit.*;

public class SettingsActivity extends PreferenceActivity
{
	public static final int SET = Menu.FIRST;
	public static final int EXIT = Menu.FIRST + 1;
	private MDWriter mMDWriter;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.app_settings);
		
	}
	
	//菜单选项响应事件 
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId()) {
            case R.id.action_display:
                Intent intent = new Intent(this,ViewActivity.class);
				intent.putExtra("Content",mMDWriter.getContent());
				startActivity(intent);
				return true;
			case android.R.id.home:
				finish();
        }
        return super.onOptionsItemSelected(item);
    }
	
	
	
	
	}
	
