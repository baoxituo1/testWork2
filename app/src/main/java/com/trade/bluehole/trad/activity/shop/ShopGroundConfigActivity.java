package com.trade.bluehole.trad.activity.shop;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.aliyun.mbaas.oss.OSSClient;
import com.aliyun.mbaas.oss.callback.SaveCallback;
import com.aliyun.mbaas.oss.model.OSSException;
import com.aliyun.mbaas.oss.model.TokenGenerator;
import com.aliyun.mbaas.oss.storage.OSSBucket;
import com.aliyun.mbaas.oss.storage.OSSData;
import com.aliyun.mbaas.oss.util.OSSLog;
import com.aliyun.mbaas.oss.util.OSSToolKit;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.soundcloud.android.crop.Crop;
import com.trade.bluehole.trad.NewProductActivity;
import com.trade.bluehole.trad.R;
import com.trade.bluehole.trad.activity.BaseActionBarActivity;
import com.trade.bluehole.trad.entity.shop.ShopCommonInfo;
import com.trade.bluehole.trad.util.ImageManager;
import com.trade.bluehole.trad.util.MyApplication;
import com.trade.bluehole.trad.util.StreamUtil;
import com.trade.bluehole.trad.util.data.DataUrlContents;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.apache.http.Header;

import java.io.File;
import java.util.UUID;

import cn.pedant.SweetAlert.SweetAlertDialog;

@EActivity(R.layout.activity_shop_ground_config)
public class ShopGroundConfigActivity extends BaseActionBarActivity {
    //商品和店铺编码标志
    public static final String SHOP_CODE_EXTRA = "shopCode";
    public static final String SHOP_GROUND_EXTRA = "shopBackGround";
    public static final String SHOP_USER_EXTRA = "userCode";
    //页面进度条
    SweetAlertDialog pDialog;

    @App
    MyApplication myApplication;

    @Extra(SHOP_CODE_EXTRA)
    String shopCode;
    @Extra(SHOP_GROUND_EXTRA)
    String shopBackGroundUrl;
    @Extra(SHOP_USER_EXTRA)
    String userCode;

    /**
     * 背景图片
     */
    @ViewById
    ImageView shopBackGround;


    @AfterViews
    void initData(){
        //阿里云
        OSSLog.enableLog(true);
        OSSClient.setApplicationContext(getApplicationContext()); // 传入应用程序context
        //初始化等待dialog
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        //如果有背景 加载
        if(null!=shopBackGroundUrl){
           // pDialog.show();
            doInUiThread(shopBackGroundUrl);
        }
    }

    /**
     * 后台线程实例化组件
     * @param backGroundUrl
     */
    @UiThread
    void doInUiThread(String backGroundUrl) {
        //加载店铺logo
        if(null!=backGroundUrl){
            ImageManager.imageLoader.displayImage(DataUrlContents.IMAGE_HOST + backGroundUrl,shopBackGround,ImageManager.options);
           // pDialog.hide();
        }
    }

    /**
     * 点击选择图片按钮
     */
    @Click(R.id.shopGroundDoneBtn)
    void checkImageOnClick(){
        //shopBackGround.setImageDrawable(null);
        Crop.pickImage(this);
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
            shopBackGround.setImageURI(uri);
            try {
                byte[] bytes= StreamUtil.readStream(resolver.openInputStream(uri));
                doUploadFile(bytes,shopBackGroundUrl);
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
        pDialog.show();
        if(fileName==null||"".equals(fileName)){
            fileName= "shop_background/"+"ground_"+ UUID.randomUUID()+".jpg";
        }
        final String  _backGroundUrl=fileName;
        OSSData ossData = new OSSData(myApplication.getOssBucket(), fileName);
        ossData.setData(data, "raw"); // 指定需要上传的数据和它的类型
        ossData.enableUploadCheckMd5sum(); // 开启上传MD5校验
        ossData.uploadInBackground(new SaveCallback() {
            @Override
            public void onSuccess(String objectKey) {
                //saveDataToServer(_backGroundUrl);
            }
            @Override
            public void onProgress(String objectKey, int byteCount, int totalSize) {

            }
            @Override
            public void onFailure(String objectKey, OSSException ossException) {

            }
        });

        saveDataToServer(_backGroundUrl);
    }


    /**
     * 向服务器推送数据更新店铺信息
     * @param fileName
     */
    void saveDataToServer(String fileName){
        pDialog.show();
        RequestParams params=new RequestParams();
        params.put("userCode", userCode);
        params.put("shopCode", shopCode);
        if(null!=fileName){
            if(null!=fileName&&!"".equals(fileName)){
                params.put("shopBackground", fileName);
            }
        }
        getClient().post(DataUrlContents.SERVER_HOST + DataUrlContents.update_shop_config, params, new BaseJsonHttpResponseHandler<String>() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, String response) {
                pDialog.hide();
                if (null != response) {
                    if ("success".equals(response)) {
                        new SweetAlertDialog(ShopGroundConfigActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("同步成功!")
                                .setContentText("更新店铺招牌")
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
    public void onDestroy() {
        super.onDestroy();
        if(null!=pDialog){
            pDialog.dismiss();
        }
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
