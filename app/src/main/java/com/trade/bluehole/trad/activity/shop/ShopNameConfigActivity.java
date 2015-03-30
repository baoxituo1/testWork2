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

@EActivity(R.layout.activity_shop_name_config)
public class ShopNameConfigActivity extends ActionBarActivity {
    //商品和店铺编码标志
    public static final String SHOP_CODE_EXTRA = "shopCode";
    public static final String SHOP_NAME_EXTRA = "shopName";

    @Extra(SHOP_CODE_EXTRA)
    String shopCode;
    @Extra(SHOP_NAME_EXTRA)
    String shopNameString;

    @ViewById
    EditText shopName;

    @AfterViews
    void initData(){
        shopName.setText(shopNameString);
    }

    @Click(R.id.shopNameDoneBtn)
    void saveShopName(){
        Intent intent = new Intent();
        // 把返回数据存入Intent
        intent.putExtra(ShopNameConfigActivity.SHOP_NAME_EXTRA,shopName.getText().toString());
        // 设置返回数据
        ShopNameConfigActivity.this.setResult(RESULT_OK, intent);
        // 关闭Activity
        ShopNameConfigActivity.this.finish();

    }
}
