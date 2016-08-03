package com.cnweak.rebash.html;

import android.util.Log;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/2/2.
 */
public class BingTranslate {

    public String BingTranslateID="translatebingcnweak";
    public String BingTranslateKey="Iz04pI1gsyeFgg/KI2oZTcIvCUIhoPTL9vy3T1BKgqg=";
    public ArrayList<String> apiname = new ArrayList<>();
    public  Map<String,HtmlUtils> sourdata = new HashMap<>();

    public ArrayList<String> getApitran() {
        return apitran;
    }

    public ArrayList<String> apitran = new ArrayList<>();
    public ArrayList<String> getApiName() {
        return apiname;
    }
    public void setTranslateData(Map<String,HtmlUtils> htmldata, ArrayList<String> apiname){
        this.apiname = apiname;
        this.sourdata = htmldata;
    }
    public Map<String,HtmlUtils> getTranslateData(){
        return sourdata;
    }
    public BingTranslate(){}

    public void StartTranslate(){
    for (int i = 0; i < apiname.size();i++){
        String sed = sourdata.get(apiname.get(i)).getApi_item_methord();
        try {
            Translate.setClientId(BingTranslateID);
            Translate.setClientSecret(BingTranslateKey);
            apitran.add(i, Translate.execute(sed, Language.ENGLISH, Language.CHINESE_SIMPLIFIED));
            Log.d("StartTranslate", "Yes");
        }
        catch (Exception e) {e.printStackTrace();
            Log.d("StartTranslate", sed);
            Log.d("StartTranslate", "No");}
    }

    }
}
