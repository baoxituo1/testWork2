package com.trade.bluehole.trad.activity.reg;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.aliyun.mbaas.oss.OSSClient;
import com.aliyun.mbaas.oss.callback.SaveCallback;
import com.aliyun.mbaas.oss.model.OSSException;
import com.aliyun.mbaas.oss.model.TokenGenerator;
import com.aliyun.mbaas.oss.storage.OSSBucket;
import com.aliyun.mbaas.oss.storage.OSSData;
import com.aliyun.mbaas.oss.util.OSSLog;
import com.aliyun.mbaas.oss.util.OSSToolKit;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.soundcloud.android.crop.Crop;
import com.trade.bluehole.trad.NewProductActivity;
import com.trade.bluehole.trad.R;
import com.trade.bluehole.trad.RegisterManageActivity;
import com.trade.bluehole.trad.activity.shop.ShopLocationActivity;
import com.trade.bluehole.trad.entity.User;
import com.trade.bluehole.trad.entity.shop.Shop;
import com.trade.bluehole.trad.util.MyApplication;
import com.trade.bluehole.trad.util.Result;
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
import de.hdodenhof.circleimageview.CircleImageView;

@EActivity(R.layout.activity_register_shop_create)
public class RegisterShopCreateActivity extends ActionBarActivity implements OnGetGeoCoderResultListener {

   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_shop_create);
    }*/
    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    // 搜索模块，也可去掉地图模块独立使用
    GeoCoder mSearch = null;
    boolean isFirstLoc = true;// 是否首次定位



    //json 转换
    Gson gson = new Gson();
    //网络请求
    AsyncHttpClient client = new AsyncHttpClient();

    @Extra(RegisterStep2Activity.user_phone_number)
    String phoneNumber;
    @Extra(RegisterStep2Activity.user_pwd_number)
    String passWord;
    @ViewById
    CircleImageView shop_logo_image;//店铺logo
    @ViewById
    EditText reg_shop_name;//店铺名称
    @ViewById
    EditText reg_shop_address;//店铺地址
    private byte[] bytes;//裁剪后图片
    //页面进度条
    SweetAlertDialog pDialog;
    // 当前的地理位置
    double latitude;
    double longitude;
    private String address;//店铺地址
    private String shopLogoImage;//店铺logo地址
    private String provinceName;
    private String cityName;
    private String district;
    @App
    MyApplication myapplication;
    //阿里云oss
    public OSSBucket sampleBucket;
    static {
        OSSClient.setGlobalDefaultTokenGenerator(new TokenGenerator() { // 设置全局默认加签器
            @Override
            public String generateToken(String httpMethod, String md5, String type, String date,
                                        String ossHeaders, String resource) {

                String content = httpMethod + "\n" + md5 + "\n" + type + "\n" + date + "\n" + ossHeaders
                        + resource;

                return OSSToolKit.generateToken(MyApplication.accessKey, MyApplication.screctKey, content);
            }
        });
        // OSSClient.setGlobalDefaultACL(AccessControlList.PUBLIC_READ_WRITE); // 设置全局默认bucket访问权限
        OSSClient.setGlobalDefaultHostId("oss-cn-beijing.aliyuncs.com"); // 指明你的bucket是放在北京数据中心
    }


    /**
     * 加载数据
     */
    @AfterViews
    void initdata(){
        loactionMap();
        OSSLog.enableLog(true);
        OSSClient.setApplicationContext(getApplicationContext()); // 传入应用程序context
        // 开始单个Bucket的设置
        sampleBucket = new OSSBucket("125");
        sampleBucket.setBucketHostId("oss-cn-beijing.aliyuncs.com"); // 可以在这里设置数据中心域名或者cname域名
        //初始化等待dialog
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("数据交互中");
        pDialog.setCancelable(false);
        //
    }

    /**
     * 定位地图
     */
    void loactionMap(){
        // 定位初始化
        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    /**
     * 点击选择商品logo按钮
     */
    @Click(R.id.shop_logo_image)
    void onSelectLogoImgClick(){
        shop_logo_image.setImageDrawable(null);
        Crop.pickImage(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_register_shop_create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_done) {
            if(null!=reg_shop_name.getText()&&!"".equals(reg_shop_name.getText().toString())){
                //yes now 开始执行开店操作
                saveData();
            }else{
                Toast.makeText(RegisterShopCreateActivity.this, "请输入店铺名称", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * 上传图片到oss服务器
     * @param data
     * @throws Exception
     */
    public void doUploadFile(byte[] data,String fileName) throws Exception {
        if(fileName==null||"".equals(fileName)){
            shopLogoImage= "shop_logo/"+"logo_"+ UUID.randomUUID()+".jpg";
        }
        OSSData ossData = new OSSData(sampleBucket, shopLogoImage);
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
     * 准备保存数据同步到服务器
     */
    void saveData(){
        pDialog.show();
        RequestParams params=new RequestParams();
        if(null!=phoneNumber&&null!=passWord&&!"".equals(reg_shop_name.getText())){
                //用户信息
                params.put("account", phoneNumber);
                params.put("mobile",  phoneNumber);
                params.put("password", passWord);
                //店铺信息
                 params.put("title",  reg_shop_name.getText());
                 params.put("shopName",  reg_shop_name.getText());
                 params.put("latitude",  latitude);
                 params.put("longitude",  longitude);
                 params.put("address",  address);
                 params.put("provinceName",  provinceName);
                 params.put("cityName",  cityName);
                 params.put("district",  district);
                //上传了logo
                if(null!=shopLogoImage&&!"".equals(shopLogoImage)){
                    params.put("shopLogo",  shopLogoImage);
                }
             client.post(DataUrlContents.SERVER_HOST+DataUrlContents.add_user_shop, params, new BaseJsonHttpResponseHandler<Result<User, Shop>>() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Result<User, Shop> response) {
                    pDialog.hide();
                    Log.d(NewProductActivity.class.getName(), response.toString());
                    if (null != response) {

                        Toast.makeText(RegisterShopCreateActivity.this, "数据提交成功：" + response, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Result<User, Shop> errorResponse) {
                    pDialog.hide();
                    Toast.makeText(RegisterShopCreateActivity.this, "服务器繁忙,请稍后重试", Toast.LENGTH_SHORT).show();
                }

                @Override
                protected Result<User, Shop> parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    return gson.fromJson(rawJsonData, new TypeToken<Result<User, Shop>>() {
                    }.getType());
                }
            });
        }else{
            pDialog.hide();
            Toast.makeText(RegisterShopCreateActivity.this, "参数错误", Toast.LENGTH_SHORT).show();
        }

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
            shop_logo_image.setImageURI(uri);
            try {
                bytes= StreamUtil.readStream(resolver.openInputStream(uri));
                //为了保证上传速度 裁剪完后直接上传
                doUploadFile(bytes,shopLogoImage);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null ) {
                Toast.makeText(RegisterShopCreateActivity.this, "定位失败", Toast.LENGTH_SHORT).show();
                return;
            }else {
               /* MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(location.getRadius()) // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(100).latitude(location.getLatitude())
                        .longitude(location.getLongitude()).build();*/
                latitude=location.getLatitude();
                longitude=location.getLongitude();
                //只执行一次成功定位
                if(isFirstLoc){
                    LatLng ptCenter = new LatLng(latitude,longitude);
                    // 反Geo搜索
                    mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ptCenter));
                }
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this, "抱歉，未能执行定位，不能获取地址.", Toast.LENGTH_LONG).show();
            return;
        }else{
            isFirstLoc=false;
             ReverseGeoCodeResult.AddressComponent ac=result.getAddressDetail();
             reg_shop_address.setText(result.getAddress());
             address=result.getAddress();
             provinceName=ac.province;
             cityName=ac.city;
             district=ac.district;
            Toast.makeText(this, "定位成功:"+result.getAddress() , Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        pDialog.dismiss();
    }
}
