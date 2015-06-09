package com.trade.bluehole.trad;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.manuelpeinado.fadingactionbar.extras.actionbarcompat.FadingActionBarHelper;
import com.nhaarman.listviewanimations.ArrayAdapter;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.dragdrop.OnItemMovedListener;
import com.nhaarman.listviewanimations.itemmanipulation.dragdrop.TouchViewDraggableManager;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.SimpleSwipeUndoAdapter;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.trade.bluehole.trad.activity.BaseActionBarActivity;
import com.trade.bluehole.trad.activity.shop.ProductClassifyActivity;
import com.trade.bluehole.trad.activity.shop.ProductClassifyActivity_;
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
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;
import org.apache.http.Header;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 商品分类管理
 */

@EActivity
public class CoverManageActivity extends BaseActionBarActivity {
    @App
    MyApplication myApplication;
    DynamicListView listview;
    @ViewById
    RelativeLayout empty_view;
    ArrayAdapter<ProductCoverRelVO> adapter;//商品分类适配器
    ShopCommonInfo shop;
    User user;
    List<ProductCoverRelVO> coverList = new ArrayList<ProductCoverRelVO>();

    private static final int INITIAL_DELAY_MILLIS = 300;

    private int mNewItemCount;
    //页面进度条
    SweetAlertDialog pDialog;
    DialogPlus coverDialog;//商品自定义分类弹出框
    DialogPlus modifyCoverDialog;//修改商品自定义分类弹出框
    MyViewHold myViewHold;//自定义弹出框展示
    MyViewHold modifyViewHold;//修改自定义类别弹出框展示
    EditText coverNameEdit;//修改商品分类 公用编辑框
    private String temp_coverCode;//临时类别更新code，注意清除.
    private Integer temp_position;//临时类别更新位置，注意清除.
    private boolean isUpdateIndex=false;//是否更新了排序
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setContentView(R.layout.activity_cover_manage);
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
        listview = (DynamicListView) findViewById(android.R.id.list);
        //listview.addHeaderView(LayoutInflater.from(this).inflate(R.layout.activity_dynamiclistview_header, listview, false));
        adapter=new CoverManageListAdapter(this);

        SimpleSwipeUndoAdapter simpleSwipeUndoAdapter = new SimpleSwipeUndoAdapter(adapter, this, new MyOnDismissCallback(adapter));
        AlphaInAnimationAdapter animAdapter = new AlphaInAnimationAdapter(simpleSwipeUndoAdapter);
        animAdapter.setAbsListView(listview);
        assert animAdapter.getViewAnimator() != null;
        animAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);
        listview.setAdapter(animAdapter);

         /* Enable drag and drop functionality */
        listview.enableDragAndDrop();
        listview.setDraggableManager(new TouchViewDraggableManager(R.id.list_row_draganddrop_touchview));
        listview.setOnItemMovedListener(new MyOnItemMovedListener(adapter));
        listview.setOnItemLongClickListener(new MyOnItemLongClickListener(listview));


         /* Enable swipe to dismiss */
        listview.enableSimpleSwipeUndo();
        /* Add new items on item click */
       // listview.setOnItemClickListener(new MyOnItemClickListener(listview));
        //初始化弹出
        initDialog();
        loadCoverListView();
    }


    /**
     * 点击列表为空的快速添加
     */
    @Click(R.id.empty_view)
    void onClickQuickAdd(){
        //清空便用code
        temp_coverCode=null;
        temp_position=null;
        coverDialog.show();
    }

    /**
     * 实例化弹出窗口 新增
     */
    void initDialog(){
        pDialog=getDialog(this);//获取进度实例化
        listview.setEmptyView(empty_view);
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






    private static class MyOnItemLongClickListener implements AdapterView.OnItemLongClickListener {

        private final DynamicListView mListView;

        MyOnItemLongClickListener(final DynamicListView listView) {
            mListView = listView;
        }

        @Override
        public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, final long id) {
            if (mListView != null) {
                mListView.startDragging(position - mListView.getHeaderViewsCount());
            }
            return true;
        }
    }

    /**
     * 左右滑动删除选项
     */
    private class MyOnDismissCallback implements OnDismissCallback {

        private final ArrayAdapter<ProductCoverRelVO> mAdapter;

        @Nullable
        private Toast mToast;

        MyOnDismissCallback(final ArrayAdapter<ProductCoverRelVO> adapter) {
            mAdapter = adapter;
        }

        @Override
        public void onDismiss(@NonNull final ViewGroup listView, @NonNull final int[] reverseSortedPositions) {
            for (int position : reverseSortedPositions) {
                //mAdapter.getLists().remove(position);
                //mAdapter.notifyDataSetChanged();
                ProductCoverRelVO pro=mAdapter.getItem(position);
                deleteCover(pro.getCoverCode(),position);
                //mAdapter.remove(position);
            }

            if (mToast != null) {
                mToast.cancel();
            }
           // mToast = Toast.makeText( CoverManageActivity.this, getString(R.string.removed_positions, Arrays.toString(reverseSortedPositions)), Toast.LENGTH_LONG);
           // mToast.show();
        }
    }

    private class MyOnItemMovedListener implements OnItemMovedListener {

        private final ArrayAdapter<ProductCoverRelVO> mAdapter;

        private Toast mToast;

        MyOnItemMovedListener(final ArrayAdapter<ProductCoverRelVO> adapter) {
            mAdapter = adapter;
        }

        @Override
        public void onItemMoved(final int originalPosition, final int newPosition) {
            if (mToast != null) {
                mToast.cancel();
            }
            //标致顺序已经修改
            isUpdateIndex=true;
            //调用重新实例化按钮
            CoverManageActivity.this.invalidateOptionsMenu();


            mToast = Toast.makeText(getApplicationContext(), getString(R.string.moved, mAdapter.getItem(newPosition), newPosition), Toast.LENGTH_SHORT);
            mToast.show();
        }
    }

    private class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        private final DynamicListView mListView;

        MyOnItemClickListener(final DynamicListView listView) {
            mListView = listView;
        }

        @Override
        public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
            mListView.insert(position, getString(R.string.newly_added_item, mNewItemCount));
            mNewItemCount++;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cover_manage, menu);
        MenuItem actionDone = menu.findItem(R.id.menu_done_cover);
        //判断是否显示 同步按钮
        if(isUpdateIndex){
            actionDone.setVisible(true);
        }else{
            actionDone.setVisible(false);
        }
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
        }else if(id==R.id.menu_done_cover){
            //保存排序
            updateCoverShowIndex();
        }else if(id==android.R.id.home){
            //如果修改过排序 提示
            if(isUpdateIndex){
                showDialogSaveIndex();
            }else{
                //点击后退跳转到 主页
               // SuperMainActivity_.intent(this).start();
                finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 保存用户新的排序
     */
    void updateCoverShowIndex(){
        List<ProductCoverRelVO>list=adapter.getItems();
        StringBuffer buf=new StringBuffer();
        for(ProductCoverRelVO ls:list){
            if(null!=ls){
                buf.append(ls.getCoverCode()).append(",");
            }
        }
        updateCoverIndex(buf.toString());
    }

    /**
     * 处理后退事件
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK ){
            //如果修改过排序 提示
            if(isUpdateIndex){
                showDialogSaveIndex();
            }else{
                //点击后退跳转到 主页
                // SuperMainActivity_.intent(this).start();
                finish();
            }
            return true;
        }
        return false;
    }
    /**
     * 如果修改了排序 弹出提示同步
     */
    void showDialogSaveIndex(){
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("是否要保存?")
                .setContentText("类别的顺序已经被更改!")
                .setConfirmText("是的,保存!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                        //同步分类排序
                        updateCoverShowIndex();
                    }
                })
                .setCancelText("不,继续退出!")
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                        finish();
                    }
                })
                .show();
    }

    /**
     * 更新分类排序
     */
    private void updateCoverIndex(String codes) {
        pDialog.show();
        User user = myApplication.getUser();
        if (user != null && user.getUserCode() != null) {
            RequestParams params = new RequestParams();
            params.put("shopCode", user.getShopCode());
            params.put("coverCodes", codes);
            getClient().get(DataUrlContents.SERVER_HOST + DataUrlContents.update_shop_cover_index, params, new BaseJsonHttpResponseHandler<Result<String, String>>() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Result<String, String> response) {
                        pDialog.hide();
                        //标致顺序已经同步隐藏
                        isUpdateIndex=false;
                        //调用重新实例化按钮
                        CoverManageActivity.this.invalidateOptionsMenu();
                        Toast.makeText(getApplicationContext(), "分类排序同步成功", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Result<String, String> errorResponse) {
                    Toast.makeText(CoverManageActivity.this,"系统错误:"+statusCode, Toast.LENGTH_SHORT).show();
                    pDialog.hide();
                }

                @Override
                protected Result<String, String> parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    // return new ObjectMapper().readValues(new JsonFactory().createParser(rawJsonData), Result.class).next();
                    return gson.fromJson(rawJsonData, new TypeToken<Result<String, String>>() {
                    }.getType());
                }
            });
        }
    }

    /**
     * load数据
     */
    private void loadCoverListView() {
        pDialog.show();
        User user = myApplication.getUser();
        if (user != null && user.getUserCode() != null) {
            RequestParams params = new RequestParams();
            params.put("userCode", user.getUserCode());
            params.put("shopCode", user.getShopCode());
            params.put("pageSize", 500);
            getClient().get(DataUrlContents.SERVER_HOST + DataUrlContents.load_pro_covers_number_list, params, new BaseJsonHttpResponseHandler<Result<ProductCoverRelVO, String>>() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Result<ProductCoverRelVO, String> response) {
                    if (null != response) {
                        pDialog.hide();
                        if (response.isSuccess()) {
                            //Toast.makeText(HeaderAnimatorActivity.this, "获取数据成功", Toast.LENGTH_SHORT).show();
                            //把数据添加到全局
                            coverList.clear();
                            coverList.addAll(response.getList());
                            //不显示未分配的
                            if(!coverList.isEmpty()){
                                coverList.remove(0);
                            }
                            adapter.addAll(coverList);
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(CoverManageActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Result<ProductCoverRelVO, String> errorResponse) {
                    Toast.makeText(CoverManageActivity.this, R.string.load_data_error, Toast.LENGTH_SHORT).show();
                    pDialog.hide();
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
        pDialog.show();
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
            getClient().post(DataUrlContents.SERVER_HOST + DataUrlContents.save_shop_cover, params, new BaseJsonHttpResponseHandler<Result<ShopCoverType, String>>() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Result<ShopCoverType, String> response) {
                    if (null != response) {
                        pDialog.hide();
                        if (response.isSuccess()) {
                            //Toast.makeText(HeaderAnimatorActivity.this, "获取数据成功", Toast.LENGTH_SHORT).show();
                            //把数据添加到全局
                            ProductCoverRelVO obj = new ProductCoverRelVO();
                            if (_position != null) {
                                coverList.get(_position).setCoverName(response.getBzseObj().getCoverTypeName());
                                //coverList.set(_position,response.getBzseObj());
                            } else {
                                ShopCoverType cover = response.getBzseObj();
                                obj.setCoverName(cover.getCoverTypeName());
                                obj.setCoverCode(cover.getCoverTypeCode());
                                obj.setShopCode(cover.getShopCode());
                                obj.setProNumber(0);
                                coverList.add(obj);
                            }
                            adapter.clear();
                            adapter.addAll(coverList);
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(CoverManageActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(CoverManageActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Result<ShopCoverType, String> errorResponse) {
                    Toast.makeText(CoverManageActivity.this, R.string.load_data_error, Toast.LENGTH_SHORT).show();
                    pDialog.hide();
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




    /**
     * 删除分类数据
     */
    private void deleteCover(String coverTypeCode,final int position) {
        pDialog.show();
        User user = myApplication.getUser();
        if (user != null && user.getUserCode() != null) {
            RequestParams params = new RequestParams();
            params.put("coverTypeCode", coverTypeCode);
            getClient().get(DataUrlContents.SERVER_HOST + DataUrlContents.del_shop_cover_type, params, new BaseJsonHttpResponseHandler<Result<String, String>>() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Result<String, String> response) {
                    if (null != response) {
                        pDialog.hide();
                        if (response.isSuccess()) {
                            Toast.makeText(CoverManageActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                            coverList.remove(position);
                            adapter.remove(position);
                        } else {
                            Toast.makeText(CoverManageActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Result<String, String> errorResponse) {
                    Toast.makeText(CoverManageActivity.this, R.string.load_data_error, Toast.LENGTH_SHORT).show();
                    pDialog.hide();
                }

                @Override
                protected Result<String, String> parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    // return new ObjectMapper().readValues(new JsonFactory().createParser(rawJsonData), Result.class).next();
                    return gson.fromJson(rawJsonData, new TypeToken<Result<String, String>>() {
                    }.getType());
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        pDialog.dismiss();
    }
}
