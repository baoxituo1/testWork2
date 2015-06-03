package com.trade.bluehole.trad.activity.camera;



import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.trade.bluehole.trad.R;
import com.trade.bluehole.trad.activity.shop.ShopAuthenticActivity;
import com.trade.bluehole.trad.util.camera.CameraInterface;
import com.trade.bluehole.trad.util.camera.DisplayUtil;
import com.trade.bluehole.trad.util.camera.preview.CameraTextureView;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.ByteArrayOutputStream;

@EActivity
public class CameraActivity extends Activity implements CameraInterface.CamOpenOverCallback {
    private static final String TAG = "yanzi";
    CameraTextureView textureView = null;
    ImageButton shutterBtn;
    ImageView show_result_image;
    Bitmap bitmap;

    float previewRate = -1f;
    @ViewById
    RelativeLayout camera_show_result_layout;
    @ViewById
    FrameLayout camera_texture_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread openThread = new Thread(){
            @Override
            public void run() {
                // TODO Auto-generated method stub
                CameraInterface.getInstance().doOpenCamera(CameraActivity.this);
            }
        };
        openThread.start();
        setContentView(R.layout.activity_camera);
        initUI();
        initViewParams();
        textureView.setAlpha(1.0f);

        shutterBtn.setOnClickListener(new BtnListeners());
    }


    private void initUI(){
        textureView = (CameraTextureView)findViewById(R.id.camera_textureview);
        shutterBtn = (ImageButton)findViewById(R.id.btn_shutter);
        show_result_image = (ImageView)findViewById(R.id.show_result_image);
    }
    private void initViewParams(){
        LayoutParams params = textureView.getLayoutParams();
        Point p = DisplayUtil.getScreenMetrics(this);
        params.width = p.x;
        params.height = p.y;
        previewRate = DisplayUtil.getScreenRate(this); //默认全屏的比例预览
        textureView.setLayoutParams(params);

        //手动设置拍照ImageButton的大小为120dip×120dip,原图片大小是64×64
        LayoutParams p2 = shutterBtn.getLayoutParams();
        p2.width = DisplayUtil.dip2px(this, 80);
        p2.height = DisplayUtil.dip2px(this, 80);
        shutterBtn.setLayoutParams(p2);

    }

    @Override
    public void cameraHasOpened() {
        // TODO Auto-generated method stub
        SurfaceTexture surface = textureView._getSurfaceTexture();
        CameraInterface.getInstance().doStartPreview(surface, previewRate);
    }
    private class BtnListeners implements OnClickListener{

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch(v.getId()){
                case R.id.btn_shutter:
                    CameraInterface.getInstance().doTakePicture(CameraActivity.this);
                    break;
                default:break;
            }
        }

    }

    /**
     * 点击提交图片
     */
    @Click(R.id.camera_image_to_submit)
    void onSubmitImageClick(){
        Intent intent =new Intent(CameraActivity.this,ShopAuthenticActivity.class);
        bitmap=smallBimap(bitmap);
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte [] bitmapByte =baos.toByteArray();
        intent.putExtra("bitmap", bitmapByte);
        this.setResult(RESULT_OK,intent);
        this.finish();
    }


    /**
     * 取消当前图片 重新拍摄
     */
    @Click(R.id.camera_image_to_cancel)
    void onCancelImageClick(){
        //隐藏结果界面
        camera_show_result_layout.setVisibility(View.GONE);
        show_result_image.setImageBitmap(null);

        //开启摄像界面
        camera_texture_layout.setVisibility(View.VISIBLE);
        shutterBtn.setVisibility(View.VISIBLE);

        CameraInterface.getInstance().reStartCamera();
    }

    /**
     * 显示拍照结果
     * @param b
     */
    public void showResultImage(Bitmap b){
        bitmap=b;
        //隐藏摄像界面
        camera_texture_layout.setVisibility(View.GONE);
        shutterBtn.setVisibility(View.GONE);
        //展示结果界面
        camera_show_result_layout.setVisibility(View.VISIBLE);
        show_result_image.setImageBitmap(smallBimap(b));
    }


    private static Bitmap smallBimap(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(0.7f,0.7f); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        return resizeBmp;
    }
}
