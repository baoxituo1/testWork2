package com.trade.bluehole.trad.util;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import com.aliyun.mbaas.oss.OSSClient;
import com.aliyun.mbaas.oss.model.TokenGenerator;
import com.aliyun.mbaas.oss.storage.OSSBucket;
import com.aliyun.mbaas.oss.util.OSSLog;
import com.aliyun.mbaas.oss.util.OSSToolKit;
import com.baidu.mapapi.SDKInitializer;
import com.loopj.android.http.AsyncHttpClient;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.trade.bluehole.trad.entity.User;
import com.trade.bluehole.trad.entity.shop.ShopCommonInfo;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.rest.RestService;

@EApplication
public class MyApplication extends Application {

    @SystemService
    NotificationManager notificationManager;

    //阿里云oss
    public static final String accessKey = "ictZeAtTIlkEXGta"; // 测试代码没有考虑AK/SK的安全性
    public static final String screctKey = "8CQkQa7IytCb73hvk12EUazS0hUPw2";
    public static  OSSBucket sampleBucket;//阿里云 oss
    private User user;
    private ShopCommonInfo shop;
    /** 表示通过Intent传递到下一个Activity的图片列表 */
    public static final String ARG_PHOTO_LIST = "trade.android.app.chooseimages.PHOTO_LIST";
    /** 表示通过Intent传递到上一个Activity的图片列表 */
    public static final String RES_PHOTO_LIST = "trade.android.app.chooseimages.PHOTO_LIST";

    /** 表示选择的图片发生了变化 */
    public static final int RESULT_CHANGE = 10010;

    /** 最多能够选择的图片个数 */
    public static final int MAX_SIZE = 15;


    /**
     * 阿里云实例化
     */
    static {
        OSSClient.setGlobalDefaultTokenGenerator(new TokenGenerator() { // 设置全局默认加签器
            @Override
            public String generateToken(String httpMethod, String md5, String type, String date,
                                        String ossHeaders, String resource) {

                String content = httpMethod + "\n" + md5 + "\n" + type + "\n" + date + "\n" + ossHeaders
                        + resource;

                return OSSToolKit.generateToken(MyApplication.accessKey, MyApplication.screctKey, content);
            }
        });
        // OSSClient.setGlobalDefaultACL(AccessControlList.PUBLIC_READ_WRITE); // 设置全局默认bucket访问权限
        OSSClient.setGlobalDefaultHostId("oss-cn-beijing.aliyuncs.com"); // 指明你的bucket是放在北京数据中心
    }

    public void onCreate() {
        super.onCreate();
        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(this);
        initSomeStuff();
        initImageLoader(getApplicationContext());
        //阿里云配置
        OSSLog.enableLog(true);
        OSSClient.setApplicationContext(getApplicationContext()); // 传入应用程序context

    }



    /**
     * oss请求
     * 获取单例对象方法
     *
     * @return
     * @throws Exception
     */
    public static synchronized OSSBucket getOssBucket()
    {
        try
        {
            if (sampleBucket == null)
            {
                Log.i("MyApplication", "实例化阿里云oss请求client");
                // 开始单个Bucket的设置
                sampleBucket = new OSSBucket("125");
                sampleBucket.setBucketHostId("oss-cn-beijing.aliyuncs.com"); // 可以在这里设置数据中心域名或者cname域名
            }
        }
        catch (Exception e)
        {
            Log.e("MyApplication", "实例化阿里云oss请求client，配置错误，请检查"+e);
        }
        return sampleBucket;
    }


    @Background
    void initSomeStuff() {
        // init some stuff in background
    }


    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());

        ImageManager.init();
    }


    public User getUser() {
        return user;
    }

    public ShopCommonInfo getShop() {
        return shop;
    }

    public void setShop(ShopCommonInfo shop) {
        this.shop = shop;
    }

    public void setUser(User user) {
        this.user = user;
    }
}