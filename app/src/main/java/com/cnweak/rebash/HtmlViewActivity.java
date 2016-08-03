package com.cnweak.rebash;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.cnweak.rebash.html.BingTranslate;
import com.cnweak.rebash.html.GetCodeforgeCode;
import com.cnweak.rebash.html.HtmlUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2016/1/20.
 */
public class HtmlViewActivity extends Activity {
    private TextView apiusage , apiexample,apitranslate;
    private ListView apilist;
        private String[] apu_url = new String[]{"http://f.cnweak.com/code/smail/smail.html"};
    private AlertDialog dialog;
    private boolean isstate = false;
    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private BingTranslate translate = new BingTranslate();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.html_view_html);
        apilist = (ListView) findViewById(R.id.API_list_view);
        apiusage = (TextView)findViewById(R.id.API_item_methord);
		apitranslate = (TextView)findViewById(R.id.API_item_translate);
        apiexample = (TextView)findViewById(R.id.API_item_example);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialog = builder.setMessage("请稍后....").show();
        new AsyncHttpTask().execute(apu_url);
    }


    //数据初始化处理
    private void InitTransData(){

        ArrayAdapter<String> mapter = new ArrayAdapter<>(HtmlViewActivity.this, R.layout.html_item_view, translate.getApiName());
        apilist.setAdapter(mapter);DisShow(1);
        apilist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ItemTextShow(i);
            }

        });
    }






     //处理点击后的Item内容
	private void ItemTextShow(int i){
        Map<String,HtmlUtils> tranmap = new BingTranslate().getTranslateData();

        if(tranmap.get(i)!= null){

            apiusage.setText("用法:\n" + tranmap.get(i).getApi_item_methord());
        }

        if(tranmap.get(i) != null){
            apiexample.setText("例:\n" + tranmap.get(i).getApi_item_example());
        }

        if(tranmap.get(i)  != null){
         apitranslate.setText("参译:\n" + tranmap.get(i).getApi_item_translate());
        }


	}








     //关闭消息窗任务调度
    public void DisShow(long duration){
        Runnable runner = new Runnable() {
            @Override
            public void run() {dialog.dismiss();}
        };
        executor.schedule(runner, duration, TimeUnit.MILLISECONDS);
    }


    //翻译
    public void Translates(long duration){
        Runnable runner = new Runnable() {
            @Override
            public void run(){
                isstate = true;
                Message msg = new Message();
                translate.StartTranslate();
                SendMessage(msg.what = 1);
                isstate = false;
            }
        };
        executor.schedule(runner, duration,TimeUnit.MILLISECONDS);
        if(executor.isShutdown()){
            SendMessage(new Message().what = 0);
        }
    }

    private void SendMessage(int i) {
        switch (i){
            case -1:
                Toast.makeText(this, "翻译失败", Toast.LENGTH_LONG);
                break;
            case 0:
                Toast.makeText(this, "翻译时间不够", Toast.LENGTH_LONG);
                break;
            case 1:
                Toast.makeText(this, "翻译完成", Toast.LENGTH_LONG);
                translate = new BingTranslate();
                break;
            case 2:
                break;

        }

    }


    //菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.html_menu, menu);
        return true;
    }







//Item选择事件
    @Override
    public boolean onMenuItemSelected(int i,MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_translate: Translates(50);isstate = true;break;
            case R.id.menu_item_point:new AsyncHttpTask().execute(apu_url);break;
            case R.id.menu_item_java:startActivity(new Intent(this, GetCodeforgeCode.class));break;
            case android.R.id.home:finish();break;
            default:break;
        }
        return true;
    }








    //同步数据任务
    public class AsyncHttpTask extends AsyncTask<String ,Void ,Boolean>{
        protected Boolean doInBackground(String...strings) {
            Boolean isok = false;
            Map<String,HtmlUtils> apis = new HashMap();
            ArrayList<String> apiname = new ArrayList<>();
            try {
                Elements bodyelement = Jsoup.connect(strings[0]).get().body().getElementsByTag("tr");
                System.out.println(bodyelement);
                for (int c = 0; c < bodyelement.size(); c++) {
                    HtmlUtils sielement = new HtmlUtils();
                    String[] nametmp = new String[]{"no id","no name","no usage","no example"};
                    Element sindex = bodyelement.get(c).children().first();
                    Element siname = sindex.nextElementSibling();
                    Element sitext = siname.nextElementSibling();
                    Element exampe = sitext.nextElementSibling();
                    if (sindex.hasText()){sielement.setApi_item_id(sindex.text());
                    }else{sielement.setApi_item_methord(nametmp[0]);}
                    if (siname.hasText()){sielement.setApi_item_name(siname.text());nametmp[1] = siname.text();}
                        apiname.add(nametmp[1]);
                    if (sitext.hasText()){sielement.setApi_item_methord(sitext.text());
                    }else{sielement.setApi_item_methord(nametmp[2]);}
                    if (exampe.hasText()){sielement.setApi_item_example(exampe.text());
                    }else{sielement.setApi_item_example(nametmp[3]);}
                    apis.put(sielement.getApi_item_name(),sielement);
                }
                translate.setTranslateData(apis,apiname);
                isok =true;
            } catch (Exception ex) {ex.printStackTrace();}
            return isok;
        }
        protected void onPostExecute(Boolean result) {
            if(result){

                InitTransData();
            }
        }
    }



    /***
     * parents()获取父级的同类
     * accumulateParents获取根
     * child获取元素的子元素，并从 0 开始索引编号。
     * children获取此元素的子元素。
     * textNodes 获取此子元素的可见文本。列表不可修改，可修改文本
     * dataNodes获取元素的子数据节点。列表不可修改，可修改数据。
     * select选择器，使用dom查询匹配所指定匹配的元素
     * appendChild向此元素添加子子元素。
     * prependChild将元素添加到子元素的首位。
     * insertChildren将当前节点转移到指定元素索引处。
     * appendElement创建一个新元素的标记名称，并将其添加为最后一个子级。
     * prependElement创建一个新元素的标记名称，并将其添加为第一个孩子。
     * appendText创建并将一个新的文本节点追加到此元素。
     * prependText创建并添加一个新的文本节点到此元素。
     * append将内部 HTML 添加到此元素。提供的 HTML 将被解析，和每个节点追加到子元素到结束点。
     * prepend将内部 HTML 添加到此元素。提供的 HTML 将被解析，和每个节点其前面添加的元素的子元素开始。
     * before 将指定的 HTML (作为前一个同级) 插入到 DOM 在此元素之前。
     * after插入指定的 HTML DOM 后此元素 (如下面的兄弟姐妹)。
     * empty 删除所有元素的子节点
     * wrap换行此元素周围的 HTML。
     * siblingElements获取一个同级元素。如果元素具有没有同级元素，将返回一个空列表。
     * nextElementSibling获取此元素的下一个同级元素
     * previousElementSibling获取此元素的上元一个同级。
     * firstElementSibling获取此元素的第一个元素兄弟。
     * elementSiblingIndex其元素同级列表中获取此元素的列表索引。例如，如果这是的第一个元素
     * lastElementSibling获取此元素的最后一个元素同级
     * getElementsByTag指定的标记名称，包括和递归下级元素。
     * getElementById查找按 ID，包括或在此元素下的元素。
     * getElementsByClass发现有此类，包括或在此元素下的元素。大小写不敏感。
     * getElementsByAttribute查找已命名的属性集的元素。大小写不敏感。
     * getElementsByAttributeStarting与所提供的前缀开始一个属性名称的元素
     * getElementsByAttributeValue查找具有特定值的属性的元素。大小写不敏感。
     * getElementsByAttributeValueNot查找元素不具有此属性，或有一个不同的值。大小写不敏感。
     * getElementsByAttributeValueStarting查找具有以值前缀开头的属性元素。大小写不敏感。
     * getElementsByAttributeValueEnding查找具有以价值后缀结尾的属性元素。大小写不敏感。
     * getElementsByAttributeValueContaining查找具有的属性值包含匹配字符串的元素。大小写不敏感。
     * getElementsByAttributeValueMatching查找具有的属性的值与提供的正则表达式相匹配的元素。
     * getElementsByIndexLessThan查找的元素的同级索引小于所提供的索引。
     * getElementsByIndexGreaterThan查找的元素的同级索引大于所提供的索引。
     * getElementsByIndexEquals查找此同级元素的给定同级元素
     * getElementsContainingText查找包含指定的字符串的元素。搜索是大小写不敏感的。可直接显示的文本
     * getElementsContainingOwnText查找直接包含指定的字符串的元素。搜索是大小写不敏感的。必须直接显示的文本
     * getElementsMatchingText查找的元素的文本匹配提供的正则表达式。
     * getElementsMatchingText查找的元素的文本匹配提供的正则表达式。
     * getElementsMatchingOwnText查找其自己的文本匹配提供的正则表达式的元素。
     * getElementsMatchingOwnText查找元素的文本匹配正则表达式。
     * getAllElements找到此元素 (包括自我，和子元素) 下的所有元素。
     * text获取此元素及其所有子级的合并案文。
     * ownText获取由只; 此元素的文本不会所有子元素的合并案文。
     * hasText如果此元素具有任何文本内容 (即不只是空白) 进行测试。
     * data获取此元素的组合的数据
     * className获取此元素的"class"属性，其中可能包括多个类名称，空间的文本值
     * hasClass如果此元素具有一个类的测试。大小写不敏感。
     * addClass将一个类名称添加到此元素
     * removeClass删除此元素
     * toggleClass切换此元素
     * val获取窗体元素 (输入、 文本等) 的值。
     * html检索元素的内部 HTML
     *
     *
     *
     *
     *
     *
     * */

}
