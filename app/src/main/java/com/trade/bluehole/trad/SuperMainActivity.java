package com.trade.bluehole.trad;

import android.app.ActionBar;
import android.app.Activity;

import android.content.Intent;
import android.content.res.Configuration;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Delete;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.gson.Gson;
import com.ikimuhendis.ldrawer.ActionBarDrawerToggle;
import com.ikimuhendis.ldrawer.DrawerArrowDrawable;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.trade.bluehole.trad.activity.BaseActionBarActivity;
import com.trade.bluehole.trad.activity.BaseActivity;
import com.trade.bluehole.trad.activity.msg.MessagePageviewActivity_;
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
import com.trade.bluehole.trad.util.update.UpdateManager;
import com.trade.bluehole.trad.util.view.InnerListView;

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
public class SuperMainActivity extends BaseActivity implements  BaseSliderView.OnSliderClickListener{
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
    InnerListView listView;//站内信和新闻公告
    @ViewById //商品数 浏览量 收藏量 商品收藏 店铺名 账号信息
    TextView all_pro_number,all_view_number,all_shop_collect_number,all_pro_collect_number,reg_shop_name,account;
    @ViewById
    CircleImageView shop_logo_image;//左侧边栏logo
    @ViewById
    LinearLayout main_left_home_layout,main_left_user_layout,main_left_message_layout;

    //站内信集合列表
    List<IndexProCommentVO> noticeList;

    ShowcaseView showcaseView;
    private SliderLayout mDemoSlider;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_main);
        mDemoSlider = (SliderLayout)findViewById(R.id.slider);
    }


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
     * 当点击更新版本
     */
    @Click(R.id.main_update_layout)
    void onClickUpdateVision(){
        UpdateManager manager = new UpdateManager(SuperMainActivity.this);
        // 检查软件更新
        manager.checkUpdate(0);
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
     * 实例化头部新闻
     */
    @UiThread
    void initDataHead(List<Notice> notices){
        //HashMap<String,String> url_maps = new HashMap<String, String>();
        if(notices!=null&&!notices.isEmpty()){
            for(Notice ls:notices){
                TextSliderView textSliderView = new TextSliderView(this);
                // url_maps.put(ls.getTitle(), ls.getSource());
                textSliderView
                        .description(ls.getTitle())
                        .image(DataUrlContents.IMAGE_HOST+ls.getSource())
                        .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                        .setOnSliderClickListener(this);
                textSliderView.bundle(new Bundle());
                textSliderView.getBundle().putString("extra", ls.getNewCode());
                mDemoSlider.addSlider(textSliderView);
            }
        }

       /* for(String name : url_maps.keySet()){
            TextSliderView textSliderView = new TextSliderView(this);
            // initialize a SliderLayout
            textSliderView
                    .description(name)
                    .image(url_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit);
            //.setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra",name);

            mDemoSlider.addSlider(textSliderView);
        }*/
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.ZoomOut);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(6000);
        // mDemoSlider.addOnPageChangeListener(this);
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
       // Toast.makeText(this,slider.getBundle().get("extra") + "",Toast.LENGTH_SHORT).show();
        Intent intent = WebViewActivity_.intent(SuperMainActivity.this).get();
        intent.putExtra(WebViewActivity.NOTICE_TYPE, "0");
        intent.putExtra(WebViewActivity.NOTICE_CODE, slider.getBundle().get("extra")+"");
        startActivity(intent);
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

                    initDataHead(obj.getNotices());
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
       // initData();
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    private long firstTime = 0;
    /**
     * 处理后退事件
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK ){
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
}
