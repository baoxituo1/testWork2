package com.trade.bluehole.trad.adaptor.sale;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.trade.bluehole.trad.R;
import com.trade.bluehole.trad.entity.sale.ProductSaleVO;
import com.trade.bluehole.trad.util.data.DataUrlContents;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 选择打折商品适配器
 * Created by Administrator on 2015-04-14.
 */
public class SelectProductSaleAdapter extends BaseAdapter {
    private Context context;
    LayoutInflater inflater;
    DisplayImageOptions options;
    DecimalFormat    df   = new DecimalFormat("######0.0");
    public List<ProductSaleVO> getLists() {
        return lists;
    }

    public void setLists(List<ProductSaleVO> lists) {
        this.lists = lists;
    }

    private List<ProductSaleVO> lists = new ArrayList<ProductSaleVO>();

    public SelectProductSaleAdapter(Context ctx) {
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
        ProductSaleVO obj = lists.get(position);
        if (null == view) {
            view = inflater.inflate(R.layout.i_select_sale_pro_list_item, parent, false);
            viewHolder = new HoldObject();
            viewHolder.sale_image = (ImageView) view.findViewById(R.id.sale_image);
            viewHolder.sale_name = (TextView) view.findViewById(R.id.sale_name);
            viewHolder.sale_price = (TextView) view.findViewById(R.id.sale_price);
            viewHolder.sale_old_price = (TextView) view.findViewById(R.id.sale_old_price);
            viewHolder.sale_sale_num = (TextView) view.findViewById(R.id.sale_sale_num);
            viewHolder.progressBar = (ProgressBar) view.findViewById(R.id.progress);
            view.setTag(viewHolder);
        } else {
            viewHolder = (HoldObject) convertView.getTag();
        }
        if (!lists.isEmpty() && lists.size() > position) {
            ImageLoader.getInstance()
                    .displayImage(DataUrlContents.IMAGE_HOST + obj.getProductImage() + DataUrlContents.img_list_head_img, viewHolder.sale_image, options, new SimpleImageLoadingListener() {
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
            viewHolder.sale_name.setText(obj.getProductName());
            if(null!=obj.getDynamicCode()&&!"".equals(obj.getDynamicCode())){//如果有打折信息
                //第一个展示促销价格
                viewHolder.sale_price.setText("￥" + obj.getSalePrice());
                viewHolder.sale_old_price.setVisibility(View.VISIBLE);
                viewHolder.sale_old_price.setText("￥" + obj.getOldPrice());
                viewHolder.sale_old_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);//添加删除线

                viewHolder.sale_sale_num.setVisibility(View.VISIBLE);
                viewHolder.sale_sale_num.setText(df.format((obj.getSalePrice() / obj.getOldPrice()) * 10) + " 折");
            }else{//如果没有打折信息
                viewHolder.sale_old_price.setVisibility(View.GONE);
                viewHolder.sale_sale_num.setVisibility(View.GONE);
                //第一个展示原价
                viewHolder.sale_price.setText("￥" + obj.getOldPrice());
            }
        }
        return view;
    }

    static class HoldObject {
        ProgressBar progressBar;
        ImageView sale_image;
        TextView sale_name;
        TextView sale_price;
        TextView sale_old_price;
        TextView sale_sale_num;
    }
}
