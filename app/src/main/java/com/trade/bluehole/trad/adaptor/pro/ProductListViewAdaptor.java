package com.trade.bluehole.trad.adaptor.pro;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.trade.bluehole.trad.NewProductActivity;
import com.trade.bluehole.trad.NewProductActivity_;
import com.trade.bluehole.trad.R;
import com.trade.bluehole.trad.activity.webview.ProductWebViewActivity;
import com.trade.bluehole.trad.activity.webview.ProductWebViewActivity_;
import com.trade.bluehole.trad.activity.webview.WebViewActivity_;
import com.trade.bluehole.trad.entity.ProductIndexVO;
import com.trade.bluehole.trad.util.data.DataUrlContents;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015-04-01.
 */
public class ProductListViewAdaptor extends BaseAdapter {
    private Context context;
    LayoutInflater inflater;
    DisplayImageOptions options;
    public List<ProductIndexVO> getLists() {
        return lists;
    }

    public void setLists(List<ProductIndexVO> lists) {
        this.lists = lists;
    }

    private List<ProductIndexVO> lists=new ArrayList<ProductIndexVO>();
    public ProductListViewAdaptor(Context ctx){
        context=ctx;
        inflater =LayoutInflater.from(context);
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
        return lists.isEmpty()?0:lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.isEmpty()?null:lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=convertView;
        final  HoldObject viewHolder;
        final ProductIndexVO obj=lists.get(position);
        if(null==view){
            view=inflater.inflate(R.layout.i_pro_product_list_item,parent,false);
            viewHolder=new HoldObject();
            viewHolder.product_cover_image=(ImageView)view.findViewById(R.id.pro_image);
            viewHolder.product_name=(TextView)view.findViewById(R.id.pro_name);
            viewHolder.product_number=(TextView)view.findViewById(R.id.pro_number);
            viewHolder.product_price=(TextView)view.findViewById(R.id.pro_price);
            viewHolder.progressBar=(ProgressBar)view.findViewById(R.id.progress);
            viewHolder.pro_view_btn=(RelativeLayout)view.findViewById(R.id.pro_view_btn);
            viewHolder.pro_edit_layout=(LinearLayout)view.findViewById(R.id.pro_edit_layout);
            view.setTag(viewHolder);
        }else{
            viewHolder=(HoldObject) convertView.getTag();
        }
        if(!lists.isEmpty()&&lists.size()>position){
            ImageLoader.getInstance()
                    .displayImage(DataUrlContents.IMAGE_HOST + lists.get(position).getCoverMiddleImage() + DataUrlContents.img_list_head_img, viewHolder.product_cover_image, options, new SimpleImageLoadingListener() {
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
            viewHolder.product_name.setText(lists.get(position).getProductName());
            viewHolder.product_price.setText("￥"+lists.get(position).getProductPrice());
            viewHolder.product_number.setText("库存量10件");
            //预览被点击
            viewHolder.pro_view_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = ProductWebViewActivity_.intent(context).get();
                    intent.putExtra(ProductWebViewActivity.PRO_CODE, obj.getProductCode());
                    intent.putExtra(ProductWebViewActivity.SHOP_CODE, obj.getShopCode());
                    context.startActivity(intent);
                }
            });
            //商品修改被点击
            viewHolder.pro_edit_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = NewProductActivity_.intent(context).get();
                    intent.putExtra(NewProductActivity.PRODUCT_CODE_EXTRA, obj.getProductCode());
                    intent.putExtra(NewProductActivity.SHOP_CODE_EXTRA, obj.getShopCode());
                    context.startActivity(intent);
                }
            });
        }
        return view;
    }

    static  class HoldObject{
        ProgressBar progressBar;
        ImageView product_cover_image;
        TextView product_name;
        TextView product_price;
        TextView product_number;
        RelativeLayout pro_view_btn;
        LinearLayout pro_edit_layout;
        TextView pro_hot;
    }
}
