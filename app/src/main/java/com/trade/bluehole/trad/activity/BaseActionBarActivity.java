package com.trade.bluehole.trad.activity;

import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;

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
}
