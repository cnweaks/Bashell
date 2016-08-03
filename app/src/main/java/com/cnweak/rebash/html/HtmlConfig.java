package com.cnweak.rebash.html;
import android.content.Context;
import android.content.res.Resources;

import com.cnweak.rebash.R;

import java.util.HashMap;
import java.util.Map;

public class HtmlConfig {
    private Map<Integer, Object> HtmlData = new HashMap();

    public HtmlConfig() {
    }

    public Map<Integer, Object> getDataAll() {
        return HtmlData;
    }

    public void setSmailData(Integer id, Map<Integer, HtmlViewSmail> SmailData) {
        HtmlData.put(id, SmailData);
    }

    public HtmlViewSmail NewSmailData(String ItemName, String TextUsed, String TextExample) {
        return new HtmlViewSmail(ItemName, TextUsed, TextExample);
    }

        /*Smail用法*/
    class HtmlViewSmail {
        private String ItemName;
        private String TextUsed;
        private String TextExample;


        public HtmlViewSmail(String ItemName, String TextUsed, String TextExample) {
            this.ItemName = ItemName;
            this.TextUsed = TextUsed;
            this.TextExample = TextExample;
        }

        public HtmlViewSmail() {
        }

        public String getItemName(Integer ID) {
            return ItemName;
        }

        public void setItemName(String itemName) {
            ItemName = itemName;
        }

        public String getTextUsed(Integer ID) {
            return TextUsed;
        }

        public void setTextUsed(String textUsed) {
            TextUsed = textUsed;
        }

        public String getTextExample(Integer ID) {
            return TextExample;
        }

        public void setTextExample(String textExample) {
            TextExample = textExample;
        }
    }













    public HtmlViewJava NewJavaData(String fileName, Integer fileType) {
        return new HtmlViewJava(fileName, fileType);
    }
          /*处理codejava*/
    class HtmlViewJava {
        private String fileNames;
        private Integer fileTypes;
        public int FE_JAVA= 0;//java文件
        public int FE_JAR = 1;//Jar包
        public int FE_XML = 2;//xml文件
        public int FE_TXT = 3;//TXT文件
        public int FE_FOR = 4;//文件夹
        public int FE_CLS = 5;//Class文件
        public int FE_PNG = 6;//png文件
        public int FE_OTR = 7;//png文件

              public HtmlViewJava() { }
              public HtmlViewJava(String fileName, Integer fileType) {
                this.fileNames = fileName; this.fileTypes = fileType;}

        public Integer getFileType() {
                  return fileTypes;
              }

              public String getFileNames() {
                  return fileNames;
              }

    }




    public void PutResult(Integer i,Map<Integer,HtmlConfig.HtmlViewSmail> m){


    }










            /*处理连接地址*/
    public LinkRequst getLinkClass(Context contacts, String Linkhost) {
        return new LinkRequst(contacts,Linkhost);}


    class LinkRequst {
        String LinkRe = null;
        public LinkRequst() {}
        public LinkRequst(Context contacts, String Linkhost) {new LinkRequst().getLink(contacts,Linkhost);}

        public String getLink(Context contacts, String Linkhost) {

            Resources rescot = contacts.getResources();
            if (Linkhost == contacts.getResources().getString(R.string.smail)) {
                LinkRe = rescot.getString(R.string.smail_host) +
                        rescot.getString(R.string.xg) +rescot.getString(R.string.smail_end_path) +
                        rescot.getString(R.string.xg) +rescot.getString(R.string.smail_file_name);
            }
            if (Linkhost == contacts.getResources().getString(R.string.codejava)) {
                LinkRe = rescot.getString(R.string.forge_host) + rescot.getString(R.string.xg) +
                        rescot.getString(R.string.xg) +rescot.getString(R.string.forge_first_path) +
                        rescot.getString(R.string.xg) +rescot.getString(R.string.forge_next_pathx)+
                        rescot.getString(R.string.xg) +rescot.getString(R.string.forge_end_path) +
                        rescot.getString(R.string.xg) +rescot.getString(R.string.smail_file_name);
            }
            if (Linkhost == contacts.getResources().getString(R.string.javatext)) {
                LinkRe = rescot.getString(R.string.forge_host) +
                        rescot.getString(R.string.xg) +rescot.getString(R.string.forge_next_pathx) +
                        rescot.getString(R.string.xg) +rescot.getString(R.string.forge_end_path) +
                        rescot.getString(R.string.xg) +rescot.getString(R.string.smail_file_name);
            }
            return LinkRe;
        }
        public String getLink(){return LinkRe;}
    }


}