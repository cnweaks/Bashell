package com.cnweak.rebash.fileftp;


import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.view.animation.*;
import android.widget.*;
import java.io.*;
import java.text.*;
import java.util.*;
import com.cnweak.rebash.R;
public class MyAdapter extends BaseAdapter {
    private FileHandler fileHandler = new FileHandler();
    public MyAnimation myAnimation = new MyAnimation();
    public LayoutInflater lif;
    public List<Map<String, Object>> list;
    private int layoutID;
    private String[] flag;
    private int[] itemID;
    public Context context;
    public ListView lv;
    public List actvList;

    //构造函数
    public MyAdapter(List actvList, ListView lv, Context context, List<Map<String, Object>> list, int layoutID, String[] flag, int[] itemID) {
        this.lif = LayoutInflater.from(context);
        this.list = list;
        this.layoutID = layoutID;
        this.flag = flag;
        this.itemID = itemID;
        this.context = context;
        this.lv = lv;
        this.actvList = actvList;
    }

    //getCount决定getView执行的次数
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //设置listItem布局
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //将ListItem的布局通过lif对象实例化为一个View,注意Context和View之间的区别,Context是View的父类
        convertView = lif.inflate(layoutID, null);
        //获得Layout布局里的组件id-----itemID[i]是Adapter的第五个参数
        ImageView iv = (ImageView) convertView.findViewById(itemID[0]);
        TextView tv = (TextView) convertView.findViewById(itemID[1]);
        TextView tv1 = (TextView) convertView.findViewById(itemID[2]);
        tv1.setText((String) list.get(position).get(flag[2]));
        //Button btn = (Button) convertView.findViewById(R.id.btn);
        //listItem里的控件设置动画(这里为什么只是下拉显示？？？？)
        Animation alpha = myAnimation.getAlpha();
        Animation translate = myAnimation.getTranslate();
        AnimationSet set = myAnimation.getSet(alpha, translate);
        //注意单个View加载set的方法
        convertView.startAnimation(set);
//        btn.startAnimation(set);
        iv.startAnimation(set);
        if (position == 0) {
            //ListItem第一行的布局显示
            //id到value的赋值----flag[i]是Adapter的第四个参数-------position是listView的行数
            iv.setBackgroundDrawable(null);//setBackgroundResource()参数不能为null
            tv.setText((String) list.get(position).get(flag[1]));
            //btn.setText(R.string.more);
            //btn.setBackgroundColor(Color.parseColor("#00FFFF"));
            //btn.setOnClickListener(new View.OnClickListener() {
			//		@Override
			//		public void onClick(View v) {
			//			//startDiolag(position);
			//		}
			//	});
        } else {
            //ListItem第二行以后的布局显示
            //iv.setBackgroundResource((Integer) list.get(position).get(flag[0]));
            File f = (File) list.get(position).get(flag[1]);
            tv.setText(f.getName());
            //btn.setOnClickListener(new View.OnClickListener() {
			//		@Override
			//		public void onClick(View v) {
			//			///startDiolag(position);
			//		}
			//	});
        }
//        遍历ListItem里的所有组件
//        for (int i = 0; i < flag.length; i++) {
//            //得到组件的正确类型
//            if (convertView.findViewById(itemID[i]) instanceof ImageView) {
//                //实例化convertView里的组件
//                ImageView iv = (ImageView) convertView.findViewById(itemID[i]);
//                //key——id的映射
//                iv.setBackgroundResource((Integer) list.get(position).get(flag[i]));
//            } else if (convertView.findViewById(itemID[i]) instanceof TextView) {
//                TextView tv = (TextView) convertView.findViewById(itemID[i]);
//                File f = (File)list.get(position).get(flag[i]);
//                tv.setText(f.getName());
//            }
//        }
        return convertView;
    }

	
    //创建文件夹对话框---注意参数position要设置成final
    void creatFileDialog(final int position) {
        final View v = lif.inflate(R.layout.file_dialog_creatfile, null);
        final EditText et = (EditText) v.findViewById(R.id.et);
        final File onClickFile = (File) list.get(position).get(flag[1]);
        //hint必须在监听器之前设置要不然显示不出来
        et.setHint(R.string.input_dir_name);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle(R.string.creat);
        builder.setView(v);
        builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String fileName = et.getText().toString();
                fileHandler.createFile(context, onClickFile, fileName);
            }
        }).setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, R.string.back, Toast.LENGTH_SHORT).show();
            }
        }).show();
    }

    //删除文件对话框
    void deleteFileDialog(final int position) {
        final File f = (File) list.get(position).get(flag[1]);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle(R.string.delete);
        builder.setMessage(R.string.sure_delete);
        builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fileHandler.deleteFile(context, f);
            }
        }).setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, R.string.back, Toast.LENGTH_SHORT).show();
            }
        }).show();
    }

    //重命名对话框
    void renameFileDialog(final int position) {
        final View v = lif.inflate(R.layout.file_dialog_creatfile, null);
        final EditText et = (EditText) v.findViewById(R.id.et);
        final File file = (File) list.get(position).get(flag[1]);
        et.setHint(R.string.input_new_name);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle(R.string.rename);
        builder.setView(v);
        builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String fileName = et.getText().toString();
                fileHandler.renameFile(context, file, fileName);
            }
        }).setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, R.string.back, Toast.LENGTH_SHORT).show();
            }
        }).show();
    }

    //移动文件对话框
    void moveFileDialog(final int position) {
        final View v = lif.inflate(R.layout.file_dialog_creatfile, null);
        final EditText et = (EditText) v.findViewById(R.id.et);
        final File file = (File) list.get(position).get(flag[1]);
        et.setHint(R.string.input_path);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle(R.string.move);
        builder.setView(v);
        builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String path = et.getText().toString();
                fileHandler.moveFile(context, file, path);
            }
        }).setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, R.string.back, Toast.LENGTH_SHORT).show();
            }
        }).show();
    }

    //文件详情对话框
    void fileDetilDialog(int position) {
        final View v = lif.inflate(R.layout.file_dialog_detil, null);
        File file = (File) list.get(position).get(flag[1]);
        TextView tv_size = (TextView) v.findViewById(R.id.tv_size);
        TextView tv_length = (TextView) v.findViewById(R.id.tv_length);
        TextView tv_time = (TextView) v.findViewById(R.id.tv_time);
        long time = file.lastModified();
        //修改时间转换成当前时间
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String nowTime = sdf.format(date);
        tv_time.setText(nowTime);
        //注意UI更新不能在子线程里，会报错，必须用hangdler异步消息处理机制来更新UI
        final SizeHandler sizeHandler = new SizeHandler(tv_size);
        final LengthHandler lengthHandler = new LengthHandler(tv_length);
        //注意其他类内部类的实例化方式
        final FileHandler.SizeRunnable sizeRunnable = fileHandler.new SizeRunnable(file, sizeHandler);
        final FileHandler.LengthRunnable lengthRunnable = fileHandler.new LengthRunnable(file, lengthHandler);
        final Thread sizeThread = new Thread(sizeRunnable);
        final Thread lengthThread = new Thread(lengthRunnable);
        sizeThread.start();
        lengthThread.start();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle(R.string.detil);
        builder.setView(v);
        builder.setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //关闭线程并初始化size和length
                        sizeHandler.removeCallbacks(sizeRunnable);
                        sizeThread.interrupt();
                        lengthHandler.removeCallbacks(lengthRunnable);
                        lengthThread.interrupt();
                        fileHandler.size = 0;
                        fileHandler.length = 0;
                        Toast.makeText(context, R.string.back, Toast.LENGTH_SHORT).show();
                    }
                }
        );
        builder.show();
    }

    //文件大小handler
    class SizeHandler extends Handler {
        TextView tv_size;

        public SizeHandler(TextView tv_size) {
            this.tv_size = tv_size;
        }

        @Override
        public void handleMessage(Message msg) {
            tv_size.setText(msg.obj.toString());
        }
    }

    //文件内容handler
    class LengthHandler extends Handler {
        TextView tv_length;

        public LengthHandler(TextView tv_length) {
            this.tv_length = tv_length;
        }

        @Override
        public void handleMessage(Message msg) {
            tv_length.setText(String.valueOf(msg.arg1));
        }
    }

    //查找文件对话框
  public  void findDialog() {
        View v = lif.inflate(R.layout.file_dialog_find, null);
        //为AutoCompleteTextView加载适配器
        AutoCompleteTextView actv = (AutoCompleteTextView) v.findViewById(R.id.actv);
        ArrayAdapter aa = new ArrayAdapter(context, android.R.layout.simple_list_item_1, actvList);
        actv.setAdapter(aa);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle(R.string.find);
        builder.setView(v);
        builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                actv.getText();
            }
        });
        builder.setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, R.string.back, Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

}
