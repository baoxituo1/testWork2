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
import com.trade.bluehole.trad.adaptor.pro.ProductListViewAdaptor;
import com.trade.bluehole.trad.entity.ProductIndexVO;
import com.trade.bluehole.trad.entity.User;
import com.trade.bluehole.trad.util.Result;
import com.trade.bluehole.trad.util.data.DataUrlContents;

import org.androidannotations.annotations.AfterExtras;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;
import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

@EActivity(R.layout.activity_product_classify)
public class ProductClassifyActivity extends ActionBarActivity {

    //商品和店铺编码标志
    public static final String SHOP_CODE_EXTRA = "shopCode";
    public static final String USER_CODE_EXTRA = "userCode";
    public static final String COVER_CODE_EXTRA = "coverCode";
    public static final String COVER_NAME_EXTRA = "coverName";

    @Extra(SHOP_CODE_EXTRA)
    String shopCode;
    @Extra(USER_CODE_EXTRA)
    String userCode;
    @Extra(COVER_CODE_EXTRA)
    String coverCode;
    @Extra(COVER_NAME_EXTRA)
    String coverName;


    @ViewById(R.id.listview)
    ListView listview;
    ProductListViewAdaptor adaptor;
    AsyncHttpClient client = new AsyncHttpClient();
    Gson gson = new Gson();
    List<ProductIndexVO> mList=new ArrayList<ProductIndexVO>();
    //页面进度条
    SweetAlertDialog pDialog;
   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_classify);
    }*/

    @AfterExtras
    void initData(){
       // ActionBar actionBar = getActionBar();
       // actionBar.setDisplayHomeAsUpEnabled(true);

        adaptor=new ProductListViewAdaptor(this);

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
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
            client.get(DataUrlContents.SERVER_HOST+DataUrlContents.load_pro_all_list, params, new BaseJsonHttpResponseHandler<Result<ProductIndexVO, String>>() {

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_product_classify, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        pDialog.dismiss();
    }
}
