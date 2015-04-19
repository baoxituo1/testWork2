package com.trade.bluehole.trad.activity.webview;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.manuelpeinado.fadingactionbar.extras.actionbarcompat.FadingActionBarHelper;
import com.trade.bluehole.trad.R;
import com.trade.bluehole.trad.util.data.DataUrlContents;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_web_view)
public class WebViewActivity extends ActionBarActivity {
    public final static String NOTICE_TYPE="state";
    public final static String NOTICE_CODE="newCode";

    @Extra(NOTICE_TYPE)
    String state;
    @Extra(NOTICE_CODE)
    String code;

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
        if("0".equals(state)){//新闻
            webView.loadUrl(DataUrlContents.SERVER_HOST+DataUrlContents.load_notice_for_web_view+code);
        }else{//站内信
            webView.loadUrl(DataUrlContents.SERVER_HOST+DataUrlContents.load_letter_for_web_view+code);
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
