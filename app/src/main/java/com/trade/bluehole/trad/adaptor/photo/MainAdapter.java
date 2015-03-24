package com.trade.bluehole.trad.adaptor.photo;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.trade.bluehole.trad.NewProductActivity;
import com.trade.bluehole.trad.R;
import com.trade.bluehole.trad.activity.photo.ImageDirActivity;
import com.trade.bluehole.trad.adaptor.MyBaseAdapter;
import com.trade.bluehole.trad.entity.photo.Photo;
import com.trade.bluehole.trad.util.ImageManager;
import com.trade.bluehole.trad.util.MyApplication;

import java.util.List;



/**
 * 首页图片已经选择的图片列表。<br/>
 * <br/>
 * Created by yanglw on 2014/8/17.
 */
public class MainAdapter extends MyBaseAdapter<Photo>
{
    NewProductActivity ctx;
    public MainAdapter(NewProductActivity context, List<Photo> list)
    {
        super(context, list);
        ctx=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ObjectHold oh;
        Photo p=mList.get(position);
        View view=convertView;
        if (view == null)
        {
            oh =new ObjectHold();
            view = (ImageView) mInflater.inflate(R.layout.i_photo_main_grid, parent, false);
            oh.imageView=(ImageView)view.findViewById(R.id.grid_view_img);
            view.setTag(oh);
        }else{
            oh=(ObjectHold)view.getTag();
        }
        if(null==p.imgPath||"".equals(p.imgPath)){//后面放个加号增加图片
            //imageView.setImageResource(R.drawable.onekey_number_add);
            //imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            oh.imageView.setImageResource(R.drawable.onekey_number_add);
            oh.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ctx.addMoreProImageClick();
                }
            });
        }else{
            ImageManager.imageLoader.displayImage("file:///" + mList.get(position).imgPath,  oh.imageView,ImageManager.options);
        }
        //ImageLoader.getInstance().displayImage("file:///" + mList.get(position).imgPath,imageView,ImageManager.options);
        return view;
    }

    static class ObjectHold{
        ImageView imageView;
    }
}
