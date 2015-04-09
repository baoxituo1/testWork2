package com.trade.bluehole.trad;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.trade.bluehole.trad.activity.reg.RegisterStep2Activity;
import com.trade.bluehole.trad.activity.reg.RegisterStep2Activity_;
import com.trade.bluehole.trad.entity.JsonResult;
import com.trade.bluehole.trad.entity.User;
import com.trade.bluehole.trad.entity.shop.ShopCommonInfo;
import com.trade.bluehole.trad.util.RegexValidateUtil;
import com.trade.bluehole.trad.util.Result;
import com.trade.bluehole.trad.util.data.DataUrlContents;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.apache.http.Header;

import cn.pedant.SweetAlert.SweetAlertDialog;

@EActivity(R.layout.activity_register_manage)
public class RegisterManageActivity extends ActionBarActivity {

   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_manage);
    }*/

    @ViewById
    EditText reg_phone_number;//用户输入的手机号码
    Gson gson = new Gson();
    //弹出框
    SweetAlertDialog pDialog;
    AsyncHttpClient client = new AsyncHttpClient();


    @AfterViews
    void initData(){
        pDialog=new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("确定要发送到?")
                .setContentText("13888888888!")
                .setConfirmText("确定,发送")
                .setCancelText("不,取消")
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        pDialog.hide();
                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        // sDialog.dismissWithAnimation();
                        pDialog.hide();
                        sendYzmAndGoNext(reg_phone_number.getText().toString());
                    }
                });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_register_manage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_next) {
            String _phone=reg_phone_number.getText().toString();
            if(null!=reg_phone_number&&!"".equals(_phone)){
                if(RegexValidateUtil.checkMobileNumber(_phone)){
                    pDialog.setContentText(_phone);
                    pDialog.show();
                }else{
                    Toast.makeText(RegisterManageActivity.this,"请输入正确的手机号",Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(RegisterManageActivity.this,"请输入手机号",Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 发送验证码并且跳转
     */
    void sendYzmAndGoNext(String phoneCode){
        RequestParams params=new RequestParams();
        params.put("phoneCode", phoneCode);
        client.get(DataUrlContents.SERVER_HOST+DataUrlContents.send_phone_yzm, params, new BaseJsonHttpResponseHandler<JsonResult>() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, JsonResult response) {
                if (null != response) {
                    if (response.isSuccess()) {
                        Intent intent= RegisterStep2Activity_.intent(RegisterManageActivity.this).get();
                        intent.putExtra(RegisterStep2Activity.user_phone_number,reg_phone_number.getText().toString());
                        intent.putExtra(RegisterStep2Activity.user_yzm_number, response.getCode());
                        startActivity(intent);
                    } else {
                        if("104".equals(response.getCode())){
                            //Toast.makeText(RegisterManageActivity.this, "您的手机号码已经被注册", Toast.LENGTH_SHORT).show();
                            //弹出提示
                            new SweetAlertDialog(RegisterManageActivity.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("手机号码已经注册!")
                                    .setContentText("是否现在去登录？")
                                    .setConfirmText("去登录!")
                                    .setCancelText("不")
                                    .showCancelButton(true)
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            pDialog.hide();
                                            LoginSystemActivity_.intent(RegisterManageActivity.this).start();
                                            finish();
                                        }
                                    }).show();
                        }else{
                            Toast.makeText(RegisterManageActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, JsonResult errorResponse) {
                         Toast.makeText(RegisterManageActivity.this, "服务器繁忙", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected JsonResult parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                // return new ObjectMapper().readValues(new JsonFactory().createParser(rawJsonData), Result.class).next();
                return gson.fromJson(rawJsonData, JsonResult.class);
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        pDialog.dismiss();
    }
}
