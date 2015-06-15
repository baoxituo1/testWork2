package com.trade.bluehole.trad;

import android.app.ActionBar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.LinearLayout;

import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Delete;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.github.amlcurran.showcaseview.ShowcaseView;

import com.google.gson.Gson;
import com.ikimuhendis.ldrawer.DrawerArrowDrawable;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.trade.bluehole.trad.activity.BaseActionBarActivity;
import com.trade.bluehole.trad.activity.BaseActivity;
import com.trade.bluehole.trad.activity.feedback.CustomActivity_;
import com.trade.bluehole.trad.activity.feedback.HelpInfoActivity_;
import com.trade.bluehole.trad.activity.feedback.UserFeedBackActivity_;
import com.trade.bluehole.trad.activity.msg.MessagePageviewActivity_;
import com.trade.bluehole.trad.activity.shop.ShopAuthenticActivity_;
import com.trade.bluehole.trad.activity.user.AccountUserManageActivity_;
import com.trade.bluehole.trad.activity.webview.WebViewActivity;
import com.trade.bluehole.trad.activity.webview.WebViewActivity_;
import com.trade.bluehole.trad.adaptor.main.MainNoticeAdapter;
import com.trade.bluehole.trad.entity.User;
import com.trade.bluehole.trad.entity.msg.IndexProCommentVO;
import com.trade.bluehole.trad.entity.msg.IndexVO;
import com.trade.bluehole.trad.entity.msg.Notice;
import com.trade.bluehole.trad.entity.shop.ShopCommonInfo;
import com.trade.bluehole.trad.util.ImageManager;
import com.trade.bluehole.trad.util.MyApplication;
import com.trade.bluehole.trad.util.data.DataUrlContents;
import com.trade.bluehole.trad.util.model.UserModel;

import com.trade.bluehole.trad.util.view.InnerListView;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;

import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;

import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.apache.http.Header;

import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

@EActivity
public class SuperMainActivity extends BaseActivity implements BaseSliderView.OnSliderClickListener {
    public static final String UPDATE_ACTION = "com.trade.bluehole.trad.UPDATE_ACTION";
    public static final int REFRESH_DELAY = 2000;
    private DrawerLayout mDrawerLayout;
    private LinearLayout mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private com.ikimuhendis.ldrawer.ActionBarDrawerToggle mDrawerToggle2;
    private DrawerArrowDrawable drawerArrow;
    private boolean drawerArrowColor;
    //认证状态
    private int authenticState;
    AsyncHttpClient client = BaseActionBarActivity.getClient();
    Gson gson = new Gson();
    @App
    MyApplication myApplication;
    User user;
    ShopCommonInfo shop;
    MainNoticeAdapter adapter;//站内通知适配器
    @ViewById
    InnerListView listView;//站内信和新闻公告
    @ViewById //商品数 浏览量 收藏量 商品收藏 店铺名 账号信息
    TextView all_pro_number, all_view_number, all_shop_collect_number, all_pro_collect_number, reg_shop_name, account,main_shop_authentic_no_text;
    @ViewById
    CircleImageView shop_logo_image;//左侧边栏logo
    @ViewById
    LinearLayout main_left_home_layout, main_left_user_layout, main_left_message_layout;
    @ViewById
    RelativeLayout main_shop_authentic_ok,main_shop_authentic_no;

    //站内信集合列表
    List<IndexProCommentVO> noticeList;

    ShowcaseView showcaseView;
    private SliderLayout mDemoSlider;
    //变更店铺信息广播
    SuperActivityReceiver broderService;

    DisplayImageOptions options;

    //友盟反馈
    FeedbackAgent agent;
    //工具条
    Toolbar mToolbar ;

    int sdkLevel;

    //是否首次检查更新
    boolean firstCheckVersion=true;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_main);
        //UpdateConfig.setDebug(true);
        //友盟检查更新
        initUpdate();
        mDemoSlider = (SliderLayout) findViewById(R.id.slider);
        broderService = new SuperActivityReceiver();
        //创建intentFilter
        IntentFilter filter = new IntentFilter();
        //制定BroadCastReceiver监听的Action
        filter.addAction(UPDATE_ACTION);
        //注册BroadcastReceiver
        registerReceiver(broderService, filter);
        //友盟反馈
        agent = new FeedbackAgent(this);
        agent.sync();
    }

    /**
     * 检查版本更新
     */
   void  initUpdate(){
        UmengUpdateAgent.update(this);
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
            @Override
            public void onUpdateReturned(
                    int updateStatus,
                    UpdateResponse updateInfo) {
                switch (updateStatus) {
                    case UpdateStatus.Yes: // has update
                        Toast.makeText(SuperMainActivity.this, "发现更新", Toast.LENGTH_SHORT).show();
                        break;
                    case UpdateStatus.No: // has no
                        if(!firstCheckVersion){
                            // update
                            Toast.makeText(SuperMainActivity.this, "已经是最新版本", Toast.LENGTH_SHORT).show();
                        }
                        firstCheckVersion=false;
                        break;
                    case UpdateStatus.NoneWifi: // none
                        // wifi
                        Toast.makeText(SuperMainActivity.this, "没有wifi连接， 只在wifi下更新", Toast.LENGTH_SHORT).show();
                        break;
                    case UpdateStatus.Timeout: // time
                        // out
                        Toast.makeText(SuperMainActivity.this, "连接超时", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

        });
    }


    @AfterViews
    void initData() {
        //当前版本号
        String version_sdk = android.os.Build.VERSION.SDK;
        sdkLevel=Integer.valueOf(version_sdk);
        mToolbar=new Toolbar(this);
        mToolbar.setNavigationIcon(R.drawable.menu_check);
        mToolbar.setLogo(R.drawable.logo_icon);
        user = myApplication.getUser();
        shop = myApplication.getShop();
        //是否有登陆信息
        if (null != user && null != shop) {
            reg_shop_name.setText(shop.getTitle());
            account.setText(user.getAccount());
            //if(null!=shop.getShopLogo()){
            options = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.default_user)
                    .showImageOnFail(R.drawable.sample)
                    .showImageOnLoading(R.drawable.sample)
                    .cacheInMemory(false)
                    .cacheOnDisk(false)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
            ImageManager.imageLoader.displayImage(DataUrlContents.IMAGE_HOST + shop.getShopLogo() + DataUrlContents.img_logo_img, shop_logo_image, options);
            //}
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
            if(sdkLevel>=19){
                mDrawerToggle2 = new com.ikimuhendis.ldrawer.ActionBarDrawerToggle(this, mDrawerLayout, drawerArrow, R.string.drawer_open, R.string.drawer_close) {

                    public void onDrawerClosed(View view) {
                        super.onDrawerClosed(view);
                        invalidateOptionsMenu();
                    }

                    public void onDrawerOpened(View drawerView) {
                        super.onDrawerOpened(drawerView);
                        invalidateOptionsMenu();
                    }
                };
                mDrawerLayout.setDrawerListener(mDrawerToggle2);
                mDrawerToggle2.syncState();
            }else{
                mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close) {

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
            }

            //实例化adapter
            adapter = new MainNoticeAdapter(this, null);
            //当站内信列表被点击
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = WebViewActivity_.intent(SuperMainActivity.this).get();
                    intent.putExtra(WebViewActivity.NOTICE_TYPE, noticeList.get(position).getState());
                    intent.putExtra(WebViewActivity.NOTICE_CODE, noticeList.get(position).getMessAgeCode());
                    startActivity(intent);
                }
            });
            //装载数据
            loadData();
            //
           /* Target viewTarget = new ViewTarget(R.id.main_btn_add_pro, this);
            new ShowcaseView.Builder(this)
                    .setTarget(viewTarget)
                    .setContentTitle("添加新商品")
                    .setContentText("点击新增按钮,快速添加一件商品")
                    //.hideOnTouchOutside()
                    .setStyle(R.style.CustomShowcaseTheme2)
                    .singleShot(42)
                    .build();*/

            /*new ShowcaseView.Builder(this)
                    .setTarget(new ActionViewTarget(this, ActionViewTarget.Type.HOME))
                    .setContentTitle("ShowcaseView")
                    .setContentText("This is highlighting the Home button")
                    .hideOnTouchOutside()
                    .build();*/
        } else {
            Toast.makeText(SuperMainActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 点击新增商品
     */
    @Click(R.id.main_btn_add_pro)
    void onClickAddProBtn() {
        Intent intent = NewProductActivity_.intent(this).get();
        intent.putExtra(NewProductActivity.SHOP_CODE_EXTRA, user.getShopCode());
        intent.putExtra("from", "home");
        startActivity(intent);
    }

    /**
     * 点击管理商品
     */
    @Click(R.id.main_btn_manage_pro)
    void onClickManageProBtn() {
        HeaderAnimatorActivity_.intent(this).start();
    }

    /**
     * 点击店铺管理
     */
    @Click(R.id.main_btn_shop_config)
    void onClickManageShopBtn() {
        ShopConfigActivity_.intent(this).start();
    }

    /**
     * 点击动态管理
     */
    @Click(R.id.main_btn_manage_activity)
    void onClickManageActivityBtn() {
        ActivityManageActivity_.intent(this).start();
    }

    /**
     * 点击折扣管理
     */
    @Click(R.id.main_btn_manage_sale)
    void onClickManageDynamicBtn() {
        DynamicManageActivity_.intent(this).start();
    }

    /**
     * 点击分类管理
     */
    @Click(R.id.main_btn_manage_cover)
    void onClickManageCoverBtn() {
        // Intent intent=new Intent(this,CoverManageActivity.class);
        CoverManageActivity_.intent(this).start();
        // startActivity(intent);
    }

    /**
     * 点击上传认证资料
     */
    @Click(R.id.main_shop_authentic_no)
    void uploadAuthentiClick(){
        //authenticState
        Intent intent= ShopAuthenticActivity_.intent(this).get();
        startActivityForResult(intent,111);
       // intent.putExtra("authenticState", authenticState);
    }
   /* *//**
     * 点击查看上传资料
     *//*
    @Click(R.id.main_shop_authentic_no)
    void noAuthentiClick(){
        //authenticState
       ShopAuthenticActivity_.intent(this).start();
    }*/

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
    void onClickManageUserInfoBtn() {
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
    void onClickMessageInfoBtn() {
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
     * 当点击帮助
     */
    @Click(R.id.main_help)
    void onClickHelp() {
        HelpInfoActivity_.intent(this).start();
    }

    /**
     * 当点击联系我们
     */
    @Click(R.id.main_link)
    void onClickLinkUs() {
        UserFeedBackActivity_.intent(this).start();
    }
    /**
     * 当点击用户反馈
     */
    @Click(R.id.main_feed_back)
    void onClickFeedBack() {
        //FeedbackAgent agent = new FeedbackAgent(this);
        //agent.startFeedbackActivity();
       // UserFeedBackActivity_.intent(this).start();
        CustomActivity_.intent(this).start();
    }

    /**
     * 当点击更新版本
     */
    @Click(R.id.main_update_layout)
    void onClickUpdateVision() {
       /* UpdateManager manager = new UpdateManager(SuperMainActivity.this);
        // 检查软件更新
        manager.checkUpdate(0);*/
        //友盟检查更新
        UmengUpdateAgent.update(this);
    }

    /**
     * 点击左侧头部
     */
    @Click(R.id.super_left_head)
    void onCLickHomeHead() {

    }

    /**
     * 用户退出清空数据
     */
    void userQuitClean() {
        myApplication.setUser(null);
        myApplication.setShop(null);
        //清除本地默认登录数据
        new Delete().from(UserModel.class).where("UserAccount = ?", user.getAccount()).execute();
    }

    /**
     * 实例化头部新闻
     */
    @UiThread
    void initDataHead(List<Notice> notices) {
        //HashMap<String,String> url_maps = new HashMap<String, String>();
        if (notices != null && !notices.isEmpty()) {
            for (Notice ls : notices) {
                TextSliderView textSliderView = new TextSliderView(this);
                // url_maps.put(ls.getTitle(), ls.getSource());
                textSliderView
                        .description(ls.getTitle())
                        .image(DataUrlContents.IMAGE_HOST + ls.getSource())
                        .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                        .setOnSliderClickListener(this);
                textSliderView.bundle(new Bundle());
                textSliderView.getBundle().putString("extra", ls.getNewCode());
                mDemoSlider.addSlider(textSliderView);
            }
        }

        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.ZoomOut);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(8000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mDemoSlider.startAutoCycle();
            }
        }, 5000);
        // mDemoSlider.addOnPageChangeListener(this);
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        // Toast.makeText(this,slider.getBundle().get("extra") + "",Toast.LENGTH_SHORT).show();
        Intent intent = WebViewActivity_.intent(SuperMainActivity.this).get();
        intent.putExtra(WebViewActivity.NOTICE_TYPE, "0");
        intent.putExtra(WebViewActivity.NOTICE_CODE, slider.getBundle().get("extra") + "");
        startActivity(intent);
    }

    /**
     * 读取数据
     */
    private void loadData() {
        RequestParams params = new RequestParams();
        params.put("shopCode", user.getShopCode());
        params.put("pageSize", 500);
        client.get(DataUrlContents.SERVER_HOST + DataUrlContents.load_shop_statistical_info, params, new BaseJsonHttpResponseHandler<IndexVO>() {


            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, IndexVO obj) {
                Log.d(LoginSystemActivity.class.getName(), statusCode + "");
                if (null != obj) {
                    //商品数
                    if (null != obj.getTotalProNum()) {
                        all_pro_number.setText(obj.getTotalProNum() + "");
                    }
                    //总浏览
                    if (null != obj.getAccessShopNum()) {
                        all_view_number.setText(obj.getAccessShopNum() + "");
                    }
                    //店铺收藏
                    if (null != obj.getShopcollectNum()) {
                        all_shop_collect_number.setText(obj.getShopcollectNum() + "");
                    }
                    //商品收藏
                    if (null != obj.getAllshopcollectNum()) {
                        all_pro_collect_number.setText(obj.getAllshopcollectNum() + "");
                    }
                    //店铺认证
                    if (null != obj.getAuthenticState()) {
                        authenticState = obj.getAuthenticState();
                        if (authenticState == 3) {
                            main_shop_authentic_no.setVisibility(View.GONE);
                            main_shop_authentic_ok.setVisibility(View.VISIBLE);
                        } else {
                            main_shop_authentic_no.setVisibility(View.VISIBLE);
                            main_shop_authentic_ok.setVisibility(View.GONE);
                            if (authenticState == 1) {
                                main_shop_authentic_no_text.setText("申请中");
                            } else if (authenticState == 2) {
                                main_shop_authentic_no_text.setText("处理中");
                            } else if (authenticState == 4) {
                                main_shop_authentic_no_text.setText("未通过");
                            }
                        }
                    }
                    initDataHead(obj.getNotices());
                    noticeList = obj.getMessAge();
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




    /**
     * 返回结果回调
     * @param requestCode
     * @param resultCode
     * @param result
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if(requestCode==111&& resultCode == RESULT_OK){
            authenticState=result.getIntExtra("authState",0);
            if (authenticState == 1) {
                main_shop_authentic_no_text.setText("申请中");
            } else if (authenticState == 2) {
                main_shop_authentic_no_text.setText("处理中");
            } else if (authenticState == 3) {
                main_shop_authentic_no.setVisibility(View.GONE);
                main_shop_authentic_ok.setVisibility(View.VISIBLE);
            }else if (authenticState == 4) {
                main_shop_authentic_no_text.setText("未通过");
            }else{
                main_shop_authentic_no_text.setText("未认证");
            }
        }
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
        // initData();
        if(null!=mDrawerToggle2||null!=mDrawerToggle){
            if(sdkLevel>=19){
                mDrawerToggle2.syncState();
            }else{
                mDrawerToggle.syncState();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(sdkLevel>=19){
            mDrawerToggle2.syncState();
        }else {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    private long firstTime = 0;

    /**
     * 处理后退事件
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {                                         //如果两次按键时间间隔大于2秒，则不退出
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                firstTime = secondTime;//更新firstTime
                return true;
            } else {                                                    //两次按键小于2秒时，退出应用
                super.AppExit(this);
            }
        }
        return true;
    }

    @UiThread
    public void reloadImage(String url){
        ImageManager.imageLoader.displayImage(DataUrlContents.IMAGE_HOST + url + DataUrlContents.img_logo_img, shop_logo_image, options);
        ImageManager.imageLoader.displayImage(DataUrlContents.IMAGE_HOST + url + DataUrlContents.img_logo_img, shop_logo_image, options);
    }

    /**
     * 接收广播
     */
    public class SuperActivityReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String fileName = intent.getStringExtra("fileName");
            String shopName = intent.getStringExtra("shopName");
            //Toast.makeText(SuperMainActivity.this, "接收到广播,fileName:" + fileName, Toast.LENGTH_SHORT).show();
            if(null!=fileName){
                reloadImage(fileName);
            }else if(null!=shopName){
                reg_shop_name.setText(shopName);
            }
        }
    }
}
