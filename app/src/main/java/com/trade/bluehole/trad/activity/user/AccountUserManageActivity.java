package com.trade.bluehole.trad.activity.user;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.manuelpeinado.fadingactionbar.extras.actionbarcompat.FadingActionBarHelper;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.trade.bluehole.trad.R;
import com.trade.bluehole.trad.activity.BaseActionBarActivity;
import com.trade.bluehole.trad.entity.User;
import com.trade.bluehole.trad.entity.UserBase;
import com.trade.bluehole.trad.entity.shop.ShopCommonInfo;
import com.trade.bluehole.trad.util.ImageManager;
import com.trade.bluehole.trad.util.MyApplication;
import com.trade.bluehole.trad.util.data.DataUrlContents;
import com.trade.bluehole.trad.util.view.MyViewHold;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.apache.http.Header;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 用户账户信息
 */
@EActivity
public class AccountUserManageActivity  extends BaseActionBarActivity {

    @ViewById
    CircleImageView shop_logo_image;
    @ViewById
    TextView user_account_text,user_nick_text,user_name_text,user_phone_text,user_email_text,user_qq_text,user_sina_text,user_sex_text,user_age_text;
    @ViewById
    TextView user_account_label,user_nick_label,user_name_label,user_phone_label,user_email_label,user_qq_label,user_sina_label,user_sex_label,user_age_label;
    @ViewById
    RelativeLayout user_nick_layout,user_name_layout,user_phone_layout,user_email_layout,user_qq_layout,user_sina_layout,user_sex_layout,user_age_layout;

    @App
    MyApplication myApplication;
    User user;
    UserBase base;
    ShopCommonInfo shop;
    MyViewHold modifyViewHold;//修改自定义类别弹出框展示
    DialogPlus modifyCoverDialog;//修改商品自定义分类弹出框
    EditText commonEdit;//修改 公用编辑框
    private String  temp_userCode;//临时类别更新code，注意清除.
    private Integer temp_position=0;//临时类别更新位置，注意清除.
    //页面进度条
    SweetAlertDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user=myApplication.getUser();
        shop = myApplication.getShop();
        temp_userCode=user.getUserCode();
        //初始化等待dialog
        pDialog = getDialog(this);
        pDialog.show();

        //获取actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        //自动收缩actionbar
        FadingActionBarHelper helper = new FadingActionBarHelper()
                .actionBarBackground(R.drawable.ab_background)
                .headerLayout(R.layout.action_bar_user_header)
                .contentLayout(R.layout.activity_account_user_manage);
        setContentView(helper.createView(this));
        helper.initActionBar(this);
        //初始化头像
        if (shop != null) {
            if (null != shop.getShopLogo()) {
                ImageManager.imageLoader.displayImage(DataUrlContents.IMAGE_HOST + shop.getShopLogo()+DataUrlContents.img_logo_img, shop_logo_image, ImageManager.options);
            }
            /*if(null!=shop.getShopBackground()){
                ImageManager.imageLoader.displayImage(DataUrlContents.IMAGE_HOST + shop.getShopBackground(), header_image, ImageManager.options);
            }*/
        }
        //实例化弹窗
        initDialog();
        //读取数据
        loadData();
    }

    /**
     * 修改昵称
     */
    @Click(R.id.user_nick_layout)
    void onEditNickClick(){
        temp_position=1;
        showEditClick(user_nick_label.getText().toString(),base.getNickName());
    }
    /**
     * 修改姓名
     */
    @Click(R.id.user_name_layout)
    void onEditNameClick(){
        temp_position=2;
        showEditClick(user_name_label.getText().toString(),base.getRealName());
    }
    /**
     * 修改手机号
     */
    @Click(R.id.user_phone_layout)
    void onEditPhoneClick(){
        temp_position=3;
        showEditClick(user_phone_label.getText().toString(),base.getMobile());
    }
    /**
     * 修改邮箱
     */
    @Click(R.id.user_email_layout)
    void onEditEmailClick(){
        temp_position=4;
        showEditClick(user_email_label.getText().toString(),base.getEmail());
    }
    /**
     * 修改qq
     */
    @Click(R.id.user_qq_layout)
    void onEditQQClick(){
        temp_position=5;
        showEditClick(user_qq_label.getText().toString(),base.getQq());
    }
    /**
     * 修改微博
     */
    @Click(R.id.user_sina_layout)
    void onEditSinaClick(){
        temp_position=6;
        showEditClick(user_sina_label.getText().toString(),base.getWeibo());
    }
    /**
     * 修改性别
     */
    @Click(R.id.user_sex_layout)
    void onEditSexClick(){
        temp_position=7;
        showEditClick(user_sex_label.getText().toString(),base.getSex()==null?"":base.getSex().toString());
    }
    /**
     * 修改年龄
     */
    @Click(R.id.user_age_layout)
    void onEditAgelick(){
        temp_position=8;
        showEditClick(user_age_label.getText().toString(), base.getAge() == null ? "" : base.getAge().toString());
    }



    /**
     * 弹出框按钮点击事件
     */
    OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(DialogPlus dialog, View view) {
            switch (view.getId()) {
                case R.id.footer_confirm_button:
                    //关闭软盘
                    View _view = getWindow().peekDecorView();
                    if (view != null) {
                        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(_view.getWindowToken(), 0);
                    }
                    //修改
                    if(null!=temp_userCode||!"".equals(temp_userCode)){
                        if(null!=modifyViewHold.contentView){
                            View v=modifyViewHold.contentView;
                            View hv=modifyViewHold.headView;
                            commonEdit = (EditText) v.findViewById(R.id.main_cover_item_add);
                            TextView _temp=(TextView) hv.findViewById(R.id.header_name);
                            if(commonEdit.getText()!=null&&!"".equals(commonEdit.getText().toString())){
                                //执行保存
                                    updateUserInfo(commonEdit.getText().toString());
                                    dialog.dismiss();//隐藏弹窗
                            }else{
                                Toast.makeText(AccountUserManageActivity.this, "请填写"+_temp.getText().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    break;
                case R.id.footer_close_button:
                    //清空便用code
                    temp_userCode=null;
                    dialog.dismiss();//隐藏弹窗
                    break;
            }

        }
    };

    /**
     * 实例化弹出窗口
     */
    void initDialog(){
        modifyViewHold=new MyViewHold(R.layout.i_pro_cover_edit_item);
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
     * 当点击某个类别编辑的时候弹出
     * @param optionName
     */
    public void showEditClick(String optionName,String optionValue){
        View v=modifyViewHold.contentView;
        View hv=modifyViewHold.headView;
        //设置标题
        TextView _temp=(TextView) hv.findViewById(R.id.header_name);
        _temp.setText(optionName);
        TextView _temp2=(TextView) hv.findViewById(R.id.header_notice);
        _temp2.setText("修改用户信息");
        //设置内容
        commonEdit = (EditText) v.findViewById(R.id.main_cover_item_add);
        if(null!=optionValue&&!"".equals(optionValue)){
            commonEdit.setText(optionValue);
        }else{
            commonEdit.setText(null);
            commonEdit.setHint("请输入"+optionName);
        }


        modifyCoverDialog.show();
    }


    /**
     * 读取数据
     */
    private void loadData(){
        RequestParams params=new RequestParams();
        params.put("userCode", user.getUserCode());
        getClient().get(DataUrlContents.SERVER_HOST + DataUrlContents.load_user_base, params, new BaseJsonHttpResponseHandler<UserBase>() {


            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, UserBase obj) {
                if (null != obj) {
                    base = obj;
                    setDataInThread();
                } else {
                    pDialog.hide();
                    Toast.makeText(AccountUserManageActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, UserBase errorResponse) {
                pDialog.hide();
                Toast.makeText(AccountUserManageActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected UserBase parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                // return new ObjectMapper().readValues(new JsonFactory().createParser(rawJsonData), Result.class).next();
                return gson.fromJson(rawJsonData, UserBase.class);
            }
        });
    }

    /**
     * 实例化数据
     * @param
     */
    @UiThread
    void setDataInThread(){
        if(null!=base&&null!=user){
            if(null!=user.getAccount()){//账号
                user_account_text.setText(user.getAccount());
            }
            if(null!=base.getNickName()){//昵称
                user_nick_label.setTextColor(getResources().getColor(R.color.eggplant));
                user_nick_text.setTextColor(getResources().getColor(R.color.eggplant));
                user_nick_text.setText(base.getNickName());
            }
            if(null!=base.getRealName()){//真实姓名
                user_name_label.setTextColor(getResources().getColor(R.color.eggplant));
                user_name_text.setTextColor(getResources().getColor(R.color.eggplant));
                user_name_text.setText(base.getRealName());
            }
           /* if(null!=base.getRealName()){//真实姓名
                user_name_label.setTextColor(getResources().getColor(R.color.eggplant));
                user_name_text.setTextColor(getResources().getColor(R.color.eggplant));
                user_name_text.setText(base.getRealName());
            }*/
            if(null!=base.getMobile()){//手机号
                user_phone_label.setTextColor(getResources().getColor(R.color.eggplant));
                user_phone_text.setTextColor(getResources().getColor(R.color.eggplant));
                user_phone_text.setText(base.getMobile());
            }
            if(null!=base.getEmail()){//邮箱
                user_email_label.setTextColor(getResources().getColor(R.color.eggplant));
                user_email_text.setTextColor(getResources().getColor(R.color.eggplant));
                user_email_text.setText(base.getMobile());
            }
            if(null!=base.getQq()){//QQ
                user_qq_label.setTextColor(getResources().getColor(R.color.eggplant));
                user_qq_text.setTextColor(getResources().getColor(R.color.eggplant));
                user_qq_text.setText(base.getQq());
            }
            if(null!=base.getWeibo()){//微博
                user_sina_label.setTextColor(getResources().getColor(R.color.eggplant));
                user_sina_text.setTextColor(getResources().getColor(R.color.eggplant));
                user_sina_text.setText(base.getWeibo());
            }
            if(null!=base.getSex()){//性别
                user_sex_label.setTextColor(getResources().getColor(R.color.eggplant));
                user_sex_text.setTextColor(getResources().getColor(R.color.eggplant));
                user_sex_text.setText(base.getSex());
            }
            if(null!=base.getAge()){//性别
                user_age_label.setTextColor(getResources().getColor(R.color.eggplant));
                user_age_text.setTextColor(getResources().getColor(R.color.eggplant));
                user_age_text.setText(base.getAge()+"");
            }
            pDialog.hide();
        }
    }


    /**
     * 更新分类信息
     */
    private void updateUserInfo(String val) {
        if (user != null && user.getUserCode() != null) {
            RequestParams params = new RequestParams();
            params.put("userCode", user.getUserCode());
            if(temp_position.intValue()==1){
                params.put("nickName", val);
            }else if(temp_position.intValue()==2){
                params.put("realName", val);
            }else if(temp_position.intValue()==3){
                params.put("mobile", val);
            }else if(temp_position.intValue()==4){
                params.put("email", val);
            }else if(temp_position.intValue()==5){
                params.put("qq", val);
            }else if(temp_position.intValue()==6){
                params.put("weibo", val);
            }else if(temp_position.intValue()==7){
                params.put("sex", val);
            }else if(temp_position.intValue()==8){
                params.put("age", val);
            }

            getClient().post(DataUrlContents.SERVER_HOST + DataUrlContents.update_user_base, params, new BaseJsonHttpResponseHandler<UserBase>() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, UserBase response) {
                    if (null != response) {
                        base=response;
                        setDataInThread();
                    } else {
                        Toast.makeText(AccountUserManageActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, UserBase errorResponse) {
                    Toast.makeText(AccountUserManageActivity.this, R.string.load_data_error, Toast.LENGTH_SHORT).show();
                }

                @Override
                protected UserBase parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    // return new ObjectMapper().readValues(new JsonFactory().createParser(rawJsonData), Result.class).next();
                    return gson.fromJson(rawJsonData, UserBase.class);
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_account_user_manage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }else if(id==android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
