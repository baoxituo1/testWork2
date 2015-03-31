package com.trade.bluehole.trad.activity.shop;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.trade.bluehole.trad.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

/**
 * 店铺地址设置
 */
@EActivity(R.layout.activity_shop_address_config)
public class ShopAddressConfigActivity extends ActionBarActivity {
    //商品和店铺编码标志
    public static final String SHOP_CODE_EXTRA = "shopCode";
    public static final String SHOP_latitude_EXTRA = "latitude";
    public static final String SHOP_longitude_EXTRA = "longitude";
    public static final String SHOP_provinceName_EXTRA = "provinceNameExr";
    public static final String SHOP_cityNameName_EXTRA = "cityNameNameExr";
    public static final String SHOP_districtName_EXTRA = "districtNameExr";
    public static final String SHOP_address_EXTRA = "addressNameExr";

    @Extra(SHOP_CODE_EXTRA)
    String shopCode;
    @Extra(SHOP_latitude_EXTRA)
    Double latitude;
    @Extra(SHOP_longitude_EXTRA)
    Double longitude;
    @Extra(SHOP_provinceName_EXTRA)
    String provinceNameExr;
    @Extra(SHOP_cityNameName_EXTRA)
    String cityNameNameExr;
    @Extra(SHOP_districtName_EXTRA)
    String districtNameExr;
    @Extra(SHOP_address_EXTRA)
    String addressNameExr;


    @ViewById
    TextView provinceShopName,cityName,districtName,addressName;

    /**
     * 初始化数据
     */
    @AfterViews
    void initData(){
        provinceShopName.setText(provinceNameExr);
        cityName.setText(cityNameNameExr);
        districtName.setText(districtNameExr);
        addressName.setText(addressNameExr);
    }

    @Click(R.id.shopRelocationBtn)
    void onRelocationClick(){
        Intent intent=new Intent(this,ShopLocationActivity.class);
        this.startActivity(intent);
    }
}
