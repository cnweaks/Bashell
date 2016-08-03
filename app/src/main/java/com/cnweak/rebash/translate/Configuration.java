package com.cnweak.rebash.translate;
import android.content.Context;
import com.cnweak.rebash.R;
/**
 * 读取翻译配置
 *
 * */
public class Configuration {
     private static Context contacts;
    public Configuration(Context contacts) {
        this.contacts = contacts;
    }

    public static String getTranslateID() {
       return contacts.getResources().getString(R.string.api_id);
    }

    public static String getTranslateKey() {
        return contacts.getResources().getString(R.string.api_key);
    }

    public static String getTranslateFilePatch() {
        return contacts.getResources().getString(R.string.api_file_path);
    }

    public static String getTranslatedFileName() {
        return contacts.getResources().getString(R.string.api_file_name);
    }

    public static String getTranslatedToFileEndLine() {
        return contacts.getResources().getString(R.string.api_end_line);
    }

    public static String getTranslateWorkDir() {
        return contacts.getResources().getString(R.string.api_work_dir);
    }
    public static String getTranslateValuePath() {
        return contacts.getResources().getString(R.string.ai_value_path);
    }
}