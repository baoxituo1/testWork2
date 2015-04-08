package com.trade.bluehole.trad.activity.reg;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.trade.bluehole.trad.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_register_step2)
public class RegisterStep2Activity extends ActionBarActivity {

    public final static String user_phone_number="userPhoneNumber";
    public final static String user_yzm_number="userYzmNumber";
    public final static String user_pwd_number="userPwdNumber";
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

    @AfterViews
    void initData(){
        phoneNotice.setText("+"+phoneNumber);
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
}
