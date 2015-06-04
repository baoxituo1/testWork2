package com.trade.bluehole.trad.activity.feedback;

import java.util.List;

import com.trade.bluehole.trad.R;
import com.trade.bluehole.trad.activity.BaseActionBarActivity;
import com.trade.bluehole.trad.util.DateProcess;
import com.trade.bluehole.trad.util.ImageManager;
import com.trade.bluehole.trad.util.MyApplication;
import com.trade.bluehole.trad.util.data.DataUrlContents;
import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.SyncListener;
import com.umeng.fb.model.Conversation;
import com.umeng.fb.model.Reply;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EActivity;

import de.hdodenhof.circleimageview.CircleImageView;

@EActivity
public class CustomActivity extends BaseActionBarActivity {

    private ListView mListView;
    private FeedbackAgent mAgent;
    private Conversation mComversation;
    private Context mContext;
    private ReplyAdapter adapter;
    private List<Reply> mReplyList;
    private Button sendBtn;
    private EditText inputEdit;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private final String TAG = CustomActivity.class.getName();
    @App
    MyApplication myApplication;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_umeng_feedback);
        mContext = this;

        initView();
        mAgent = new FeedbackAgent(this);
        mComversation = mAgent.getDefaultConversation();
        adapter = new ReplyAdapter();
        mListView.setAdapter(adapter);
        sync();

    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.fb_reply_list);
        sendBtn = (Button) findViewById(R.id.fb_send_btn);
        inputEdit = (EditText) findViewById(R.id.fb_send_content);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.fb_reply_refresh);
        sendBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String content = inputEdit.getText().toString();
                inputEdit.getEditableText().clear();
                if (!TextUtils.isEmpty(content)) {
                    mComversation.addUserReply(content);//添加到会话列表
                    mHandler.sendMessage(new Message());
                    sync();
                }

            }
        });

        //下拉刷新
        mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                sync();
            }
        });
    }

    // 数据同步
    private void sync() {

        mComversation.sync(new SyncListener() {

            @Override
            public void onSendUserReply(List<Reply> replyList) {

            }

            @Override
            public void onReceiveDevReply(List<Reply> replyList) {
                mSwipeRefreshLayout.setRefreshing(false);
                if (replyList == null || replyList.size() < 1) {
                    return;
                }
                mHandler.sendMessage(new Message());
            }
        });
    }


    /**
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if(id==android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    // adapter
    class ReplyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mComversation.getReplyList().size();
        }

        @Override
        public Object getItem(int arg0) {
            return mComversation.getReplyList().get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            boolean isComMsg = false;
            Reply reply = mComversation.getReplyList().get(position);
            //if (convertView == null) {
                if (Reply.TYPE_DEV_REPLY.endsWith(reply.type)) {
                    isComMsg = true;
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.i_chatting_item_msg_text_right, null);
                } else {
                    isComMsg = false;
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.i_chatting_item_msg_text_left, null);
                }
                // convertView = LayoutInflater.from(mContext).inflate(R.layout.i_fb_custom_item, null);
                holder = new ViewHolder();
                holder.reply_item = (TextView) convertView.findViewById(R.id.fb_reply_item);
                holder.tvSendTime = (TextView) convertView.findViewById(R.id.tv_sendtime);
                holder.tvUserName = (TextView) convertView.findViewById(R.id.tv_username);
                holder.tvContent = (TextView) convertView.findViewById(R.id.tv_chatcontent);
                holder.icon = (CircleImageView) convertView.findViewById(R.id.iv_userhead);
                convertView.setTag(holder);
           // } else {
            //    holder = (ViewHolder) convertView.getTag();
            //}


            holder.tvSendTime.setText(DateProcess.longToDateString(reply.created_at));
            if (isComMsg) {
                holder.tvUserName.setText("开发者");
                holder.icon.setImageResource(R.drawable.logo_icon);
            } else {
                holder.tvUserName.setText(myApplication.getShop().getTitle());
                ImageManager.imageLoader.displayImage(DataUrlContents.IMAGE_HOST + myApplication.getShop().getShopLogo() + DataUrlContents.img_list_head_img, holder.icon, ImageManager.options);
            }
            holder.tvContent.setText(reply.content);
            // holder.icon.setImageResource(imgs[entity.getImg()]);


           /* String content;
            if (Reply.TYPE_DEV_REPLY.endsWith(reply.type)) {
                holder.reply_item.setGravity(Gravity.RIGHT);
                content = reply.content + ": 开发者";
            } else {
                content = "用  户 :" + reply.content;
            }
            holder.reply_item.setText(content);*/
            return convertView;
        }


        class ViewHolder {
            TextView reply_item;
            public TextView tvSendTime;
            public TextView tvUserName;
            public TextView tvContent;
            public CircleImageView icon;
            public boolean isComMsg = true;
        }
    }

}
