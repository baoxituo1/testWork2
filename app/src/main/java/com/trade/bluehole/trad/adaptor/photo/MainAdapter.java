package com.trade.bluehole.trad.adaptor.photo;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.trade.bluehole.trad.NewProductActivity;
import com.trade.bluehole.trad.R;
import com.trade.bluehole.trad.activity.photo.ImageDirActivity;
import com.trade.bluehole.trad.activity.photo.PhotoDesignActivity;
import com.trade.bluehole.trad.activity.photo.PhotoDesignActivity_;
import com.trade.bluehole.trad.adaptor.MyBaseAdapter;
import com.trade.bluehole.trad.entity.photo.Photo;
import com.trade.bluehole.trad.util.ImageManager;
import com.trade.bluehole.trad.util.MyApplication;
import com.trade.bluehole.trad.util.data.DataUrlContents;

import java.util.List;



/**
 * 首页图片已经选择的图片列表。<br/>
 * <br/>
 *
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
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        ObjectHold oh;
        Photo p=mList.get(position);
        View view=convertView;
       // if (view == null)
       // {
            oh =new ObjectHold();
            view = mInflater.inflate(R.layout.i_photo_main_grid, parent, false);
            oh.imageView=(ImageView)view.findViewById(R.id.grid_view_img);
            oh.clearBtn=(ImageView)view.findViewById(R.id.clear_btn);
            view.setTag(oh);
       // }else{
            oh=(ObjectHold)view.getTag();
       // }
        String _uri;
        if(null==p.imgPath||"".equals(p.imgPath)){//后面放个加号增加图片
            oh.imageView.setImageResource(R.drawable.new_add_2);
        }else{
            if("1".equals(p.dataType)){
                _uri=DataUrlContents.IMAGE_HOST + mList.get(position).imgPath;
                ImageManager.imageLoader.displayImage(DataUrlContents.IMAGE_HOST + mList.get(position).imgPath+DataUrlContents.img_list_head_img, oh.imageView, ImageManager.options);
            }else {
                _uri="file:///" + mList.get(position).imgPath;
                ImageManager.imageLoader.displayImage(_uri, oh.imageView, ImageManager.options);
            }
            //Log.d("AdapterPictureSaved--", "Saved path: " + _uri);
            //点击图片优化触发事件
            final String temp=_uri;
            oh.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = PhotoDesignActivity_.intent(ctx).get();
                    intent.putExtra(PhotoDesignActivity.IMAGE_URI, temp);
                    intent.putExtra(PhotoDesignActivity.IMAGE_URI_POSITION, position);
                    ctx.startActivityForResult(intent, NewProductActivity.PRODUCT_DESIGN_PHOTO);
                }
            });
        }
        //ImageLoader.getInstance().displayImage("file:///" + mList.get(position).imgPath,imageView,ImageManager.options);

        if(position<(mList.size()-1)){
            final int index=position;
            oh.clearBtn.setVisibility(View.VISIBLE);
            oh.clearBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(ctx,"clear_clean click",Toast.LENGTH_SHORT).show();
                    //添加到activity的待删除列表
                    if(null!=mList.get(index).id!="".equals(mList.get(index).id)){
                        ctx.del_image_ids+=mList.get(index).id+",";
                    }
                    mList.remove(index);
                    ctx.shrinkDrawGridLayout();
                    MainAdapter.this.notifyDataSetChanged();
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
