package com.trade.bluehole.trad.activity.camera;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;

import com.aliyun.mbaas.oss.callback.SaveCallback;
import com.aliyun.mbaas.oss.model.OSSException;
import com.aliyun.mbaas.oss.storage.OSSData;
import com.soundcloud.android.crop.Crop;
import com.trade.bluehole.trad.NewProductActivity;
import com.trade.bluehole.trad.R;
import com.trade.bluehole.trad.SuperMainActivity;
import com.trade.bluehole.trad.entity.shop.ShopCommonInfo;
import com.trade.bluehole.trad.record.ui.BaseActivity;
import com.trade.bluehole.trad.record.widget.SurfaceVideoView;
import com.trade.bluehole.trad.util.MyApplication;
import com.trade.bluehole.trad.util.StreamUtil;
import com.trade.bluehole.trad.util.camera.FileUtil;
import com.yixia.camera.demo.utils.ToastUtils;
import com.yixia.weibo.sdk.FFMpegUtils;
import com.yixia.weibo.sdk.util.DeviceUtils;
import com.yixia.weibo.sdk.util.StringUtils;

import java.io.File;
import java.util.UUID;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * 通用单独播放界面
 * 
 * @author tangjun
 *
 */
public class VideoPlayerActivity extends BaseActivity implements SurfaceVideoView.OnPlayStateListener, OnErrorListener, OnPreparedListener, OnClickListener, OnCompletionListener, OnInfoListener {
    private static final  String TAG = "VideoPlayerActivity";
	/** 播放控件 */
	private SurfaceVideoView mVideoView;
	/** 暂停按钮 */
	private View mPlayerStatus;
	private View mLoading;

	/** 播放路径 */
	private String mPath;
	/** 是否需要回复播放 */
	private boolean mNeedResume;
    /** 上传按钮 */
    FancyButton uploadButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 防止锁屏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		mPath = getIntent().getStringExtra("path");
		if (StringUtils.isEmpty(mPath)) {
			finish();
			return;
		}

		setContentView(R.layout.rd_activity_video_player);
		mVideoView = (SurfaceVideoView) findViewById(R.id.videoview);
        uploadButton = (FancyButton) findViewById(R.id.pro_upload_video);
		mPlayerStatus = findViewById(R.id.play_status);
		mLoading = findViewById(R.id.loading);

		mVideoView.setOnPreparedListener(this);
		mVideoView.setOnPlayStateListener(this);
		mVideoView.setOnErrorListener(this);
		mVideoView.setOnClickListener(this);
		mVideoView.setOnInfoListener(this);
		mVideoView.setOnCompletionListener(this);

		mVideoView.getLayoutParams().height = DeviceUtils.getScreenWidth(this);

		findViewById(R.id.root).setOnClickListener(this);
        uploadButton.setOnClickListener(this);
		mVideoView.setVideoPath(mPath);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mVideoView != null && mNeedResume) {
			mNeedResume = false;
			if (mVideoView.isRelease())
				mVideoView.reOpen();
			else
				mVideoView.start();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mVideoView != null) {
			if (mVideoView.isPlaying()) {
				mNeedResume = true;
				mVideoView.pause();
			}
		}
	}

	@Override
	protected void onDestroy() {
		if (mVideoView != null) {
			mVideoView.release();
			mVideoView = null;
		}
		super.onDestroy();
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		mVideoView.setVolume(SurfaceVideoView.getSystemVolumn(this));
		mVideoView.start();
//		new Handler().postDelayed(new Runnable() {
//
//			@SuppressWarnings("deprecation")
//			@Override
//			public void run() {
//				if (DeviceUtils.hasJellyBean()) {
//					mVideoView.setBackground(null);
//				} else {
//					mVideoView.setBackgroundDrawable(null);
//				}
//			}
//		}, 300);
		mLoading.setVisibility(View.GONE);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		switch (event.getKeyCode()) {//跟随系统音量走
		case KeyEvent.KEYCODE_VOLUME_DOWN:
		case KeyEvent.KEYCODE_VOLUME_UP:
			mVideoView.dispatchKeyEvent(this, event);
			break;
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public void onStateChanged(boolean isPlaying) {
		mPlayerStatus.setVisibility(isPlaying ? View.GONE : View.VISIBLE);
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		if (!isFinishing()) {
			//播放失败
		}
		finish();
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.root:
			finish();
			break;
		case R.id.videoview:
			if (mVideoView.isPlaying())
				mVideoView.pause();
			else
				mVideoView.start();
			break;
        case R.id.pro_upload_video:
            ToastUtils.showToast(this.getApplicationContext(), "点击上传!");
            try {
                doUploadFile(mPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
            break;

		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		if (!isFinishing())
			mVideoView.reOpen();
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
  @Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		switch (what) {
		case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
			//音频和视频数据不正确
			break;
		case MediaPlayer.MEDIA_INFO_BUFFERING_START:
			if (!isFinishing())
				mVideoView.pause();
			break;
		case MediaPlayer.MEDIA_INFO_BUFFERING_END:
			if (!isFinishing())
				mVideoView.start();
			break;
		case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
			if (DeviceUtils.hasJellyBean()) {
				mVideoView.setBackground(null);
			} else {
				mVideoView.setBackgroundDrawable(null);
			}
			break;
		}
		return false;
	}



    /**
     * 上传视频到阿里云代码
     *
     * @param mPath 视频本地路径
     * @throws Exception
     */
    public void doUploadFile(String mPath) throws Exception {
       // ToastUtils.showToast(this.getApplicationContext(), "mPath:"+mPath);
        String nameUuid=UUID.randomUUID().toString();
        /**获取视频截图**/
        //截图保存路径
        String thumbPath = FileUtil.initPath();
        String jpegName = thumbPath + "/" + nameUuid +".jpg";
        Log.e(TAG, "saveBitmap:jpegName = " + jpegName);
        //获取截图
        FFMpegUtils.captureThumbnails(mPath,jpegName,"900*600");
        //ToastUtils.showToast(this.getApplicationContext(), "thumbPath:" + jpegName);

        //发送广播通知制作了视频
        Intent sendIntent=new Intent(NewProductActivity.UPDATE_NEW_VIEDO);
        sendIntent.putExtra("video_address", mPath);
        sendIntent.putExtra("thumb_address", jpegName);
        sendBroadcast(sendIntent);
        //退出全部选择视频应用
        super.AppExit(this);
    }


}