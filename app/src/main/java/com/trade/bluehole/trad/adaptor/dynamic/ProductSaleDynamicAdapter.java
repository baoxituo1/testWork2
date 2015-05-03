package com.trade.bluehole.trad.adaptor.dynamic;

import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.trade.bluehole.trad.DynamicManageActivity;
import com.trade.bluehole.trad.HeaderAnimatorActivity;
import com.trade.bluehole.trad.R;
import com.trade.bluehole.trad.activity.shop.ProductClassifyActivity;
import com.trade.bluehole.trad.activity.shop.SearchProductActivity;
import com.trade.bluehole.trad.entity.dynamic.DynaicInfoVO;
import com.trade.bluehole.trad.util.data.DataUrlContents;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015-04-13.
 */
public class ProductSaleDynamicAdapter extends BaseAdapter {
    private DynamicManageActivity context;
    LayoutInflater inflater;
    DisplayImageOptions options;
    DecimalFormat df   = new DecimalFormat("######0.0");
    public List<DynaicInfoVO> getLists() {
        return lists;
    }

    public void setLists(List<DynaicInfoVO> lists) {
        this.lists = lists;
    }

    private List<DynaicInfoVO> lists = new ArrayList<DynaicInfoVO>();

    public ProductSaleDynamicAdapter(DynamicManageActivity ctx) {
        context = ctx;
        inflater = LayoutInflater.from(context);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.abc_cab_background_internal_bg)
                        // .showImageForEmptyUri(R.drawable.ic_empty)
                        // .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    @Override
    public int getCount() {
        return lists.isEmpty() ? 0 : lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.isEmpty() ? null : lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final HoldObject viewHolder;
        final DynaicInfoVO obj = lists.get(position);
        if (null == view) {
            view = inflater.inflate(R.layout.i_dynamic_index_list_item, parent, false);
            viewHolder = new HoldObject();
            viewHolder.dy_image = (ImageView) view.findViewById(R.id.dy_image);
            viewHolder.dy_name = (TextView) view.findViewById(R.id.dy_name);
            viewHolder.dy_price = (TextView) view.findViewById(R.id.dy_price);
            viewHolder.dy_old_price = (TextView) view.findViewById(R.id.dy_old_price);
            viewHolder.dy_sale_num = (TextView) view.findViewById(R.id.dy_sale_num);
            viewHolder.dy_date = (TextView) view.findViewById(R.id.dy_date);
            viewHolder.progressBar = (ProgressBar) view.findViewById(R.id.progress);
            viewHolder.dy_del_data = (RelativeLayout) view.findViewById(R.id.dy_del_data);
            viewHolder.dy_copy_data = (RelativeLayout) view.findViewById(R.id.dy_copy_data);
            viewHolder.dy_share_data = (RelativeLayout) view.findViewById(R.id.dy_share_data);
            view.setTag(viewHolder);
        } else {
            viewHolder = (HoldObject) convertView.getTag();
        }
        if (!lists.isEmpty() && lists.size() > position) {
            ImageLoader.getInstance()
                    .displayImage(DataUrlContents.IMAGE_HOST + obj.getProductImage() + DataUrlContents.img_list_head_img, viewHolder.dy_image, options, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            viewHolder.progressBar.setProgress(0);
                            viewHolder.progressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            viewHolder.progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            viewHolder.progressBar.setVisibility(View.GONE);
                        }
                    }, new ImageLoadingProgressListener() {
                        @Override
                        public void onProgressUpdate(String imageUri, View view, int current, int total) {
                            viewHolder.progressBar.setProgress(Math.round(100.0f * current / total));
                        }
                    });
            viewHolder.dy_name.setText(obj.getProductName());
            viewHolder.dy_price.setText("￥" + obj.getSalePrice());
            viewHolder.dy_old_price.setText("￥" + obj.getOldPrice());
            viewHolder.dy_old_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);//添加删除线
            viewHolder.dy_sale_num.setText(df.format((obj.getSalePrice() / obj.getOldPrice()) * 10) + " 折");
            viewHolder.dy_date.setText(obj.getSaleStartDate() + " 至 " + obj.getSaleEndDate());
            //点击删除状态
            viewHolder.dy_del_data.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.showDelConfirm(obj.getProductCode());
                }
            });
            //复制地址被点击
            viewHolder.dy_copy_data.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager clip = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
                    clip.setText(DataUrlContents.SERVER_HOST + DataUrlContents.show_view_pro_web + "?productCode=" + obj.getProductCode() + "&shopCode=" + obj.getShopCode()); // 复制
                    Toast.makeText(context, "成功复制到剪切板!", Toast.LENGTH_SHORT).show();
                }
            });
            //分享被点击
            viewHolder.dy_share_data.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.shareProduct(obj.getProductCode(), obj.getProductName(), DataUrlContents.IMAGE_HOST + obj.getProductImage() + DataUrlContents.img_list_head_img);
                }
            });
        }
        return view;
    }

    static class HoldObject {
        ProgressBar progressBar;
        RelativeLayout dy_del_data;
        RelativeLayout dy_copy_data;
        RelativeLayout dy_share_data;
        ImageView dy_image;
        TextView dy_name;
        TextView dy_price;
        TextView dy_old_price;
        TextView dy_sale_num;
        TextView dy_date;
    }
}
