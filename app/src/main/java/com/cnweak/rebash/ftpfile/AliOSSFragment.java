package com.cnweak.rebash.ftpfile;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.cnweak.rebash.FileActivity;
import com.cnweak.rebash.R;
import com.cnweak.rebash.alioss.AliOSSData;
import com.cnweak.rebash.alioss.GetObjectSamples;
import com.cnweak.rebash.alioss.ListObjectsSamples;
import com.cnweak.rebash.alioss.PutObjectSamples;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class AliOSSFragment extends Fragment {
    private OSS oss;
    private Button upload,download,listbut,manage;
    public class AliOSSTabListener implements ActionBar.TabListener {
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {ft.add(android.R.id.content, AliOSSFragment.this, null);}
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {ft.remove(AliOSSFragment.this);}
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) { /*激活tab后的事件*/   }
    }
    private Map<Integer,Object> list= null;
    private CommonFileListAdapter mFileListAdapter = null;
    private AliOSSData od = new AliOSSData();
    private FileActivity mParent;
    private Master mMaster;
    private View mView = null;
    public AliOSSFragment() {    }
    public void init(FileActivity parent){ mParent = parent;mMaster = Master.getFtpMasterInstance();}
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);mMaster = Master.getFtpMasterInstance();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(od.getOssKeyId(), od.getOssKeySecret());
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        OSSLog.enableLog();
       // Log.e("错误", "AyncListObjects====》读取成功");
      //  Log.i("正常", "AyncListObjects====》读取成功");
       // Log.w("警告", "AyncListObjects====》读取成功");
       // Log.wtf("提示","AyncListObjects====》读取成功");
        oss = new OSSClient(null,od.getOssZone(), credentialProvider, conf);
        if(mView==null){
            mView = inflater.inflate(R.layout.aliyun_oss, container, false);

            upload = (Button)mView.findViewById(R.id.oss_up);
            download = (Button)mView.findViewById(R.id.oss_down);
            listbut = (Button)mView.findViewById(R.id.oss_syc);
            manage = (Button)mView.findViewById(R.id.oss_manage);
            upload.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        public void run() {
                            boolean isok =  new PutObjectSamples().putObjectFromLocalFile(oss);
                            Message msg = new Message();
                            if(isok){
                            Log.d("上传线程:", "上传成功，线程id = " + Thread.currentThread().getId() + "\n");
                                msg.what = 1;
                            }else {
                            Log.d("上传线程:", "上传失败，线程id = " + Thread.currentThread().getId() + "\n");
                                msg.what = 2;}
                            h.sendMessage(msg);
                        }
                    }).start();
                }
            });
// 下载
            download.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                   new Thread(new Runnable() {
                        public void run() {
                   new GetObjectSamples(oss, od.getBucket(), od.getDwFile()).asyncGetObjectSample();

                        }
                    }).start();
                }
            });
// 罗列
            listbut.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        public void run() {
                            new ListObjectsSamples(oss).asyncListObjectsWithPrefix();
                        }
                    }).start();
                }
            });
// manage
            manage.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        public void run() {
                            list = new ListObjectsSamples(oss).AyncListObjects();
                            boolean isok = Boolean.parseBoolean(list.get(0).toString());
                                    Message msg = new Message();
                            if (isok) {
                               /// Log.e("文件名", "列表成功，线程id = " + Thread.currentThread().getId() + "\n");
                                msg.what = 3;
                            } else {
                               // Log.d("文件名", "列表失败，线程id = " + Thread.currentThread().getId() + "\n");
                                msg.what = 2;
                            }

                            h.sendMessage(msg);
                        }
                    }).start();
                }
            });
        }
        setButtonStatus();
        return mView;
    }
    private void setButtonStatus(){
        boolean b = Global.getInstance().mIsMasterConnected;
        upload.setEnabled(!b);
        download.setEnabled(!b);
        listbut.setEnabled(!b);
        manage.setEnabled(!b);
    }

    //线程消息接收
    Handler h = new Handler(){
        public void handleMessage (Message msg)
        {
            switch(msg.what)
            {
                case 1:
                C.makeToast(mParent, "成功");
                break;
                case 2:
                C.makeToast(mParent, "失败");
                break;
                case 3:
                C.makeToast(mParent, "列表载入成功");
                updateUI();
                break;
            }
        }
    };

    private void updateUI() {
        Map<Integer,String> objectname;
        Map<Integer,Boolean> objecttype;
        Map<Integer,Date> objectdate;
        Map<Integer,Long> objectsize;
        if (mFileListAdapter == null) {
            //1为文件名称，2为文件尺寸，3为修改日期，4,为文件类型
            objectname= (Map<Integer, String>) list.get(1);
            objectsize = (Map<Integer, Long>) list.get(2);
            objectdate= (Map<Integer,Date>) list.get(3);
            objecttype= (Map<Integer,Boolean>) list.get(4);
            SimpleDateFormat formatdate = new SimpleDateFormat("yyyy-MM-dd");
            ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String,Object>>();
            for(int i = 0; i < objectname.size();i++){
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("Filetype", objecttype.get(i));//文件类型
                map.put("FileName", objectname.get(i));//文件名称
                //Log.w("文件名",objectname.get(i));
                map.put("FileSize", getFileSize(objectsize.get(i)));//文件尺寸
                map.put("FileTime", formatdate.format(objectdate.get(i)));//文件日期
                //System.out.println("文件类型：" + objecttype.get(i));
                listItem.add(map);}
            ListView MList = (ListView) mView.findViewById(R.id.aliyun_list);
            SimpleAdapter mSimpleAdapter = new SimpleAdapter(mParent,
                    listItem,//需要绑定的数据
                    R.layout.file_item,//每一行的布局
                    new String[] {"FileName","FileSize", "FileTime"},
                    new int[]    {R.id.file_name, R.id.file_size, R.id.file_time});
            MList.setAdapter(mSimpleAdapter);//为ListView绑定适配器
            registerForContextMenu(MList);
        }
    }
    public String getFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }
}
