package com.trade.bluehole.trad.fragment.msg;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.trade.bluehole.trad.R;
import com.trade.bluehole.trad.adaptor.msg.LetterRecyclerAdapter;
import com.trade.bluehole.trad.adaptor.msg.MessageRecyclerAdapter;
import com.trade.bluehole.trad.entity.User;
import com.trade.bluehole.trad.entity.msg.MessageVO;
import com.trade.bluehole.trad.entity.msg.ShopLetter;
import com.trade.bluehole.trad.util.MyApplication;
import com.trade.bluehole.trad.util.Result;
import com.trade.bluehole.trad.util.data.DataUrlContents;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.apache.http.Header;

/**
 * 系统消息
 * Created by Administrator on 2015-04-19.
 */

@EFragment
public class NoticeFragment extends Fragment {
    public static final String ARG_OBJECT = "object";

    @App
    MyApplication myApplication;
    @ViewById(R.id.msg_recycler_view)
    RecyclerView mRecyclerView;
    AsyncHttpClient client = new AsyncHttpClient();
    Gson gson = new Gson();
    private Integer state=0;//加载类型
    private RecyclerView.LayoutManager mLayoutManager;//创建线性布局管理器
    MessageRecyclerAdapter mAdapter ;//适配器
    LetterRecyclerAdapter letterAdapter;//消息适配器
    User user;
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_msg_notice, container, false);
        user = myApplication.getUser();
        Bundle args = getArguments();
        //查询的类型 0 新闻 1 公告
        state=args.getInt(ARG_OBJECT);
       // ((TextView) rootView.findViewById(R.id.text1)).setText(Integer.toString(args.getInt(ARG_OBJECT)));
        return rootView;
    }

    @AfterViews
    void inintData(){
        mRecyclerView.setHasFixedSize(true);//内容变化不影响布局开启
        mLayoutManager=new LinearLayoutManager(getActivity());//设置线性布局管理
        mRecyclerView.setLayoutManager(mLayoutManager);
        if(state.intValue()==0){
            mAdapter =new MessageRecyclerAdapter(getActivity(),null);//实例化适配器
            mRecyclerView.setAdapter(mAdapter);//设置适配器
            loadMessageList();//读取数据
        }else if(state.intValue()==1){
            letterAdapter=new LetterRecyclerAdapter(getActivity(),null);
            mRecyclerView.setAdapter(letterAdapter);
            loadLetterList();
        }

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
                            Toast.makeText(getActivity(), "获取数据失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Result<MessageVO, String> errorResponse) {
                    Toast.makeText(getActivity(), R.string.load_data_error, Toast.LENGTH_SHORT).show();
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
    /**
     * load数据消息
     */
    private void loadLetterList() {

        if (user != null && user.getUserCode() != null) {
            RequestParams params = new RequestParams();
            params.put("shopCode", user.getShopCode());
            params.put("pageSize", 500);
            client.get(DataUrlContents.SERVER_HOST + DataUrlContents.load_letter_all_list, params, new BaseJsonHttpResponseHandler<Result<ShopLetter, String>>() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Result<ShopLetter, String> response) {
                    if (null != response) {
                        if (response.isSuccess()) {
                            //Toast.makeText(HeaderAnimatorActivity.this, "获取数据成功", Toast.LENGTH_SHORT).show();
                            //把数据添加到全局
                            letterAdapter.setmDataset(response.getList());
                            letterAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getActivity(), "获取数据失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Result<ShopLetter, String> errorResponse) {
                    Toast.makeText(getActivity(), R.string.load_data_error, Toast.LENGTH_SHORT).show();
                }

                @Override
                protected Result<ShopLetter, String> parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    // return new ObjectMapper().readValues(new JsonFactory().createParser(rawJsonData), Result.class).next();
                    return gson.fromJson(rawJsonData, new TypeToken<Result<ShopLetter, String>>() {
                    }.getType());
                }
            });
        }
    }
}
