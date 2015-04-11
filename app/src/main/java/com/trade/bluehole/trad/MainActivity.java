package com.trade.bluehole.trad;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.trade.bluehole.trad.entity.User;
import com.trade.bluehole.trad.entity.shop.ShopCommonInfo;
import com.trade.bluehole.trad.util.ImageManager;
import com.trade.bluehole.trad.util.MyApplication;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import de.hdodenhof.circleimageview.CircleImageView;


@EActivity(R.layout.activity_main)
public class MainActivity extends ActionBarActivity {

    @ViewById
    TextView shopName;
    @ViewById
    CircleImageView shop_logo_image;
    @App
    MyApplication myApplication;
    final static int REQUEST_CODE=12;
    /**
     * 登陆信息
     */
    ShopCommonInfo shop;
    User user;







    /**
     * 初始化
     */
    @AfterViews
    void iniData(){
        user=myApplication.getUser();
        shop=myApplication.getShop();
        if(shop!=null){
            shopName.setText(shop.getTitle());
            if(null!=shop.getShopLogo()){
                ImageManager.imageLoader.displayImage("http://125.oss-cn-beijing.aliyuncs.com/" + shop.getShopLogo(),shop_logo_image,ImageManager.options);
            }
        }
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

    @Click(R.id.shopMainButton)
    void shopMainButton(){
       /* Intent intent=new Intent(this,SuperMainActivity.class);
        startActivity(intent);*/
        SuperMainActivity_.intent(this).start();
    }

    @Click(R.id.addProductButton)
    void addProduct(){
        NewProductActivity_.intent(this).start();
    }
    @Click(R.id.newMain)
    void newMainClick(){
        //Intent intent=new Intent(this,HeaderAnimatorActivity.class);
       // startActivity(intent);
        HeaderAnimatorActivity_.intent(this).start();
    }


    @OnActivityResult(12)
    void onResult(int resultCode, Intent data) {
       String name= data.getStringExtra("result");
    }


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
}
