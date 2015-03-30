package com.trade.bluehole.trad.activity.shop;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.trade.bluehole.trad.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_shop_slogan_config)
public class ShopSloganConfigActivity extends ActionBarActivity {

    //商品和店铺编码标志
    public static final String SHOP_CODE_EXTRA = "shopCode";
    public static final String SHOP_SLOGAN_EXTRA = "shopSlogan";

    @Extra(SHOP_CODE_EXTRA)
    String shopCode;
    @Extra(SHOP_SLOGAN_EXTRA)
    String shopSloganString;

    @ViewById
    EditText shopSlogan;

    @AfterViews
    void initData(){
        shopSlogan.setText(shopSloganString);
    }

    @Click(R.id.shopSloganDoneBtn)
    void saveShopSlogan(){
        Intent intent = new Intent();
        // 把返回数据存入Intent
        intent.putExtra(ShopSloganConfigActivity.SHOP_SLOGAN_EXTRA,shopSlogan.getText().toString());
        // 设置返回数据
        ShopSloganConfigActivity.this.setResult(RESULT_OK, intent);
        // 关闭Activity
        ShopSloganConfigActivity.this.finish();



    }
}
