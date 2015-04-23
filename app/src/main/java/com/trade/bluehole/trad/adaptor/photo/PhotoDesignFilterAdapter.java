package com.trade.bluehole.trad.adaptor.photo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.trade.bluehole.trad.R;
import com.trade.bluehole.trad.activity.photo.PhotoDesignActivity;
import com.trade.bluehole.trad.util.photo.GPUImageFilterTools;
import com.trade.bluehole.trad.util.photo.GPUImageFilterTools.FilterType;

import java.util.ArrayList;
import java.util.List;


/**
 * 美化图片效果适配器
 * Created by Administrator on 2015-04-22.
 */
public class PhotoDesignFilterAdapter extends RecyclerView.Adapter<PhotoDesignFilterAdapter.ViewHolder>{
    GPUImageFilterTools.FilterList filters= GPUImageFilterTools.filters;
    static PhotoDesignActivity ctx;
    List<RelativeLayout> layouts=new ArrayList<>();
    public PhotoDesignFilterAdapter(PhotoDesignActivity ctx){
        this.ctx=ctx;
        initFilter();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.r_photo_filter_item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mTextView.setText(filters.names.get(position));
            //点击触发效果
            holder.back_layout.setOnClickListener(new MyClickListener(holder,position));
            //统一管理
            layouts.add(holder.back_layout);
    }

    /**
     * 实现自定义点击类
     */
    class MyClickListener implements View.OnClickListener{
        ViewHolder holder;
        int position;
      public  MyClickListener(ViewHolder holder,int position){
          this.position=position;
          this.holder=holder;
      }
        @Override
        public void onClick(View v) {
            ctx.chooseFilter(filters.filters.get(position));
            cancelLayoutBackGround();
            holder.back_layout.setBackgroundColor(ctx.getResources().getColor(R.color.gainsboro));
        }
    }

    /**
     * 取消所有背景效果
     */
    void cancelLayoutBackGround(){
        for(RelativeLayout ls:layouts){
            ls.setBackgroundColor(ctx.getResources().getColor(R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return null!=filters?filters.names.size():0;
    }

    // 定义一个参考对象
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        private ImageView imageView;
        private RelativeLayout back_layout;
        public ViewHolder(View v) {
            super(v);
            mTextView =(TextView) v.findViewById(R.id.photo_filter_text);
            imageView = (ImageView) v.findViewById(R.id.photo_filter_image);
            back_layout = (RelativeLayout) v.findViewById(R.id.photo_filter_layout);
        }
    }


    void initFilter(){
        filters.addFilter("对比度", FilterType.CONTRAST);
        filters.addFilter("反色", FilterType.INVERT);
        filters.addFilter("像素化", FilterType.PIXELATION);
        filters.addFilter("色彩", FilterType.HUE);
        filters.addFilter("伽马", FilterType.GAMMA);
        filters.addFilter("明亮", FilterType.BRIGHTNESS);
        filters.addFilter("深褐色", FilterType.SEPIA);
        filters.addFilter("灰度", FilterType.GRAYSCALE);
        filters.addFilter("锐利", FilterType.SHARPEN);
        filters.addFilter("素描", FilterType.SOBEL_EDGE_DETECTION);
        filters.addFilter("3x3 回转", FilterType.THREE_X_THREE_CONVOLUTION);
        filters.addFilter("浮雕", FilterType.EMBOSS);
        filters.addFilter("分色效果", FilterType.POSTERIZE);
        filters.addFilter("分组过滤", FilterType.FILTER_GROUP);
        filters.addFilter("色彩饱和", FilterType.SATURATION);
        filters.addFilter("曝光", FilterType.EXPOSURE);
        filters.addFilter("突出阴影", FilterType.HIGHLIGHT_SHADOW);
        filters.addFilter("黑白", FilterType.MONOCHROME);
        filters.addFilter("模糊", FilterType.OPACITY);
        filters.addFilter("三原色", FilterType.RGB);
        filters.addFilter("白平衡", FilterType.WHITE_BALANCE);
        filters.addFilter("暗角效果", FilterType.VIGNETTE);
        filters.addFilter("颜色调和曲线", FilterType.TONE_CURVE);
    }
}
