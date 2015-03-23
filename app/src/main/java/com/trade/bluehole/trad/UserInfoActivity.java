package com.trade.bluehole.trad;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.trade.bluehole.trad.entity.ProductIndexVO;
import com.trade.bluehole.trad.entity.User;
import com.trade.bluehole.trad.entity.UserBase;
import com.trade.bluehole.trad.util.MyApplication;
import com.trade.bluehole.trad.util.Result;
import com.yalantis.phoenix.PullToRefreshView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.ViewsById;
import org.apache.http.Header;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EActivity(R.layout.activity_user_info)
public class UserInfoActivity extends ActionBarActivity {
        @App
        MyApplication myapplication;
        public static final int REFRESH_DELAY = 2000;
        AsyncHttpClient client = new AsyncHttpClient();
        Gson gson = new Gson();
        private PullToRefreshView mPullToRefreshView;

        @ViewById(R.id.account)
        EditText accountName;
        @ViewById(R.id.nickName)
        EditText nickName;
        @ViewById(R.id.realName)
        EditText realName;
        @ViewById
        EditText mobile;
        @ViewById
        ImageView user_head_img;

         @AfterViews
         void initView(){

             mPullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
             mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
                 @Override
                 public void onRefresh() {
                     mPullToRefreshView.postDelayed(new Runnable() {
                         @Override
                         public void run() {
                             mPullToRefreshView.setRefreshing(false);
                         }
                     }, REFRESH_DELAY);
                 }
             });

             User user= myapplication.getUser();
             if(user!=null&&user.getUserCode()!=null){
                 RequestParams params=new RequestParams();
                 params.put("userCode",user.getUserCode());
                 params.put("pageSize",500);
                 client.get("http://192.168.1.161:8080/qqt_up/shopjson/showUserMsgJson.do", params, new BaseJsonHttpResponseHandler<Result<UserBase,User >>() {


                     @Override
                     public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Result<UserBase,User > response) {
                         Log.d(LoginSystemActivity.class.getName(), statusCode + "");
                         if (null != response) {
                             if (response.isSuccess()) {
                                 Toast.makeText(UserInfoActivity.this, "获取数据成功", Toast.LENGTH_SHORT).show();
                                 UserBase ub=response.getBzseObj();
                                 User u=response.getObj();
                                 accountName.setText(u.getAccount());
                                 nickName.setText(ub.getNickName());
                                 mobile.setText(ub.getMobile());
                                 realName.setText(ub.getRealName());
                                 ImageLoader.getInstance().displayImage("http://192.168.1.108:800/"+ub.getHeadBigImage(),user_head_img);
                             } else {
                                 Toast.makeText(UserInfoActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                             }
                         }
                     }

                     @Override
                     public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Result<UserBase,User > errorResponse) {

                     }

                     @Override
                     protected Result<UserBase,User > parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                         // return new ObjectMapper().readValues(new JsonFactory().createParser(rawJsonData), Result.class).next();
                         return gson.fromJson(rawJsonData, new TypeToken<Result<UserBase,User >>(){}.getType());
                     }
                 });
             }
         }





}
