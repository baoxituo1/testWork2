package com.trade.bluehole.trad.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.AbsListView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.trade.bluehole.trad.R;


/**
 * 图片加载工具类。<br/>
 * <br/>
 */
public class ImageManager
{
    public static ImageLoader imageLoader;
    public static DisplayImageOptions options;
    public static AbsListView.OnScrollListener pauseScrollListener;

    public static void init()
    {
        imageLoader = ImageLoader.getInstance();

        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.sample)
                .showImageOnFail(R.drawable.sample)
                .showImageOnLoading(R.drawable.sample)
                .cacheInMemory(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        pauseScrollListener = new PauseOnScrollListener(imageLoader, true, true);
    }
}
