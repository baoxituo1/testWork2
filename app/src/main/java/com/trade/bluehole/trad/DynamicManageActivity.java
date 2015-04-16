package com.trade.bluehole.trad;

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
import com.trade.bluehole.trad.activity.actity.NewActivityShopActivity;
import com.trade.bluehole.trad.activity.actity.NewActivityShopActivity_;
import com.trade.bluehole.trad.activity.sale.SelectSaleProductActivity_;
import com.trade.bluehole.trad.adaptor.actity.ShopActivityListAdapter;
import com.trade.bluehole.trad.adaptor.dynamic.ProductSaleDynamicAdapter;
import com.trade.bluehole.trad.entity.User;
import com.trade.bluehole.trad.entity.actity.ShopActivity;
import com.trade.bluehole.trad.entity.dynamic.DynaicInfoVO;
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


@EActivity(R.layout.activity_dynamic_manage)
public class DynamicManageActivity extends ActionBarActivity {
    AsyncHttpClient client = new AsyncHttpClient();
    Gson gson = new Gson();
    @App
    MyApplication myApplication;
    @ViewById
    ListView listView;

    User user;
    List<DynaicInfoVO> lists=new ArrayList<DynaicInfoVO>();//活动数据集
    ProductSaleDynamicAdapter adapter;


    @AfterViews
    void initData(){
        user=myApplication.getUser();//获取用户数据
        adapter=new ProductSaleDynamicAdapter(this);
        //list 点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DynaicInfoVO obj=lists.get(position);
                if(null!=obj){
                    Intent intent= NewActivityShopActivity_.intent(DynamicManageActivity.this).get();
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
        client.get(DataUrlContents.SERVER_HOST + DataUrlContents.load_dynamic_sale_detail, params, new BaseJsonHttpResponseHandler<Result<DynaicInfoVO,String>>() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Result<DynaicInfoVO, String> obj) {
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
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Result<DynaicInfoVO, String> errorResponse) {
                Toast.makeText(DynamicManageActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected Result<DynaicInfoVO, String> parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return gson.fromJson(rawJsonData, new TypeToken<Result<DynaicInfoVO, String>>() {
                }.getType());
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dynamic_manage2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.dy_menu_add) {
            SelectSaleProductActivity_.intent(this).start();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}