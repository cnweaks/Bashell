package com.cnweak.rebash.alioss;

import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.utils.BinaryUtil;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.AppendObjectRequest;
import com.alibaba.sdk.android.oss.model.AppendObjectResult;
import com.alibaba.sdk.android.oss.model.DeleteObjectRequest;
import com.alibaba.sdk.android.oss.model.DeleteObjectResult;
import com.alibaba.sdk.android.oss.model.ObjectMetadata;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by zhouzhuo on 12/3/15.
 */
public class PutObjectSamples {
    private OSS oss;
AliOSSData ossdata = new AliOSSData();
    // 从本地文件上传，采用阻塞的同步接口
    public boolean putObjectFromLocalFile(OSS oss) {
        this.oss = oss;
        boolean isupok = false;
        // 构造上传请求,bucket,OSS路径+名称，本地路径+文件
        //OSS端不能在前加“/”
        PutObjectRequest put = new PutObjectRequest(ossdata.getBucket(),ossdata.getOSPath()+"/"+ossdata.getUpFile(),ossdata.getSDPath()+ossdata.getUpPath()+"/"+ossdata.getUpFile());
        try {
            PutObjectResult putResult = oss.putObject(put);
            isupok = true;

        } catch (ClientException e) {
            // 本地异常如网络异常等
            e.printStackTrace();
        } catch (ServiceException e) {
            // 服务异常

        }
        return isupok;
    }

    // 从本地文件上传，使用非阻塞的异步接口
    public void asyncPutObjectFromLocalFile() {
        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(ossdata.getBucket(),ossdata.getUpFile(), ossdata.getUpPath());

        // 异步上传时可以设置进度回调
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {

            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
            }
        });

        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {

            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.d("PutObject", "UploadSuccess");

                Log.d("ETag", result.getETag());
                Log.d("RequestId", result.getRequestId());
            }


            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
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
    }

    // 直接上传二进制数据，使用阻塞的同步接口
    public void putObjectFromByteArray() {
        // 构造测试的上传数据
        byte[] uploadData = new byte[100 * 1024];
        new Random().nextBytes(uploadData);

        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(ossdata.getBucket(),ossdata.getUpFile(), ossdata.getUpPath());
        try {
            PutObjectResult putResult = oss.putObject(put);

            Log.d("PutObject", "UploadSuccess");

            Log.d("ETag", putResult.getETag());
            Log.d("RequestId", putResult.getRequestId());
        } catch (ClientException e) {
            // 本地异常如网络异常等
            e.printStackTrace();
        } catch (ServiceException e) {
            // 服务异常
            Log.e("RequestId", e.getRequestId());
            Log.e("ErrorCode", e.getErrorCode());
            Log.e("HostId", e.getHostId());
            Log.e("RawMessage", e.getRawMessage());
        }
    }

    // 上传时设置ContentType等，也可以添加自定义meta信息
    public void putObjectWithMetadataSetting() {
        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(ossdata.getBucket(),ossdata.getUpFile(), ossdata.getUpPath());
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("application/octet-stream");
        metadata.addUserMetadata("x-oss-meta-name1", "value1");

        put.setMetadata(metadata);

        // 异步上传时可以设置进度回调
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {

            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
            }
        });

        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {

            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.d("PutObject", "UploadSuccess");

                Log.d("ETag", result.getETag());
                Log.d("RequestId", result.getRequestId());
            }


            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
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
    }

    // 上传文件可以设置server回调
    public void asyncPutObjectWithServerCallback() {
        // 构造上传请求
        final PutObjectRequest put = new PutObjectRequest(ossdata.getBucket(),ossdata.getUpFile(), ossdata.getUpPath());

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("application/octet-stream");

        put.setMetadata(metadata);

        put.setCallbackParam(new HashMap<String, String>() {
            {
                put("callbackUrl", "110.75.82.106/mbaas/callback");
                put("callbackBody", "test");
            }
        });

        // 异步上传时可以设置进度回调
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
            }
        });

        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {

            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.d("PutObject", "UploadSuccess");

                // 只有设置了servercallback，这个值才有数据
                String serverCallbackReturnJson = result.getServerCallbackReturnBody();

                Log.d("servercallback", serverCallbackReturnJson);
            }


            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
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
    }

    public void asyncPutObjectWithMD5Verify() {
        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(ossdata.getBucket(),ossdata.getUpFile(), ossdata.getUpPath());

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("application/octet-stream");
        try {
            // 设置Md5以便校验
            metadata.setContentMD5(BinaryUtil.calculateBase64Md5(ossdata.getUpPath()+"/"+ossdata.getUpFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        put.setMetadata(metadata);

        // 异步上传时可以设置进度回调
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
            }
        });

        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {

            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.d("PutObject", "UploadSuccess");

                Log.d("ETag", result.getETag());
                Log.d("RequestId", result.getRequestId());
            }


            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
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
    }

    // 追加文件
    public void appendObject() {
        // 如果bucket中objectKey存在，将其删除
        try {
            DeleteObjectRequest delete = new DeleteObjectRequest(ossdata.getBucket(),ossdata.getUpFile());
            DeleteObjectResult result = oss.deleteObject(delete);
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
        AppendObjectRequest append = new AppendObjectRequest(ossdata.getBucket(),ossdata.getUpFile(), ossdata.getUpPath());

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("application/octet-stream");
        append.setMetadata(metadata);

        // 设置追加位置，只能从文件末尾开始追加，如果是新文件，从0开始
        append.setPosition(0);

        append.setProgressCallback(new OSSProgressCallback<AppendObjectRequest>() {
            public void onProgress(AppendObjectRequest request, long currentSize, long totalSize) {
                Log.d("AppendObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
            }
        });

        OSSAsyncTask task = oss.asyncAppendObject(append, new OSSCompletedCallback<AppendObjectRequest, AppendObjectResult>() {

            public void onSuccess(AppendObjectRequest request, AppendObjectResult result) {
                Log.d("AppendObject", "AppendSuccess");
                Log.d("NextPosition", "" + result.getNextPosition());
            }


            public void onFailure(AppendObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
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
    }

}
