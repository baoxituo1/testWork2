package com.trade.bluehole.trad.util;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;

import com.baidu.mapapi.SDKInitializer;
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
    //阿里云oss
    public static final String accessKey = "ictZeAtTIlkEXGta"; // 测试代码没有考虑AK/SK的安全性
    public static final String screctKey = "8CQkQa7IytCb73hvk12EUazS0hUPw2";
    private User user;
    private ShopCommonInfo shop;
    /** 表示通过Intent传递到下一个Activity的图片列表 */
    public static final String ARG_PHOTO_LIST = "trade.android.app.chooseimages.PHOTO_LIST";
    /** 表示通过Intent传递到上一个Activity的图片列表 */
    public static final String RES_PHOTO_LIST = "trade.android.app.chooseimages.PHOTO_LIST";

    /** 表示选择的图片发生了变化 */
    public static final int RESULT_CHANGE = 10010;

    /** 最多能够选择的图片个数 */
    public static final int MAX_SIZE = 10;
    public void onCreate() {
        super.onCreate();
        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(this);
        initSomeStuff();
        initImageLoader(getApplicationContext());
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
    @SystemService
    NotificationManager notificationManager;



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
}