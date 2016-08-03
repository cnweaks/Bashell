package com.cnweak.rebash.alioss;

import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.ListObjectsRequest;
import com.alibaba.sdk.android.oss.model.ListObjectsResult;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by zhouzhuo on 12/3/15.
 */
public class ListObjectsSamples {
    private OSS oss;
    private AliOSSData od = new AliOSSData();
    public ListObjectsSamples(OSS client) {this.oss = client;}
    // 异步罗列Bucket中文件
    public Map AyncListObjects() {
        final Map<Integer,Object> listobj = new HashMap<Integer,Object>();
        // 创建罗列请求
        ListObjectsRequest listObjects = new ListObjectsRequest(od.getBucket());

        // 设置成功、失败回调，发送异步罗列请求
        OSSAsyncTask task = oss.asyncListObjects(listObjects,
                new OSSCompletedCallback<ListObjectsRequest, ListObjectsResult>() {
            public void onSuccess(ListObjectsRequest request, ListObjectsResult result) {
               // Log.d("cnweak", "文件名：AyncListObjects====》读取成功");

                Map<Integer,String> liststr = new HashMap<Integer,String>();

                Map<Integer,Boolean> listtype;
                listtype = new HashMap<Integer,Boolean>();
                Map<Integer,Date> listdate;
                listdate = new HashMap<Integer,Date>();
                Map<Integer,Long> listsize;
                listsize = new HashMap<Integer,Long>();
                for (int i = 0; i < result.getObjectSummaries().size(); i++) {
                    listsize.put(i,result.getObjectSummaries().get(i).getSize());        //文件尺寸
                    listdate.put(i, result.getObjectSummaries().get(i).getLastModified());//修改日期
                    String file = result.getObjectSummaries().get(i).getKey();
                    isFileFolder(file);
                    liststr.put(i,file);//文件名称
                    if (file.lastIndexOf("/") < 0){
                        Log.e("eeeee文件eeeee",file);
                        listtype.put(i, null);
                        if (file.indexOf("/")+1 == file.length()){
                            listtype.put(i, true);
                            //System.out.println("/的位置:" + file.indexOf("/") + ",名称长度:" + file.length());
                        }
                    }else{
                       listtype.put(i, false);
                   // Log.w("wwww文件夹wwww",file);
                    }

                }
                listobj.put(0,true);
                listobj.put(1,liststr);
                listobj.put(2,listsize);
                listobj.put(3,listdate);
                listobj.put(4,listtype);

            }

            private void isFileFolder(String files){
                int isfile = files.indexOf("/");
                String okfolder = " ";
                if(isfile > 0){
                    
                    okfolder = files.substring(0,isfile);
                    Log.e("文件夹名称",""+okfolder);
                }else{
                    
                    Log.e("文件夹名称",""+okfolder);
                    
                }
            }
            public void onFailure(ListObjectsRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
        task.waitUntilFinished();
        return listobj;
    }

    // 同步罗列指定prefix/delimiter文件
    public void listObjectsWithPrefix() {
        ListObjectsRequest listObjects = new ListObjectsRequest(od.getBucket());
        // 设定前缀
        listObjects.setPrefix("Bashell");
        listObjects.setDelimiter("/");

        try {
            // 发送同步罗列请求，等待结果返回
            ListObjectsResult result = oss.listObjects(listObjects);
            for (int i = 0; i < result.getObjectSummaries().size(); i++) {
                Log.d("listObjectsWithPrefix", "object: " + result.getObjectSummaries().get(i).getKey() + " "
                        + result.getObjectSummaries().get(i).getETag() + " "
                        + result.getObjectSummaries().get(i).getLastModified());
            }

            for (int i = 0; i < result.getCommonPrefixes().size(); i++) {
                Log.d("listObjectsWithPrefix", "prefixes: " + result.getCommonPrefixes().get(i));
            }
        }
        catch (ClientException clientException) {
            clientException.printStackTrace();
        }
        catch (ServiceException serviceException) {
            Log.e("ErrorCode", serviceException.getErrorCode());
            Log.e("RequestId", serviceException.getRequestId());
            Log.e("HostId", serviceException.getHostId());
            Log.e("RawMessage", serviceException.getRawMessage());
        }
    }

    // 异步下载指定前缀文件
    public void asyncListObjectsWithPrefix() {
        ListObjectsRequest listObjects = new ListObjectsRequest(od.getBucket());
        // 设定前缀
        listObjects.setPrefix("file");

        // 设置成功、失败回调，发送异步罗列请求
        OSSAsyncTask task = oss.asyncListObjects(listObjects, new OSSCompletedCallback<ListObjectsRequest, ListObjectsResult>() {

            public void onSuccess(ListObjectsRequest request, ListObjectsResult result) {
                Log.d("AyncListObjects", "Success!");
                for (int i = 0; i < result.getObjectSummaries().size(); i++) {
                    Log.d("AyncListObjects", "object: " + result.getObjectSummaries().get(i).getKey() + " "
                            + result.getObjectSummaries().get(i).getETag() + " "
                            + result.getObjectSummaries().get(i).getLastModified());
                }
            }


            public void onFailure(ListObjectsRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
        task.waitUntilFinished();
    }

}
