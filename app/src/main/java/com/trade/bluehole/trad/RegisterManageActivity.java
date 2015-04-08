package com.trade.bluehole.trad;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.trade.bluehole.trad.activity.reg.RegisterStep2Activity;
import com.trade.bluehole.trad.activity.reg.RegisterStep2Activity_;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_register_manage)
public class RegisterManageActivity extends ActionBarActivity {

   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_manage);
    }*/

    @ViewById
    EditText reg_phone_number;//用户输入的手机号码

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_register_manage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_next) {
            if(null!=reg_phone_number&&!"".equals(reg_phone_number.getText().toString())){
                Intent intent= RegisterStep2Activity_.intent(this).get();
                intent.putExtra(RegisterStep2Activity.user_phone_number,reg_phone_number.getText().toString());
                intent.putExtra(RegisterStep2Activity.user_yzm_number,"2580");
                startActivity(intent);
            }else{
                Toast.makeText(RegisterManageActivity.this,"请输入手机号",Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
