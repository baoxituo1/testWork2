package com.trade.bluehole.trad;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.trade.bluehole.trad.adaptor.ProductGridviewAdaptor;
import com.trade.bluehole.trad.entity.ProductIndexVO;
import com.trade.bluehole.trad.entity.User;
import com.trade.bluehole.trad.util.MyApplication;
import com.trade.bluehole.trad.util.Result;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.ViewsById;
import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.activity_product_manager)
public class ProductManagerActivity extends ActionBarActivity {
    @App
    MyApplication myapplication;
    ProductGridviewAdaptor adaptor;
    AsyncHttpClient client = new AsyncHttpClient();
    Gson gson = new Gson();
    List<ProductIndexVO>mList=new ArrayList<ProductIndexVO>();
   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_manager);
    }*/

    @ViewById
    GridView product_gridview;



    @AfterViews
    void initGridView(){
       adaptor=new ProductGridviewAdaptor(this);
       User user= myapplication.getUser();
        if(user!=null&&user.getUserCode()!=null){
            RequestParams params=new RequestParams();
            params.put("userCode",user.getUserCode());
            params.put("pageSize",500);
            client.get("http://192.168.1.161:8080/qqt_up/shopjson/findUserProList.do", params, new BaseJsonHttpResponseHandler<Result<ProductIndexVO, String>>() {


                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Result<ProductIndexVO, String> response) {
                    Log.d(LoginSystemActivity.class.getName(), statusCode + "");
                    if (null != response) {
                        if (response.isSuccess()) {
                            Toast.makeText(ProductManagerActivity.this, "获取数据成功", Toast.LENGTH_SHORT).show();
                            //把数据添加到全局
                            mList.addAll(response.getAaData());
                            adaptor.setLists(response.getAaData());
                            product_gridview.setAdapter(adaptor);
                            adaptor.notifyDataSetChanged();
                        } else {
                            Toast.makeText(ProductManagerActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
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
     * 当视图的选项被点击
     * @param position
     */
    @ItemClick(R.id.product_gridview)
    public void gridViewItemClicked(int position){
        if(mList!=null&&mList.size()>position){
            ProductIndexVO pr= mList.get(position);
            Intent intent=NewProductActivity_.intent(this).get();
            intent.putExtra(NewProductActivity.PRODUCT_CODE_EXTRA,pr.getProductCode());
            intent.putExtra(NewProductActivity.SHOP_CODE_EXTRA,pr.getShopCode());
            startActivity(intent);
        }else{
            Toast.makeText(ProductManagerActivity.this, "数据错误", Toast.LENGTH_SHORT).show();
        }

    }

}
