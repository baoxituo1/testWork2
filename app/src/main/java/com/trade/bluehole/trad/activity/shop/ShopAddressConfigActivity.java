package com.trade.bluehole.trad.activity.shop;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.soundcloud.android.crop.Crop;
import com.trade.bluehole.trad.NewProductActivity;
import com.trade.bluehole.trad.R;
import com.trade.bluehole.trad.activity.BaseActionBarActivity;
import com.trade.bluehole.trad.entity.User;
import com.trade.bluehole.trad.entity.shop.ShopCommonInfo;
import com.trade.bluehole.trad.util.MyApplication;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.apache.http.Header;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 店铺地址设置
 */
@EActivity(R.layout.activity_shop_address_config)
public class ShopAddressConfigActivity extends BaseActionBarActivity {
    //商品和店铺编码标志
    public static final String SHOP_CODE_EXTRA = "shopCode";
    public static final String SHOP_latitude_EXTRA = "latitude";
    public static final String SHOP_longitude_EXTRA = "longitude";
    public static final String SHOP_provinceName_EXTRA = "provinceNameExr";
    public static final String SHOP_cityNameName_EXTRA = "cityNameNameExr";
    public static final String SHOP_districtName_EXTRA = "districtNameExr";
    public static final String SHOP_address_EXTRA = "addressNameExr";
    User user=null;
    //页面进度条
    SweetAlertDialog pDialog;
    @App
    MyApplication myapplication;

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
        //获取用户
        user=myapplication.getUser();
        //初始化等待dialog
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
       // pDialog.show();
        provinceShopName.setText(provinceNameExr);
        cityName.setText(cityNameNameExr);
        districtName.setText(districtNameExr);
        addressName.setText(addressNameExr);
    }

    @Click(R.id.shopRelocationBtn)
    void onRelocationClick(){
        Intent intent=new Intent(this,ShopLocationActivity.class);
        this.startActivityForResult(intent,171);
    }

    /**
     * 接受Activity结果
     *
     * @param requestCode
     * @param resultCode
     * @param result
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
       if(requestCode==171&&resultCode == RESULT_OK){
            if (result != null)
            {
                 latitude = result.getDoubleExtra(ShopAddressConfigActivity.SHOP_latitude_EXTRA,0);
                 longitude = result.getDoubleExtra(ShopAddressConfigActivity.SHOP_longitude_EXTRA,0);
                 provinceNameExr = result.getStringExtra(ShopAddressConfigActivity.SHOP_provinceName_EXTRA);
                 cityNameNameExr = result.getStringExtra(ShopAddressConfigActivity.SHOP_cityNameName_EXTRA);
                 districtNameExr = result.getStringExtra(ShopAddressConfigActivity.SHOP_districtName_EXTRA);
                 addressNameExr = result.getStringExtra(ShopAddressConfigActivity.SHOP_address_EXTRA);
                 provinceShopName.setText(provinceNameExr);
                 cityName.setText(cityNameNameExr);
                 districtName.setText(districtNameExr);
                 addressName.setText(addressNameExr);
                 saveDataToServer();
            }
        }
    }

    /**
     * 向服务器推送数据更新店铺信息
     */
    void saveDataToServer(){
        pDialog.show();
        RequestParams params=new RequestParams();
        params.put("userCode", user.getUserCode());
        params.put("shopCode", user.getShopCode());
        if(0!=latitude){
                params.put("latitude", latitude);
        }
        if(0!=longitude){
                params.put("longitude", longitude);
        }
        if(null!="provinceNameExr"&&!"".equals(provinceNameExr)){
                params.put("provinceName", provinceNameExr);
        }
        if(null!="cityNameNameExr"&&!"".equals(cityNameNameExr)){
                params.put("cityName",cityNameNameExr);
        }
        if(null!="districtNameExr"&&!"".equals(districtNameExr)){
                params.put("district", districtNameExr);
        }
        if(null!="addressNameExr"&&!"".equals(addressNameExr)){
                params.put("address", addressNameExr);
        }
        getClient().post("http://192.168.1.161:8080/qqt_up/shopjson/editShop.do", params, new BaseJsonHttpResponseHandler<String>() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, String response) {
                pDialog.hide();
                Log.d(NewProductActivity.class.getName(), response.toString());
                if (null != response) {
                    if("success".equals(response)){
                        new SweetAlertDialog(ShopAddressConfigActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("同步成功!")
                                .setContentText("修改店铺地址信息")
                                .show();
                    }
                    //Toast.makeText(ShopConfigActivity.this, "数据提交：" + response, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, String errorResponse) {
            }

            @Override
            protected String parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return gson.fromJson(rawJsonData, String.class);
            }
        });
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        pDialog.dismiss();
    }
}
