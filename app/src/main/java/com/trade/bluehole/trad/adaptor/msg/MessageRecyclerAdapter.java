package com.trade.bluehole.trad.adaptor.msg;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.trade.bluehole.trad.R;
import com.trade.bluehole.trad.activity.msg.MessageAllActivity;
import com.trade.bluehole.trad.activity.webview.WebViewActivity;
import com.trade.bluehole.trad.activity.webview.WebViewActivity_;
import com.trade.bluehole.trad.entity.msg.MessageVO;
import com.trade.bluehole.trad.util.DateProcess;

import java.util.List;

/**
 * 消息管理 recycler适配器  新闻
 * Created by Administrator on 2015-04-19.
 */
public class MessageRecyclerAdapter extends RecyclerView.Adapter<MessageRecyclerAdapter.ViewHolder>{
    List<MessageVO>mDataset;
    Context ctx;
    private final ColorGenerator mGenerator;

    // 定义一个参考对象
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public TextView create_date;
        private ImageView imageView;
        private LinearLayout msg_notice_layout;
        public ViewHolder(View v) {
            super(v);
            mTextView =(TextView) v.findViewById(R.id.textView);
            create_date =(TextView) v.findViewById(R.id.create_date);
            imageView = (ImageView) v.findViewById(R.id.imageView);
            msg_notice_layout = (LinearLayout) v.findViewById(R.id.msg_notice_layout);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MessageRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.r_message_recycler_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // 定义一个构造函数
    public MessageRecyclerAdapter(Context ctx,List<MessageVO> myDataset) {
        this.ctx=ctx;
        this.mDataset = myDataset;
        mGenerator = ColorGenerator.DEFAULT;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MessageVO item=mDataset.get(position);
        holder.mTextView.setText(mDataset.get(position).getMsgTitle());
        //final Drawable drawable = item.getDrawable();
        String title = item.getMsgTitle();
        String header = "活";
        if (null != title && title.length() > 2) {
            header = title.substring(0, 1);
        }
        final TextDrawable drawable = TextDrawable.builder().buildRoundRect(header, mGenerator.getColor(header), toPx(10));
        holder.imageView.setImageDrawable(drawable);
        holder.mTextView.setText(item.getMsgTitle());

       /* if (item.getRedState().intValue()==0) {
            holder.mTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, ctx.getResources().getDrawable(R.drawable.ic_developer_email),null);
        }else {
            holder.mTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }*/

        holder.create_date.setText(DateProcess.longToDateString(Long.valueOf(item.getCreateTime())));

        //当被点击
        holder.msg_notice_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=WebViewActivity_.intent(ctx).get();
                intent.putExtra(WebViewActivity.NOTICE_TYPE,"0");
                intent.putExtra(WebViewActivity.NOTICE_CODE,item.getMsgCode());
                ctx.startActivity(intent);
            }
        });

    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset==null?0:mDataset.size();
    }

    public int toPx(int dp) {
        Resources resources = ctx.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
    }

    public void setmDataset(List<MessageVO> mDataset) {
        this.mDataset = mDataset;
    }
}
