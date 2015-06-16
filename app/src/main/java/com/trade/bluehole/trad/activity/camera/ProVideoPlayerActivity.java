package com.trade.bluehole.trad.activity.camera;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.trade.bluehole.trad.HeaderAnimatorActivity_;
import com.trade.bluehole.trad.NewProductActivity;
import com.trade.bluehole.trad.R;
import com.trade.bluehole.trad.SuperMainActivity_;
import com.trade.bluehole.trad.entity.pro.ProductResultVO;
import com.trade.bluehole.trad.record.ui.BaseActivity;
import com.trade.bluehole.trad.record.widget.SurfaceVideoView;
import com.trade.bluehole.trad.util.camera.FileUtil;
import com.trade.bluehole.trad.util.data.DataUrlContents;
import com.yixia.camera.demo.utils.ToastUtils;
import com.yixia.weibo.sdk.FFMpegUtils;
import com.yixia.weibo.sdk.util.DeviceUtils;
import com.yixia.weibo.sdk.util.StringUtils;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.apache.http.Header;

import java.util.UUID;

import cn.pedant.SweetAlert.SweetAlertDialog;
import mehdi.sakout.fancybuttons.FancyButton;

@EActivity
public class ProVideoPlayerActivity extends BaseActivity implements SurfaceVideoView.OnPlayStateListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, View.OnClickListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnInfoListener {
    private static final  String TAG = "VideoPlayerActivity";
    /** 播放控件 */
    private SurfaceVideoView mVideoView;
    /** 暂停按钮 */
    private View mPlayerStatus;
    private View mLoading;

    /** 播放路径 */
    private String mPath;
    private String proCode;
    /** 是否需要回复播放 */
    private boolean mNeedResume;
    public static Gson gson = new Gson();
    SweetAlertDialog pDialog;

    private static AsyncHttpClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 防止锁屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("交互中...");
        pDialog.setCancelable(false);

        mPath = getIntent().getStringExtra("path");
        proCode = getIntent().getStringExtra("proCode");
        if (StringUtils.isEmpty(mPath)||StringUtils.isEmpty(proCode)) {
            finish();
            return;
        }

        setContentView(R.layout.activity_pro_video_player);
        mVideoView = (SurfaceVideoView) findViewById(R.id.videoview);
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
        mVideoView.setVideoPath(mPath);

        if (client == null) {
            Log.i("BaseActionBarActivity", "实例化网络请求client");
            client = new AsyncHttpClient();
            client.setTimeout(30000);//30秒
        }
    }

    /**
     * 当点击后退
     */
    @Click(R.id.video_play_back)
    void onClickBack(){
        finish();
    }
    /**
     * 当点击删除
     */
    @Click(R.id.video_play_delete)
    void onClickDelete(){
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("确定要删除?")
                .setContentText("确定要删除该商品视频么？")
                .setConfirmText("是的,删除!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                       /* //点击后退跳转到 主页
                        if((proCode!=null&&!"".equals(proCode))||"items".equals(form)){
                            //HeaderAnimatorActivity_.intent(NewProductActivity.this).start();
                            // finish();
                            Intent intent= HeaderAnimatorActivity_.intent(NewProductActivity.this).get();
                            //intent.putExtra("product",response);//商品实体
                            intent.putExtra("position",position);//商品位置
                            intent.putExtra("delFlag",delFlag);//商品状态
                            setResult(RESULT_OK, intent);
                            finish();
                        }else{
                            SuperMainActivity_.intent(NewProductActivity.this).start();
                        }
                        mList.clear();
                        finish();*/
                        delServerData();
                    }
                })
                .setCancelText("否，取消")
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                    }
                })
                .show();
    }



    /**
     * 删除商品视频
     */
    void delServerData(){
        pDialog.show();
        RequestParams params=new RequestParams();
        params.put("productCode",proCode);
        Log.e("NewProductActivity", "productCode:" + proCode);
        client.post(DataUrlContents.SERVER_HOST + DataUrlContents.del_shop_video, params, new BaseJsonHttpResponseHandler<String>() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, String response) {
                //Log.d(NewProductActivity.class.getName(), response.toString());
                pDialog.hide();
                if(null!=response){
                    //弹出提示
                    new SweetAlertDialog(ProVideoPlayerActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("成功!")
                            .setContentText("删除商品视频成功")
                            .show();
                    //
                    Intent intent=new Intent(ProVideoPlayerActivity.this,NewProductActivity.class);
                    intent.putExtra("delVideo","ok");
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, String errorResponse) {
                Toast.makeText(ProVideoPlayerActivity.this, "请求失败:" + statusCode, Toast.LENGTH_SHORT).show();
                pDialog.hide();
            }

            @Override
            protected String parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return gson.fromJson(rawJsonData, String.class);
            }
        });
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
            ToastUtils.showToast(this.getApplicationContext(), "播放失败!");
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
        ToastUtils.showToast(this.getApplicationContext(), "mPath:"+mPath);
        String nameUuid= UUID.randomUUID().toString();
        /**获取视频截图**/
        //截图保存路径
        String thumbPath = FileUtil.initPath();
        String jpegName = thumbPath + "/" + nameUuid +".jpg";
        Log.e(TAG, "saveBitmap:jpegName = " + jpegName);
        //获取截图
        FFMpegUtils.captureThumbnails(mPath, jpegName, "900*600");
        ToastUtils.showToast(this.getApplicationContext(), "thumbPath:" + jpegName);

        //发送广播通知制作了视频
        Intent sendIntent=new Intent(NewProductActivity.UPDATE_NEW_VIEDO);
        sendIntent.putExtra("video_address", mPath);
        sendIntent.putExtra("thumb_address", jpegName);
        sendBroadcast(sendIntent);
        //退出全部选择视频应用
        super.AppExit(this);
    }



   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pro_video_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

}