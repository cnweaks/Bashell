package com.cnweak.rebash.alioss;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/1/30.
 */
public class AliOSSData {
    private static String osszone = "oss-cn-shanghai.aliyuncs.com";
    private static String accessKeyId = "aKoMIpfySqAeMTLF";
    private static String accessKeySecret = "M5KDyVLABYEvXMBSlPu8WIeisQrsuR";

    //bucket and SDpatch
    private static String Bucket = "cnweakbash";
    private static String SDPath = "/sdcard";

    //上传
    private static String UpPath = "/alioss";
    private static String OSPath = "test";
    private static String UpFile = "alibaba.alibaba";

    //下载
    private static String DwPath = "/test";
    private static String DwFile = "alibaba.alibaba";

    public void setOssLink(String osszone,String accessKeyId,String accessKeySecret){
        this.osszone = osszone; this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
    }
    public void setOssUpConf(String UpPath,String UpFile){
        this.UpPath = UpPath; this.UpFile = UpFile;
    }
    public void setOssDwConf(String DwPath,String DwFile){
        this.DwPath = DwPath; this.DwFile = DwFile;
    }
    public void setBucketSD(String Bucket ,String SDPath){
        this.Bucket = Bucket; this.SDPath = SDPath;
    }


    public static String getOssZone(){
        return osszone;
    }
    public static String getOssKeyId(){
        return accessKeyId;
    }
    public static String getOssKeySecret(){
        return accessKeySecret;
    }
    public static String getBucket() {
        return Bucket;
    }

    public static String getSDPath() {
        return SDPath;
    }

    public static String getUpPath() {
        return UpPath;
    }

    public static String getUpFile() {
        return UpFile;
    }

    public static String getDwPath() {
        return DwPath;
    }

    public static String getDwFile() {
        return DwFile;
    }
    public static String getOSPath() {
        return OSPath;
    }

}
