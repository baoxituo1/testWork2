package com.trade.bluehole.trad.activity.shop;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.trade.bluehole.trad.NewProductActivity;
import com.trade.bluehole.trad.NewProductActivity_;
import com.trade.bluehole.trad.R;
import com.trade.bluehole.trad.activity.BaseActionBarActivity;
import com.trade.bluehole.trad.adaptor.pro.ProductListViewAdaptor;
import com.trade.bluehole.trad.entity.ProductIndexVO;
import com.trade.bluehole.trad.entity.User;
import com.trade.bluehole.trad.entity.shop.ShopCommonInfo;
import com.trade.bluehole.trad.util.MyApplication;
import com.trade.bluehole.trad.util.Result;
import com.trade.bluehole.trad.util.data.DataUrlContents;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

import org.androidannotations.annotations.AfterExtras;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;
import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

@EActivity(R.layout.activity_product_classify)
public class ProductClassifyActivity extends BaseActionBarActivity {
    //商品和店铺编码标志
    public static final String SHOP_CODE_EXTRA = "shopCode";
    public static final String USER_CODE_EXTRA = "userCode";
    public static final String COVER_CODE_EXTRA = "coverCode";
    public static final String COVER_NAME_EXTRA = "coverName";

    /**
     * 登陆信息
     */
    ShopCommonInfo shop;
    User user;

    @Extra(SHOP_CODE_EXTRA)
    String shopCode;
    @Extra(USER_CODE_EXTRA)
    String userCode;
    @Extra(COVER_CODE_EXTRA)
    String coverCode;
    @Extra(COVER_NAME_EXTRA)
    String coverName;

    @App
    MyApplication myApplication;
    @ViewById(R.id.listview)
    ListView listview;
    ProductListViewAdaptor adaptor;
    List<ProductIndexVO> mList=new ArrayList<ProductIndexVO>();
    //页面进度条
    SweetAlertDialog pDialog;
   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_classify);
    }*/

    @AfterViews
    void initData(){
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
       // actionBar.setDisplayHomeAsUpEnabled(true);
        if(null==coverName||"".equals(coverName)){
            actionBar.setTitle("未分类");
        }else{
            actionBar.setTitle(coverName);
        }
        user = myApplication.getUser();
        shop = myApplication.getShop();
        adaptor=new ProductListViewAdaptor(this,"class");

        pDialog = getDialog(this);
        if(null==coverCode||"".equals(coverCode)){
            coverCode="no_cover";
        }
        populateListView();
    }


    /**
     * 当视图的选项被点击
     * @param position
     */
    @ItemClick(R.id.listview)
    public void gridViewItemClicked(int position){
            ProductIndexVO pr= mList.get(position>1?position-1:position);
            Intent intent= NewProductActivity_.intent(this).get();
            intent.putExtra(NewProductActivity.PRODUCT_CODE_EXTRA,pr.getProductCode());
            intent.putExtra(NewProductActivity.SHOP_CODE_EXTRA,pr.getShopCode());
            startActivity(intent);
    }


    /**
     * load数据
     */
    private void populateListView() {
        if(shopCode!=null&&userCode!=null){
            pDialog.show();
            RequestParams params=new RequestParams();
            params.put("userCode",userCode);
            params.put("shopCode",shopCode);
            params.put("coverCode",coverCode);
            params.put("pageSize",500);
            getClient().get(DataUrlContents.SERVER_HOST+DataUrlContents.load_pro_all_list, params, new BaseJsonHttpResponseHandler<Result<ProductIndexVO, String>>() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Result<ProductIndexVO, String> response) {
                    pDialog.hide();
                    if (null != response) {
                        if (response.isSuccess()) {
                            // Toast.makeText(ProductClassifyActivity.this, R.string.load_data_success, Toast.LENGTH_SHORT).show();
                            //把数据添加到全局
                            mList.clear();
                            mList.addAll(response.getAaData());
                            adaptor.setLists(mList);
                            listview.setAdapter(adaptor);
                            adaptor.notifyDataSetChanged();
                        } else {
                            //Toast.makeText(ProductClassifyActivity.this, R.string.load_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Result<ProductIndexVO, String> errorResponse) {
                    pDialog.hide();
                    Toast.makeText(ProductClassifyActivity.this, R.string.load_data_error, Toast.LENGTH_SHORT).show();
                }

                @Override
                protected Result<ProductIndexVO, String> parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    // return new ObjectMapper().readValues(new JsonFactory().createParser(rawJsonData), Result.class).next();
                    return gson.fromJson(rawJsonData, new TypeToken<Result<ProductIndexVO,String>>(){}.getType());
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

        UMImage urlImage = new UMImage(this,imagUrl);

        //设置微信好友分享内容
        WeiXinShareContent weixinContent = new WeiXinShareContent();
        //设置分享文字
        weixinContent.setShareContent(proName);
        //设置title
        weixinContent.setTitle(shop.getTitle());
        //设置分享内容跳转URL
        weixinContent.setTargetUrl(_targetUrl);
        //设置分享图片
        weixinContent.setShareImage(urlImage);
        mController.setShareMedia(weixinContent);

        //设置微信朋友圈分享内容
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareContent(proName);
        //设置朋友圈title
        circleMedia.setTitle(shop.getTitle());
        circleMedia.setShareImage(urlImage);
        circleMedia.setTargetUrl(_targetUrl);
        mController.setShareMedia(circleMedia);



        mController.openShare(this, false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_product_classify, menu);
        return true;
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

    @Override
    public void onDestroy(){
        super.onDestroy();
        pDialog.dismiss();
    }
}
