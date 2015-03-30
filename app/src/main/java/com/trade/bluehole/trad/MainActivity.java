package com.trade.bluehole.trad;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.ViewsById;


@EActivity(R.layout.activity_main)
public class MainActivity extends ActionBarActivity {

    @ViewById
    TextView hello_text;

    final static int REQUEST_CODE=12;

   /* @ViewsById(R.id.hello_text)
    TextView hello_text;*/
   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }*/


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void onBackPressed(){
        Toast.makeText(this,"back pressed",Toast.LENGTH_SHORT).show();
    }

    @Click
    void loginButtonClicked(){
        Toast.makeText(this,"登陆后台 pressed",Toast.LENGTH_SHORT).show();
        LoginSystemActivity_.intent(this).startForResult(12);
    }

    @Click(R.id.productGridViewButton)
    void loadProductData(){
        ProductManagerActivity_.intent(this).start();
    }

    @Click(R.id.shopConfigButton)
    void configShopInfo(){
        ShopConfigActivity_.intent(this).start();
    }

    @Click(R.id.userInfoButton)
    void loadUserInfo(){
        UserInfoActivity_.intent(this).start();
    }

    @Click(R.id.addProductButton)
    void addProduct(){
        NewProductActivity_.intent(this).start();
    }


    @OnActivityResult(12)
    void onResult(int resultCode, Intent data) {
       String name= data.getStringExtra("result");
        hello_text.setText("欢迎:"+name);
    }
}
