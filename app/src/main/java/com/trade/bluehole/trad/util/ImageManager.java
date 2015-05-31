package com.trade.bluehole.trad.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.AbsListView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.trade.bluehole.trad.R;

import java.util.ArrayList;
import java.util.List;


/**
 * 图片加载工具类。<br/>
 * <br/>
 */
public class ImageManager
{
    public static ImageLoader imageLoader;
    public static DisplayImageOptions options;
    public static AbsListView.OnScrollListener pauseScrollListener;
    private static List<Integer> imageRes;
    public static void init()
    {
        imageLoader = ImageLoader.getInstance();

        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.list_default_yellow)
                .showImageOnFail(R.drawable.sample)
                .showImageOnLoading(R.drawable.sample)
                .cacheInMemory(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        pauseScrollListener = new PauseOnScrollListener(imageLoader, true, true);
        imageRes=new ArrayList<>();
        imageRes.add(R.drawable.list_default_blue);
        imageRes.add(R.drawable.list_default_green);
        imageRes.add(R.drawable.list_default_red);
        imageRes.add(R.drawable.list_default_yellow);
        imageRes.add(R.drawable.list_default_pink);
    }

    public static int getImage(Object key) {
        return ((Integer)imageRes.get(Math.abs(key.hashCode()) % imageRes.size())).intValue();
    }
}
