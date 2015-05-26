package com.trade.bluehole.trad.activity.feedback;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.trade.bluehole.trad.R;
import com.trade.bluehole.trad.activity.BaseActionBarActivity;
import com.trade.bluehole.trad.entity.User;
import com.trade.bluehole.trad.entity.UserBase;
import com.trade.bluehole.trad.entity.shop.ShopCommonInfo;
import com.trade.bluehole.trad.util.MyApplication;
import com.trade.bluehole.trad.util.data.DataUrlContents;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.apache.http.Header;

import cn.pedant.SweetAlert.SweetAlertDialog;

@EActivity
public class UserFeedBackActivity extends BaseActionBarActivity {
    User user;
    ShopCommonInfo shop;
    SweetAlertDialog pDialog;
    @App
    MyApplication myApplication;
    @ViewById
    EditText user_feed_back;
    @ViewById
    EditText user_feed_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feed_back);
        user=myApplication.getUser();
        shop = myApplication.getShop();
        pDialog=getDialog(this);
    }

    /**
     * 当用户点击保存按钮
     */
    @Click(R.id.user_feed_submit)
    void onClickSaveFeedBack(){
        String feedBack=user_feed_back.getText().toString();
        String feedContent=user_feed_content.getText().toString();
        if(feedBack!=null&&!"".equals(feedBack)){
            if(feedBack.length()>300){
                Toast.makeText(UserFeedBackActivity.this, "内容不能超过300字符", Toast.LENGTH_SHORT).show();
            }else if(feedBack.length()<5){
                Toast.makeText(UserFeedBackActivity.this, "请填写完整的反馈吧", Toast.LENGTH_SHORT).show();
            }else {
                if(feedContent!=null&&!"".equals(feedContent)){
                    if(feedBack.length()>50){
                        Toast.makeText(UserFeedBackActivity.this, "内容不能超过50字符", Toast.LENGTH_SHORT).show();
                    }else{
                        saveUserFeedBack(feedBack,feedContent);
                    }
                }else{
                    Toast.makeText(UserFeedBackActivity.this, "请填写联系信息", Toast.LENGTH_SHORT).show();
                }
            }
        }else{
            Toast.makeText(UserFeedBackActivity.this, "请填写反馈内容", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 保存用户反馈
     */
    private void saveUserFeedBack(String feedBack,String feedContent) {
        if (user != null && user.getUserCode() != null) {
            pDialog.show();
            RequestParams params = new RequestParams();
            params.put("userCode", user.getUserCode());
            params.put("sourceType", 2);
            params.put("feedbackContent", feedBack);
            params.put("contact", feedContent);

            getClient().post(DataUrlContents.SERVER_HOST + DataUrlContents.save_user_feed_back, params, new BaseJsonHttpResponseHandler<String>() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, String response) {
                    pDialog.hide();
                    if (null != response) {
                        Toast.makeText(UserFeedBackActivity.this, "反馈成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(UserFeedBackActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, String errorResponse) {
                    pDialog.hide();
                    Toast.makeText(UserFeedBackActivity.this, R.string.load_data_error, Toast.LENGTH_SHORT).show();
                }

                @Override
                protected String parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    // return new ObjectMapper().readValues(new JsonFactory().createParser(rawJsonData), Result.class).next();
                    return gson.fromJson(rawJsonData, String.class);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_feed_back, menu);
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
