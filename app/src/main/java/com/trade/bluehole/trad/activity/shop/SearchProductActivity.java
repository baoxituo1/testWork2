package com.trade.bluehole.trad.activity.shop;

import android.app.Activity;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.trade.bluehole.trad.R;
import com.trade.bluehole.trad.adaptor.pro.ProductListViewAdaptor;
import com.trade.bluehole.trad.entity.ProductIndexVO;
import com.trade.bluehole.trad.entity.User;
import com.trade.bluehole.trad.entity.shop.ShopCommonInfo;
import com.trade.bluehole.trad.util.MyApplication;
import com.trade.bluehole.trad.util.Result;
import com.trade.bluehole.trad.util.data.DataUrlContents;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;

import org.androidannotations.annotations.AfterExtras;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 查询商品
 */
@EActivity(R.layout.activity_search_product)
public class SearchProductActivity extends Activity {

    // 首先在您的Activity中添加如下成员变量
    public final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
    @App
    MyApplication myApplication;

    @ViewById(R.id.listView)
    ListView listView;
    @ViewById
    EditText search_pro_name;
    @ViewById
    RelativeLayout empty_view;
    @ViewById
    TextView no_item_text;

    private static AsyncHttpClient client;
    public static Gson gson = new Gson();
    //页面进度条
    SweetAlertDialog pDialog;
    ShopCommonInfo shop;
    User user;
    ProductListViewAdaptor adaptor;
    List<ProductIndexVO> mList=new ArrayList<>();


    @AfterViews
    void initData(){
        // ActionBar actionBar = getActionBar();
        // actionBar.setDisplayHomeAsUpEnabled(true);
        //no_item_text.setText("输入商品名称搜索商品吧~");
        listView.setEmptyView(empty_view);
        user = myApplication.getUser();
        shop = myApplication.getShop();
        adaptor=new ProductListViewAdaptor(this,"search");

        client = new AsyncHttpClient();
        client.setTimeout(30000);//30秒

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("读取中...");
        pDialog.setCancelable(false);

        mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
                SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.SINA, SHARE_MEDIA.TENCENT, SHARE_MEDIA.DOUBAN,
                SHARE_MEDIA.RENREN, SHARE_MEDIA.YIXIN);
        //设置新浪SSO handler
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        //设置腾讯微博SSO handler
        mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
        //loadListView();
    }


    /**
     * 当点击查询按钮
     */
    @Click(R.id.pro_search_btn)
    void onCLickSearchBtn(){
        if(null!=search_pro_name&&!"".equals(search_pro_name.getText().toString())){
            loadListView(search_pro_name.getText().toString());
        }else{
            Toast.makeText(SearchProductActivity.this, "请输入商品名称", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * load数据
     */
    private void loadListView(String proName) {
        if(user!=null){
            pDialog.show();
            RequestParams params=new RequestParams();
            params.put("userCode",user.getUserCode());
            params.put("shopCode",user.getShopCode());
            params.put("proName",proName);
            params.put("pageSize",500);
            client.post(DataUrlContents.SERVER_HOST + DataUrlContents.load_pro_all_list, params, new BaseJsonHttpResponseHandler<Result<ProductIndexVO, String>>() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Result<ProductIndexVO, String> response) {
                    pDialog.hide();
                    if (null != response) {
                        no_item_text.setText("没有查询到数据,换个名称搜索~");
                        if (response.isSuccess()) {
                            // Toast.makeText(ProductClassifyActivity.this, R.string.load_data_success, Toast.LENGTH_SHORT).show();
                            //把数据添加到全局
                            mList.clear();
                            mList.addAll(response.getAaData());
                            adaptor.setLists(mList);
                            listView.setAdapter(adaptor);
                            adaptor.notifyDataSetChanged();
                        } else {
                            Toast.makeText(SearchProductActivity.this, R.string.load_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Result<ProductIndexVO, String> errorResponse) {
                    pDialog.hide();
                    Toast.makeText(SearchProductActivity.this, R.string.load_data_error, Toast.LENGTH_SHORT).show();
                }

                @Override
                protected Result<ProductIndexVO, String> parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    // return new ObjectMapper().readValues(new JsonFactory().createParser(rawJsonData), Result.class).next();
                    return gson.fromJson(rawJsonData, new TypeToken<Result<ProductIndexVO, String>>() {
                    }.getType());
                }
            });
        }
    }


    public void shareProduct(String code,String proName,String imagUrl){
        String _targetUrl=DataUrlContents.SERVER_HOST + DataUrlContents.show_view_pro_web + "?productCode=" + code + "&shopCode=" + user.getShopCode();
        // 设置分享内容
        mController.setShareContent(proName+_targetUrl);
        // 设置分享图片, 参数2为图片的url地址
        mController.setShareMedia(new UMImage(this, imagUrl));
        // 添加QQ支持, 并且设置QQ分享内容的target url
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this,  myApplication.qq_appId, myApplication.qq_appKey);
        qqSsoHandler.setTitle(shop.getTitle());
        qqSsoHandler.setTargetUrl(DataUrlContents.SERVER_HOST + DataUrlContents.show_view_pro_web + "?productCode=" + code + "&shopCode=" + user.getShopCode());
        qqSsoHandler.addToSocialSDK();
        // 添加QZone平台
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this, myApplication.qq_appId, myApplication.qq_appKey);
        qZoneSsoHandler.addToSocialSDK();

        mController.openShare(this, false);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        pDialog.dismiss();
    }
}
