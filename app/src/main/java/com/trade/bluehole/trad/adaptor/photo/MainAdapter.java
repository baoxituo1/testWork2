package com.trade.bluehole.trad.adaptor.photo;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

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
            view = mInflater.inflate(R.layout.i_photo_main_grid, parent, false);
            oh.imageView=(ImageView)view.findViewById(R.id.grid_view_img);
            oh.clearBtn=(ImageView)view.findViewById(R.id.clear_btn);
            view.setTag(oh);
        }else{
            oh=(ObjectHold)view.getTag();
        }
        if(null==p.imgPath||"".equals(p.imgPath)){//后面放个加号增加图片
            oh.imageView.setImageResource(R.drawable.onekey_number_add);
        }else{
            if(p.dataType=="1"){
                ImageManager.imageLoader.displayImage("http://125.oss-cn-beijing.aliyuncs.com/" + mList.get(position).imgPath, oh.imageView, ImageManager.options);
            }else {
                ImageManager.imageLoader.displayImage("file:///" + mList.get(position).imgPath, oh.imageView, ImageManager.options);
            }
        }
        //ImageLoader.getInstance().displayImage("file:///" + mList.get(position).imgPath,imageView,ImageManager.options);
        if(position<(mList.size()-1)){
            final int index=position;
            oh.clearBtn.setVisibility(View.VISIBLE);
            oh.clearBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mList.remove(index);
                    MainAdapter.this.notifyDataSetChanged();
                    Toast.makeText(ctx,"clear_clean click",Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            oh.clearBtn.setVisibility(View.GONE);
        }
        return view;
    }

    static class ObjectHold{
        ImageView imageView;
        ImageView clearBtn;
    }
}
