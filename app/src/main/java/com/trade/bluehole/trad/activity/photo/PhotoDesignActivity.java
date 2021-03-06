package com.trade.bluehole.trad.activity.photo;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.trade.bluehole.trad.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;

import com.trade.bluehole.trad.adaptor.photo.PhotoDesignFilterAdapter;
import com.trade.bluehole.trad.util.ImageManager;
import com.trade.bluehole.trad.util.photo.GPUImageFilterTools;
import com.trade.bluehole.trad.util.photo.GPUImageFilterTools.FilterAdjuster;

/**
 * 图片调整
 */
@EActivity(R.layout.activity_photo_design)
public class PhotoDesignActivity extends ActionBarActivity implements SeekBar.OnSeekBarChangeListener,GPUImageView.OnPictureSavedListener {
    public static final String IMAGE_URI = "photoUri";//待显示图片资源
    public static final String IMAGE_URI_POSITION = "position";//列表中位置
    private static final int REQUEST_PICK_IMAGE = 1;
    private GPUImageFilter mFilter;
    private FilterAdjuster mFilterAdjuster;//过滤器选择
    @ViewById(R.id.photo_gpu_image)
     GPUImageView mGPUImageView;//优化视图

    @ViewById(R.id.filter_recycler_view)//优化效果列表
    RecyclerView mRecyclerView;

    @Extra(IMAGE_URI)
    String uri;//图片路径
    @Extra(IMAGE_URI_POSITION)
    Integer position;//图片路径

    @ViewById(R.id.photo_seekBar)
    SeekBar seekBar;//强度拖动条

    PhotoDesignFilterAdapter filterAdapter;//效果适配器
    private LinearLayoutManager  mLayoutManager;//创建线性布局管理器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 实例化
     */
    @AfterViews
    void initData(){
        if(null!=uri&&!"".equals(uri)){
            ImageManager.imageLoader.loadImage(uri, new MyImageLoadingListener());
        }
        seekBar.setOnSeekBarChangeListener(this);
        //处理顶部过滤效果
        mRecyclerView.setHasFixedSize(true);//内容变化不影响布局开启
        mLayoutManager=new LinearLayoutManager(this);//设置线性布局管理
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    /**
     * 图片加载
     */
    private  class MyImageLoadingListener implements ImageLoadingListener {

        @Override
        public void onLoadingStarted(String s, View view) {
        }

        @Override
        public void onLoadingFailed(String s, View view, FailReason failReason) {
        }

        @Override
        public void onLoadingComplete(String s, View view, Bitmap bitmap) {
            mGPUImageView.setImage(bitmap);
            filterAdapter=new PhotoDesignFilterAdapter(PhotoDesignActivity.this);
            mRecyclerView.setAdapter(filterAdapter);//设置适配器
            filterAdapter.notifyDataSetChanged();
        }

        @Override
        public void onLoadingCancelled(String s, View view) {

        }
    }



    /**
     * 选择过滤器,并实例化到图片
     * @param filter
     */
    private void switchFilterTo(final GPUImageFilter filter) {
        if (mFilter == null || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            mFilter = filter;
            mGPUImageView.setFilter(mFilter);
            mFilterAdjuster = new FilterAdjuster(mFilter);
            //根据当前模式是否支持调整，动态显示拖动条
            seekBar.setVisibility( mFilterAdjuster.canAdjust() ? View.VISIBLE : View.GONE);
        }
    }

    private void saveImage() {
        String fileName = System.currentTimeMillis() + ".jpg";
        mGPUImageView.saveToPictures("GPUImage", fileName, this);
        //        mGPUImageView.saveToPictures("GPUImage", fileName, 1600, 1600, this);
    }

    /**
     * 当保存图片时候
     * @param uri
     */
    @Override
    public void onPictureSaved(final Uri uri) {
        this.uri=uri.toString();
       // Toast.makeText(this, "Saved: " + uri.toString(), Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, "Saved path: " + uri.getPath(), Toast.LENGTH_SHORT).show();
        //Log.d("onPictureSaved--","Saved path: " + getRealPathFromURI(uri));
        //组织参数
        Intent intent = new Intent();
        intent.putExtra(IMAGE_URI, getRealPathFromURI(uri));
        intent.putExtra(IMAGE_URI_POSITION, position);
        setResult(RESULT_OK, intent);//返回结果
        finish();//关闭
    }

    /**
     * 当滑动条拖动
     * @param seekBar
     * @param progress
     * @param fromUser
     */
    @Override
    public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
        if (mFilterAdjuster != null) {
            mFilterAdjuster.adjust(progress);
        }
        mGPUImageView.requestRender();
    }

    /**
     * 选择图片效果
     * @param type
     */
    public void chooseFilter(GPUImageFilterTools.FilterType type){
        switchFilterTo(GPUImageFilterTools.createFilterForType(this, type));
        mGPUImageView.requestRender();
    }


    @Override
    public void onStartTrackingTouch(final SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(final SeekBar seekBar) {
    }


    /**
     * 顶部按钮创建和捕捉事件
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photo_design, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.photo_menu_done) {
            saveImage();
            return true;
        }else if(id == R.id.photo_menu_advance){
            GPUImageFilterTools.showDialog(this, new GPUImageFilterTools.OnGpuImageFilterChosenListener() {
                @Override
                public void onGpuImageFilterChosenListener(final GPUImageFilter filter) {
                    switchFilterTo(filter);
                    mGPUImageView.requestRender();
                }

            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
}
