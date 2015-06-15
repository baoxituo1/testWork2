package com.trade.bluehole.trad.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.trade.bluehole.trad.service.ActivityStackMgr;
import com.trade.bluehole.trad.service.AppManager;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by Administrator on 2015-05-25.
 */
public class BaseActivity extends Activity{

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        //ActivityStackMgr.getActivityStackMgr().pushActivity(this);
        AppManager.getAppManager().addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 结束Activity&从堆栈中移除
        AppManager.getAppManager().finishActivity(this);
    }

    public void finish() {
       // ActivityStackMgr.getActivityStackMgr().popNofinishActivity(this);
        super.finish();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
    /**
     * 退出应用程序
     */
    public void AppExit(Context context) {
        AppManager.getAppManager().AppExit(context);
    }
}
