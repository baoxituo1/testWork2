package com.trade.bluehole.trad.activity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.trade.bluehole.trad.entity.User;
import com.trade.bluehole.trad.entity.shop.Shop;
import com.trade.bluehole.trad.entity.shop.ShopCommonInfo;
import com.trade.bluehole.trad.util.MyApplication;
import com.trade.bluehole.trad.util.data.DataUrlContents;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * action bar 基础类
 * Created by Administrator on 2015-04-19.
 */
public class BaseActionBarActivity extends ActionBarActivity {
    // 首先在您的Activity中添加如下成员变量
    public final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
    MyApplication myApplication;
    private static AsyncHttpClient client;
    public static Gson gson = new Gson();

    public BaseActionBarActivity() {
        super();
    }

    //页面读取进度
    private SweetAlertDialog pDialog;
    public static ActionBar actionBar;
    User user;
    ShopCommonInfo shop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        myApplication = (MyApplication) getApplication();
        user = myApplication.getUser();
        shop = myApplication.getShop();
        /**友盟分享*/
        //设置分享排序
       /* mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QZONE,
                SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.TENCENT, SHARE_MEDIA.RENREN, SHARE_MEDIA.YIXIN);
        mController.getConfig().setPlatformOrder(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QZONE,
                SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.TENCENT, SHARE_MEDIA.RENREN, SHARE_MEDIA.YIXIN);*/
        mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
                SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE,SHARE_MEDIA.SINA,SHARE_MEDIA.TENCENT, SHARE_MEDIA.DOUBAN,
                SHARE_MEDIA.RENREN, SHARE_MEDIA.YIXIN);
        //设置新浪SSO handler
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        //设置腾讯微博SSO handler
        mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
        //竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * 网络请求
     * 获取单例对象方法
     *
     * @return
     * @throws Exception
     */
    public static synchronized AsyncHttpClient getClient() {
        try {
            if (client == null) {
                Log.i("BaseActionBarActivity", "实例化网络请求client");
                client = new AsyncHttpClient();
                client.setTimeout(30000);//30秒
            }
        } catch (Exception e) {
            Log.e("BaseActionBarActivity", "实例化网络请求client时异常，配置错误，请检查" + e);
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
    public SweetAlertDialog getDialog(Context ctx) {
        try {
            Log.i("BaseActionBarActivity", "实例化Dialog");
            //初始化等待dialog
            pDialog = new SweetAlertDialog(ctx, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("读取中...");
            pDialog.setCancelable(false);
        } catch (Exception e) {
            Log.e("BaseActionBarActivity", "实例化Dialog时异常，配置错误，请检查" + e);
        }
        return pDialog;
    }
}
