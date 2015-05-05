package com.trade.bluehole.trad.activity.webview;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.trade.bluehole.trad.R;
import com.trade.bluehole.trad.util.data.DataUrlContents;
import com.trade.bluehole.trad.util.view.ProgressWebView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

/**
 * 商品详情预览
 * Created by Administrator on 2015-04-21.
 */
@EActivity(R.layout.product_web_view)
public class ProductWebViewActivity extends ActionBarActivity {
    public final static String SHOP_CODE="shopCode";
    public final static String PRO_CODE="proCode";

    @Extra(SHOP_CODE)
    String shopCode;
    @Extra(PRO_CODE)
    String proCode;

    @ViewById
    WebView webView;
    @ViewById
    NumberProgressBar pb;

    @AfterViews
    void initData(){

        //获取actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

       // webView.setWebViewClient(new WebViewClient());
        WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);
       // ws.setBuiltInZoomControls(true);
        //ws.setSupportZoom(true); //支持缩放
        /**
         * 用WebView显示图片，可使用这个参数
         * 设置网页布局类型：
         * 1、LayoutAlgorithm.NARROW_COLUMNS ： 适应内容大小
         * 2、LayoutAlgorithm.SINGLE_COLUMN:适应屏幕，内容将自动缩放
         */
        ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        ws.setLoadWithOverviewMode(true);
        ws.setDefaultTextEncodingName("utf-8"); //设置文本编码
        ws.setAppCacheEnabled(true);
        ws.setCacheMode(WebSettings.LOAD_DEFAULT);//设置缓存模式

        //添加Javascript调用java对象
       // webView.addJavascriptInterface(this, "java2js");
        webView.setWebViewClient(new WebViewClientDemo());
        webView.setWebChromeClient(new WebViewClient() );
        webView.loadUrl(DataUrlContents.SERVER_HOST + DataUrlContents.show_view_pro_web + "?productCode="+proCode+"&shopCode="+shopCode);
        //webView.loadUrl("http://192.168.1.169:8080/qqt_up/Mshop/showMshop.htm?shopCode=402881294c8e30b3014c8e374ea60000");
    }


    private class WebViewClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            pb.setProgress(newProgress);
            if(newProgress==100){
                pb.setVisibility(View.GONE);
            }
            super.onProgressChanged(view, newProgress);
        }

    }

    private class WebViewClientDemo extends android.webkit.WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);// 当打开新链接时，使用当前的 WebView，不会使用系统其他浏览器
            return true;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_web_view, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }else if(id==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}