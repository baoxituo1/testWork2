package com.trade.bluehole.trad.adaptor;

import android.content.Context;
import android.graphics.Bitmap;
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
import com.trade.bluehole.trad.entity.ProductIndexVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015-03-23.
 */
public class ProductGridviewAdaptor extends BaseAdapter {
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
    public ProductGridviewAdaptor(Context ctx){
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
        if(null==view){
            view=inflater.inflate(R.layout.i_pro_gridview_product,parent,false);
            viewHolder=new HoldObject();
            viewHolder.product_cover_image=(ImageView)view.findViewById(R.id.product_cover_image);
            viewHolder.product_name=(TextView)view.findViewById(R.id.product_name);
            viewHolder.product_price=(TextView)view.findViewById(R.id.product_price);
            viewHolder.progressBar=(ProgressBar)view.findViewById(R.id.progress);
            view.setTag(viewHolder);
        }else{
            viewHolder=(HoldObject) convertView.getTag();
        }
        if(!lists.isEmpty()&&lists.size()>position){
            ImageLoader.getInstance()
                    .displayImage("http://125.oss-cn-beijing.aliyuncs.com/"+lists.get(position).getCoverMiddleImage(), viewHolder.product_cover_image, options, new SimpleImageLoadingListener() {
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
            viewHolder.product_price.setText("$"+lists.get(position).getProductPrice());
        }
        return view;
    }

    static  class HoldObject{
        ProgressBar progressBar;
        ImageView product_cover_image;
        TextView product_name;
        TextView product_price;
    }
}
