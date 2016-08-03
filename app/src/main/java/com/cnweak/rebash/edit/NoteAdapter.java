/*
 *  Copyright (C) 2015, Jhuster, All Rights Reserved
 *
 *  Author:  Jhuster(lujun.hust@gmail.com)
 *  
 *  https://github.com/Jhuster/JNote
 *  
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 */
package com.cnweak.rebash.edit;
import android.content.*;
import android.view.*;
import android.widget.*;
import com.cnweak.rebash.data.*;
import com.cnweak.rebash.data.BashDataBase.*;
import com.cnweak.rebash.*;
import java.text.*;
import java.util.*;
public class NoteAdapter extends BaseAdapter {
    
	private Context mContext;
	
	protected class ViewHolder {
	    TextView mNoteDate;
	    TextView mNoteTitle;
	}
	
	public NoteAdapter(Context context) {				
		mContext = context;
	}
	
	@Override
	public int getCount() {
		return BashDataBase.getInstance().size();
	}

	@Override
	public Object getItem(int position) {		
		return BashDataBase.getInstance().get(position);
	}

	@Override
	public long getItemId(int position) {		
		return ((Note)getItem(position)).key;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {				
		
		if(convertView == null) {		
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);          
            convertView = (LinearLayout)inflater.inflate(R.layout.layout_note_item, null);
            ViewHolder holder = new ViewHolder();
            holder.mNoteDate  = (TextView)convertView.findViewById(R.id.NoteDateText);
            holder.mNoteTitle  = (TextView)convertView.findViewById(R.id.NoteTitleText);                       
            convertView.setTag(holder);
		}				

		Note note = (Note)getItem(position);
		if(note != null) {		   
		    ViewHolder holder = (ViewHolder)convertView.getTag();
		    holder.mNoteDate.setText(getDateStr(note.date));
		    holder.mNoteTitle.setText(note.title);		    					   
		}		
		
		return convertView;
	}
	
    public static String getDateStr(long milliseconds) {
        return new SimpleDateFormat("yyyy年MM月dd日 EEEE HH点mm分",Locale.CHINA).format(milliseconds);
    }
}
