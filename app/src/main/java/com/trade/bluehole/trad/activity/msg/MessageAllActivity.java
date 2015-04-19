package com.trade.bluehole.trad.activity.msg;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.trade.bluehole.trad.R;
import com.trade.bluehole.trad.adaptor.msg.MessageRecyclerAdapter;
import com.trade.bluehole.trad.entity.User;
import com.trade.bluehole.trad.entity.msg.MessageVO;
import com.trade.bluehole.trad.entity.pro.ProductCoverRelVO;
import com.trade.bluehole.trad.util.MyApplication;
import com.trade.bluehole.trad.util.Result;
import com.trade.bluehole.trad.util.data.DataUrlContents;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.apache.http.Header;

@EActivity
public class MessageAllActivity extends ActionBarActivity {

    @App
    MyApplication myApplication;
    //循环利用视图
    @ViewById(R.id.msg_recycler_view)
    RecyclerView mRecyclerView;//循环利用list
    private RecyclerView.LayoutManager mLayoutManager;//创建线性布局管理器
    AsyncHttpClient client = new AsyncHttpClient();
    Gson gson = new Gson();
    User user;
    MessageRecyclerAdapter mAdapter ;//适配器
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_all);
    }

    @AfterViews
    void initData(){
         user = myApplication.getUser();
        //获取actionbar 并开启后退
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        mRecyclerView.setHasFixedSize(true);//内容变化不影响布局开启
        mLayoutManager=new LinearLayoutManager(this);//设置线性布局管理
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter =new MessageRecyclerAdapter(this,null);//实例化适配器
        mRecyclerView.setAdapter(mAdapter);//设置适配器

        loadMessageList();//读取数据
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_message_all, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if(id==android.R.id.home){//点击后退关闭
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * load数据
     */
    private void loadMessageList() {

        if (user != null && user.getUserCode() != null) {
            RequestParams params = new RequestParams();
            params.put("shopCode", user.getShopCode());
            params.put("pageSize", 500);
            client.get(DataUrlContents.SERVER_HOST + DataUrlContents.load_news_all_list, params, new BaseJsonHttpResponseHandler<Result<MessageVO, String>>() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Result<MessageVO, String> response) {
                    if (null != response) {
                        if (response.isSuccess()) {
                            //Toast.makeText(HeaderAnimatorActivity.this, "获取数据成功", Toast.LENGTH_SHORT).show();
                            //把数据添加到全局
                            mAdapter.setmDataset(response.getList());
                            mAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(MessageAllActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Result<MessageVO, String> errorResponse) {
                    Toast.makeText(MessageAllActivity.this, R.string.load_data_error, Toast.LENGTH_SHORT).show();
                }

                @Override
                protected Result<MessageVO, String> parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    // return new ObjectMapper().readValues(new JsonFactory().createParser(rawJsonData), Result.class).next();
                    return gson.fromJson(rawJsonData, new TypeToken<Result<MessageVO, String>>() {
                    }.getType());
                }
            });
        }
    }
}
