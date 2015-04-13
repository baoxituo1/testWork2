package com.trade.bluehole.trad;

import android.app.ActionBar;
import android.app.Activity;

import android.content.Intent;
import android.content.res.Configuration;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ikimuhendis.ldrawer.ActionBarDrawerToggle;
import com.ikimuhendis.ldrawer.DrawerArrowDrawable;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.trade.bluehole.trad.adaptor.main.MainNoticeAdapter;
import com.trade.bluehole.trad.entity.User;
import com.trade.bluehole.trad.entity.UserBase;
import com.trade.bluehole.trad.entity.msg.IndexVO;
import com.trade.bluehole.trad.util.MyApplication;
import com.trade.bluehole.trad.util.Result;
import com.trade.bluehole.trad.util.data.DataUrlContents;
import com.yalantis.phoenix.PullToRefreshView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.apache.http.Header;

@EActivity(R.layout.activity_super_main)
public class SuperMainActivity extends Activity {
    public static final int REFRESH_DELAY = 2000;
    private DrawerLayout mDrawerLayout;
    private LinearLayout mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerArrowDrawable drawerArrow;
    private boolean drawerArrowColor;
    AsyncHttpClient client = new AsyncHttpClient();
    Gson gson = new Gson();
    @App
    MyApplication myApplication;
    User user;
    MainNoticeAdapter adapter;//站内通知适配器
    @ViewById
    ListView listView;
    @ViewById
    TextView all_pro_number,all_view_number,all_shop_collect_number,all_pro_collect_number;
   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_main);
    }*/

    @AfterViews
    void initData(){
        user=myApplication.getUser();
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (LinearLayout) findViewById(R.id.navdrawer);

        drawerArrow = new DrawerArrowDrawable(this) {
            @Override
            public boolean isLayoutRtl() {
                return false;
            }
        };
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, drawerArrow, R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        //实例化adapter
        adapter=new MainNoticeAdapter(this,null);
        //装载数据
        loadData();
    }

    /**
     * 点击新增商品
     */
    @Click(R.id.main_btn_add_pro)
    void onClickAddPro(){
        Intent intent=NewProductActivity_.intent(this).get();
        intent.putExtra(NewProductActivity.SHOP_CODE_EXTRA,user.getShopCode());
        startActivity(intent);
    }
    /**
     * 点击管理商品
     */
    @Click(R.id.main_btn_manage_pro)
    void onClickManagePro(){
        HeaderAnimatorActivity_.intent(this).start();
    }
    /**
     * 点击店铺管理
     */
    @Click(R.id.main_btn_shop_config)
    void onClickManageShop(){
        ShopConfigActivity_.intent(this).start();
    }

    /**
     * 点击店铺管理
     */
    @Click(R.id.main_btn_manage_activity)
    void onClickManageActivity(){
        ActivityManageActivity_.intent(this).start();
    }


    /**
     * 读取数据
     */
    private void loadData(){
        RequestParams params=new RequestParams();
        params.put("shopCode",user.getShopCode());
        params.put("pageSize",500);
        client.get(DataUrlContents.SERVER_HOST+DataUrlContents.load_shop_statistical_info, params, new BaseJsonHttpResponseHandler<IndexVO>() {


            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, IndexVO obj) {
                Log.d(LoginSystemActivity.class.getName(), statusCode + "");
                if (null != obj) {
                    //商品数
                    if(null!=obj.getTotalProNum()){
                        all_pro_number.setText(obj.getTotalProNum()+"");
                    }
                    //总浏览
                    if(null!=obj.getAccessShopNum()){
                        all_view_number.setText(obj.getAccessShopNum()+"");
                    }
                    //店铺收藏
                    if(null!=obj.getShopcollectNum()){
                        all_shop_collect_number.setText(obj.getShopcollectNum()+"");
                    }
                    //商品收藏
                    if(null!=obj.getAllshopcollectNum()){
                        all_pro_collect_number.setText(obj.getAllshopcollectNum()+"");
                    }

                    adapter.setLists(obj.getMessAge());
                    listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, IndexVO errorResponse) {
                Toast.makeText(SuperMainActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected IndexVO parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                // return new ObjectMapper().readValues(new JsonFactory().createParser(rawJsonData), Result.class).next();
                return gson.fromJson(rawJsonData, IndexVO.class);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                mDrawerLayout.closeDrawer(mDrawerList);
            } else {
                mDrawerLayout.openDrawer(mDrawerList);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
}
