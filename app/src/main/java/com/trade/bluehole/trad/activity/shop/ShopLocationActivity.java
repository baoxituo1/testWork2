package com.trade.bluehole.trad.activity.shop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.trade.bluehole.trad.R;
import com.trade.bluehole.trad.activity.BaseActionBarActivity;

import org.androidannotations.annotations.Click;

import cn.pedant.SweetAlert.SweetAlertDialog;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * 此demo用来展示如何结合定位SDK实现定位，并使用MyLocationOverlay绘制定位位置 同时展示如何使用自定义图标绘制并点击时弹出泡泡
 */

public class ShopLocationActivity extends BaseActionBarActivity implements OnClickListener, OnGetGeoCoderResultListener {
    private static final String LTAG = ShopLocationActivity.class.getSimpleName();
    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;
    // 当前的地理位置
    double latitude;
    double longitude;
    ShopLocationActivity c = this;
    // 搜索模块，也可去掉地图模块独立使用
    GeoCoder mSearch = null;
    MapView mMapView;
    BaiduMap mBaiduMap;

    // UI相关
    OnCheckedChangeListener radioButtonListener;
    Button requestLocButton;
    boolean isFirstLoc = true;// 是否首次定位
    SweetAlertDialog pDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_location_config);

        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);
        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
        //FancyButton saveBtn=(FancyButton)this.findViewById(R.id.map_set_save);
        //saveBtn.setOnClickListener(this);
        initMapListener();
    }


    /**
     * 监听地图变化
     */
    private void initMapListener() {
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {

            @Override
            public void onMapStatusChangeStart(MapStatus arg0) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus arg0) {

            }

            @Override
            public void onMapStatusChange(MapStatus status) {
                showMapState(status);
            }
        });
    }

    /**
     * 事实状态
     *
     * @param status
     */
    private void showMapState(MapStatus status) {
        LatLng mCenterLatLng = status.target;
        latitude = mCenterLatLng.latitude;
        longitude = mCenterLatLng.longitude;
        Log.d(LTAG, "lat:" + mCenterLatLng.latitude + "---lng:" + mCenterLatLng.longitude);
    }


    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null)
                return;
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius()) // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(u);
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }
    /**
     * 按照城市地址搜索定位
     * @param result
     */
    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this, "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
            return;
        }
        mBaiduMap.clear();
        mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation()).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding)));
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result.getLocation()));
        String strInfo = String.format("纬度：%f 经度：%f",result.getLocation().latitude, result.getLocation().longitude);
        Toast.makeText(this, strInfo, Toast.LENGTH_LONG).show();
    }

    /**
     * 接收按照经纬度查询地址
     */
    @Override
    public void onGetReverseGeoCodeResult(final ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this, "抱歉，未能找到结果,不能执行保存.", Toast.LENGTH_LONG).show();
            return;
        }else{
            Toast.makeText(this, ""+result.getAddress() , Toast.LENGTH_LONG).show();
        }
        final ReverseGeoCodeResult.AddressComponent ac=result.getAddressDetail();
        pDialog= new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("确定设置当前位置吗?")
                .setContentText(result.getAddress())
                .setCancelText("不,取消!")
                .setConfirmText("是的,确定!")
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        Intent it = new Intent();
                        it.putExtra(ShopAddressConfigActivity.SHOP_latitude_EXTRA,result.getLocation().latitude);
                        it.putExtra(ShopAddressConfigActivity.SHOP_longitude_EXTRA, result.getLocation().longitude);
                        it.putExtra(ShopAddressConfigActivity.SHOP_provinceName_EXTRA, ac.province);
                        it.putExtra(ShopAddressConfigActivity.SHOP_cityNameName_EXTRA, ac.city);
                        it.putExtra(ShopAddressConfigActivity.SHOP_districtName_EXTRA,ac.district);
                        it.putExtra(ShopAddressConfigActivity.SHOP_address_EXTRA, result.getAddress());
                        c.setResult(Activity.RESULT_OK, it);
                        c.finish();
                       // sDialog.cancel();
                    }
                });
        pDialog .show();
    }

    @Override
    public void onClick(View v) {
       /* switch (v.getId()) {
            case R.id.map_set_save:// 保存位置
                onSaveLocationClick();
                break;
            default:
                break;
        }*/

    }

    void onSaveLocationClick() {
        LatLng ptCenter = new LatLng(latitude,longitude);
        // 反Geo搜索
        mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ptCenter));
    }


    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        if(null!=pDialog){
            pDialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_location_shop, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.map_set_save) {
            onSaveLocationClick();
            return true;
        }else if(id==android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
