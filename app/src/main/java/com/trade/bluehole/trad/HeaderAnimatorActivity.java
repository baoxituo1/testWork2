package com.trade.bluehole.trad;

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
import com.trade.bluehole.trad.adaptor.pro.ProductListViewAdaptor;
import com.trade.bluehole.trad.animator.IO2014HeaderAnimator;
import com.trade.bluehole.trad.entity.ProductIndexVO;
import com.trade.bluehole.trad.entity.User;
import com.trade.bluehole.trad.entity.shop.ShopCommonInfo;
import com.trade.bluehole.trad.util.ImageManager;
import com.trade.bluehole.trad.util.MyApplication;
import com.trade.bluehole.trad.util.Result;


import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import it.carlom.stikkyheader.core.StikkyHeaderBuilder;

@EActivity(R.layout.activity_header_animator)
public class HeaderAnimatorActivity extends ActionBarActivity {

    @ViewById(R.id.listview)
    ListView listview;
    @ViewById
    TextView shopName;
    @ViewById
    CircleImageView shop_logo_image;
    @App
    MyApplication myApplication;
    ProductListViewAdaptor adaptor;
    AsyncHttpClient client = new AsyncHttpClient();
    Gson gson = new Gson();
    List<ProductIndexVO> mList=new ArrayList<ProductIndexVO>();
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
        populateListView();
    }

    /**
     * load数据
     */
    private void populateListView() {

        adaptor=new ProductListViewAdaptor(this);
        User user= myApplication.getUser();
        if(user!=null&&user.getUserCode()!=null){
            RequestParams params=new RequestParams();
            params.put("userCode",user.getUserCode());
            params.put("pageSize",500);
            client.get("http://192.168.1.161:8080/qqt_up/shopjson/findUserProList.do", params, new BaseJsonHttpResponseHandler<Result<ProductIndexVO, String>>() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Result<ProductIndexVO, String> response) {
                    if (null != response) {
                        if (response.isSuccess()) {
                            Toast.makeText(HeaderAnimatorActivity.this, "获取数据成功", Toast.LENGTH_SHORT).show();
                            //把数据添加到全局
                            mList.addAll(response.getAaData());
                            adaptor.setLists(response.getAaData());
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


}
