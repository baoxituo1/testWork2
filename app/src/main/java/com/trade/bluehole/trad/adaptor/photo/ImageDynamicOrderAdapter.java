package com.trade.bluehole.trad.adaptor.photo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.trade.bluehole.trad.R;
import com.trade.bluehole.trad.entity.photo.Photo;
import com.trade.bluehole.trad.util.ImageManager;
import com.trade.bluehole.trad.util.data.DataUrlContents;

import org.askerov.dynamicgrid.BaseDynamicGridAdapter;

import java.util.List;

/**
 * 图片动态排序适配器
 * Created by Administrator on 2015-04-23.
 */
public class ImageDynamicOrderAdapter extends BaseDynamicGridAdapter {

    public ImageDynamicOrderAdapter(Context context, List<?> items, int columnCount) {
        super(context, items, columnCount);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CheeseViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.i_dynamic_image_order_item, null);
            holder = new CheeseViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (CheeseViewHolder) convertView.getTag();
        }
        holder.build((Photo) getItem(position));
        return convertView;
    }

    private class CheeseViewHolder {
        private TextView titleText;
        private ImageView image;

        private CheeseViewHolder(View view) {
            titleText = (TextView) view.findViewById(R.id.item_title);
            image = (ImageView) view.findViewById(R.id.item_img);
        }

        void build(Photo obj) {
            //titleText.setText(title);
            image.setImageResource(R.drawable.ic_launcher);
            if("1".equals(obj.dataType)){

                ImageManager.imageLoader.displayImage(DataUrlContents.IMAGE_HOST + obj.imgPath+DataUrlContents.img_list_head_img, image, ImageManager.options);
            }else {
                ImageManager.imageLoader.displayImage("file:///" + obj.imgPath, image, ImageManager.options);
            }
        }
    }
}
