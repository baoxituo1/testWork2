package com.trade.bluehole.trad.activity.sale;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.trade.bluehole.trad.DynamicManageActivity;
import com.trade.bluehole.trad.LoginSystemActivity;
import com.trade.bluehole.trad.R;
import com.trade.bluehole.trad.activity.actity.NewActivityShopActivity;
import com.trade.bluehole.trad.activity.actity.NewActivityShopActivity_;
import com.trade.bluehole.trad.adaptor.dynamic.ProductSaleDynamicAdapter;
import com.trade.bluehole.trad.adaptor.sale.SelectProductSaleAdapter;
import com.trade.bluehole.trad.entity.User;
import com.trade.bluehole.trad.entity.dynamic.DynaicInfoVO;
import com.trade.bluehole.trad.entity.sale.ProductSaleVO;
import com.trade.bluehole.trad.util.MyApplication;
import com.trade.bluehole.trad.util.Result;
import com.trade.bluehole.trad.util.data.DataUrlContents;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.activity_select_sale_product)
public class SelectSaleProductActivity extends ActionBarActivity {

    AsyncHttpClient client = new AsyncHttpClient();
    Gson gson = new Gson();
    @App
    MyApplication myApplication;
    @ViewById
    ListView listView;

    User user;
    List<ProductSaleVO> lists=new ArrayList<>();//活动数据集
    SelectProductSaleAdapter adapter;


    @AfterViews
    void initData(){
        user=myApplication.getUser();//获取用户数据
        adapter=new SelectProductSaleAdapter(this);
        //list 点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProductSaleVO obj=lists.get(position);
                if(null!=obj){
                    Intent intent= NewActivityShopActivity_.intent(SelectSaleProductActivity.this).get();
                    intent.putExtra(NewActivityShopActivity.ACTIVITY_CODE_EXTRA,obj.getDynamicCode());
                    startActivity(intent);
                }
            }
        });
        loadData();//装载数据
    }


    /**
     * 读取数据
     */
    private void loadData(){
        RequestParams params=new RequestParams();
        params.put("shopCode",user.getShopCode());
        params.put("pageSize", 500);
        client.get(DataUrlContents.SERVER_HOST + DataUrlContents.load_product_sale_detail, params, new BaseJsonHttpResponseHandler<Result<ProductSaleVO,String>>() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Result<ProductSaleVO, String> obj) {
                Log.d(LoginSystemActivity.class.getName(), statusCode + "");
                if (null != obj && obj.success) {

                    lists.clear();
                    lists.addAll(obj.getList());
                    adapter.setLists(lists);
                    listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Result<ProductSaleVO, String> errorResponse) {
                Toast.makeText(SelectSaleProductActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected Result<ProductSaleVO, String> parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return gson.fromJson(rawJsonData, new TypeToken<Result<ProductSaleVO, String>>() {
                }.getType());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select_sale_product, menu);
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
}
