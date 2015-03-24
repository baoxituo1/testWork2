package com.trade.bluehole.trad;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.mbaas.oss.OSSClient;
import com.aliyun.mbaas.oss.callback.SaveCallback;
import com.aliyun.mbaas.oss.model.AccessControlList;
import com.aliyun.mbaas.oss.model.OSSException;
import com.aliyun.mbaas.oss.model.TokenGenerator;
import com.aliyun.mbaas.oss.storage.OSSFile;
import com.aliyun.mbaas.oss.util.OSSLog;
import com.aliyun.mbaas.oss.util.OSSToolKit;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.soundcloud.android.crop.Crop;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.apache.http.Header;

import com.aliyun.mbaas.oss.storage.OSSBucket;
import com.trade.bluehole.trad.activity.photo.ImageDirActivity;
import com.trade.bluehole.trad.activity.photo.PreviewActivity;
import com.trade.bluehole.trad.adaptor.photo.MainAdapter;
import com.trade.bluehole.trad.entity.User;
import com.trade.bluehole.trad.entity.photo.Photo;
import com.trade.bluehole.trad.util.MyApplication;
import com.trade.bluehole.trad.util.Result;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

@EActivity(R.layout.activity_product_add)
public class NewProductActivity extends ActionBarActivity {
    @App
    MyApplication myapplication;
    User user=null;
    AsyncHttpClient client = new AsyncHttpClient();
    static final String accessKey = "ictZeAtTIlkEXGta"; // 测试代码没有考虑AK/SK的安全性
    static final String screctKey = "8CQkQa7IytCb73hvk12EUazS0hUPw2";
    public OSSBucket sampleBucket;
    private ArrayList<Photo> mList;
    private MainAdapter mAdapter;
    String imageUrls="";
    static {
        OSSClient.setGlobalDefaultTokenGenerator(new TokenGenerator() { // 设置全局默认加签器
            @Override
            public String generateToken(String httpMethod, String md5, String type, String date,
                                        String ossHeaders, String resource) {

                String content = httpMethod + "\n" + md5 + "\n" + type + "\n" + date + "\n" + ossHeaders
                        + resource;

                return OSSToolKit.generateToken(accessKey, screctKey, content);
            }
        });
        OSSClient.setGlobalDefaultACL(AccessControlList.PUBLIC_READ_WRITE); // 设置全局默认bucket访问权限
        OSSClient.setGlobalDefaultHostId("oss-cn-beijing.aliyuncs.com"); // 指明你的bucket是放在北京数据中心
    }




    @ViewById(R.id.result_image)
    ImageView resultView;
    @ViewById(R.id.tv)
    TextView mTextView;
    @ViewById(R.id.gridview)
    GridView gridView;
    @ViewById
    TextView product_name;
    @ViewById
    TextView product_price;
    @ViewById
    TextView product_number;

    @AfterViews
    void initData(){
        user=myapplication.getUser();
        OSSLog.enableLog(true);
        OSSClient.setApplicationContext(getApplicationContext()); // 传入应用程序context
        // 开始单个Bucket的设置
        sampleBucket = new OSSBucket("125");
        sampleBucket.setBucketHostId("oss-cn-beijing.aliyuncs.com"); // 可以在这里设置数据中心域名或者cname域名
        sampleBucket.setBucketACL(AccessControlList.PUBLIC_READ_WRITE);


        mList = new ArrayList<Photo>();
        Photo p=new Photo();
        mList.add(p);
        mAdapter = new MainAdapter(this, mList);
        gridView.setAdapter(mAdapter);
    }
   /* @Click(R.id.addProductImage)
    void addProImageClick(){
        resultView.setImageDrawable(null);
        Crop.pickImage(this);
    }*/

    /**
     * 点击选择多张图片
     */
   // @Click(R.id.addMoreProductImage)
    public void addMoreProImageClick(){
        resultView.setImageDrawable(null);
        Intent intent = new Intent(getApplicationContext(), ImageDirActivity.class);
        if(mList.size()>0){
            mList.remove(mList.size()-1);
        }

        intent.putExtra(MyApplication.ARG_PHOTO_LIST, mList);
        startActivityForResult(intent, 13);
    }

    /**
     * 上传图片
     */
    @Click(R.id.uploadProductImage)
    void uploadProImageClick(){
        if(!mList.isEmpty()&&null!=user){
            imageUrls="";
            for(Photo ls:mList){
                try {
                    String fileName= "pro/"+"image_"+UUID.randomUUID()+".jpg";
                    doUploadFile(ls.imgPath,fileName);
                    imageUrls+=fileName+",";
                    //Log.e("NewProductActivity", "oss-file-name:"+ls.fileName);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            uploadProImg();
        }else{
            Toast.makeText(this,"上传列表为空或者未登陆",Toast.LENGTH_SHORT).show();
        }
    }

    public void doUploadFile(String formUrl,String fileName) throws Exception {

        OSSFile ossFile = new OSSFile(sampleBucket, fileName);
        ossFile.setUploadFilePath(formUrl, "image/jpg");
        ossFile.uploadInBackground(new SaveCallback() {

            @Override
            public void onProgress(String objectKey, int byteCount, int totalSize) {
                Log.e("NewProductActivity","objectKey:"+objectKey+",byteCount:"+byteCount+",totalSize:"+totalSize);
            }

            @Override
            public void onFailure(String arg0, OSSException arg1) {
                Log.e("NewProductActivity",arg1.toString());
            }
            @Override
            public void onSuccess(String arg0) {
                Log.e("NewProductActivity", "上传成功");
            }
        });
    }


    void uploadProImg(){
        RequestParams params=new RequestParams();
        params.put("userCode",user.getUserCode());
        params.put("productName",product_name.getText());
        params.put("productPrice",product_price.getText());
        params.put("productNumber",product_number.getText());
        params.put("imageUrls",imageUrls);
        Log.e("NewProductActivity", "imageUrls:"+imageUrls);
        client.get("http://192.168.1.161:8080/qqt_up/shopjson/saveProductJson.do", params, new BaseJsonHttpResponseHandler<String>() {


            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, String response) {
                Log.d(LoginSystemActivity.class.getName(), statusCode + "");
                if (null != response) {
                        Toast.makeText(NewProductActivity.this, response, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, String errorResponse) {
            }

            @Override
            protected String parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return rawJsonData;
            }
        });
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(result.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, result);
        }else if(requestCode==13&&resultCode == RESULT_OK){
            if (result != null)
            {
                ArrayList<Photo> list = result.getParcelableArrayListExtra(MyApplication.RES_PHOTO_LIST);

                mList.clear();
                if (list != null)
                {
                    mList.addAll(list);
                }
                if(mList.size()<10){
                    Photo p=new Photo();
                    p.id="99911111";
                    mList.add(p);
                }
                mAdapter.notifyDataSetChanged();
                mTextView.setText(getString(R.string.check_length, mAdapter.getCount()));
                if(mList.size()>5){
                    LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) gridView.getLayoutParams(); // 取控件mGrid当前的布局参数
                    linearParams.height=gridView.getHeight()*2+10;
                    gridView.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件mGrid2
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_add_pro, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.save_product)
        {
            uploadProImageClick();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void beginCrop(Uri source) {
        Uri outputUri = Uri.fromFile(new File(getCacheDir(), "cropped"));
        new Crop(source).output(outputUri).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            resultView.setImageURI(Crop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
