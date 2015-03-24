package com.trade.bluehole.trad.adaptor.photo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.trade.bluehole.trad.R;
import com.trade.bluehole.trad.adaptor.MyBaseAdapter;
import com.trade.bluehole.trad.entity.photo.Photo;
import com.trade.bluehole.trad.util.ImageManager;

import java.util.List;



/**
 * <br/>
 * <br/>
 * Created by yanglw on 2014/8/17.
 */
public class ImagesAdapter extends MyBaseAdapter<Photo>
{
    private List<Photo> mCheckList;

    public ImagesAdapter(Context context, List<Photo> list, List<Photo> checkList)
    {
        super(context, list);
        mCheckList = checkList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Holder holder;
        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.i_photo_images, parent, false);
            holder = new Holder();
            holder.mImageView = (ImageView) convertView.findViewById(R.id.iv);
            holder.mCheckImgaeView = (ImageView) convertView.findViewById(R.id.check);

            convertView.setTag(holder);
        }
        else
        {
            holder = (Holder) convertView.getTag();
        }

        Photo photo = mList.get(position);

        if (mCheckList != null && mCheckList.contains(photo))
        {
            holder.mCheckImgaeView.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.mCheckImgaeView.setVisibility(View.INVISIBLE);
        }
        ImageManager.imageLoader.displayImage("file:///" + photo.imgPath, holder.mImageView,ImageManager.options);
        //ImageLoader.getInstance().displayImage("file:///" + photo.imgPath,holder.mImageView,ImageManager.options);
        return convertView;
    }

    public void setCheck(int postion, View view)
    {
        Photo photo = mList.get(postion);

        boolean checked = mCheckList.contains(photo);

        Holder holder = (Holder) view.getTag();

        if (checked)
        {
            mCheckList.remove(photo);
            holder.mCheckImgaeView.setVisibility(View.INVISIBLE);
        }
        else
        {
            mCheckList.add(photo);
            holder.mCheckImgaeView.setVisibility(View.VISIBLE);
        }
    }


    private class Holder
    {
        public ImageView mImageView, mCheckImgaeView;
    }

}
