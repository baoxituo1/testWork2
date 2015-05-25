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
import com.trade.bluehole.trad.service.ActivityStackMgr;
import com.trade.bluehole.trad.service.AppManager;
import com.trade.bluehole.trad.util.MyApplication;
import com.trade.bluehole.trad.util.data.DataUrlContents;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

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

        AppManager.getAppManager().addActivity(this);

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
        if(null!=shop&&null!=user){
            // 添加QQ支持, 并且设置QQ分享内容的target url
            UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this, myApplication.qq_appId, myApplication.qq_appKey);
            qqSsoHandler.setTitle(shop.getTitle());
            qqSsoHandler.setTargetUrl(DataUrlContents.SERVER_HOST + DataUrlContents.show_view_shop_web + "?shopCode=" + user.getShopCode());
            qqSsoHandler.addToSocialSDK();

            // 添加QZone平台
            QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this, myApplication.qq_appId, myApplication.qq_appKey);
            qZoneSsoHandler.addToSocialSDK();

            //设置新浪SSO handler
            mController.getConfig().setSsoHandler(new SinaSsoHandler());
            //设置腾讯微博SSO handler
            mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
            String appID = "wx0e3efd036f185a84";
            String appSecret = "7020c04d9239c752b76de75d34a9fe2e";
            // 添加微信平台
            UMWXHandler wxHandler = new UMWXHandler(this,appID,appSecret);
            wxHandler.addToSocialSDK();
            // 添加微信朋友圈
            UMWXHandler wxCircleHandler = new UMWXHandler(this,appID,appSecret);
            wxCircleHandler.setToCircle(true);
            wxCircleHandler.addToSocialSDK();
        }else{
            //跳到登录
            Log.e("BaseActionBarActivity", "user shop null，配置错误，请检查" );
        }

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


    public void shareProduct(String code,String proName,String imagUrl){
        String _targetUrl=DataUrlContents.SERVER_HOST + DataUrlContents.show_view_pro_web + "?productCode=" + code + "&shopCode=" + user.getShopCode();
        // 设置分享内容
        mController.setShareContent(proName+_targetUrl);
        // 设置分享图片, 参数2为图片的url地址
        mController.setShareMedia(new UMImage(this, imagUrl));
        // 添加QQ支持, 并且设置QQ分享内容的target url
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this,  myApplication.qq_appId, myApplication.qq_appKey);
        qqSsoHandler.setTitle(shop.getTitle());
        qqSsoHandler.setTargetUrl(DataUrlContents.SERVER_HOST + DataUrlContents.show_view_pro_web + "?productCode=" + code + "&shopCode=" + user.getShopCode());
        qqSsoHandler.addToSocialSDK();
        // 添加QZone平台
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this, myApplication.qq_appId, myApplication.qq_appKey);
        qZoneSsoHandler.addToSocialSDK();

        mController.openShare(this, false);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 结束Activity&从堆栈中移除
        AppManager.getAppManager().finishActivity(this);
    }

    public void finish() {
        ActivityStackMgr.getActivityStackMgr().popNofinishActivity(this);
        super.finish();
    }
}
