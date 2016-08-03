package com.cnweak.rebash.fileftp;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class FileUtils {

    private static ConnectivityManager connectManager;
    private static List<Activity> activitys=new ArrayList<Activity>();
    static List<String> upFilePath=new ArrayList<String>();

    public static boolean netWorkAvaliable(Context context){
        boolean avaliable=false;
        if(connectManager==null){
            connectManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        NetworkInfo networkinfo = connectManager.getActiveNetworkInfo();
        if (networkinfo!=null&&networkinfo.isAvailable()) {
            avaliable=true;
        }
        return avaliable;
    }

    public static String formatFromSize(long size) {
        String suffix = null;
        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
            }
        }

        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null)
            resultBuffer.append(suffix);
        return resultBuffer.toString();
    }

    public static void addActivity(Activity act){
        if(act!=null)
            FileUtils.activitys.add(act);
    }

    public static void ExitApp(){
        int n=0,count=FileUtils.activitys.size();
        if(count>0)
            while(n<count&&FileUtils.activitys.get(n)!=null){
                FileUtils.activitys.get(n).finish();
                System.exit(0);
            }
    }
    public static String getExceptionMessage(Exception ex){
        String result="";
        StackTraceElement[] stes = ex.getStackTrace();
        for(int i=0;i<stes.length;i++){
            result=result+stes[i].getClassName()
                    + "." + stes[i].getMethodName()
                    + "  " + stes[i].getLineNumber() +"line"
                    +"\r\n";
        }
        return result;
    }

}