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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.trade.bluehole.trad.activity.BaseActionBarActivity;
import com.trade.bluehole.trad.activity.BaseActivity;
import com.trade.bluehole.trad.entity.User;
import com.trade.bluehole.trad.entity.shop.ShopCommonInfo;
import com.trade.bluehole.trad.util.MyApplication;
import com.trade.bluehole.trad.util.Result;
import com.trade.bluehole.trad.util.base.EncryptUrlPara;
import com.trade.bluehole.trad.util.data.DataUrlContents;
import com.trade.bluehole.trad.util.full.SystemUiHider;
import com.trade.bluehole.trad.util.model.FirstVisitModel;
import com.trade.bluehole.trad.util.model.UserModel;
import com.trade.bluehole.trad.util.update.UpdateManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.apache.http.Header;

import cn.pedant.SweetAlert.SweetAlertDialog;
import mehdi.sakout.fancybuttons.FancyButton;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see
 */
@EActivity(R.layout.activity_fullscreen)
public class FullscreenActivity extends BaseActivity {

    @App
    MyApplication myapplication;
    @ViewById
    LinearLayout bottom_layout;
    @ViewById
    RelativeLayout re_error_login;

    private static AsyncHttpClient client=new AsyncHttpClient();
    public static Gson gson = new Gson();
    //页面进度条
    SweetAlertDialog pDialog;

     @AfterViews
     void initData() {
         client.setTimeout(50000);//30秒
         //初始化等待dialog
         pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
         pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
         pDialog.setTitleText("读取中...");
         pDialog.setCancelable(false);

         isFirst();
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
             //先不做什么 也停在这
             Animation alphaAnimation= AnimationUtils.loadAnimation(this,R.anim.bottomitem_in);
             bottom_layout.setVisibility(View.VISIBLE);
             bottom_layout.startAnimation(alphaAnimation);
         }else{
             //不是首次查询时候有帐号信息
             UserModel user=new Select().from(UserModel.class).executeSingle();
             //有帐号信息
             if(user!=null){
                 loginServer(user.userAccount,user.passWord);
             }else{
                 //先不做什么 也停在这
                 Animation alphaAnimation= AnimationUtils.loadAnimation(this,R.anim.bottomitem_in);
                 bottom_layout.setVisibility(View.VISIBLE);
                 bottom_layout.startAnimation(alphaAnimation);
             }
         }
     }


    //去服务器确认账号
    void loginServer(String account,String password){
        pDialog.show();
        // Toast.makeText(this,"account:"+account.getText()+",password:"+password.getText(),Toast.LENGTH_SHORT).show();
        RequestParams params=new RequestParams();
       // params.put("account",account);
        //params.put("password", password);
        params.put("account", EncryptUrlPara.encrypt(account));
        params.put("password", EncryptUrlPara.encrypt(password));
        params.put("md5Flag", "true");
        client.get(DataUrlContents.SERVER_HOST + DataUrlContents.user_login, params, new BaseJsonHttpResponseHandler<Result<User, ShopCommonInfo>>() {


            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Result<User, ShopCommonInfo> response) {
                Log.d(LoginSystemActivity.class.getName(), statusCode + "");
                re_error_login.setVisibility(View.GONE);
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
                re_error_login.setVisibility(View.VISIBLE);
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
     * 点击重试
     */
    @Click(R.id.re_to_login)
    void onReLoading(){
        isFirst();
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


    @Override
    public void onDestroy(){
        super.onDestroy();
        pDialog.dismiss();
    }
}




