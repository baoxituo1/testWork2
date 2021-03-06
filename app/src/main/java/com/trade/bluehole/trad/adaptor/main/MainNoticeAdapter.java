package com.trade.bluehole.trad.adaptor.main;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.trade.bluehole.trad.R;
import com.trade.bluehole.trad.entity.msg.IndexProCommentVO;

import java.util.List;

/**
 * Created by Administrator on 2015-04-11.
 */
public class MainNoticeAdapter extends BaseAdapter {
    List<IndexProCommentVO> lists;
    private final ColorGenerator mGenerator;


    Context ctx;
    public MainNoticeAdapter(Context ctx,List<IndexProCommentVO> lists){
        this.lists=lists;
        this.ctx=ctx;
        mGenerator = ColorGenerator.DEFAULT;
    }

    @Override
    public int getCount() {
        return lists==null?0:lists.size();
    }

    @Override
    public IndexProCommentVO getItem(int position) {
        return lists==null?null:lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(ctx, R.layout.i_super_notice_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        IndexProCommentVO item = getItem(position);

        //final Drawable drawable = item.getDrawable();
        String title=item.getTitle();
        String header="信";
        if(null!=title&&title.length()>2){
             header= title.substring(0,1);
        }
        final TextDrawable drawable = TextDrawable.builder().buildRoundRect(header, mGenerator.getColor(header), toPx(10));
        holder.imageView.setImageDrawable(drawable);
        holder.textView.setText(item.getTitle());

        // if navigation is supported, show the ">" navigation icon
        /*if (item.getNavigationInfo() != DataSource.NO_NAVIGATION) {
            holder.textView.setCompoundDrawablesWithIntrinsicBounds(null,
                    null,
                    getResources().getDrawable(R.drawable.ic_action_next_item),
                    null);
        }
        else {*/
            holder.textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null,null);
       // }

        // fix for animation not playing for some below 4.4 devices
       /* if (drawable instanceof AnimationDrawable) {
            holder.imageView.post(new Runnable() {
                @Override
                public void run() {
                    ((AnimationDrawable) drawable).stop();
                    ((AnimationDrawable) drawable).start();
                }
            });
        }*/

        return convertView;
    }

    private static class ViewHolder {

        private ImageView imageView;

        private TextView textView;

        private ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.imageView);
            textView = (TextView) view.findViewById(R.id.textView);
        }
    }

    public void setLists(List<IndexProCommentVO> lists) {
        this.lists = lists;
    }

    public int toPx(int dp) {
        Resources resources = ctx.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
    }
}
