package com.trade.bluehole.trad;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.soundcloud.android.crop.Crop;
import com.trade.bluehole.trad.activity.BaseActionBarActivity;
import com.trade.bluehole.trad.activity.shop.ProductClassifyActivity;
import com.trade.bluehole.trad.activity.shop.ProductClassifyActivity_;
import com.trade.bluehole.trad.activity.shop.SearchProductActivity;
import com.trade.bluehole.trad.activity.shop.SearchProductActivity_;
import com.trade.bluehole.trad.activity.webview.ShopWebViewActivity_;
import com.trade.bluehole.trad.adaptor.pro.ProductCoverNumberAdapter;
import com.trade.bluehole.trad.adaptor.pro.ProductListViewAdaptor;
import com.trade.bluehole.trad.animator.IO2014HeaderAnimator;
import com.trade.bluehole.trad.entity.ProductIndexVO;
import com.trade.bluehole.trad.entity.User;
import com.trade.bluehole.trad.entity.pro.ProductCoverRelVO;
import com.trade.bluehole.trad.entity.pro.ShopCoverType;
import com.trade.bluehole.trad.entity.shop.ShopCommonInfo;
import com.trade.bluehole.trad.util.ImageManager;
import com.trade.bluehole.trad.util.MyApplication;
import com.trade.bluehole.trad.util.Result;
import com.trade.bluehole.trad.util.data.DataUrlContents;
import com.trade.bluehole.trad.util.view.MyViewHold;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.TencentWbShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;


import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;
import org.apache.http.Header;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import it.carlom.stikkyheader.core.StikkyHeaderBuilder;
import mehdi.sakout.fancybuttons.FancyButton;

@EActivity(R.layout.activity_header_animator)
public class HeaderAnimatorActivity extends BaseActionBarActivity {

    @ViewById(R.id.listview)
    ListView listview;
    @ViewById
    TextView shopName,no_item_text;
    @ViewById
    CircleImageView shop_logo_image;
    @ViewById
    ImageView header_image,no_item_add;
    @ViewById
    RelativeLayout main_sale_ing_btn, main_sale_out_btn, main_sale_cover_btn;
    @ViewById
    TextView main_sale_ing_txt, main_sale_out_txt, main_sale_cover_txt;
    @ViewById
    LinearLayout /*btn_cover_layout,*/ btn_cover_ok_layout;
    @ViewById
    RelativeLayout empty_view;
    @App
    MyApplication myApplication;
    ProductListViewAdaptor adaptor;
    ProductCoverNumberAdapter coverNumberAdapter;
    List<ProductIndexVO> mList = new ArrayList<ProductIndexVO>();
    List<ProductCoverRelVO> coverList = new ArrayList<ProductCoverRelVO>();
    //页面进度条
    SweetAlertDialog pDialog;
    private String searchType = "1";
    private String serverName = DataUrlContents.load_pro_all_list;
    //分类页面编辑状态
    private boolean showCoverManager = false;
    DialogPlus coverDialog;//商品自定义分类弹出框
    DialogPlus modifyCoverDialog;//修改商品自定义分类弹出框
    MyViewHold myViewHold;//自定义弹出框展示
    MyViewHold modifyViewHold;//修改自定义类别弹出框展示
    EditText coverNameEdit;//修改商品分类 公用编辑框
    private String temp_coverCode;//临时类别更新code，注意清除.
    private Integer temp_position;//临时类别更新位置，注意清除.

    //listviw anim
    private static final int INITIAL_DELAY_MILLIS = 500;
    SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;
    /**
     * 登陆信息
     */
    ShopCommonInfo shop;
    User user;

   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_header_animator);
        mListView = (ListView) this.findViewById(R.id.listview);
        initData();
    }*/

    @AfterViews
    void initData() {
        user = myApplication.getUser();
        shop = myApplication.getShop();
        initDialog();
        addQZoneQQPlatform();
        //list 动画
        adaptor = new ProductListViewAdaptor(this,"main");
        swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(adaptor);
        swingBottomInAnimationAdapter.setAbsListView(listview);
        assert swingBottomInAnimationAdapter.getViewAnimator() != null;
        swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);
        listview.setAdapter(swingBottomInAnimationAdapter);

        coverNumberAdapter = new ProductCoverNumberAdapter(this);
        if (shop != null) {
            shopName.setText(shop.getTitle());
            if (null != shop.getShopLogo()) {
                ImageManager.imageLoader.displayImage(DataUrlContents.IMAGE_HOST + shop.getShopLogo()+DataUrlContents.img_logo_img, shop_logo_image, ImageManager.options);
            }
        }
        IO2014HeaderAnimator animator = new IO2014HeaderAnimator(this);
        StikkyHeaderBuilder.stickTo(listview)
                .setHeader(R.id.header, (ViewGroup) getWindow().getDecorView())
                .minHeightHeaderDim(R.dimen.min_height_header_materiallike)
                .animator(animator)
                .build();
        //家在列表数据
        populateListView();
        //初始化弹出框
        listview.setEmptyView(empty_view);

    }


    /**
     * 实例化弹出窗口 新增
     */
    void initDialog(){
        pDialog=getDialog(this);//获取进度实例化
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
     * 当视图的选项被点击
     *
     * @param position
     */
    @ItemClick(R.id.listview)
    public void gridViewItemClicked(int position) {
        //分类被点击
        if ("2".equals(searchType)) {
            //只有不显示编辑状态的时候 商品分类才能被点击
            if (!showCoverManager) {
                ProductCoverRelVO pcr = coverList.get(position - 1);
                if(pcr.getProNumber()!=null&&pcr.getProNumber()>0){
                    Intent intent = ProductClassifyActivity_.intent(this).get();
                    intent.putExtra(ProductClassifyActivity.SHOP_CODE_EXTRA, user.getShopCode());
                    intent.putExtra(ProductClassifyActivity.USER_CODE_EXTRA, user.getUserCode());
                    intent.putExtra(ProductClassifyActivity.COVER_CODE_EXTRA, pcr.getCoverCode());
                    intent.putExtra(ProductClassifyActivity.COVER_NAME_EXTRA, pcr.getCoverName());
                    startActivity(intent);
                }else{
                    Toast.makeText(HeaderAnimatorActivity.this, "该分类目前没有数据", Toast.LENGTH_SHORT).show();
                }
            }


        } else {//商品被点击
           /* ProductIndexVO pr = mList.get(position - 1);
            Intent intent = NewProductActivity_.intent(this).get();
            intent.putExtra(NewProductActivity.PRODUCT_CODE_EXTRA, pr.getProductCode());
            intent.putExtra(NewProductActivity.SHOP_CODE_EXTRA, pr.getShopCode());
            startActivity(intent);*/

        }

    }

    /**
     * 当点击预览店铺
     */
    @Click(R.id.view_shop_layout)
    void onClickViewShopBtn(){
        Intent intent = ShopWebViewActivity_.intent(this).get();
        intent.putExtra(ShopWebViewActivity_.SHOP_CODE, user.getShopCode());
        this.startActivity(intent);
    }
    /**
     * 当点击复制店铺地址
     */
    @Click(R.id.copy_shop_view_rul)
    void onClickCopyShopUrlBtn(){
        ClipboardManager clip = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        clip.setText(DataUrlContents.SERVER_HOST + DataUrlContents.show_view_shop_web + "?&shopCode=" + user.getShopCode()); // 复制
        Toast.makeText(getApplication(), "成功复制到剪切板!", Toast.LENGTH_SHORT).show();

    }
    /**
     * 当点击分享店铺地址
     */
    @Click(R.id.share_shop_view_rul)
    void onClickShareShopUrlBtn(){

        String _targetUrl=DataUrlContents.SERVER_HOST + DataUrlContents.show_view_shop_web + "?&shopCode=" + user.getShopCode();
        // 设置分享内容
        mController.setShareContent(shop.getSlogan()+_targetUrl);
        // 设置分享图片, 参数2为图片的url地址
        mController.setShareMedia(new UMImage(this, DataUrlContents.IMAGE_HOST + shop.getShopLogo() + DataUrlContents.img_logo_img));
        mController.openShare(this, false);

    }



    /**
     * 点击销售中
     */
    @Click(R.id.main_sale_ing_btn)
    void onSaleIngClick() {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)listview.getLayoutParams();
        params.bottomMargin=0;
        listview.setLayoutParams(params);
        searchType = "1";
        //btn_cover_layout.setVisibility(View.GONE);
        main_sale_ing_txt.setTextColor(getResources().getColor(R.color.red_btn_bg_color));
        main_sale_out_txt.setTextColor(getResources().getColor(R.color.white));
        main_sale_cover_txt.setTextColor(getResources().getColor(R.color.white));
        //设置无数据时候
        no_item_text.setText("还没有商品,赶快去添加~");
        if(no_item_add.getVisibility()==View.GONE){
            no_item_add.setVisibility(View.VISIBLE);
        }
        //隐藏完成按钮界面
        btn_cover_ok_layout.setVisibility(View.GONE);
        populateListView();
    }

    /**
     * 点击已下架
     */
    @Click(R.id.main_sale_out_btn)
    void onSaleOutClick() {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)listview.getLayoutParams();
        params.bottomMargin=0;
        listview.setLayoutParams(params);

        searchType = "0";
        showCoverManager = false;//非可编辑状态
        //btn_cover_layout.setVisibility(View.GONE);
        main_sale_out_txt.setTextColor(getResources().getColor(R.color.red_btn_bg_color));
        main_sale_ing_txt.setTextColor(getResources().getColor(R.color.white));
        main_sale_cover_txt.setTextColor(getResources().getColor(R.color.white));
        //设置无数据时候
        no_item_text.setText("还没有下架的商品~");
        if(no_item_add.getVisibility()==View.VISIBLE){
            no_item_add.setVisibility(View.GONE);
        }
        //隐藏完成按钮界面
        btn_cover_ok_layout.setVisibility(View.GONE);
        populateListView();
    }

    /**
     * 点击分类
     */
    @Click(R.id.main_sale_cover_btn)
    void onCoverOutClick() {
        //设置list距离底部
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)listview.getLayoutParams();
        params.bottomMargin=100;
        listview.setLayoutParams(params);

        searchType = "2";
        showCoverManager = false;//非可编辑状态
       // btn_cover_layout.setVisibility(View.VISIBLE);
        main_sale_cover_txt.setTextColor(getResources().getColor(R.color.red_btn_bg_color));
        main_sale_ing_txt.setTextColor(getResources().getColor(R.color.white));
        main_sale_out_txt.setTextColor(getResources().getColor(R.color.white));
        //设置无数据时候
        no_item_text.setText("还没有分类,赶快添加~");
        if(no_item_add.getVisibility()==View.VISIBLE){
            no_item_add.setVisibility(View.GONE);
        }
        loadCoverListView();
    }

    /**
     * 点击查询商品
     */
     @Click(R.id.shop_search_layout)
     void onClickSearchProduct(){
         SearchProductActivity_.intent(this).start();
     }

    /**
     * 点击列表为空的快速添加
     */
    @Click(R.id.empty_view)
    void onClickQuickAdd(){
        if("1".equals(searchType)){
            Intent intent=NewProductActivity_.intent(this).get();
            intent.putExtra(NewProductActivity.SHOP_CODE_EXTRA,user.getShopCode());
            startActivity(intent);
        }else if("2".equals(searchType)){
            temp_coverCode=null;
            temp_position=null;
            coverDialog.show();
        }
    }


    /***********************
     * 以下分类标签点击功能
     *
     **********************/

    /**
     * 当编辑分类按钮被点击
     */
    @Click(R.id.btn_cover_edit)
    void onEditCoversClick() {
        //如果是显示状态隐藏  否则显示
        showCoverManager = true;//可编辑状态
        //暂时停用列表点击事件
        //waiting....
        //列表显示编辑
        coverNumberAdapter.showCoverEdit();
        //显示完成按钮界面
        btn_cover_ok_layout.setVisibility(View.VISIBLE);
        //隐藏基本按钮界面
        //btn_cover_layout.setVisibility(View.GONE);

    }

    /**
     * 当编辑分类完成按钮被点击
     */
    @Click(R.id.btn_cover_ok)
    void onEditCoversOkClick() {

        //如果是显示状态隐藏  否则显示
        showCoverManager = false;//可编辑状态
        //列表显示编辑
        coverNumberAdapter.hideCoverEdit();
        //显示基本按钮界面
        //btn_cover_layout.setVisibility(View.VISIBLE);
        //隐藏完成按钮界面
        btn_cover_ok_layout.setVisibility(View.GONE);
    }

    /**
     * 当新增分类按钮被点击
     */
    @Click(R.id.btn_cover_add)
    void onAddCoverBtnOnClick(){
        //清空便用code
        temp_coverCode=null;
        temp_position=null;
        coverDialog.show();
    }

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
     * 点击店铺详情查看店铺详细设置
     */
    @Click(R.id.main_head_shop)
    void onSopHeadClick(){
        //暂时注释掉
        //ShopConfigActivity_.intent(this).start();
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
                            if(null!=textView.getText()){
                                saveOrUpdateCovers(textView.getText().toString(),null);
                            }else{
                                Toast.makeText(HeaderAnimatorActivity.this, "cover name can not null!", Toast.LENGTH_SHORT).show();
                            }
                        }
                     //修改
                    }else{
                        if(null!=modifyViewHold.contentView){
                            View v=modifyViewHold.contentView;
                            coverNameEdit = (EditText) v.findViewById(R.id.main_cover_item_add);
                            if(null!=coverNameEdit&&coverNameEdit.getText()!=null){
                                saveOrUpdateCovers(coverNameEdit.getText().toString(),temp_coverCode);
                            }else{
                                Toast.makeText(HeaderAnimatorActivity.this, "cover name can not null!", Toast.LENGTH_SHORT).show();
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
     * 点击新增按钮
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.new_product) {
            //NewProductActivity_.intent(this).start();
            Intent intent=NewProductActivity_.intent(this).get();
            intent.putExtra(NewProductActivity.SHOP_CODE_EXTRA,user.getShopCode());
            startActivity(intent);
            return true;
        }else if(id==android.R.id.home){
            SuperMainActivity_.intent(this).start();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void beginCrop(Uri source) {
        Uri outputUri = Uri.fromFile(new File(getCacheDir(), "cropped"));
        new Crop(source).output(outputUri).asSquare().start(this);
    }


    /**
     * load数据
     */
    private void populateListView() {
        User user = myApplication.getUser();
        if (user != null && user.getUserCode() != null) {
            pDialog.show();
            RequestParams params = new RequestParams();
            params.put("userCode", user.getUserCode());
            params.put("type", searchType);
            params.put("pageSize", 500);
            getClient().get(DataUrlContents.SERVER_HOST + DataUrlContents.load_pro_all_list, params, new BaseJsonHttpResponseHandler<Result<ProductIndexVO, String>>() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Result<ProductIndexVO, String> response) {
                    pDialog.hide();
                    if (null != response) {
                        if (response.isSuccess()) {
                            // Toast.makeText(HeaderAnimatorActivity.this, "获取数据成功", Toast.LENGTH_SHORT).show();
                            //把数据添加到全局
                            mList.clear();
                            mList.addAll(response.getAaData());
                            adaptor.setLists(mList);
                          //  listview.setAdapter(adaptor);
                            listview.setAdapter(swingBottomInAnimationAdapter);
                            adaptor.notifyDataSetChanged();
                        } else {
                            Toast.makeText(HeaderAnimatorActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Result<ProductIndexVO, String> errorResponse) {
                    pDialog.hide();
                    Toast.makeText(HeaderAnimatorActivity.this, R.string.load_data_error, Toast.LENGTH_SHORT).show();
                }

                @Override
                protected Result<ProductIndexVO, String> parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    // return new ObjectMapper().readValues(new JsonFactory().createParser(rawJsonData), Result.class).next();
                    return gson.fromJson(rawJsonData, new TypeToken<Result<ProductIndexVO, String>>() {
                    }.getType());
                }
            });
        }
    }


    /**
     * load数据
     */
    private void loadCoverListView() {
        User user = myApplication.getUser();
        if (user != null && user.getUserCode() != null) {
            pDialog.show();
            RequestParams params = new RequestParams();
            params.put("userCode", user.getUserCode());
            params.put("shopCode", user.getShopCode());
            params.put("pageSize", 500);
            serverName = DataUrlContents.load_pro_covers_number_list;
            getClient().get(DataUrlContents.SERVER_HOST + serverName, params, new BaseJsonHttpResponseHandler<Result<ProductCoverRelVO, String>>() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Result<ProductCoverRelVO, String> response) {
                    pDialog.hide();
                    if (null != response) {
                        if (response.isSuccess()) {
                            //Toast.makeText(HeaderAnimatorActivity.this, "获取数据成功", Toast.LENGTH_SHORT).show();
                            //把数据添加到全局
                            coverList.clear();
                            coverList.addAll(response.getList());
                            coverNumberAdapter.setLists(coverList);
                            listview.setAdapter(coverNumberAdapter);
                            coverNumberAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(HeaderAnimatorActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Result<ProductCoverRelVO, String> errorResponse) {
                    pDialog.hide();
                    Toast.makeText(HeaderAnimatorActivity.this, R.string.load_data_error, Toast.LENGTH_SHORT).show();
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
            pDialog.show();
            RequestParams params = new RequestParams();
            params.put("userCode", user.getUserCode());
            params.put("shopCode", user.getShopCode());
            params.put("coverTypeName", coverTypeName);
            if(null!=coverTypeCode){
                params.put("coverTypeCode", coverTypeCode);
            }
            getClient().get(DataUrlContents.SERVER_HOST + DataUrlContents.save_shop_cover, params, new BaseJsonHttpResponseHandler<Result<ShopCoverType, String>>() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Result<ShopCoverType, String> response) {
                    pDialog.hide();
                    if (null != response) {
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
                            coverNumberAdapter.setLists(coverList);
                            coverNumberAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(HeaderAnimatorActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Result<ShopCoverType, String> errorResponse) {
                    pDialog.hide();
                    Toast.makeText(HeaderAnimatorActivity.this, R.string.load_data_error, Toast.LENGTH_SHORT).show();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**使用SSO授权必须添加如下代码 */
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode) ;
        if(ssoHandler != null){
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }

    }

    /**
     * 支持qq分享回掉
     */
    private void addQZoneQQPlatform() {
        // 添加QQ支持, 并且设置QQ分享内容的target url
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this,  myApplication.qq_appId, myApplication.qq_appKey);
        qqSsoHandler.setTitle(shop.getTitle());
        qqSsoHandler.setTargetUrl(DataUrlContents.SERVER_HOST + DataUrlContents.show_view_shop_web + "?shopCode="+user.getShopCode());
        qqSsoHandler.addToSocialSDK();

        // 添加QZone平台
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this, myApplication.qq_appId, myApplication.qq_appKey);
        qZoneSsoHandler.addToSocialSDK();
    }

    public void shareProduct(String code,String proName,String imagUrl){
        String _targetUrl=DataUrlContents.SERVER_HOST + DataUrlContents.show_view_pro_web + "?productCode=" + code + "&shopCode=" + user.getShopCode();
        // 设置分享内容
        mController.setShareContent(proName+_targetUrl);
        // 设置分享图片, 参数2为图片的url地址
        mController.setShareMedia(new UMImage(this, imagUrl));
        // 添加QQ支持, 并且设置QQ分享内容的target url
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this,  myApplication.qq_appId, myApplication.qq_appKey);
        qqSsoHandler.setTitle(shop.getTitle());
        qqSsoHandler.setTargetUrl(DataUrlContents.SERVER_HOST + DataUrlContents.show_view_pro_web + "?productCode=" + code + "&shopCode=" + user.getShopCode());
        qqSsoHandler.addToSocialSDK();
        // 添加QZone平台
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this, myApplication.qq_appId, myApplication.qq_appKey);
        qZoneSsoHandler.addToSocialSDK();

        mController.openShare(this, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        pDialog.dismiss();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_header_animator, menu);
        return true;
    }

}
