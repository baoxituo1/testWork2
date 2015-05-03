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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.trade.bluehole.trad.activity.BaseActionBarActivity;
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
import com.yalantis.phoenix.PullToRefreshView;

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
 * 店铺打折动态
 */
@EActivity(R.layout.activity_dynamic_manage)
public class DynamicManageActivity extends BaseActionBarActivity {
    public static final int REFRESH_DELAY = 1500;
    @App
    MyApplication myApplication;
    @ViewById
    ListView listView;
    @ViewById
    RelativeLayout empty_view;
    private PullToRefreshView mPullToRefreshView;

    //页面进度条
    SweetAlertDialog pDialog;
    User user;
    List<DynaicInfoVO> lists=new ArrayList<DynaicInfoVO>();//活动数据集
    ProductSaleDynamicAdapter adapter;//适配器
    DialogPlus confirmDialog;//确认操作
    String tempProCode;//临时code

    @AfterViews
    void initData(){
        user=myApplication.getUser();//获取用户数据

        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
               /* mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullToRefreshView.setRefreshing(false);

                    }
                }, REFRESH_DELAY);*/
                loadData();
            }
        });

        adapter=new ProductSaleDynamicAdapter(this);

        listView.setEmptyView(empty_view);
        initDialog();
        loadData();//装载数据
    }

    /**
     * 点击列表为空的快速添加
     */
    @Click(R.id.empty_view)
    void onClickQuickAdd(){
        SelectSaleProductActivity_.intent(this).start();
    }


    /**
     * 删除弹出框按钮点击事件
     */
    OnClickListener delClickListener = new OnClickListener() {
        @Override
        public void onClick(DialogPlus dialog, View view) {
            switch (view.getId()) {
                case R.id.footer_confirm_button:
                    //删除商品
                    delSaleDynamic(tempProCode);
                    break;
                case R.id.footer_close_button:
                    break;
            }
            dialog.dismiss();
        }
    };
    /**
     * 实例化弹出窗口
     */
    void initDialog(){
        pDialog=getDialog(this);//获取进度实例化
        confirmDialog = new DialogPlus.Builder(this)
                .setContentHolder(new ViewHolder(R.layout.dialog_confirm_content))
                        //.setHeader(R.layout.dialog_label_header)
                .setFooter(R.layout.dialog_footer)
                .setGravity(DialogPlus.Gravity.BOTTOM)
                .setOnClickListener(delClickListener)
                .create();
    }

    /**
     * 读取数据
     */
    private void loadData(){
        pDialog.show();
        RequestParams params=new RequestParams();
        params.put("shopCode",user.getShopCode());
        params.put("pageSize", 500);
        getClient().get(DataUrlContents.SERVER_HOST + DataUrlContents.load_dynamic_sale_detail, params, new BaseJsonHttpResponseHandler<Result<DynaicInfoVO, String>>() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Result<DynaicInfoVO, String> obj) {
                pDialog.hide();
                mPullToRefreshView.setRefreshing(false);
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
                pDialog.hide();
            }

            @Override
            protected Result<DynaicInfoVO, String> parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return gson.fromJson(rawJsonData, new TypeToken<Result<DynaicInfoVO, String>>() {
                }.getType());
            }
        });
    }

    /**
     * 展示删除动态的确认按钮
     * @param proCode
     */
    public void showDelConfirm(String proCode){
        tempProCode=proCode;
        confirmDialog.show();
    }


    /**
     * 删除动态
     * @param proCode
     */
    void delSaleDynamic(String proCode){
        RequestParams params=new RequestParams();
        params.put("productCode",proCode);
        params.put("salePrice", 0);
        getClient().get(DataUrlContents.SERVER_HOST + DataUrlContents.update_product_sale_detail, params, new BaseJsonHttpResponseHandler<String>() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, String obj) {
                Log.d(LoginSystemActivity.class.getName(), statusCode + "");
                if (null != obj) {
                    loadData();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, String errorResponse) {
                Toast.makeText(DynamicManageActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected String parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return gson.fromJson(rawJsonData, String.class);
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
        }else if(id==android.R.id.home){
            //点击后退跳转到 主页
            SuperMainActivity_.intent(this).start();
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
