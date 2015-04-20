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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.trade.bluehole.trad.R;
import com.trade.bluehole.trad.activity.BaseActionBarActivity;
import com.trade.bluehole.trad.activity.actity.NewActivityShopActivity;
import com.trade.bluehole.trad.activity.actity.NewActivityShopActivity_;
import com.trade.bluehole.trad.adaptor.actity.ShopActivityListAdapter;
import com.trade.bluehole.trad.entity.User;
import com.trade.bluehole.trad.entity.actity.ShopActivity;
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

@EActivity(R.layout.activity_activity_manage)
public class ActivityManageActivity extends BaseActionBarActivity {
    @App
    MyApplication myApplication;
    @ViewById
    ListView listView;
    @ViewById
    RelativeLayout empty_view;
    //页面进度条
    SweetAlertDialog pDialog;
    ShopActivityListAdapter adapter=new ShopActivityListAdapter(this,null);
    User user;
    List<ShopActivity> lists=new ArrayList<ShopActivity>();//活动数据集


    @AfterViews
    void initData(){
        user=myApplication.getUser();//获取用户数据
        pDialog=getDialog(this);//获取进度实例化
        listView.setEmptyView(empty_view);
        //list 点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShopActivity obj=lists.get(position);
                if(null!=obj){
                    Intent intent=NewActivityShopActivity_.intent(ActivityManageActivity.this).get();
                    intent.putExtra(NewActivityShopActivity.ACTIVITY_CODE_EXTRA,obj.getActivityCode());
                    startActivity(intent);
                }
            }
        });
        loadData();//装载数据
    }

    /**
     * 点击列表为空的快速添加
     */
    @Click(R.id.empty_view)
    void onClickQuickAdd(){
        NewActivityShopActivity_.intent(this).start();
    }

    /**
     * 读取数据
     */
    private void loadData(){
        pDialog.show();
        RequestParams params=new RequestParams();
        params.put("shopCode",user.getShopCode());
        params.put("pageSize",500);
        getClient().get(DataUrlContents.SERVER_HOST + DataUrlContents.load_shop_activity, params, new BaseJsonHttpResponseHandler<Result<ShopActivity, String>>() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Result<ShopActivity, String> obj) {
                pDialog.hide();
                if (null != obj && obj.success) {

                    lists.clear();
                    lists.addAll(obj.getList());
                    adapter.setLists(lists);
                    listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Result<ShopActivity, String> errorResponse) {
                pDialog.hide();
                Toast.makeText(ActivityManageActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected Result<ShopActivity, String> parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return gson.fromJson(rawJsonData, new TypeToken<Result<ShopActivity, String>>() {
                }.getType());
            }
        });
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_manage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.activity_menu_add) {
            NewActivityShopActivity_.intent(this).start();
            return true;
        }else if(id==android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        pDialog.dismiss();
    }
}
