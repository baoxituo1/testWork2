package com.trade.bluehole.trad.activity.reg;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.trade.bluehole.trad.R;
import com.trade.bluehole.trad.activity.BaseActionBarActivity;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_register_step_pwd)
public class RegisterStepPwdActivity extends BaseActionBarActivity {

   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_step_pwd);
    }*/

    @Extra(RegisterStep2Activity.user_phone_number)
    String phoneNumber;//用户传递的手机号码
    @ViewById(R.id.reg_input_pwd)
    EditText pwd;//用户输入的密码
    @ViewById(R.id.reg_input_pwd_repeated)
    EditText pwdRepeat;//用户重复输入的密码


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_register_step_pwd, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_next) {
            if(null!=pwd&&!"".equals(pwd.getText().toString())){
                if(pwd.getText().toString().length()<4){
                    Toast.makeText(RegisterStepPwdActivity.this, "密码长度不能小于4位", Toast.LENGTH_SHORT).show();
                }else {
                    if (null != pwdRepeat && !"".equals(pwdRepeat.getText().toString())) {
                        if (pwd.getText().toString().equals(pwdRepeat.getText().toString())) {//如果相同
                            Intent intent = RegisterShopCreateActivity_.intent(this).get();
                            intent.putExtra(RegisterStep2Activity.user_phone_number, phoneNumber);
                            intent.putExtra(RegisterStep2Activity.user_pwd_number, pwd.getText().toString());
                            startActivity(intent);
                        } else {
                            Toast.makeText(RegisterStepPwdActivity.this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RegisterStepPwdActivity.this, "请重复输入密码", Toast.LENGTH_SHORT).show();
                    }
                }
            }else{
                Toast.makeText(RegisterStepPwdActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
