package com.trade.bluehole.trad.activity.webview;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.trade.bluehole.trad.R;
import com.trade.bluehole.trad.util.data.DataUrlContents;

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

    @AfterViews
    void initData(){

        //获取actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(DataUrlContents.SERVER_HOST + DataUrlContents.show_view_pro_web + "?code="+proCode+"&shopCode="+shopCode);
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