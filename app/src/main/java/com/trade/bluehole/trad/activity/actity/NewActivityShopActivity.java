package com.trade.bluehole.trad.activity.actity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aliyun.mbaas.oss.callback.SaveCallback;
import com.aliyun.mbaas.oss.model.OSSException;
import com.aliyun.mbaas.oss.storage.OSSData;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.soundcloud.android.crop.Crop;
import com.trade.bluehole.trad.ActivityManageActivity_;
import com.trade.bluehole.trad.LoginSystemActivity;
import com.trade.bluehole.trad.R;
import com.trade.bluehole.trad.activity.BaseActionBarActivity;
import com.trade.bluehole.trad.entity.actity.ShopActivityBase;
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
import org.androidannotations.annotations.ViewById;
import org.apache.http.Header;

import java.io.File;
import java.util.UUID;

import cn.pedant.SweetAlert.SweetAlertDialog;
import mehdi.sakout.fancybuttons.FancyButton;

@EActivity(R.layout.activity_new_activity_shop)
public class NewActivityShopActivity extends BaseActionBarActivity {
        //商品和店铺编码标志
        public static final String SHOP_CODE_EXTRA = "shopCode";
        public static final String ACTIVITY_CODE_EXTRA = "activityCode";

        @ViewById
        ImageView addImage;
        @ViewById
        MaterialEditText activity_input_name,activity_input_details,activity_input_rules,activity_input_address;
        @ViewById
        FancyButton btn_activity_up_down,btn_activity_del;
        @ViewById
        LinearLayout activity_update_btn_layout;//底部按钮布局
        @App
        MyApplication myApplication;
        @Extra(ACTIVITY_CODE_EXTRA)
        String activityCode;

        private ShopCommonInfo shop;

        //页面进度条
        SweetAlertDialog pDialog;
        //图片名称
        String fileName;
        DialogPlus confirmDialog;//确认操作
        Integer delFlag=0;


        @AfterViews
        void initData() {
            shop = myApplication.getShop();
            if (null != shop) {
                activity_input_address.setText(shop.getAddress());
            }
            //修改的话读取数据
            if(null!=activityCode&&!"".equals(activityCode)){
                activity_update_btn_layout.setVisibility(View.VISIBLE);
                loadDetailData();
            }
            //初始化等待dialog
            pDialog = getDialog(this);
            //pDialog.show();

            //实例化弹出窗口
            initDialog();
        }

        /**
         * 初始化弹出框
         */
        void initDialog(){
            confirmDialog = new DialogPlus.Builder(this)
                    .setContentHolder(new ViewHolder(R.layout.dialog_confirm_content))
                            //.setHeader(R.layout.dialog_label_header)
                    .setFooter(R.layout.dialog_footer)
                    .setGravity(DialogPlus.Gravity.BOTTOM)
                    .setOnClickListener(delClickListener)
                    .create();
        }

        /**
         * 删除弹出框按钮点击事件
         */
        OnClickListener delClickListener = new OnClickListener() {
            @Override
            public void onClick(DialogPlus dialog, View view) {
                switch (view.getId()) {
                    case R.id.footer_confirm_button:
                        //删除活动
                        updateDetailData("del", 0);
                        // Toast.makeText(NewProductActivity.this, "Confirm button clicked", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.footer_close_button:
                        break;
                }
                dialog.dismiss();
            }
        };




        /**
         * 点击新增图片
         */
        @Click(R.id.addImage)
            void onClickAddImage(){
            addImage.setImageDrawable(null);
            Crop.pickImage(this);
         }
        /**
         * 点击开始结束活动按钮
         */
        @Click(R.id.btn_activity_up_down)
        void onClickUpDownBtn(){
            if(delFlag ==1){
                updateDetailData("update",0);
            }else{
                updateDetailData("update",1);
            }
         }
        /**
         * 点击删除活动按钮
         */
        @Click(R.id.btn_activity_del)
        void onClickDeleteBtn(){
            confirmDialog.show();
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
       // Uri outputUri = Uri.fromFile(new File(getCacheDir(), "cropped"));
       // Crop cp=new Crop(source);
       // cp.withAspect(300,150);
        //cp.output(outputUri).start(this);
       // new Crop(source).output(outputUri).asSquare().start(this);

        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
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
            addImage.setImageURI(uri);
            addImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

            try {
                byte[] bytes= StreamUtil.readStream(resolver.openInputStream(uri));
                doUploadFile(bytes,fileName);
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
    public void doUploadFile(byte[] data,String afileName) throws Exception {
        //pDialog.show();
        if(afileName==null||"".equals(afileName)){
            fileName= "shop_activity/"+"activity_"+ UUID.randomUUID()+".jpg";
        }else{
            fileName=afileName;
        }
        //final String  _backGroundUrl=fileName;
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

        // saveDataToServer(_backGroundUrl);
    }

    /**
     * 向服务器推送数据更新店铺信息
     * @param
     */
    boolean saveDataToServer(){
        //校验
        if(null==fileName||"".equals(fileName)){
            Toast.makeText(NewActivityShopActivity.this, "请选择活动图片" , Toast.LENGTH_SHORT).show();
            return false;
        }else  if("".equals(activity_input_name.getText().toString())){
            Toast.makeText(NewActivityShopActivity.this, "活动名称不能为空" , Toast.LENGTH_SHORT).show();
            return false;
        }else if("".equals(activity_input_details.getText().toString())){
            Toast.makeText(NewActivityShopActivity.this, "活动详情不能为空" , Toast.LENGTH_SHORT).show();
            return false;
        }else if("".equals(activity_input_rules.getText().toString())){
            Toast.makeText(NewActivityShopActivity.this, "活动规则不能为空" , Toast.LENGTH_SHORT).show();
            return false;
        }else if("".equals(activity_input_address.getText().toString())){
            Toast.makeText(NewActivityShopActivity.this, "活动地址不能为空" , Toast.LENGTH_SHORT).show();
            return false;
        }

        pDialog.show();
        RequestParams params=new RequestParams();
        params.put("shopCode", shop.getShopCode());
        params.put("activityImg", fileName);//活动图片
        if(null!=activityCode&&!"".equals(activityCode)){
            params.put("activityCode",activityCode);
        }
        params.put("activityName", activity_input_name.getText().toString());//活动名称
        params.put("activityAddress", activity_input_address.getText().toString());//活动地址
        params.put("activityProfile", activity_input_details.getText().toString());//活动简介
        params.put("activityDetails", activity_input_details.getText().toString());//活动详情
        params.put("activityRules", activity_input_rules.getText().toString());//活动规则
        if(null!=fileName&&!"".equals(fileName)){
                params.put("shopBackground", fileName);
        }
        getClient().post(DataUrlContents.SERVER_HOST + DataUrlContents.save_or_update_activity, params, new BaseJsonHttpResponseHandler<String>() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, String response) {
                pDialog.hide();
                if (null != response) {
                    if ("success".equals(response)) {
                        new SweetAlertDialog(NewActivityShopActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("同步成功!")
                                .setContentText("活动提交成功")
                                .show();
                        ActivityManageActivity_.intent(NewActivityShopActivity.this).start();
                        finish();
                    }
                    //Toast.makeText(ShopConfigActivity.this, "数据提交：" + response, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, String errorResponse) {
                pDialog.hide();
                Toast.makeText(NewActivityShopActivity.this, "请求失败："+statusCode , Toast.LENGTH_SHORT).show();
            }

            @Override
            protected String parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return gson.fromJson(rawJsonData, String.class);
            }
        });

        return true;
    }


    /**
     * 读取数据
     */
    private void loadDetailData(){
        RequestParams params=new RequestParams();
        params.put("activityCode",activityCode);
        getClient().get(DataUrlContents.SERVER_HOST + DataUrlContents.load_activity_detail, params, new BaseJsonHttpResponseHandler<ShopActivityBase>() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, ShopActivityBase obj) {
                Log.d(LoginSystemActivity.class.getName(), statusCode + "");
                if (null != obj) {
                    if (null != obj.getActivityImg() && !"".equals(obj.getActivityImg())) {
                        fileName = obj.getActivityImg();
                        addImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        ImageManager.imageLoader.displayImage(DataUrlContents.IMAGE_HOST + obj.getActivityImg() + DataUrlContents.activity_logo_img, addImage, ImageManager.options);
                    }
                    activity_input_name.setText(obj.getActivityName());
                    activity_input_address.setText(obj.getActivityAddress());
                    activity_input_details.setText(obj.getActivityDetails());
                    activity_input_rules.setText(obj.getActivityRules());
                    delFlag = obj.getDelFlag();
                    if (delFlag == 1) {//如果是开始状态，变更按钮为停止
                        btn_activity_up_down.setText(getResources().getString(R.string.title_activity_end_activity_shop));
                    } else {
                        btn_activity_up_down.setText(getResources().getString(R.string.title_activity_start_activity_shop));
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, ShopActivityBase errorResponse) {
                Toast.makeText(NewActivityShopActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected ShopActivityBase parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return gson.fromJson(rawJsonData, ShopActivityBase.class);
            }
        });
    }


    /**
     * 更新数据
     */
    private void updateDetailData(final String type,final Integer aDelFlag){
        RequestParams params=new RequestParams();
        params.put("activityCode",activityCode);
        params.put("shopCode",shop.getShopCode());
        params.put("delFlag",aDelFlag);
        String method=DataUrlContents.update_activity_state;
        if("del".equals(type)){
            method=DataUrlContents.delete_activity;
        }
        getClient().get(DataUrlContents.SERVER_HOST + method, params, new BaseJsonHttpResponseHandler<String>() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, String obj) {
                Log.d(LoginSystemActivity.class.getName(), statusCode + "");
                if (null != obj ) {
                    if("del".equals(type)){
                        Toast.makeText(NewActivityShopActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        ActivityManageActivity_.intent(NewActivityShopActivity.this).start();
                        finish();
                    }else{
                        delFlag=aDelFlag;//变更主状态
                        if(aDelFlag ==1){//如果是开始状态，变更按钮为停止
                            btn_activity_up_down.setText(getResources().getString(R.string.title_activity_end_activity_shop));
                        }else{
                            btn_activity_up_down.setText(getResources().getString(R.string.title_activity_start_activity_shop));
                        }
                        Toast.makeText(NewActivityShopActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, String errorResponse) {
                Toast.makeText(NewActivityShopActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_activity_shop, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.activity_menu_done) {
            saveDataToServer();
            return true;
        }else if(id==android.R.id.home){
            //点击后退跳转到 主页
            ActivityManageActivity_.intent(this).start();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
