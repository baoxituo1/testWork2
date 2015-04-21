package com.trade.bluehole.trad;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.mbaas.oss.OSSClient;
import com.aliyun.mbaas.oss.callback.SaveCallback;
import com.aliyun.mbaas.oss.model.OSSException;
import com.aliyun.mbaas.oss.model.TokenGenerator;
import com.aliyun.mbaas.oss.storage.OSSBucket;
import com.aliyun.mbaas.oss.storage.OSSData;
import com.aliyun.mbaas.oss.storage.OSSFile;
import com.aliyun.mbaas.oss.util.OSSLog;
import com.aliyun.mbaas.oss.util.OSSToolKit;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.soundcloud.android.crop.Crop;
import com.trade.bluehole.trad.activity.BaseActionBarActivity;
import com.trade.bluehole.trad.activity.shop.ShopAddressConfigActivity;
import com.trade.bluehole.trad.activity.shop.ShopAddressConfigActivity_;
import com.trade.bluehole.trad.activity.shop.ShopGroundConfigActivity;
import com.trade.bluehole.trad.activity.shop.ShopGroundConfigActivity_;
import com.trade.bluehole.trad.activity.shop.ShopNameConfigActivity;
import com.trade.bluehole.trad.activity.shop.ShopNameConfigActivity_;
import com.trade.bluehole.trad.activity.shop.ShopSloganConfigActivity;
import com.trade.bluehole.trad.activity.shop.ShopSloganConfigActivity_;
import com.trade.bluehole.trad.entity.User;
import com.trade.bluehole.trad.entity.photo.Photo;
import com.trade.bluehole.trad.entity.pro.ProductResultVO;
import com.trade.bluehole.trad.entity.shop.ShopCommonInfo;
import com.trade.bluehole.trad.util.ImageManager;
import com.trade.bluehole.trad.util.MyApplication;
import com.trade.bluehole.trad.util.StreamUtil;
import com.trade.bluehole.trad.util.data.DataUrlContents;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.apache.http.Header;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.UUID;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 店铺设置
 */
@EActivity(R.layout.activity_shop_config)
public class ShopConfigActivity extends BaseActionBarActivity {
    //店铺信息
    String shopLogoFleName=null;
    ShopCommonInfo shopInfo=null;
    User user=null;
    //页面进度条
    SweetAlertDialog pDialog;
    @App
    MyApplication myapplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.title);
    }


    @AfterViews
    void initData(){
        //获取用户
        user=myapplication.getUser();
        //初始化等待dialog
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Loading");
            pDialog.setCancelable(false);
            pDialog.show();
        //加载数据
        if(null!=user){
            loadServerData();
        }
    }

    @ViewById
    ImageView shopLogoImage;
    @ViewById
    TextView shopName,shopSlogan,shopAddress;



    /**
     * 装载数据根据商品编码和商铺编码
     */
    void loadServerData(){
        RequestParams params=new RequestParams();
        params.put("userCode", user.getUserCode());
        getClient().get(DataUrlContents.SERVER_HOST+DataUrlContents.load_user_shop_info, params, new BaseJsonHttpResponseHandler<ShopCommonInfo>() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, ShopCommonInfo response) {
                Log.d(NewProductActivity.class.getName(), response.toString());
                if (null != response) {
                    doInUiThread(response);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, ShopCommonInfo errorResponse) {
            }

            @Override
            protected ShopCommonInfo parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return gson.fromJson(rawJsonData,ShopCommonInfo.class);
            }
        });
    }

    /**
     * 后台线程实例化组件
     * @param obj
     */
    @UiThread
    void doInUiThread(ShopCommonInfo obj) {
        if(null!=obj){
            shopInfo=obj;
            //加载店铺logo
            if(null!=obj.getShopLogo()){
                ImageManager.imageLoader.displayImage(DataUrlContents.IMAGE_HOST + obj.getShopLogo()+DataUrlContents.img_logo_img,shopLogoImage,ImageManager.options);
                shopLogoFleName=obj.getShopLogo();
            }
            shopName.setText(obj.getTitle());
            shopSlogan.setText(obj.getSlogan());
            shopAddress.setText(obj.getAddress());
            //隐藏进度条
            pDialog.hide();
        }
    }

    /**
     * 当设置店铺logo被点击
     */
    @Click(R.id.shopLogoLayout)
    void onShopLogoLayoutClick() {
        shopLogoImage.setImageDrawable(null);
        Crop.pickImage(this);
    }

    /**
     * 修改店铺名称
     */
    @Click(R.id.shopNameLayout)
    void updateShopNameClick(){
        Intent intent= ShopNameConfigActivity_.intent(this).get();
        intent.putExtra(ShopNameConfigActivity.SHOP_CODE_EXTRA,user.getShopCode());
        intent.putExtra(ShopNameConfigActivity.SHOP_NAME_EXTRA, shopInfo.getTitle());
        startActivityForResult(intent, 14);
    }
    /**
     * 修改店铺公告
     */
    @Click(R.id.shopSloganLayout)
    void updateShopSloganClick(){
        Intent intent= ShopSloganConfigActivity_.intent(this).get();
        intent.putExtra(ShopSloganConfigActivity.SHOP_CODE_EXTRA,user.getShopCode());
        intent.putExtra(ShopSloganConfigActivity.SHOP_SLOGAN_EXTRA, shopInfo.getSlogan());
        startActivityForResult(intent, 15);
    }
    /**
     * 修改店铺公告
     */
    @Click(R.id.shopGroundLayout)
    void updateShopGroundClick(){
        Intent intent= ShopGroundConfigActivity_.intent(this).get();
        intent.putExtra(ShopGroundConfigActivity.SHOP_CODE_EXTRA,user.getShopCode());
        intent.putExtra(ShopGroundConfigActivity.SHOP_USER_EXTRA,user.getUserCode());
        intent.putExtra(ShopGroundConfigActivity.SHOP_GROUND_EXTRA, shopInfo.getShopBackground());
        startActivity(intent);
    }
    /**
     * 修改店铺地址
     */
    @Click(R.id.shopAddressLayout)
    void updateShopAddressClick(){
        Intent intent= ShopAddressConfigActivity_.intent(this).get();
        intent.putExtra(ShopAddressConfigActivity.SHOP_CODE_EXTRA,user.getShopCode());
        intent.putExtra(ShopAddressConfigActivity.SHOP_latitude_EXTRA,shopInfo.getLatitude());
        intent.putExtra(ShopAddressConfigActivity.SHOP_longitude_EXTRA, shopInfo.getLongitude());
        intent.putExtra(ShopAddressConfigActivity.SHOP_provinceName_EXTRA, shopInfo.getProvinceName());
        intent.putExtra(ShopAddressConfigActivity.SHOP_cityNameName_EXTRA, shopInfo.getCityName());
        intent.putExtra(ShopAddressConfigActivity.SHOP_districtName_EXTRA, shopInfo.getDistrict());
        intent.putExtra(ShopAddressConfigActivity.SHOP_address_EXTRA, shopInfo.getAddress());
        startActivityForResult(intent, 17);
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
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(result.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, result);
        }else if(requestCode==14&&resultCode == RESULT_OK){
            if (result != null)
            {
                String _shopName = result.getStringExtra(ShopNameConfigActivity.SHOP_NAME_EXTRA);
                ShopCommonInfo sc=new ShopCommonInfo();
                sc.setTitle(_shopName);
                sc.setShopCode(user.getShopCode());
                saveDataToServer(sc);
            }
        }else if(requestCode==15&&resultCode == RESULT_OK){
            if (result != null)
            {
                String _shopSlogan= result.getStringExtra(ShopSloganConfigActivity.SHOP_SLOGAN_EXTRA);
                ShopCommonInfo sc=new ShopCommonInfo();
                sc.setSlogan(_shopSlogan);
                sc.setShopCode(user.getShopCode());
                saveDataToServer(sc);
            }
        }
    }

    /**
     * 开始裁剪
     *
     * @param source
     */
    private void beginCrop(Uri source) {
        Uri outputUri = Uri.fromFile(new File(getCacheDir(), "cropped"));
        new Crop(source).output(outputUri).asSquare().start(this);
    }

    /**
     * 接收结果
     *
     * @param resultCode
     * @param result
     */
    private void handleCrop(int resultCode, Intent result) {
        ContentResolver resolver = getContentResolver();
        if (resultCode == RESULT_OK) {
            Uri uri=Crop.getOutput(result);
            shopLogoImage.setImageURI(uri);
            try {
                byte[] bytes= StreamUtil.readStream(resolver.openInputStream(uri));
                doUploadFile(bytes,shopLogoFleName);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 上传代码
     * @param data
     * @throws Exception
     */
    public void doUploadFile(byte[] data,String fileName) throws Exception {
        if(fileName==null||"".equals(fileName)){
             fileName= "shop_logo/"+"logo_"+ UUID.randomUUID()+".jpg";
        }
        OSSData ossData = new OSSData(myapplication.getOssBucket(), fileName);
        ossData.setData(data, "raw"); // 指定需要上传的数据和它的类型
        ossData.enableUploadCheckMd5sum(); // 开启上传MD5校验
        ossData.uploadInBackground(new SaveCallback() {
            @Override
            public void onSuccess(String objectKey) {

            }
            @Override
            public void onProgress(String objectKey, int byteCount, int totalSize) {

            }
            @Override
            public void onFailure(String objectKey, OSSException ossException) {

            }
        });
    }


    /**
     * 向服务器推送数据更新店铺信息
     * @param obj
     */
    void saveDataToServer(final ShopCommonInfo obj){
        pDialog.show();
        RequestParams params=new RequestParams();
        params.put("userCode", user.getUserCode());
        params.put("shopCode", user.getShopCode());
        if(null!=obj){
            if(null!=obj.getTitle()&&!"".equals(obj.getTitle())){
                params.put("title", obj.getTitle());
                params.put("shopName", obj.getTitle());
            }
            if(null!=obj.getSlogan()&&!"".equals(obj.getSlogan())){
                params.put("slogan", obj.getSlogan());
            }
        }
        getClient().post(DataUrlContents.SERVER_HOST + DataUrlContents.update_shop_config, params, new BaseJsonHttpResponseHandler<String>() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, String response) {
                pDialog.hide();
                Log.d(NewProductActivity.class.getName(), response.toString());
                if (null != response) {
                    if ("success".equals(response)) {
                        String message = "";
                        if (null != obj.getTitle() && !"".equals(obj.getTitle())) {
                            message = "修改店铺名称!";
                            shopName.setText(obj.getTitle());
                        } else if (null != obj.getSlogan() && !"".equals(obj.getSlogan())) {
                            message = "修改店铺公告!";
                            shopSlogan.setText(obj.getSlogan());
                        }
                        new SweetAlertDialog(ShopConfigActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("同步成功!")
                                .setContentText(message)
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }else if(id==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
