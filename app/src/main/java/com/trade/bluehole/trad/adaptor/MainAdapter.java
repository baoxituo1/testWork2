package com.trade.bluehole.trad.adaptor;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.trade.bluehole.trad.R;
import com.trade.bluehole.trad.entity.photo.Photo;

import java.util.List;


//import my.android.app.chooseimages.utils.ImageManager;

/**
 * 首页图片已经选择的图片列表。<br/>
 * <br/>
 *
 */
public class MainAdapter extends MyBaseAdapter<Photo>
{
    public MainAdapter(Context context, List<Photo> list)
    {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
       /* ImageView imageView;
        if (convertView == null)
        {
            imageView = (ImageView) mInflater.inflate(R.layout.i_main, parent, false);
        }
        else
        {
            imageView = (ImageView) convertView;
        }
        ImageManager.imageLoader.displayImage("file:///" + mList.get(position).imgPath,
                                              imageView,
                                              ImageManager.options);
        return imageView;*/

        return null;

    }
}
