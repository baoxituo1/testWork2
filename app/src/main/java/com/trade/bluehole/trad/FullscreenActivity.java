package com.trade.bluehole.trad;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.trade.bluehole.trad.activity.BaseActionBarActivity;
import com.trade.bluehole.trad.entity.User;
import com.trade.bluehole.trad.entity.shop.ShopCommonInfo;
import com.trade.bluehole.trad.util.MyApplication;
import com.trade.bluehole.trad.util.Result;
import com.trade.bluehole.trad.util.data.DataUrlContents;
import com.trade.bluehole.trad.util.full.SystemUiHider;
import com.trade.bluehole.trad.util.model.FirstVisitModel;
import com.trade.bluehole.trad.util.model.UserModel;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.apache.http.Header;

import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see
 */
@EActivity(R.layout.activity_fullscreen)
public class FullscreenActivity extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    @App
    MyApplication myapplication;

    private static AsyncHttpClient client=new AsyncHttpClient();
    public static Gson gson = new Gson();
    //页面进度条
    SweetAlertDialog pDialog;

     @AfterViews
     void initData() {
         //初始化等待dialog
         pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
         pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
         pDialog.setTitleText("读取中...");
         pDialog.setCancelable(false);

         isFirst();
         systemUiHider();

     }

     /**
     * 是否首次
     */
     private void isFirst(){
         //查询首次访问数据库是否有记录
         FirstVisitModel first= new Select().from(FirstVisitModel.class).executeSingle();
         //如果不存在，插入一条记录 停在此页面
         if(null==first){
             first=new FirstVisitModel();
             first.visiFlag="1";
             first.save();
         }else{
             //不是首次查询时候有帐号信息
             UserModel user=new Select().from(UserModel.class).executeSingle();
             //有帐号信息
             if(user!=null){
                 loginServer(user.userAccount,user.passWord);
             }else{
                 //先不做什么 也停在这
             }
         }
     }


    //去服务器确认账号
    void loginServer(String account,String password){
        pDialog.show();
        // Toast.makeText(this,"account:"+account.getText()+",password:"+password.getText(),Toast.LENGTH_SHORT).show();
        RequestParams params=new RequestParams();
        params.put("account",account);
        params.put("password", password);
        params.put("md5Flag", "true");
        client.get(DataUrlContents.SERVER_HOST + DataUrlContents.user_login, params, new BaseJsonHttpResponseHandler<Result<User, ShopCommonInfo>>() {


            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Result<User, ShopCommonInfo> response) {
                Log.d(LoginSystemActivity.class.getName(), statusCode + "");
                if (null != response) {
                    pDialog.hide();
                    //登录成功跳转到首页
                    if (response.isSuccess()) {
                        myapplication.setUser(response.getBzseObj());
                        myapplication.setShop(response.getObj());
                        SuperMainActivity_.intent(FullscreenActivity.this).start();
                    } else {
                        //不成功 跳转到登录
                        LoginSystemActivity_.intent(FullscreenActivity.this).start();
                    }
                } else {
                    Toast.makeText(FullscreenActivity.this, "服务器繁忙", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Result<User, ShopCommonInfo> errorResponse) {
                Toast.makeText(FullscreenActivity.this, "服务器繁忙", Toast.LENGTH_SHORT).show();
                pDialog.hide();
            }

            @Override
            protected Result<User, ShopCommonInfo> parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                // return new ObjectMapper().readValues(new JsonFactory().createParser(rawJsonData), Result.class).next();
                return gson.fromJson(rawJsonData, new TypeToken<Result<User, ShopCommonInfo>>() {
                }.getType());
            }
        });
    }


    /**
     * 加载按钮首次显示和动画
     */
    private void systemUiHider(){
        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);
        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    /**
     * 当登录被点击
     */
    @Click(R.id.go_to_login)
    void onGotoLoginOnClick(){
        LoginSystemActivity_.intent(this).start();
    }

    /**
     * 当注册被点击
     */
    @Click(R.id.go_to_register)
    void onGotoRegisterOnClick(){
        RegisterManageActivity_.intent(this).start();
    }
}




