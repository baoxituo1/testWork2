package com.trade.bluehole.trad.adaptor.actity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.trade.bluehole.trad.R;
import com.trade.bluehole.trad.entity.actity.ShopActivity;
import com.trade.bluehole.trad.entity.msg.IndexProCommentVO;
import com.trade.bluehole.trad.util.DateProcess;
import com.trade.bluehole.trad.util.ImageManager;
import com.trade.bluehole.trad.util.data.DataUrlContents;

import java.util.List;

import javax.sql.DataSource;

/**
 * Created by Administrator on 2015-04-13.
 */
public class ShopActivityListAdapter extends BaseAdapter {
    List<ShopActivity> lists;
    private final ColorGenerator mGenerator;
    DisplayImageOptions options;

    Context ctx;

    public ShopActivityListAdapter(Context ctx, List<ShopActivity> lists) {
        this.lists = lists;
        this.ctx = ctx;
        mGenerator = ColorGenerator.DEFAULT;
    }

    @Override
    public int getCount() {
        return lists == null ? 0 : lists.size();
    }

    @Override
    public ShopActivity getItem(int position) {
        return lists == null ? null : lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(ctx, R.layout.i_activity_list_item_new, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ShopActivity item = getItem(position);

        //设置图片
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(ImageManager.getImage(item.getActivityName()))
                .showImageForEmptyUri(R.mipmap.logo_launcher)
                .showImageOnFail(R.mipmap.logo_launcher)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageManager.imageLoader.displayImage(DataUrlContents.IMAGE_HOST + item.getActivityImg() + DataUrlContents.img_list_head_img, holder.eshoppings_item_image, options);
        holder.eshoppings_item_title.setText(item.getActivityName());
        holder.eshoppings_item_start.setText(DateProcess.longToDateString(Long.valueOf(item.getCreateDate())));
        holder.eshoppings_item_category.setText(item.getActivityAddress());
        if (item.getDelFlag().intValue()==1) {
            holder.eshoppings_item_on_sale.setText("活动进行中");
        }else {
            holder.eshoppings_item_on_sale.setText("活动已结束");
            holder.eshoppings_item_fave.setVisibility(View.GONE);
        }
        //final Drawable drawable = item.getDrawable();
       /* String title = item.getActivityName();
        String header = "活";
        if (null != title && title.length() > 2) {
            header = title.substring(0, 1);
        }
        final TextDrawable drawable = TextDrawable.builder().buildRoundRect(header, mGenerator.getColor(header), toPx(10));
        holder.imageView.setImageDrawable(drawable);
        holder.textView.setText(item.getActivityName());

        // if navigation is supported, show the ">" navigation icon
        if (item.getDelFlag().intValue()==1) {
             holder.textView.setCompoundDrawablesWithIntrinsicBounds(null, null, ctx.getResources().getDrawable(R.drawable.ing),  null);
        }else {
             holder.textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }*/


        return convertView;
    }

    private static class ViewHolder {

        private ImageView imageView;
        private ImageView eshoppings_item_image;
        private ImageView eshoppings_item_fave;

        private TextView textView;
        private TextView eshoppings_item_title;
        private TextView eshoppings_item_start;
        private TextView eshoppings_item_category;
        private TextView eshoppings_item_on_sale;

        private ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.imageView);
            eshoppings_item_image = (ImageView) view.findViewById(R.id.eshoppings_item_image);
            eshoppings_item_fave = (ImageView) view.findViewById(R.id.eshoppings_item_fave);
            textView = (TextView) view.findViewById(R.id.textView);
            eshoppings_item_title = (TextView) view.findViewById(R.id.eshoppings_item_title);
            eshoppings_item_start = (TextView) view.findViewById(R.id.eshoppings_item_start);
            eshoppings_item_category = (TextView) view.findViewById(R.id.eshoppings_item_category);
            eshoppings_item_on_sale = (TextView) view.findViewById(R.id.eshoppings_item_on_sale);
        }
    }

    public void setLists(List<ShopActivity> lists) {
        this.lists = lists;
    }

    public int toPx(int dp) {
        Resources resources = ctx.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
    }
}
