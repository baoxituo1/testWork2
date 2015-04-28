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

import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ikimuhendis.ldrawer.ActionBarDrawerToggle;
import com.ikimuhendis.ldrawer.DrawerArrowDrawable;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.trade.bluehole.trad.activity.BaseActionBarActivity;
import com.trade.bluehole.trad.activity.msg.MessageAllActivity_;
import com.trade.bluehole.trad.activity.msg.MessagePageviewActivity_;
import com.trade.bluehole.trad.activity.user.AccountUserManageActivity_;
import com.trade.bluehole.trad.activity.webview.WebViewActivity;
import com.trade.bluehole.trad.activity.webview.WebViewActivity_;
import com.trade.bluehole.trad.adaptor.main.MainNoticeAdapter;
import com.trade.bluehole.trad.entity.User;
import com.trade.bluehole.trad.entity.msg.IndexProCommentVO;
import com.trade.bluehole.trad.entity.msg.IndexVO;
import com.trade.bluehole.trad.entity.shop.ShopCommonInfo;
import com.trade.bluehole.trad.util.ImageManager;
import com.trade.bluehole.trad.util.MyApplication;
import com.trade.bluehole.trad.util.data.DataUrlContents;
import com.trade.bluehole.trad.util.model.UserModel;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.apache.http.Header;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

@EActivity(R.layout.activity_super_main)
public class SuperMainActivity extends Activity {
    public static final int REFRESH_DELAY = 2000;
    private DrawerLayout mDrawerLayout;
    private LinearLayout mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerArrowDrawable drawerArrow;
    private boolean drawerArrowColor;
    AsyncHttpClient client = BaseActionBarActivity.getClient();
    Gson gson = new Gson();
    @App
    MyApplication myApplication;
    User user;
    ShopCommonInfo shop;
    MainNoticeAdapter adapter;//站内通知适配器
    @ViewById
    ListView listView;//站内信和新闻公告
    @ViewById //商品数 浏览量 收藏量 商品收藏 店铺名 账号信息
    TextView all_pro_number,all_view_number,all_shop_collect_number,all_pro_collect_number,reg_shop_name,account;
    @ViewById
    CircleImageView shop_logo_image;//左侧边栏logo
    @ViewById
    LinearLayout main_left_home_layout,main_left_user_layout,main_left_message_layout;
    //站内信集合列表
    List<IndexProCommentVO> noticeList;


    @AfterViews
    void initData(){
        user=myApplication.getUser();
        shop=myApplication.getShop();
        //是否有登陆信息
        if(null!=user&&null!=shop){
            reg_shop_name.setText(shop.getTitle());
            account.setText(user.getAccount());
            if(null!=shop.getShopLogo()){
                ImageManager.imageLoader.displayImage(DataUrlContents.IMAGE_HOST + shop.getShopLogo()+DataUrlContents.img_logo_img,shop_logo_image,ImageManager.options);
            }
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
            //当站内信列表被点击
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent=WebViewActivity_.intent(SuperMainActivity.this).get();
                    intent.putExtra(WebViewActivity.NOTICE_TYPE,noticeList.get(position).getState());
                    intent.putExtra(WebViewActivity.NOTICE_CODE,noticeList.get(position).getMessAgeCode());
                    startActivity(intent);
                }
            });
            //装载数据
            loadData();
        }else{
            Toast.makeText(SuperMainActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 点击新增商品
     */
    @Click(R.id.main_btn_add_pro)
    void onClickAddProBtn(){
        Intent intent=NewProductActivity_.intent(this).get();
        intent.putExtra(NewProductActivity.SHOP_CODE_EXTRA,user.getShopCode());
        startActivity(intent);
    }
    /**
     * 点击管理商品
     */
    @Click(R.id.main_btn_manage_pro)
    void onClickManageProBtn(){
        HeaderAnimatorActivity_.intent(this).start();
    }
    /**
     * 点击店铺管理
     */
    @Click(R.id.main_btn_shop_config)
    void onClickManageShopBtn(){
        ShopConfigActivity_.intent(this).start();
    }

    /**
     * 点击动态管理
     */
    @Click(R.id.main_btn_manage_activity)
    void onClickManageActivityBtn(){
        ActivityManageActivity_.intent(this).start();
    }
    /**
     * 点击折扣管理
     */
    @Click(R.id.main_btn_manage_sale)
    void onClickManageDynamicBtn(){
        DynamicManageActivity_.intent(this).start();
    }
    /**
     * 点击分类管理
     */
    @Click(R.id.main_btn_manage_cover)
    void onClickManageCoverBtn(){
       // Intent intent=new Intent(this,CoverManageActivity.class);
        CoverManageActivity_.intent(this).start();
       // startActivity(intent);
    }

    /*****************
     *               *
     *  以下是侧边栏   *
     *               *
     *****************/

    /**
     * 点击返回主页管理
     */
    @Click(R.id.main_left_home_layout)
    void onClickHomeBtn() {
        //main_left_home_layout.setFocusable(true);
        // main_left_home_layout.setFocusableInTouchMode(true);
    }

    /**
     * 点击账号信息管理
     */
    @Click(R.id.main_left_user_layout)
    void onClickManageUserInfoBtn(){
        /*Intent intent=new Intent(this, AccountUserManageActivity.class);
        startActivity(intent);*/
        AccountUserManageActivity_.intent(this).start();
        mDrawerLayout.closeDrawers();
       // main_left_user_layout.setFocusable(true);
    }
    /**
     * 点击信息管理
     */
    @Click(R.id.main_left_message_layout)
    void onClickMessageInfoBtn(){
       /// MessageAllActivity_.intent(this).start();
        MessagePageviewActivity_.intent(this).start();
    }
    /**
     * 点击退出
     */
    @Click(R.id.main_quit_layout)
    void onClickCoverManageInfoBtn() {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("确定退出")
                .setContentText("要退出账号登录么?")
                .setConfirmText("是的,退出!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        userQuitClean();
                        sDialog.cancel();
                        LoginSystemActivity_.intent(SuperMainActivity.this).start();
                        //点击后退跳转到 主页
                        finish();
                    }
                })
                .setCancelText("不退出!")
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                    }
                })
                .show();

    }

    /**
     * 用户退出清空数据
     */
    void userQuitClean(){
        myApplication.setUser(null);
        myApplication.setShop(null);
        //清除本地默认登录数据
        new Delete().from(UserModel.class).where("UserAccount = ?", user.getAccount()).execute();
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

                    noticeList=obj.getMessAge();
                    adapter.setLists(noticeList);
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
