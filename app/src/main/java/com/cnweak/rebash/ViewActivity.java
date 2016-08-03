package com.cnweak.rebash;
/*
 *  Copyright (C) 2015, cnweaks, All Rights Reserved
 *
 *  Author:  cnweaks@ecvit.com
 *  
 *  https://www.cnweak.com
 *  
 * 阅读视图Activity
 */
import android.app.*;
import android.graphics.*;
import android.graphics.Bitmap.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.widget.TextView.*;
import com.cnweak.rebash.edit.*;
import com.cnweak.rebash.hex.FileConverEncoding;

import java.io.*;
import android.content.*;

public class ViewActivity extends Activity {
	
    private static final String DEFAULT_DIR = Environment.getExternalStorageDirectory() + File.separator + "JNote";
    private TextView mTextView;
    private MDReader mMDReader;
    private ScrollView mRootView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getActionBar().setDisplayHomeAsUpEnabled(true);  
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        checkStorageDir();
        String content = getIntent().getExtras().getString("Content");
        mRootView = (ScrollView)findViewById(R.id.DisplayRootView);
        mTextView = (TextView)findViewById(R.id.DisplayTextView);
        mMDReader = new MDReader(content);
		//根据文件管理器调用和内容Item点击的判断
		if (readFileSdcardFile() == null || readFileSdcardFile() == ""){
        mTextView.setTextKeepState(mMDReader.getFormattedContent(),BufferType.SPANNABLE);
		}else{
			mTextView.setText(readFileSdcardFile());	
		}
    }
	
	//获取文件管理器Pick的文件路劲，并读取文件
	private String readFileSdcardFile(){
	String res=""; 
	Bundle bundle = this.getIntent().getExtras();
	String patch = bundle.getString("Patch");
		Toast.makeText(this, "文件编码:"+getFileEncode(patch), Toast.LENGTH_LONG).show();
	if( patch == "" || patch == null){
	}else{
		try{ 
	FileInputStream fin = new FileInputStream(patch); 
	int length = fin.available(); 
	byte [] buffer = new byte[length];
	//res = FileConverEncoding.getString(buffer, getFileEncode(patch));
	fin.close();}
    catch(Exception e){ 
	e.printStackTrace();}}
	return res;
}
//获取文件编码文件编码
public static String getFileEncode(String filepatch) {
		String charSet = "";
        try {
		FileInputStream fis = new FileInputStream(new File(filepatch));
            byte[] bf = new byte[3];
            fis.read(bf);
            fis.close();
            if (bf[0] == -17 && bf[1] == -69 && bf[2] == -65) {
                charSet = "UTF-8";
            } else if ((bf[0] == -1 && bf[1] == -2)) {
                charSet = "Unicode";
            } else if ((bf[0] == -2 && bf[1] == -1)) {
                charSet = "Unicode big endian";
            } else {
                charSet = "ANSI";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return charSet;
    }
	
	
 @Override
public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_display, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
			case android.R.id.home:
				finish();
				break;
			case R.id.action_save_md:  
				saveAsMardown();
				break;
			case R.id.action_save_txt:   
				saveAsRawContent();
				break;
			case R.id.action_save_img:
				saveAsBitmap();
				break;
			default:
				break;
        }
        return super.onOptionsItemSelected(item);
    }
	@Override
	public void startActivities(Intent[] intents)
	{
		// TODO: Implement this method
		super.startActivities(intents);
	}

    public boolean isSDCardMounted() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public void checkStorageDir() {     
        if(isSDCardMounted()) {            
            File directory = new File(DEFAULT_DIR);
            if( !directory.exists() ) {
                directory.mkdir();
            }
        }
    }
    public boolean checkSaveEnv() {
        if(!isSDCardMounted()) {
            Toast.makeText(this, "找不到 SDCard !", Toast.LENGTH_LONG).show();
            return false;
        }
        if("".equals(mMDReader.getContent())) {
            Toast.makeText(this, "没有内容,无法保存 !", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void saveAsMardown() { 
        if(!checkSaveEnv()) {
            return;
        }
        String filepath = DEFAULT_DIR+File.separator+mMDReader.getTitle()+".md";
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filepath), "UTF-8"));
            writer.write(mMDReader.getContent());
            writer.close();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "成功保存到:"+filepath, Toast.LENGTH_LONG).show();
    }

    public void saveAsRawContent() { 
        if(!checkSaveEnv()) {
            return;
        }
        String filepath = DEFAULT_DIR+File.separator+mMDReader.getTitle()+".txt";
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filepath), "UTF-8"));
            writer.write(mMDReader.getRawContent());
            writer.close();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "成功保存到:"+filepath, Toast.LENGTH_LONG).show();
    }

    public void saveAsBitmap() { 
        if(!checkSaveEnv()) {
            return;
        }
        String filepath = DEFAULT_DIR+File.separator+mMDReader.getTitle()+".jpg";
        try {
            FileOutputStream stream = new FileOutputStream(filepath);
            Bitmap bitmap = createBitmap(mRootView);
            if(bitmap!=null) {
                bitmap.compress(CompressFormat.JPEG,  100, stream);
                Toast.makeText(this, "成功保存到:"+filepath, Toast.LENGTH_LONG).show();
            }            
            stream.close();
        } 
        catch (FileNotFoundException e) {        
            e.printStackTrace();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }            
    }

    public static Bitmap createBitmap(ScrollView v) {
        int width = 0, height = 0;
        for (int i = 0; i < v.getChildCount(); i++) {
            width  += v.getChildAt(i).getWidth();
            height += v.getChildAt(i).getHeight();
        }
        Bitmap bitmap = Bitmap.createBitmap(width,height,Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas); 
        return bitmap;        
    }

    public static Bitmap createBitmap(View v) {
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);
        return bitmap;
    }
}
