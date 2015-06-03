package com.trade.bluehole.trad.activity.shop;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.mbaas.oss.callback.SaveCallback;
import com.aliyun.mbaas.oss.model.OSSException;
import com.aliyun.mbaas.oss.storage.OSSData;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.trade.bluehole.trad.NewProductActivity;
import com.trade.bluehole.trad.R;
import com.trade.bluehole.trad.SuperMainActivity;
import com.trade.bluehole.trad.activity.BaseActionBarActivity;
import com.trade.bluehole.trad.activity.camera.CameraActivity_;
import com.trade.bluehole.trad.entity.User;
import com.trade.bluehole.trad.entity.shop.ShopAuthentic;
import com.trade.bluehole.trad.entity.shop.ShopCommonInfo;
import com.trade.bluehole.trad.util.ImageManager;
import com.trade.bluehole.trad.util.MyApplication;
import com.trade.bluehole.trad.util.data.DataUrlContents;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.apache.http.Header;

import java.util.UUID;

import cn.pedant.SweetAlert.SweetAlertDialog;
import mehdi.sakout.fancybuttons.FancyButton;

@EActivity
public class ShopAuthenticActivity extends BaseActionBarActivity {

    //获取图片结果
    public static int getCameraResult=110;
    public static int getCameraResult2=111;
    Bitmap bitmap;
    byte[] imageByte1;
    byte[] imageByte2;
    User user = null;
    //页面进度条
    SweetAlertDialog pDialog;
    //认证状态
    int authState=0;
    @App
    MyApplication myapplication;

    @ViewById
    ImageView authentic_add_image_2;
    @ViewById
    ImageView authentic_add_image_1;
    @ViewById
    LinearLayout authentic_bak_layout;//申请备注
    @ViewById
    LinearLayout authentic_message_layout;//申请结果布局
    @ViewById
    TextView authentic_message_title,authentic_message_content;
    @ViewById
    FancyButton authentic_to_submit;//提交按钮
    @ViewById
    MaterialEditText authentic_applyDescribe;//用户提交的申请备注


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_authentic);

        //pDialog.show();
    }


    @AfterViews
    void initData(){
        //初始化等待dialog
        pDialog = getDialog(this);
        user = myapplication.getUser();
        loadDataToServer();
    }


    @Click(R.id.authentic_add_image_1)
    void onClickSelctImage1(){
        if(authState==0||authState==4){
            Intent intent=CameraActivity_.intent(this).get();
            startActivityForResult(intent, getCameraResult);
        }
    }
    @Click(R.id.authentic_add_image_2)
    void onClickSelctImage2(){
        if(authState==0||authState==4) {
            Intent intent = CameraActivity_.intent(this).get();
            startActivityForResult(intent, getCameraResult2);
        }
    }

    /**
     * 保存店铺认证
     */
    @Click(R.id.authentic_to_submit)
    void onClickSubmitData(){
        String applyDescribe=authentic_applyDescribe.getText().toString();
        try {
            String fileName1=null;
            String fileName2=null;
            if(authState==0){//如果是首次上传 必须选择上传图片
                if(imageByte1==null||imageByte2==null){
                    Toast.makeText(ShopAuthenticActivity.this, "请先上传证件照片", Toast.LENGTH_SHORT).show();
                }else{
                    if(null!=imageByte1&&imageByte1.length>0){
                        fileName1 = "authentic/" + "au_" + UUID.randomUUID() + ".jpg";
                        doUploadFile(imageByte1,fileName1);
                    }
                    if(null!=imageByte2&&imageByte2.length>0){
                        fileName2 = "authentic/" + "au_" + UUID.randomUUID() + ".jpg";
                        doUploadFile(imageByte2,fileName2);
                    }
                    saveDataToServer(applyDescribe, fileName1, fileName2);
                    Toast.makeText(ShopAuthenticActivity.this, "数据图片上传中", Toast.LENGTH_SHORT).show();
                }
            }else{
                if(null!=imageByte1&&imageByte1.length>0){
                    fileName1 = "authentic/" + "au_" + UUID.randomUUID() + ".jpg";
                    doUploadFile(imageByte1,fileName1);
                }
                if(null!=imageByte2&&imageByte2.length>0){
                    fileName2 = "authentic/" + "au_" + UUID.randomUUID() + ".jpg";
                    doUploadFile(imageByte2,fileName2);
                }
                saveDataToServer(applyDescribe, fileName1, fileName2);
                Toast.makeText(ShopAuthenticActivity.this, "数据图片上传中", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 装载数据
     * @param obj
     */
    @UiThread
    void initServerData(ShopAuthentic obj){
        //回填数据并且根据结果展示
        if(null!=obj){
            int state=obj.getAuthenticResult();
            authState=state;
            authentic_message_content.setText(obj.getResultDescribe());
            ImageManager.imageLoader.displayImage(DataUrlContents.IMAGE_HOST + obj.getLicenseImage(), authentic_add_image_1, ImageManager.options);
            ImageManager.imageLoader.displayImage(DataUrlContents.IMAGE_HOST + obj.getIdentityImageFront(), authentic_add_image_2, ImageManager.options);
            authentic_applyDescribe.setText(obj.getApplyDescribe());
            if(state==3){//店铺认证成功
                authentic_message_title.setText("店铺认证成功");
                //隐藏提交按钮
                authentic_to_submit.setVisibility(View.GONE);
            }else if(state==1){
                authentic_message_title.setText("店铺认证申请中");
                authentic_message_content.setText("请求正在处理中");
                //隐藏提交按钮
                authentic_to_submit.setVisibility(View.GONE);
            }else if(state==2){
                authentic_message_title.setText("店铺认证受理中");
                //隐藏提交按钮
                authentic_to_submit.setVisibility(View.GONE);
            }else if(state==4){
                authentic_message_title.setText("店铺认证未通过");
            }
        }
    }



    /**
     * 读取服务器推送数据更新认证信息
     *
     */
    void loadDataToServer() {
        pDialog.show();
        RequestParams params = new RequestParams();
        params.put("shopCode", user.getShopCode());
        getClient().post(DataUrlContents.SERVER_HOST + DataUrlContents.load_shop_authentic, params, new BaseJsonHttpResponseHandler<ShopAuthentic>() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, ShopAuthentic response) {
                pDialog.hide();
                if (null != response) {
                    initServerData(response);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, ShopAuthentic errorResponse) {
                pDialog.hide();
                Toast.makeText(ShopAuthenticActivity.this, "服务器繁忙", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected ShopAuthentic parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return gson.fromJson(rawJsonData, ShopAuthentic.class);
            }
        });
    }





    /**
     * 上传代码
     *
     * @param data
     * @throws Exception
     */
    public void doUploadFile(byte[] data,String fileName) throws Exception {
        // if(fileName==null||"".equals(fileName)){
        ShopCommonInfo sc = new ShopCommonInfo();
        sc.setShopLogo(fileName);
        //发送广播通知改变头像
        Intent sendIntent=new Intent(SuperMainActivity.UPDATE_ACTION);
        sendIntent.putExtra("fileName",fileName);
        sendBroadcast(sendIntent);
        // }
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
     * 向服务器推送数据更新认证信息
     *
     */
    void saveDataToServer(String applyDescribe,String fileName1,String fileName2) {
        pDialog.show();
        RequestParams params = new RequestParams();
        params.put("userCode", user.getUserCode());
        params.put("shopCode", user.getShopCode());
        params.put("applyDescribe", applyDescribe);
        if(null!=fileName1){
            params.put("licenseImage", fileName1);
        }
        if(null!=fileName2){
            params.put("identityImageFront",fileName2);
        }
        getClient().post(DataUrlContents.SERVER_HOST + DataUrlContents.save_shop_authentic, params, new BaseJsonHttpResponseHandler<String>() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, String response) {
                pDialog.hide();
                Log.d(NewProductActivity.class.getName(), response.toString());
                if (null != response) {
                    if ("ok".equals(response)) {
                        Toast.makeText(ShopAuthenticActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(ShopAuthenticActivity.this,SuperMainActivity.class);
                        intent.putExtra("authState",1);
                        ShopAuthenticActivity.this.setResult(RESULT_OK,intent);
                        ShopAuthenticActivity.this.finish();
                    } else {
                        Toast.makeText(ShopAuthenticActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, String errorResponse) {
                pDialog.hide();
                Toast.makeText(ShopAuthenticActivity.this, "服务器繁忙", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected String parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return gson.fromJson(rawJsonData, String.class);
            }
        });
    }


    /**
     * 返回结果回调
     * @param requestCode
     * @param resultCode
     * @param result
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if(requestCode==getCameraResult&& resultCode == RESULT_OK){
            byte[]bis=result.getByteArrayExtra("bitmap");
            imageByte1=bis;
            Bitmap bitmap= BitmapFactory.decodeByteArray(bis,0,bis.length);
            authentic_add_image_1.setImageBitmap(bitmap);
        } if(requestCode==getCameraResult2&& resultCode == RESULT_OK){
            byte[]bis=result.getByteArrayExtra("bitmap");
            imageByte2=bis;
            Bitmap bitmap= BitmapFactory.decodeByteArray(bis,0,bis.length);
            authentic_add_image_2.setImageBitmap(bitmap);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shop_authentic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }else if(id==android.R.id.home){
            Intent intent=new Intent(ShopAuthenticActivity.this,SuperMainActivity.class);
            intent.putExtra("authState",authState);
            ShopAuthenticActivity.this.setResult(RESULT_OK,intent);
            ShopAuthenticActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 处理后退事件
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent=new Intent(ShopAuthenticActivity.this,SuperMainActivity.class);
            intent.putExtra("authState",authState);
            ShopAuthenticActivity.this.setResult(RESULT_OK,intent);
            ShopAuthenticActivity.this.finish();
            return true;
        }
        return false;
    }
}
