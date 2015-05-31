package com.trade.bluehole.trad;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.mbaas.oss.callback.SaveCallback;
import com.aliyun.mbaas.oss.model.OSSException;
import com.aliyun.mbaas.oss.storage.OSSFile;
import com.cengalabs.flatui.views.FlatToggleButton;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.rengwuxian.materialedittext.MaterialEditText;
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
import org.askerov.dynamicgrid.DynamicGridView;

import com.trade.bluehole.trad.activity.BaseActionBarActivity;
import com.trade.bluehole.trad.activity.photo.ImageDirActivity;
import com.trade.bluehole.trad.activity.photo.ImageOrderChangeActivity_;
import com.trade.bluehole.trad.activity.photo.PhotoDesignActivity;
import com.trade.bluehole.trad.adaptor.pro.ProductCoverAdapter;
import com.trade.bluehole.trad.adaptor.photo.MainAdapter;
import com.trade.bluehole.trad.adaptor.pro.ProductLabelAdapter;
import com.trade.bluehole.trad.entity.Product;
import com.trade.bluehole.trad.entity.ProductBase;
import com.trade.bluehole.trad.entity.User;
import com.trade.bluehole.trad.entity.photo.Photo;
import com.trade.bluehole.trad.entity.pro.ProductAttribute;
import com.trade.bluehole.trad.entity.pro.ProductCoverRelVO;
import com.trade.bluehole.trad.entity.pro.ProductImage;
import com.trade.bluehole.trad.entity.pro.ProductLabel;
import com.trade.bluehole.trad.entity.pro.ProductLabelRelVO;
import com.trade.bluehole.trad.entity.pro.ProductResultVO;
import com.trade.bluehole.trad.entity.pro.ShopCoverType;
import com.trade.bluehole.trad.util.MyApplication;
import com.trade.bluehole.trad.util.data.DataUrlContents;
import com.trade.bluehole.trad.util.view.InnerGridView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import cn.pedant.SweetAlert.SweetAlertDialog;
import mehdi.sakout.fancybuttons.FancyButton;

@EActivity(R.layout.activity_product_add)
public class NewProductActivity extends BaseActionBarActivity {
    //商品和店铺编码标志
    public static final String SHOP_CODE_EXTRA = "shopCode";
    public static final String PRODUCT_CODE_EXTRA = "productCode";
    public static final int GIRD_VIEW_SIZE = 12;//grid最大图片数量
    public static final int PRODUCT_DESIGN_PHOTO = 15;//优化图片返回结果
    public static final int PRODUCT_ORDER_PHOTO = 16;//优化图片排序
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
    public static ArrayList<Photo> mList = new ArrayList<Photo>();
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
    boolean gridViewDraw_1=false;//选择图片表是否已经重画过
    boolean gridViewDraw_2=false;//选择图片表是否已经重画过
    boolean shrinkViewDraw_1=false;//选择图片表是否已经重画过 缩小
    boolean shrinkViewDraw_2=false;//选择图片表是否已经重画过 缩小
    int common_height;//gridView高度
    private Integer delFlag;//商品删除标志
    LayoutInflater inflater;
    List<View>allAttrView=new ArrayList<>();//所有添加的参数子视图
    //页面进度条
    SweetAlertDialog pDialog;
    int allUploadImgNum=0;//待上传图片数
    @ViewById(R.id.result_image)
    ImageView resultView;
    @ViewById(R.id.tv)
    TextView mTextView;
    @ViewById(R.id.gridview)
    InnerGridView gridView;
    @ViewById
    TextView product_price,product_number,product_cover_name,product_label_name;
    @ViewById
    MaterialEditText product_name;
    @ViewById
    FlatToggleButton toggle_checked_hot;
    @ViewById //商品底部删除上下架按钮
    LinearLayout pro_update_btn_layout;
    @ViewById //商品类别设置
    LinearLayout pro_cover_info_layout;
    @ViewById //商品属性根视图
    LinearLayout pro_attr_root_layout;
    @ViewById
    FancyButton btn_pro_up_down,btn_pro_del,change_image_index;
    @ViewById
    ScrollView pro_scroll;

    @Extra(PRODUCT_CODE_EXTRA)
    String proCode;
    @Extra(SHOP_CODE_EXTRA)
    String shopCode;

    @AfterViews
    void initData(){
        Log.d("NewProductActivity", "proCode:" + proCode + ",shopCode:" + shopCode);
        mList.clear();
        inflater=getLayoutInflater();
        pDialog=getDialog(this);
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
            onClickAddProAttr();//增加一个参数先
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
     * 点击新增商品参数
     */
    @Click(R.id.product_add_attr_layout)
    void onClickAddProAttr(){
        //Toast.makeText(getApplicationContext(), "点击新增商品参数", Toast.LENGTH_SHORT).show();
        //实例化视图
        final View attrView=inflater.inflate(R.layout.inner_dynamic_pro_attr,null);
        RelativeLayout removeLayout=(RelativeLayout) attrView.findViewById(R.id.pro_inner_remove);
        removeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pro_attr_root_layout.removeView(attrView);
                allAttrView.remove(attrView);
            }
        });
        //添加
        pro_attr_root_layout.addView(attrView);
        allAttrView.add(attrView);//添加到容器 用来取值
        scrollToBottom();
    }

    /**
     * 滚动到底部
     */
    @UiThread(delay = 300)
    void scrollToBottom(){
        pro_scroll.fullScroll(View.FOCUS_DOWN);//滚动到底部
    }

    /**
     * 点击处理图片位置调整
     */
    @Click(R.id.change_image_index)
    void onCLickImageIndex(){
        if(mList.size()>1){
            Intent intent= ImageOrderChangeActivity_.intent(this).get();
            startActivityForResult(intent,PRODUCT_ORDER_PHOTO);
        }else{
            Toast.makeText(NewProductActivity.this, "请先选择图片", Toast.LENGTH_SHORT).show();
        }
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
        updateProductStateData("1", delFlag);
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
           // System.out.println(coverAdapter.state2);
            HashMap<Integer, Boolean> state =coverAdapter.state2;
            //String options="选择的项是:";
            for(int j=0;j<coverAdapter.getCount();j++){
               // System.out.println("state.get("+j+")=="+state.get(j));
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
           // System.out.println(labelAdapter.state);
            HashMap<Integer, Boolean> state = labelAdapter.state;
            //String options = "选择的项是:";
            for (int j = 0; j < labelAdapter.getCount(); j++) {
               // System.out.println("state.get(" + j + ")==" + state.get(j));
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
        if(!mList.isEmpty()&&mList.size()>1){
            //验证内容不能为空
            if("".equals(product_name.getText().toString())){
                Toast.makeText(getApplicationContext(), "商品描述不能为空", Toast.LENGTH_SHORT).show();
                product_name.setFocusable(true);
                product_name.setFocusableInTouchMode(true);
                product_name.requestFocus();
            }else   if("".equals(product_price.getText().toString())){
                Toast.makeText(getApplicationContext(), "商品价格不能为空", Toast.LENGTH_SHORT).show();
                product_price.setFocusable(true);
                product_price.setFocusableInTouchMode(true);
                product_price.requestFocus();
            }else    if("".equals(product_number.getText().toString())){
                Toast.makeText(getApplicationContext(), "商品数量不能为空", Toast.LENGTH_SHORT).show();
                product_number.setFocusable(true);
                product_number.setFocusableInTouchMode(true);
                product_number.requestFocus();
            }else{
                //组织扩展参数
                String names="";
                String values="";
                boolean boo=true;
                for(View ls:allAttrView){
                    MaterialEditText _name=(MaterialEditText)ls.findViewById(R.id.product_attr_name);
                    MaterialEditText _value=(MaterialEditText)ls.findViewById(R.id.product_attr_val);
                    //如果有值为空
                    if("".equals(_name.getText().toString())||"".equals(_value.getText().toString())){
                        ls.setBackgroundColor(getResources().getColor(R.color.base_color_red_1));
                        Toast.makeText(getApplicationContext(), "参数不能为空", Toast.LENGTH_SHORT).show();
                        boo=false;
                        break;
                    }else{
                        ls.setBackgroundColor(getResources().getColor(R.color.whitesmoke));
                        names+=_name.getText().toString()+",";
                        values+=_value.getText().toString()+",";
                    }
                }
                //判断扩展属性是否全填
                if(boo){
                    imageUrls="";
                    for(Photo ls:mList){
                        try {
                            if(!"1".equals(ls.dataType)){
                                String fileName= "pro/"+"image_"+UUID.randomUUID()+".jpg";
                                doUploadFile(ls.imgPath,fileName);
                                imageUrls+=fileName+",";
                                allUploadImgNum++;//上传图片总数
                                //设置imagePath为新的生成的文件名 以备后台排序使用
                                ls.imgPath=fileName;
                            }
                            //Log.e("NewProductActivity", "oss-file-name:"+ls.fileName);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    uploadProImg(names,values);
                }
            }
        }else{
            Toast.makeText(this,"至少选择一张图片",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 上传到阿里云
     * @param formUrl
     * @param fileName
     * @throws Exception
     */
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
                uploadImageCallBack();
            }
        });
    }

    @UiThread
    void uploadImageCallBack(){
        --allUploadImgNum;
        if(allUploadImgNum<=0){
            Toast.makeText(NewProductActivity.this, "图片全部上传成功", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(NewProductActivity.this, "上传成功,剩余"+allUploadImgNum+"张", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 提交数据到服务器
     */
    boolean uploadProImg(String names, String values){
        RequestParams params=new RequestParams();
        //如果选了参数 却没有值
        if(allAttrView.size()>0&&("".equals(names)||"".equals(values))){
           // Toast.makeText(getApplicationContext(), "参数不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        //如果添加了参数扩展传递它
        if(!"".equals(names)&&!"".equals(values)){
            params.put("attrNames",names);
            params.put("attrValues",values);
        }
        //Toast.makeText(getApplicationContext(), names, Toast.LENGTH_SHORT).show();
        //Toast.makeText(getApplicationContext(), values, Toast.LENGTH_SHORT).show();


        //开始保存
        pDialog.show();
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
        params.put("productPrice", product_price.getText());
        params.put("productNumber",product_number.getText());
        params.put("imageUrls",imageUrls);

        //组织最新的图片名称 并插入到服务器
        String imageNames="";
        for(Photo p:mList){
            if(!"99911111".equals(p.id)&&null!=p.imgPath){
                imageNames+=p.imgPath+",";
            }
        }
        params.put("imageNames",imageNames);

        Log.d("NewProductActivity", "imageUrls:" + imageNames);
        getClient().post(DataUrlContents.SERVER_HOST + methodName, params, new BaseJsonHttpResponseHandler<String>() {


            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, String response) {
                pDialog.hide();
                if (null != response) {
                    // Toast.makeText(NewProductActivity.this, response, Toast.LENGTH_SHORT).show();
                    //Toast.makeText(NewProductActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                    if(!"".equals(imageUrls)){
                        Toast.makeText(getApplicationContext(), "图片后台处理中,稍后刷新查看", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "提交成功", Toast.LENGTH_SHORT).show();
                    }
                    HeaderAnimatorActivity_.intent(NewProductActivity.this).start();
                    mList.clear();
                    finish();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, String errorResponse) {
                Toast.makeText(NewProductActivity.this, "请求失败:" + statusCode, Toast.LENGTH_SHORT).show();
                pDialog.hide();
            }

            @Override
            protected String parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return rawJsonData;
            }
        });

        return true;
    }

    /**
     * 装载数据根据商品编码和商铺编码
     */
    void loadServerData(){
        pDialog.show();
        RequestParams params=new RequestParams();
        params.put("productCode",proCode);
        params.put("shopCode",shopCode);
        Log.e("NewProductActivity", "imageUrls:" + imageUrls);
        getClient().get(DataUrlContents.SERVER_HOST + DataUrlContents.load_pro_info, params, new BaseJsonHttpResponseHandler<ProductResultVO>() {


            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, ProductResultVO response) {
                //Log.d(NewProductActivity.class.getName(), response.toString());
                pDialog.hide();
                doInUiThread(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, ProductResultVO errorResponse) {
                Toast.makeText(NewProductActivity.this, "请求失败:" + statusCode, Toast.LENGTH_SHORT).show();
                pDialog.hide();
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
        pDialog.show();
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
                //Log.d(NewProductActivity.class.getName(), response.toString());
                pDialog.hide();
                doInProUiThread(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, String errorResponse) {
                Toast.makeText(NewProductActivity.this, "请求失败" + statusCode, Toast.LENGTH_SHORT).show();
                pDialog.hide();
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
            if(dataList.size()<GIRD_VIEW_SIZE){
                dataList.add(new Photo());
            }
            mList.addAll(dataList);
           // reDrawGridLayout();
            mAdapter.setmList(mList);
            mAdapter.notifyDataSetChanged();
            //图片区域是否需要重新计算
           /* if(dataList.size()>4){//2排已经增加
                gridViewDraw_1=true;
                if(dataList.size()>8){//已经三排不能再加
                    gridViewDraw_2=true;
                }
            }*/
        }
        //组装扩展标签
        inflaterProAttr(obj.getAttrs());



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
     * 实例化已存在标签
     */
    void inflaterProAttr(List<ProductAttribute>attrs){
        for(ProductAttribute ls:attrs){
            final View attrView=inflater.inflate(R.layout.inner_dynamic_pro_attr,null);
            MaterialEditText _name=(MaterialEditText)attrView.findViewById(R.id.product_attr_name);
            MaterialEditText _value=(MaterialEditText)attrView.findViewById(R.id.product_attr_val);
            _name.setText(ls.getAttributeName());//赋值
            _value.setText(ls.getAttributeContent());//赋值
            RelativeLayout removeLayout=(RelativeLayout) attrView.findViewById(R.id.pro_inner_remove);//设置点击删除事件
            removeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pro_attr_root_layout.removeView(attrView);
                    allAttrView.remove(attrView);
                }
            });
            //添加
            pro_attr_root_layout.addView(attrView);
            allAttrView.add(attrView);//添加到容器 用来取值
        }
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
                if(mList.size()<GIRD_VIEW_SIZE){
                    Photo p=new Photo();
                    p.id="99911111";
                    mList.add(p);
                }
                //提示选中几张图片
                int selctImage=mAdapter.getCount()-1;
                if(selctImage<0){
                    selctImage=0;
                }
                mAdapter.notifyDataSetChanged();
                mTextView.setText(getString(R.string.check_length, selctImage));
               // reDrawGridLayout();
            }
        }else if(requestCode==PRODUCT_DESIGN_PHOTO&&resultCode == RESULT_OK){//图片优化修改后返回结果
            Integer position= result.getIntExtra(PhotoDesignActivity.IMAGE_URI_POSITION,-1);
            String uri=result.getStringExtra(PhotoDesignActivity.IMAGE_URI);
            if(position<0||uri==null){
                Toast.makeText(NewProductActivity.this, "参数错误", Toast.LENGTH_SHORT).show();
            }else{
                //更改list中图片，并通知适配器重新加载
                mList.get(position).imgPath=uri;
                mList.get(position).dataType="3";
                //加入待删除列表图片id
                del_image_ids+=mList.get(position).id+",";
                mAdapter.notifyDataSetChanged();
                //Toast.makeText(NewProductActivity.this, "重载成功", Toast.LENGTH_SHORT).show();
            }

        }else if(requestCode==PRODUCT_ORDER_PHOTO&&resultCode == RESULT_OK){//图片排序后返回结果
            mAdapter.setmList(mList);
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 重构gridView
     */
    void reDrawGridLayout(){
        if(null!=mList&&mList.size()>4&&!gridViewDraw_1){
            LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) gridView.getLayoutParams(); // 取控件mGrid当前的布局参数
            common_height=gridView.getHeight();
            linearParams.height=common_height*2+10;
            gridView.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件mGrid2
            gridViewDraw_1=true;
        }
        if(null!=mList&&mList.size()>8&&!gridViewDraw_2){
            LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) gridView.getLayoutParams(); // 取控件mGrid当前的布局参数
            linearParams.height=common_height*3+15;
            gridView.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件mGrid2
            gridViewDraw_2=true;
        }
    }

    //public void shrinkDrawGridLayout(){
        /*if(null!=mList&&mList.size()<=4&&!shrinkViewDraw_1){
            LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) gridView.getLayoutParams(); // 取控件mGrid当前的布局参数
            linearParams.height-=common_height-10;
            gridView.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件mGrid2
            shrinkViewDraw_1=true;
        }
        if(null!=mList&&mList.size()<=8&&!shrinkViewDraw_2){
            LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) gridView.getLayoutParams(); // 取控件mGrid当前的布局参数
            linearParams.height-=common_height-15;
            gridView.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件mGrid2
            shrinkViewDraw_2=true;
        }*/
   // }

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
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("确定要退出?")
                    .setContentText("编辑的内容不会被保存!")
                    .setConfirmText("是的,退出!")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.cancel();
                            //点击后退跳转到 主页
                            if(proCode!=null&&!"".equals(proCode)){
                                HeaderAnimatorActivity_.intent(NewProductActivity.this).start();
                            }else{
                                SuperMainActivity_.intent(NewProductActivity.this).start();
                            }
                            mList.clear();
                            finish();
                        }
                    })
                    .setCancelText("不退出!")
                    .showCancelButton(true)
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.cancel();
                        }
                    })
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void beginCrop(Uri source) {
        //Uri outputUri = Uri.fromFile(new File(getCacheDir(), "cropped"));
        //new Crop(source).output(outputUri).asSquare().start(this);
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            resultView.setImageURI(Crop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /***
     * 改变选中图片提示
     * @param number
     */
    public void changeImageSelectNum(int number){
        //提示选中几张图片
        int selctImage=number-1;
        if(selctImage<=0){
            mTextView.setText("请选择图片");
        }else{
            mTextView.setText(getString(R.string.check_length, selctImage));
        }
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
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("确定退出?")
                    .setContentText("要放弃此次编辑么!")
                    .setConfirmText("是的,退出!")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.cancel();
                            //点击后退跳转到 主页
                            if(proCode!=null&&!"".equals(proCode)){
                               HeaderAnimatorActivity_.intent(NewProductActivity.this).start();
                            }else{
                                SuperMainActivity_.intent(NewProductActivity.this).start();
                            }
                            mList.clear();
                            finish();
                        }
                    })
                    .setCancelText("不退出!")
                    .showCancelButton(true)
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.cancel();
                        }
                    })
                    .show();
            return true;
        }
        return false;
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        pDialog.dismiss();
    }
}
