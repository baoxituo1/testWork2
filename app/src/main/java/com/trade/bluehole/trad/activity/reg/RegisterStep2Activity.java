package com.trade.bluehole.trad.activity.reg;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.trade.bluehole.trad.LoginSystemActivity_;
import com.trade.bluehole.trad.R;
import com.trade.bluehole.trad.RegisterManageActivity;
import com.trade.bluehole.trad.entity.JsonResult;
import com.trade.bluehole.trad.util.RegisterCodeTimer;
import com.trade.bluehole.trad.util.data.DataUrlContents;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.apache.http.Header;

import cn.pedant.SweetAlert.SweetAlertDialog;
import mehdi.sakout.fancybuttons.FancyButton;

@EActivity(R.layout.activity_register_step2)
public class RegisterStep2Activity extends ActionBarActivity {

    public final static String user_phone_number="userPhoneNumber";
    public final static String user_yzm_number="userYzmNumber";
    public final static String user_pwd_number="userPwdNumber";

    Gson gson = new Gson();
    //弹出框
    SweetAlertDialog pDialog;
    AsyncHttpClient client = new AsyncHttpClient();
   /*  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_step2);
    }*/
    @Extra(user_phone_number)
    String phoneNumber;//手机号码
    @Extra(user_yzm_number)
    String yzmNumber;//验证码
    @ViewById
    EditText reg_yzm;//用户输入的验证码
    @ViewById(R.id.reg_phone_number_notice)
    TextView phoneNotice;//提示用户号码
    @ViewById(R.id.btn_r_send_yzm)
    FancyButton btnSendYzm;//重新发送验证码
    RegisterCodeTimer mCodeTimer;//倒计时
    private MyCount mc;

    @AfterViews
    void initData(){
        btnSendYzm.setEnabled(false);
        phoneNotice.setText("+" + phoneNumber);
        if(null==mCodeTimer){
           // mCodeTimer = new RegisterCodeTimer(60000, 1000, mCodeHandler);
           // mCodeTimer.start();
        }
        mc = new MyCount(60000, 1000);
        mc.start();
    }


    @Click(R.id.btn_r_send_yzm)
    void onSendYzmClick(){
        sendYzm(phoneNumber);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_register_step2, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_next) {
            if(null!=yzmNumber&&!"".equals(yzmNumber)){//传递过来的验证码不为空
                if(null!=reg_yzm&&!"".equals(reg_yzm.getText().toString())){
                    if(yzmNumber.equals(reg_yzm.getText().toString())){//相同的话继续下一步
                        Intent intent= RegisterStepPwdActivity_.intent(this).get();
                        intent.putExtra(user_phone_number,phoneNumber);
                        startActivity(intent);
                    }else{
                        Toast.makeText(RegisterStep2Activity.this, "验证码输入错误", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(RegisterStep2Activity.this, "验证码不能为空", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(RegisterStep2Activity.this, "参数错误", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    /**
     * 发送验证码
     */
    void sendYzm(String phoneCode){
        RequestParams params=new RequestParams();
        params.put("phoneCode", phoneCode);
        client.get(DataUrlContents.SERVER_HOST+DataUrlContents.send_phone_yzm, params, new BaseJsonHttpResponseHandler<JsonResult>() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, JsonResult response) {
                if (null != response) {
                    if (response.isSuccess()) {
                        Toast.makeText(RegisterStep2Activity.this, "发送成功", Toast.LENGTH_SHORT).show();
                        yzmNumber=response.getCode();
                        mc.start();
                    } else {
                        Toast.makeText(RegisterStep2Activity.this, "发送失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, JsonResult errorResponse) {
                Toast.makeText(RegisterStep2Activity.this, "服务器繁忙", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected JsonResult parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                // return new ObjectMapper().readValues(new JsonFactory().createParser(rawJsonData), Result.class).next();
                return gson.fromJson(rawJsonData, JsonResult.class);
            }
        });
    }

  /*  *//**
     * 倒计时Handler
     *//*
    @SuppressLint("HandlerLeak")
    Handler mCodeHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == RegisterCodeTimer.IN_RUNNING) {// 正在倒计时
                btnSendYzm.setText(msg.obj.toString());
            } else if (msg.what == RegisterCodeTimer.END_RUNNING) {// 完成倒计时
                btnSendYzm.setEnabled(true);
                btnSendYzm.setText(msg.obj.toString());
            }
        };
    };*/

    /*定义一个倒计时的内部类*/
    class MyCount extends CountDownTimer {
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onFinish() {
            btnSendYzm.setText("获取验证码");
            btnSendYzm.setEnabled(true);
        }
        @Override
        public void onTick(long millisUntilFinished) {
            btnSendYzm.setText(millisUntilFinished / 1000 + "s 后重发");
            //Toast.makeText(RegisterStep2Activity.this, millisUntilFinished / 1000 + "", Toast.LENGTH_LONG).show();//toast有显示时间延迟
        }
    }
}
