package com.trade.bluehole.trad.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * action bar 基础类
 * Created by Administrator on 2015-04-19.
 */
public class BaseActionBarActivity extends ActionBarActivity {
    private static AsyncHttpClient client;
    public static Gson gson = new Gson();
    public BaseActionBarActivity(){
           super();
    }
    //页面读取进度
    private  SweetAlertDialog pDialog;
    public static ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    /**
     * 网络请求
     * 获取单例对象方法
     *
     * @return
     * @throws Exception
     */
    public static synchronized AsyncHttpClient getClient()
    {
        try
        {
            if (client == null)
            {
               Log.i("BaseActionBarActivity","实例化网络请求client");
                client = new AsyncHttpClient();
                client.setTimeout(30000);//30秒
            }
        }
        catch (Exception e)
        {
            Log.e("BaseActionBarActivity", "实例化网络请求client时异常，配置错误，请检查"+e);
        }
        return client;
    }


    /**
     * Dialog请求
     * 获取单例对象方法
     *
     * @return
     * @throws Exception
     */
    public   SweetAlertDialog getDialog(Context ctx)
    {
        try
        {
                Log.i("BaseActionBarActivity","实例化Dialog");
                 //初始化等待dialog
                pDialog = new SweetAlertDialog(ctx, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("读取中...");
                pDialog.setCancelable(false);
        }
        catch (Exception e)
        {
            Log.e("BaseActionBarActivity", "实例化Dialog时异常，配置错误，请检查"+e);
        }
        return pDialog;
    }
}
