package com.trade.bluehole.trad;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.trade.bluehole.trad.adaptor.ProductGridviewAdaptor;
import com.trade.bluehole.trad.adaptor.pro.ProductCoverNumberAdapter;
import com.trade.bluehole.trad.adaptor.pro.ProductListViewAdaptor;
import com.trade.bluehole.trad.animator.IO2014HeaderAnimator;
import com.trade.bluehole.trad.entity.ProductIndexVO;
import com.trade.bluehole.trad.entity.User;
import com.trade.bluehole.trad.entity.pro.ProductCoverRelVO;
import com.trade.bluehole.trad.entity.shop.ShopCommonInfo;
import com.trade.bluehole.trad.util.ImageManager;
import com.trade.bluehole.trad.util.MyApplication;
import com.trade.bluehole.trad.util.Result;
import com.trade.bluehole.trad.util.data.DataUrlContents;


import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import it.carlom.stikkyheader.core.StikkyHeaderBuilder;
import mehdi.sakout.fancybuttons.FancyButton;

@EActivity(R.layout.activity_header_animator)
public class HeaderAnimatorActivity extends ActionBarActivity {

    @ViewById(R.id.listview)
    ListView listview;
    @ViewById
    TextView shopName;
    @ViewById
    CircleImageView shop_logo_image;
    @ViewById
    FancyButton main_sale_ing_btn,main_sale_out_btn,main_sale_cover_btn;
    @App
    MyApplication myApplication;
    ProductListViewAdaptor adaptor;
    ProductCoverNumberAdapter coverNumberAdapter;
    AsyncHttpClient client = new AsyncHttpClient();
    Gson gson = new Gson();
    List<ProductIndexVO> mList=new ArrayList<ProductIndexVO>();
    //页面进度条
    SweetAlertDialog pDialog;
    private String searchType="1";
    private String serverName= DataUrlContents.load_pro_all_list;
    /**
     * 登陆信息
     */
    ShopCommonInfo shop;
    User user;

   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_header_animator);
        mListView = (ListView) this.findViewById(R.id.listview);
        initData();
    }*/

    @AfterViews
    void initData(){
        user=myApplication.getUser();
        shop=myApplication.getShop();
        adaptor=new ProductListViewAdaptor(this);
        coverNumberAdapter=new ProductCoverNumberAdapter(this);
        if(shop!=null){
            shopName.setText(shop.getTitle());
            if(null!=shop.getShopLogo()){
                ImageManager.imageLoader.displayImage("http://125.oss-cn-beijing.aliyuncs.com/" + shop.getShopLogo(),shop_logo_image,ImageManager.options);
            }
        }
        IO2014HeaderAnimator animator = new IO2014HeaderAnimator(this);
        StikkyHeaderBuilder.stickTo(listview)
                .setHeader(R.id.header, (ViewGroup) getWindow().getDecorView())
                .minHeightHeaderDim(R.dimen.min_height_header_materiallike)
                .animator(animator)
                .build();
        //初始化等待dialog
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        populateListView();
        //loadCoverListView();
    }

    /**
     * load数据
     */
    private void populateListView() {
        User user= myApplication.getUser();
        if(user!=null&&user.getUserCode()!=null){
           pDialog.show();
            RequestParams params=new RequestParams();
            params.put("userCode",user.getUserCode());
            params.put("type",searchType);
            params.put("pageSize",500);
            client.get(DataUrlContents.SERVER_HOST+DataUrlContents.load_pro_all_list, params, new BaseJsonHttpResponseHandler<Result<ProductIndexVO, String>>() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Result<ProductIndexVO, String> response) {
                    pDialog.hide();
                    if (null != response) {
                        if (response.isSuccess()) {
                           // Toast.makeText(HeaderAnimatorActivity.this, "获取数据成功", Toast.LENGTH_SHORT).show();
                            //把数据添加到全局
                            mList.clear();
                            mList.addAll(response.getAaData());
                            adaptor.setLists(mList);
                            listview.setAdapter(adaptor);
                            adaptor.notifyDataSetChanged();
                        } else {
                            Toast.makeText(HeaderAnimatorActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Result<ProductIndexVO, String> errorResponse) {

                }

                @Override
                protected Result<ProductIndexVO, String> parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    // return new ObjectMapper().readValues(new JsonFactory().createParser(rawJsonData), Result.class).next();
                    return gson.fromJson(rawJsonData, new TypeToken<Result<ProductIndexVO,String>>(){}.getType());
                }
            });
        }
    }
    /**
     * load数据
     */
    private void loadCoverListView() {
        User user= myApplication.getUser();
        if(user!=null&&user.getUserCode()!=null){
            pDialog.show();
            RequestParams params=new RequestParams();
            params.put("userCode",user.getUserCode());
            params.put("shopCode",user.getShopCode());
            params.put("pageSize",500);
            serverName=DataUrlContents.load_pro_covers_number_list;
            client.get(DataUrlContents.SERVER_HOST+serverName, params, new BaseJsonHttpResponseHandler<Result<ProductCoverRelVO, String>>() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Result<ProductCoverRelVO, String> response) {
                    pDialog.hide();
                    if (null != response) {
                        if (response.isSuccess()) {
                            //Toast.makeText(HeaderAnimatorActivity.this, "获取数据成功", Toast.LENGTH_SHORT).show();
                            //把数据添加到全局
                            coverNumberAdapter.setLists(response.getList());
                            listview.setAdapter(coverNumberAdapter);
                            coverNumberAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(HeaderAnimatorActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Result<ProductCoverRelVO, String> errorResponse) {

                }

                @Override
                protected Result<ProductCoverRelVO, String> parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    // return new ObjectMapper().readValues(new JsonFactory().createParser(rawJsonData), Result.class).next();
                    return gson.fromJson(rawJsonData, new TypeToken<Result<ProductCoverRelVO,String>>(){}.getType());
                }
            });
        }
    }

    /**
     * 点击销售中
     */
    @Click(R.id.main_sale_ing_btn)
    void onSaleIngClick(){
        searchType="1";
        main_sale_ing_btn.setTextColor(getResources().getColor(R.color.red_btn_bg_color));
        main_sale_out_btn.setTextColor(getResources().getColor(R.color.white));
        main_sale_cover_btn.setTextColor(getResources().getColor(R.color.white));
        populateListView();
    }
    /**
     * 点击已下架
     */
    @Click(R.id.main_sale_out_btn)
    void onSaleOutClick(){
        searchType="0";
        main_sale_out_btn.setTextColor(getResources().getColor(R.color.red_btn_bg_color));
        main_sale_ing_btn.setTextColor(getResources().getColor(R.color.white));
        main_sale_cover_btn.setTextColor(getResources().getColor(R.color.white));
        populateListView();
    }
    /**
     * 点击分类
     */
    @Click(R.id.main_sale_cover_btn)
    void onCoverOutClick(){
        searchType="2";
        main_sale_cover_btn.setTextColor(getResources().getColor(R.color.red_btn_bg_color));
        main_sale_ing_btn.setTextColor(getResources().getColor(R.color.white));
        main_sale_out_btn.setTextColor(getResources().getColor(R.color.white));
        loadCoverListView();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        pDialog.dismiss();
    }
}
