package com.trade.bluehole.trad;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.manuelpeinado.fadingactionbar.extras.actionbarcompat.FadingActionBarHelper;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.trade.bluehole.trad.adaptor.cover.CoverManageListAdapter;
import com.trade.bluehole.trad.entity.User;
import com.trade.bluehole.trad.entity.pro.ProductCoverRelVO;
import com.trade.bluehole.trad.entity.pro.ShopCoverType;
import com.trade.bluehole.trad.entity.shop.ShopCommonInfo;
import com.trade.bluehole.trad.util.MyApplication;
import com.trade.bluehole.trad.util.Result;
import com.trade.bluehole.trad.util.data.DataUrlContents;
import com.trade.bluehole.trad.util.view.MyViewHold;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品分类管理
 */

@EActivity
public class CoverManageActivity extends ActionBarActivity {
    @App
    MyApplication myApplication;
    ListView listview;

    CoverManageListAdapter adapter;//商品分类适配器
    AsyncHttpClient client = new AsyncHttpClient();
    Gson gson = new Gson();
    ShopCommonInfo shop;
    User user;
    List<ProductCoverRelVO> coverList = new ArrayList<ProductCoverRelVO>();

    DialogPlus coverDialog;//商品自定义分类弹出框
    DialogPlus modifyCoverDialog;//修改商品自定义分类弹出框
    MyViewHold myViewHold;//自定义弹出框展示
    MyViewHold modifyViewHold;//修改自定义类别弹出框展示
    EditText coverNameEdit;//修改商品分类 公用编辑框
    private String temp_coverCode;//临时类别更新code，注意清除.
    private Integer temp_position;//临时类别更新位置，注意清除.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user=myApplication.getUser();
        //获取actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        //自动收缩actionbar
        FadingActionBarHelper helper = new FadingActionBarHelper()
                .actionBarBackground(R.drawable.ab_background)
                .headerLayout(R.layout.action_bar_cover_header)
                .contentLayout(R.layout.activity_cover_manage);
        setContentView(helper.createView(this));
        helper.initActionBar(this);
        //配置数据列表
        listview = (ListView) findViewById(android.R.id.list);
        adapter=new CoverManageListAdapter(this);
        listview.setAdapter(adapter);
        loadCoverListView();
        //初始化弹出
        initDialog();
    }



    /**
     * 实例化弹出窗口 新增
     */
    void initDialog(){
        myViewHold=new MyViewHold(R.layout.i_pro_cover_edit_item);
        modifyViewHold=new MyViewHold(R.layout.i_pro_cover_edit_item);
        //自定义类别 新增
        coverDialog  = new DialogPlus.Builder(this)
                .setContentHolder(myViewHold)
                .setHeader(R.layout.dialog_new_cover_header)
                .setFooter(R.layout.dialog_footer)
                .setGravity(DialogPlus.Gravity.BOTTOM)
                .setOnClickListener(clickListener)
                        // .setOnItemClickListener(itemClickListener)
                .create();
        //自定义类别 修改,取出编辑框 以备赋值变量使用
        modifyCoverDialog  = new DialogPlus.Builder(this)
                .setContentHolder(modifyViewHold)
                .setHeader(R.layout.dialog_edit_cover_header)
                .setFooter(R.layout.dialog_footer)
                .setGravity(DialogPlus.Gravity.BOTTOM)
                .setOnClickListener(clickListener)
                        // .setOnItemClickListener(itemClickListener)
                .create();
    }

    /**
     * 弹出框按钮点击事件
     */
    OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(DialogPlus dialog, View view) {
            switch (view.getId()) {
                case R.id.footer_confirm_button:
                    //新增
                    if(null==temp_coverCode||"".equals(temp_coverCode)){
                        if(null!=myViewHold.contentView){
                            View c_view=myViewHold.contentView;
                            EditText textView = (EditText) c_view.findViewById(R.id.main_cover_item_add);
                            if(null!=textView.getText().toString()&&!"".equals(textView.getText().toString())){
                                saveOrUpdateCovers(textView.getText().toString(),null);
                            }else{
                                Toast.makeText(CoverManageActivity.this, "分类名称不能为空!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        //修改
                    }else{
                        if(null!=modifyViewHold.contentView){
                            View v=modifyViewHold.contentView;
                            coverNameEdit = (EditText) v.findViewById(R.id.main_cover_item_add);
                            if(coverNameEdit.getText().toString()!=null&&!"".equals(coverNameEdit.getText().toString())){
                                saveOrUpdateCovers(coverNameEdit.getText().toString(),temp_coverCode);
                            }else{
                                Toast.makeText(CoverManageActivity.this, "分类名称不能为空!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    break;
                case R.id.footer_close_button:
                    //清空便用code
                    temp_coverCode=null;
                    break;
            }
            dialog.dismiss();
        }
    };

    /**
     * 当点击适配器里边某个类别编辑的时候弹出
     * @param coverName
     */
    public void showCoverEditClick(String coverName,String coverCode,int position){
        //临时赋值code
        temp_coverCode=coverCode;
        temp_position=position;
        View v=modifyViewHold.contentView;
        coverNameEdit = (EditText) v.findViewById(R.id.main_cover_item_add);
        if(null!=coverNameEdit&&null!=coverName){
            coverNameEdit.setText(coverName);
        }
        modifyCoverDialog.show();
    }


    /**
     * load数据
     */
    private void loadCoverListView() {
        User user = myApplication.getUser();
        if (user != null && user.getUserCode() != null) {
            RequestParams params = new RequestParams();
            params.put("userCode", user.getUserCode());
            params.put("shopCode", user.getShopCode());
            params.put("pageSize", 500);
            client.get(DataUrlContents.SERVER_HOST + DataUrlContents.load_pro_covers_number_list, params, new BaseJsonHttpResponseHandler<Result<ProductCoverRelVO, String>>() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Result<ProductCoverRelVO, String> response) {
                    if (null != response) {
                        if (response.isSuccess()) {
                            //Toast.makeText(HeaderAnimatorActivity.this, "获取数据成功", Toast.LENGTH_SHORT).show();
                            //把数据添加到全局
                            coverList.clear();
                            coverList.addAll(response.getList());
                            adapter.setLists(coverList);
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(CoverManageActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Result<ProductCoverRelVO, String> errorResponse) {
                    Toast.makeText(CoverManageActivity.this, R.string.load_data_error, Toast.LENGTH_SHORT).show();
                }

                @Override
                protected Result<ProductCoverRelVO, String> parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    // return new ObjectMapper().readValues(new JsonFactory().createParser(rawJsonData), Result.class).next();
                    return gson.fromJson(rawJsonData, new TypeToken<Result<ProductCoverRelVO, String>>() {
                    }.getType());
                }
            });
        }
    }


    /**
     * 更新分类信息
     */
    private void saveOrUpdateCovers(String coverTypeName,String coverTypeCode) {
        final Integer _position=temp_position;
        User user = myApplication.getUser();
        if (user != null && user.getUserCode() != null) {
            RequestParams params = new RequestParams();
            params.put("userCode", user.getUserCode());
            params.put("shopCode", user.getShopCode());
            params.put("coverTypeName", coverTypeName);
            if(null!=coverTypeCode){
                params.put("coverTypeCode", coverTypeCode);
            }
            client.get(DataUrlContents.SERVER_HOST + DataUrlContents.save_shop_cover, params, new BaseJsonHttpResponseHandler<Result<ShopCoverType, String>>() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Result<ShopCoverType, String> response) {
                    if (null != response) {
                        if (response.isSuccess()) {
                            //Toast.makeText(HeaderAnimatorActivity.this, "获取数据成功", Toast.LENGTH_SHORT).show();
                            //把数据添加到全局
                            ProductCoverRelVO obj=new ProductCoverRelVO();
                            if(_position!=null){
                                coverList.get(_position).setCoverName(response.getBzseObj().getCoverTypeName());
                                //coverList.set(_position,response.getBzseObj());
                            }else{
                                ShopCoverType cover=response.getBzseObj();
                                obj.setCoverName(cover.getCoverTypeName());
                                obj.setCoverCode(cover.getCoverTypeCode());
                                obj.setShopCode(cover.getShopCode());
                                obj.setProNumber(0);
                                coverList.add(obj);
                            }
                            adapter.setLists(coverList);
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(CoverManageActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Result<ShopCoverType, String> errorResponse) {
                    Toast.makeText(CoverManageActivity.this, R.string.load_data_error, Toast.LENGTH_SHORT).show();
                }

                @Override
                protected Result<ShopCoverType, String> parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    // return new ObjectMapper().readValues(new JsonFactory().createParser(rawJsonData), Result.class).next();
                    return gson.fromJson(rawJsonData, new TypeToken<Result<ShopCoverType, String>>() {
                    }.getType());
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cover_manage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_add_cover) {
            //清空便用code
            temp_coverCode=null;
            temp_position=null;
            coverDialog.show();
            return true;
        }else if(id==android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}