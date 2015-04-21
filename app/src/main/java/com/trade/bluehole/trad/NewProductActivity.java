package com.trade.bluehole.trad;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.mbaas.oss.OSSClient;
import com.aliyun.mbaas.oss.callback.SaveCallback;
import com.aliyun.mbaas.oss.model.OSSException;
import com.aliyun.mbaas.oss.model.TokenGenerator;
import com.aliyun.mbaas.oss.storage.OSSFile;
import com.aliyun.mbaas.oss.util.OSSLog;
import com.aliyun.mbaas.oss.util.OSSToolKit;
import com.cengalabs.flatui.views.FlatToggleButton;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.soundcloud.android.crop.Crop;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.apache.http.Header;

import com.aliyun.mbaas.oss.storage.OSSBucket;
import com.trade.bluehole.trad.activity.BaseActionBarActivity;
import com.trade.bluehole.trad.activity.photo.ImageDirActivity;
import com.trade.bluehole.trad.adaptor.pro.ProductCoverAdapter;
import com.trade.bluehole.trad.adaptor.photo.MainAdapter;
import com.trade.bluehole.trad.adaptor.pro.ProductLabelAdapter;
import com.trade.bluehole.trad.entity.Product;
import com.trade.bluehole.trad.entity.ProductBase;
import com.trade.bluehole.trad.entity.ProductIndexVO;
import com.trade.bluehole.trad.entity.User;
import com.trade.bluehole.trad.entity.photo.Photo;
import com.trade.bluehole.trad.entity.pro.ProductCoverRelVO;
import com.trade.bluehole.trad.entity.pro.ProductImage;
import com.trade.bluehole.trad.entity.pro.ProductLabel;
import com.trade.bluehole.trad.entity.pro.ProductLabelRelVO;
import com.trade.bluehole.trad.entity.pro.ProductResultVO;
import com.trade.bluehole.trad.entity.pro.ShopCoverType;
import com.trade.bluehole.trad.util.MyApplication;
import com.trade.bluehole.trad.util.data.DataUrlContents;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import mehdi.sakout.fancybuttons.FancyButton;

@EActivity(R.layout.activity_product_add)
public class NewProductActivity extends BaseActionBarActivity {
    //商品和店铺编码标志
    public static final String SHOP_CODE_EXTRA = "shopCode";
    public static final String PRODUCT_CODE_EXTRA = "productCode";
    //选中的类别
    public HashMap<Integer, Boolean> state = new HashMap<Integer, Boolean>();
    //需要提交的类型名称
    private String coverName="";
    //需要提交的类型值
    private String coverValue="";
    //需要提交的标签名称
    private String labelName="";
    //需要提交的标签值
    private String labelValue="";
    //需要删除的图片id
    public String del_image_ids="";
    //要增加的图片集
    String wait_to_addImages="";
    @App
    MyApplication myapplication;
    User user=null;
    //grid展示图片容器
    private ArrayList<Photo> mList = new ArrayList<Photo>();
    //服务器加载回的商品图片
    private ArrayList<Photo> dataList = new ArrayList<Photo>();
    //已经选中的自定义编码为复现checkbox
    List<String>myCheckCovers=new ArrayList<String>();
    //已经选中的标签编码为复现checkbox
    List<String>myCheckLabels=new ArrayList<String>();
    private MainAdapter mAdapter;//以选择图片适配器
    ProductCoverAdapter coverAdapter;//分类设置适配器
    ProductLabelAdapter labelAdapter;//标签设置适配器
    DialogPlus coverDialog;//商品自定义分类弹出框
    DialogPlus labelDialog;//商品自定义标签弹出框
    DialogPlus confirmDialog;//确认操作
    String imageUrls="";//新增商品 待添加商品列表
    boolean gridViewDraw=false;//选择图片表是否已经重画过
    private Integer delFlag;//商品删除标志

    @ViewById(R.id.result_image)
    ImageView resultView;
    @ViewById(R.id.tv)
    TextView mTextView;
    @ViewById(R.id.gridview)
    GridView gridView;
    @ViewById
    TextView product_name,product_price,product_number,product_cover_name,product_label_name;
    @ViewById
    FlatToggleButton toggle_checked_hot;
    @ViewById //商品底部删除上下架按钮
    LinearLayout pro_update_btn_layout;
    @ViewById //商品类别设置
    LinearLayout pro_cover_info_layout;
    @ViewById
    FancyButton btn_pro_up_down,btn_pro_del;

    @Extra(PRODUCT_CODE_EXTRA)
    String proCode;
    @Extra(SHOP_CODE_EXTRA)
    String shopCode;

    @AfterViews
    void initData(){
        Log.e("NewProductActivity", "proCode:" + proCode + ",shopCode:" + shopCode);

        user=myapplication.getUser();
        //实例化分类适配器
        coverAdapter = new ProductCoverAdapter(this, false);
        //实例化标签适配器
        labelAdapter = new ProductLabelAdapter(this, false);
        //如果是修改
        if(null!=proCode&&null!=shopCode&&!"".equals(proCode)&&!"".equals(shopCode)){
            //异步加载数据
            loadServerData();
            pro_update_btn_layout.setVisibility(View.VISIBLE);//展示商品底部更新
            pro_cover_info_layout.setVisibility(View.VISIBLE);//展示底部类别设置
        }else{//如果是新增
            mList.add(new Photo());
        }
        mAdapter = new MainAdapter(this, mList);
        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==(mList.size()-1)){
                    Intent intent = new Intent(getApplicationContext(), ImageDirActivity.class);
                    if(mList.size()>0){
                        mList.remove(mList.size()-1);
                    }
                    intent.putExtra(MyApplication.ARG_PHOTO_LIST, mList);
                    startActivityForResult(intent, 13);
                }
            }
        });
        //实例化弹出窗口
        initDialog();
    }

    /**
     * 实例化弹出窗口
     */
    void initDialog(){
        //自定义类别
         coverDialog  = new DialogPlus.Builder(this)
                    .setAdapter(coverAdapter)
                    .setHeader(R.layout.dialog_cover_header)
                    .setFooter(R.layout.dialog_footer)
                    .setGravity(DialogPlus.Gravity.BOTTOM)
                    .setOnClickListener(clickListener)
                            // .setOnItemClickListener(itemClickListener)
                    .create();

        labelDialog = new DialogPlus.Builder(this)
                .setAdapter(labelAdapter)
                .setHeader(R.layout.dialog_label_header)
                .setFooter(R.layout.dialog_footer)
                .setGravity(DialogPlus.Gravity.BOTTOM)
                .setOnClickListener(labelClickListener)
                        // .setOnItemClickListener(itemClickListener)
                .create();
        confirmDialog = new DialogPlus.Builder(this)
                .setContentHolder(new ViewHolder(R.layout.dialog_confirm_content))
                //.setHeader(R.layout.dialog_label_header)
                .setFooter(R.layout.dialog_footer)
                .setGravity(DialogPlus.Gravity.BOTTOM)
                .setOnClickListener(delClickListener)
                .create();
    }
    /**
     * 当设置推荐按钮点击
     */
    @CheckedChange(R.id.toggle_checked_hot)
     void hotButtonChange(){
       // Toast.makeText(this,"toggle_checked_hot changed",Toast.LENGTH_SHORT).show();
        toggle_checked_hot.isChecked();

    }


   /* *//**
     * 弹出框点击事件
     *//*
    OnItemClickListener itemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(DialogPlus dialog, Object item, View view,final int position) {
            TextView textView = (TextView) view.findViewById(R.id.text_view);
            String clickedAppName = textView.getText().toString();
            Toast.makeText(NewProductActivity.this, clickedAppName + " clicked", Toast.LENGTH_LONG).show();
        }
    };*/
    /**
     * 弹出框按钮点击事件
     */
    OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(DialogPlus dialog, View view) {
            switch (view.getId()) {
                case R.id.footer_confirm_button:
                    //记录商品变化
                    saveProCoverChecked();
                   // Toast.makeText(NewProductActivity.this, "Confirm button clicked", Toast.LENGTH_LONG).show();
                    break;
                case R.id.footer_close_button:
                    break;
            }
            dialog.dismiss();
        }
    };
    /**
     * 删除弹出框按钮点击事件
     */
    OnClickListener delClickListener = new OnClickListener() {
        @Override
        public void onClick(DialogPlus dialog, View view) {
            switch (view.getId()) {
                case R.id.footer_confirm_button:
                    //删除商品
                    updateProductStateData("2", 0);
                   // Toast.makeText(NewProductActivity.this, "Confirm button clicked", Toast.LENGTH_LONG).show();
                    break;
                case R.id.footer_close_button:
                    break;
            }
            dialog.dismiss();
        }
    };
    /**
     * 弹出框按钮点击事件---标签设置
     */
    OnClickListener labelClickListener = new OnClickListener() {
        @Override
        public void onClick(DialogPlus dialog, View view) {
            switch (view.getId()) {
                case R.id.footer_confirm_button:
                    //记录商品变化
                    saveProLabelChecked();
                    break;
                case R.id.footer_close_button:
                    break;
            }
            dialog.dismiss();
        }
    };


    /**
     * 点击分配自定义类别
     */
    @Click(R.id.pro_cover_list_layout)
    void  onAssignCoverClick(){
        coverDialog.show();
    }
    /**
     * 点击分配自定义标签
     */
    @Click(R.id.pro_label_list_layout)
    void  onAssignLabelClick(){
        labelDialog.show();
    }

    /* @Click(R.id.addProductImage)
    void addProImageClick(){
        resultView.setImageDrawable(null);
        Crop.pickImage(this);
    }*/

    /**
     * 当商品上下架按钮被点击
     */
    @Click(R.id.btn_pro_up_down)
    void onUpDownProductBtnOnClick(){
        updateProductStateData("1",delFlag);
    }
    /**
     * 当商品删除按钮被点击
     */
    @Click(R.id.btn_pro_del)
    void onDeleteProductBtnOnClick(){
        confirmDialog.show();
    }
    /**
     * 记录商品类别选择变化
     */
    void saveProCoverChecked(){
        coverName="";
        coverValue="";
        if(coverAdapter.state2.size()>0){
            System.out.println(coverAdapter.state2);
            HashMap<Integer, Boolean> state =coverAdapter.state2;
            //String options="选择的项是:";
            for(int j=0;j<coverAdapter.getCount();j++){
                System.out.println("state.get("+j+")=="+state.get(j));
                if(state.get(j)!=null){
                    ShopCoverType coverType=(ShopCoverType)coverAdapter.getItem(j);
                    //String username=coverType.getCoverTypeName();
                    //String id=coverType.getCoverTypeCode();
                    //options+="\n"+id+"."+username;
                    coverName+=coverType.getCoverTypeName()+",";
                    coverValue+=coverType.getCoverTypeCode()+",";
                    //先设置成选择后的分类等点完成的时候再一起同步到数据库
                    product_cover_name.setText(coverName);
                }
            }
        }else{
            coverName="未分类";
            coverValue="NULL";
            product_cover_name.setText(coverName);
        }
        //显示选择内容
       // Toast.makeText(getApplicationContext(), options, Toast.LENGTH_LONG).show();


    }
    /**
     * 记录商品标签选择变化
     */
    void saveProLabelChecked(){
        labelName = "";
        labelValue = "";
        if(labelAdapter.state.size()>0) {
            System.out.println(labelAdapter.state);
            HashMap<Integer, Boolean> state = labelAdapter.state;
            //String options = "选择的项是:";
            for (int j = 0; j < labelAdapter.getCount(); j++) {
                System.out.println("state.get(" + j + ")==" + state.get(j));
                if (state.get(j) != null) {
                    ProductLabel obj = (ProductLabel) labelAdapter.getItem(j);
                    String username = obj.getLabelName();
                    String id = obj.getLabelCode();
                 //   options += "\n" + id + "." + username;
                    labelName += obj.getLabelName() + ",";
                    labelValue += obj.getLabelCode() + ",";
                    //先设置成选择后的分类等点完成的时候再一起同步到数据库
                    product_label_name.setText(labelName);
                }
            }
        }else{
            labelName="未设置";
            labelValue="NULL";
            product_label_name.setText(labelName);
        }
        //显示选择内容
        //Toast.makeText(getApplicationContext(), options, Toast.LENGTH_LONG).show();

    }


    /**
     * 上传图片
     */
   // @Click(R.id.uploadProductImage)
    void uploadProImageClick(){
        if(!mList.isEmpty()&&null!=user){
            imageUrls="";
            for(Photo ls:mList){
                try {
                    if(!"1".equals(ls.dataType)){
                        String fileName= "pro/"+"image_"+UUID.randomUUID()+".jpg";
                        doUploadFile(ls.imgPath,fileName);
                        imageUrls+=fileName+",";
                    }
                    //Log.e("NewProductActivity", "oss-file-name:"+ls.fileName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            uploadProImg();
        }else{
            Toast.makeText(this,"上传列表为空或者未登陆",Toast.LENGTH_SHORT).show();
        }
    }

    public void doUploadFile(String formUrl,String fileName) throws Exception {

        OSSFile ossFile = new OSSFile(myapplication.getOssBucket(), fileName);
        ossFile.setUploadFilePath(formUrl, "image/jpg");
        ossFile.uploadInBackground(new SaveCallback() {

            @Override
            public void onProgress(String objectKey, int byteCount, int totalSize) {
                Log.e("NewProductActivity", "objectKey:" + objectKey + ",byteCount:" + byteCount + ",totalSize:" + totalSize);
            }

            @Override
            public void onFailure(String arg0, OSSException arg1) {
                Log.e("NewProductActivity", arg1.toString());
            }

            @Override
            public void onSuccess(String arg0) {
                Log.e("NewProductActivity", "上传成功");
            }
        });
    }

    /**
     * 提交数据
     */
    void uploadProImg(){
        RequestParams params=new RequestParams();
        //如果商品编码不是空 调用更新方法
        String methodName=DataUrlContents.save_all_cshop;
        if(null!=proCode&&!"".equals(proCode)){
            methodName=DataUrlContents.update_all_cshop;
            params.put("productCode",proCode);
            params.put("coverCodes",coverValue);
            params.put("labelCodes",labelValue);
            params.put("hot",toggle_checked_hot.isChecked());
            params.put("wait_del_images",del_image_ids);
            params.put("wait_insert_images",imageUrls);

        }
        params.put("shopCode",shopCode);
        params.put("userCode",user.getUserCode());
        params.put("productName",product_name.getText());
        params.put("productPrice",product_price.getText());
        params.put("productNumber",product_number.getText());
        params.put("imageUrls",imageUrls);
        Log.e("NewProductActivity", "imageUrls:" + imageUrls);
        getClient().post(DataUrlContents.SERVER_HOST + methodName, params, new BaseJsonHttpResponseHandler<String>() {


            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, String response) {
               // Log.d(NewProductActivity.class.getName(), statusCode + "");
                if (null != response) {
                    // Toast.makeText(NewProductActivity.this, response, Toast.LENGTH_SHORT).show();
                    Toast.makeText(NewProductActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                    HeaderAnimatorActivity_.intent(NewProductActivity.this).start();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, String errorResponse) {
            }

            @Override
            protected String parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return rawJsonData;
            }
        });
    }

    /**
     * 装载数据根据商品编码和商铺编码
     */
    void loadServerData(){
        RequestParams params=new RequestParams();
        params.put("productCode",proCode);
        params.put("shopCode",shopCode);
        Log.e("NewProductActivity", "imageUrls:" + imageUrls);
        getClient().get(DataUrlContents.SERVER_HOST + DataUrlContents.load_pro_info, params, new BaseJsonHttpResponseHandler<ProductResultVO>() {


            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, ProductResultVO response) {
                Log.d(NewProductActivity.class.getName(), response.toString());
                    doInUiThread(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, ProductResultVO errorResponse) {
            }

            @Override
            protected ProductResultVO parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return gson.fromJson(rawJsonData, ProductResultVO.class);
            }
        });
    }


    /**
     * 更新商品状态 上架、下架、删除
     */
    void updateProductStateData(String type,Integer state){
        RequestParams params=new RequestParams();
        params.put("productCode", proCode);
        params.put("state", state == 1 ? 0 : 1);
        String methodName=DataUrlContents.update_product_state;
        if("2".equals(type)){//删除操作
            methodName=DataUrlContents.del_product_bycode;
        }
        getClient().get(DataUrlContents.SERVER_HOST + methodName, params, new BaseJsonHttpResponseHandler<String>() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, String response) {
                Log.d(NewProductActivity.class.getName(), response.toString());
                    doInProUiThread(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, String errorResponse) {
            }

            @Override
            protected String parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return gson.fromJson(rawJsonData, String.class);
            }
        });
    }

    /**
     * 后台线程实例化组件
     * @param obj
     */
    @UiThread
    void doInUiThread(ProductResultVO obj) {
        Product pro=obj.getPro();
        ProductBase base=obj.getProBase();
        //设置当前商品状态 并且设置按钮文字
        delFlag=pro.getDelFlag();
        if(delFlag==1){//设置为下架商品
            btn_pro_up_down.setText(getResources().getString(R.string.pro_out_sale));
        }else{
            btn_pro_up_down.setText(getResources().getString(R.string.pro_inner_sale));
        }

        List<ProductImage> images= obj.getImages();
        product_name.setText(pro.getProductName());
        product_price.setText(pro.getProductPrice() + "");
        product_number.setText(base.getProductNumber()+"");

        if(images!=null&&!images.isEmpty()){
            mList.clear();
            for(ProductImage ls:images){
                Photo photo=new Photo();
                photo.dataType="1";//load数据
                photo.imgPath=ls.getImageUrl();
                photo.id=ls.getId()+"";
                dataList.add(photo);
            }
            if(dataList.size()<10){
                dataList.add(new Photo());
            }
            mList.addAll(dataList);
            reDrawGridLayout();
            mAdapter.setmList(mList);
            mAdapter.notifyDataSetChanged();
            //图片区域是否需要重新计算
            if(dataList.size()>5){
                gridViewDraw=true;
            }
        }
        //组装类别
        List<ProductCoverRelVO> myCovers=obj.getMyCovers();
        if(null!=myCovers&&!myCovers.isEmpty()){
            String covers="";
            for(ProductCoverRelVO ls:myCovers){
                covers+=ls.getCoverName()+",";
                myCheckCovers.add(ls.getCoverCode());
            }
            product_cover_name.setText(covers);
        }
        //组装标签
        List<ProductLabelRelVO> muLabels=obj.getMuLabels();
        if(null!=muLabels&&!muLabels.isEmpty()){
            String labels="";
            for(ProductLabelRelVO ls:muLabels){
                labels+=ls.getLabelName()+",";
                myCheckLabels.add(ls.getLabelCode());
            }
            product_label_name.setText(labels);
        }
        //是否热销
        if(obj.getHotState().intValue()>0){
            toggle_checked_hot.setChecked(true);
        }
        //装载全部自定义分类
        coverAdapter.setCovers(obj.getCovers(),myCheckCovers);
        coverAdapter.notifyDataSetChanged();
        //装载全部自定义标签
        labelAdapter.setLabels(obj.getLabels(),myCheckLabels);
        labelAdapter.notifyDataSetChanged();
    }

    /**
     * 后台线程实例化组件 更新商品状态
     * @param obj
     */
    @UiThread
    void doInProUiThread(String obj) {
        if ("2".equals(obj)) {
            Toast.makeText(NewProductActivity.this, "商品已删除", Toast.LENGTH_SHORT).show();
            HeaderAnimatorActivity_.intent(this).start();
            finish();
        } else {
            if (delFlag == 1) {
                Toast.makeText(NewProductActivity.this, "商品已下架", Toast.LENGTH_SHORT).show();
                btn_pro_up_down.setText(getResources().getString(R.string.pro_inner_sale));
            } else if (delFlag == 0) {
                Toast.makeText(NewProductActivity.this, "商品已上架", Toast.LENGTH_SHORT).show();
                btn_pro_up_down.setText(getResources().getString(R.string.pro_out_sale));
            }
         }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(result.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, result);
        }else if(requestCode==13&&resultCode == RESULT_OK){
            if (result != null)
            {
                ArrayList<Photo> list = result.getParcelableArrayListExtra(MyApplication.RES_PHOTO_LIST);

                mList.clear();
               // mList.addAll(dataList);
                if (list != null)
                {
                    mList.addAll(list);
                }
                if(mList.size()<10){
                    Photo p=new Photo();
                    p.id="99911111";
                    mList.add(p);
                }
                mAdapter.notifyDataSetChanged();
                mTextView.setText(getString(R.string.check_length, mAdapter.getCount()));
                reDrawGridLayout();
            }
        }
    }

    /**
     * 重构gridView
     */
    void reDrawGridLayout(){
        if(null!=mList&&mList.size()>5&&!gridViewDraw){
            LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) gridView.getLayoutParams(); // 取控件mGrid当前的布局参数
            linearParams.height=gridView.getHeight()*2+10;
            gridView.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件mGrid2
            gridViewDraw=true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_add_pro, menu);
        return true;
    }

    /**
     * 点击完成按钮 开始上传图片和信息
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.save_product)
        {
            uploadProImageClick();
            return true;
        }else if(id==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void beginCrop(Uri source) {
        Uri outputUri = Uri.fromFile(new File(getCacheDir(), "cropped"));
        new Crop(source).output(outputUri).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            resultView.setImageURI(Crop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
