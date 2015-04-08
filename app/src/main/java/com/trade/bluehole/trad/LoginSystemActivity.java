package com.trade.bluehole.trad;



import android.app.Activity;

import android.content.Intent;
import android.preference.PreferenceActivity;

import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.*;
import com.trade.bluehole.trad.entity.User;
import com.trade.bluehole.trad.entity.shop.ShopCommonInfo;
import com.trade.bluehole.trad.util.MyApplication;
import com.trade.bluehole.trad.util.Result;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;


/**
 * A login screen that offers login via email/password and via Google+ sign in.
 * <p/>
 * ************ IMPORTANT SETUP NOTES: ************
 * In order for Google+ sign in to work with your app, you must first go to:
 * https://developers.google.com/+/mobile/android/getting-started#step_1_enable_the_google_api
 * and follow the steps in "Step 1" to create an OAuth 2.0 client for your package.
 */
@EActivity(R.layout.activity_login_system)
public class LoginSystemActivity extends ActionBarActivity {

    Gson gson = new Gson();

    @App
    MyApplication myapplication;

    AsyncHttpClient client = new AsyncHttpClient();

    @ViewById(R.id.userName)
    EditText account;

    @ViewById(R.id.userPassword)
    EditText password;

    @Click(R.id.login_user_login_btn)
    void loginButtonClick(){
       // Toast.makeText(this,"account:"+account.getText()+",password:"+password.getText(),Toast.LENGTH_SHORT).show();
        RequestParams params=new RequestParams();
        params.put("account",account.getText());
        params.put("password",password.getText());
        client.get("http://192.168.1.161:8080/qqt_up/shopjson/userLogin.do", params, new BaseJsonHttpResponseHandler<Result<User,ShopCommonInfo>>() {


            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Result<User,ShopCommonInfo> response) {
                Log.d(LoginSystemActivity.class.getName(), statusCode + "");
                if (null != response) {
                    if (response.isSuccess()) {
                        Toast.makeText(LoginSystemActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                       // HashMap u= (HashMap)response.getObj();
                       // User user=new User();
                        //user.setAccount(u.get("account").toString());
                       // user.setUserCode(u.get("userCode").toString());
                        myapplication.setUser(response.getBzseObj());
                        myapplication.setShop(response.getObj());
                        //数据是使用Intent返回
                       // Intent intent = new Intent(this,MainActivity_);
                        // 把返回数据存入Intent
                        //intent.putExtra("result",response.getObj().getAccount());
                        // 设置返回数据
                        //LoginSystemActivity.this.setResult(RESULT_OK, intent);
                        // 关闭Activity
                       // LoginSystemActivity.this.finish();
                        MainActivity_.intent(LoginSystemActivity.this).start();
                    } else {
                        Toast.makeText(LoginSystemActivity.this, "登陆失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Result<User,ShopCommonInfo> errorResponse) {

            }

            @Override
            protected Result<User,ShopCommonInfo> parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
               // return new ObjectMapper().readValues(new JsonFactory().createParser(rawJsonData), Result.class).next();
                return gson.fromJson(rawJsonData, new TypeToken<Result<User,ShopCommonInfo>>(){}.getType());
            }
        });
    }


    @Click(R.id.register_user_account)
    void registerAccountOnClick(){
        RegisterManageActivity_.intent(this).start();
    }

    protected final void debugResponse(String TAG, String response) {
        if (response != null) {
            Log.d(TAG, "Response data:");
            Log.d(TAG, response);
        }
    }
}



