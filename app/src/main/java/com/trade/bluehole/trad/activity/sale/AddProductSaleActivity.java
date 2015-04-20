package com.trade.bluehole.trad.activity.sale;

import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.trade.bluehole.trad.DynamicManageActivity_;
import com.trade.bluehole.trad.LoginSystemActivity;
import com.trade.bluehole.trad.R;
import com.trade.bluehole.trad.activity.BaseActionBarActivity;
import com.trade.bluehole.trad.entity.sale.ProductSaleVO;
import com.trade.bluehole.trad.util.DateProcess;
import com.trade.bluehole.trad.util.ImageManager;
import com.trade.bluehole.trad.util.Result;
import com.trade.bluehole.trad.util.data.DataUrlContents;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.apache.http.Header;

import java.util.Calendar;

@EActivity(R.layout.activity_add_product_sale)
public class AddProductSaleActivity extends BaseActionBarActivity implements DatePickerDialog.OnDateSetListener {
    public static final String DATEPICKER_START_TAG = "start_date";
    public static final String DATEPICKER_END_TAG = "end_date";
    public static final String PRO_SALE_IMAGE = "proImage";
    public static final String PRO_SALE_NAME = "proName";
    public static final String PRO_SALE_PRICE = "proPrice";
    public static final String PRO_SALE_CODE = "proCode";
    final Calendar calendar = Calendar.getInstance();
    DatePickerDialog datePickerDialog=null;
    DatePickerDialog endDatePickerDialog=null;


    @ViewById
    ImageView sale_pro_image;
    @ViewById
    TextView sale_name,sale_old_price;
    @Extra(PRO_SALE_IMAGE)
    String proImage;
    @Extra(PRO_SALE_NAME)
    String proName;
    @Extra(PRO_SALE_PRICE)
    Double proPrice;
    @Extra(PRO_SALE_CODE)
    String proCode;

    @ViewById
    EditText sale_price,sale_start_date,sale_end_date;

    @AfterViews
    void initData() {

        if(null!=proImage){
            ImageLoader.getInstance().displayImage(DataUrlContents.IMAGE_HOST + proImage + DataUrlContents.img_list_head_img, sale_pro_image, ImageManager.options);
            sale_name.setText(proName);
            sale_old_price.setText("￥"+proPrice);
        }

         datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);
         endDatePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);

        //开始时间
        String date=calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH)+1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
        sale_start_date.setText(date);
        //结束时间
        String date2=calendar.get(Calendar.YEAR) + "-" +(calendar.get(Calendar.MONTH)+1) + "-" + (calendar.get(Calendar.DAY_OF_MONTH)+3);
        sale_end_date.setText(date2);
        DatePickerDialog dpd = (DatePickerDialog) getSupportFragmentManager().findFragmentByTag(DATEPICKER_START_TAG);
        if (dpd != null) {
            dpd.setOnDateSetListener(this);
        }
        DatePickerDialog dpd2 = (DatePickerDialog) getSupportFragmentManager().findFragmentByTag(DATEPICKER_END_TAG);
        if (dpd2 != null) {
            dpd2.setOnDateSetListener(this);
        }
    }

    /**
     * 点击选择开始日期
     */
    @Click(R.id.sale_layout_start)
    void onClickSelectStartDate(){
       // datePickerDialog.setVibrate(true);//是否震动
        datePickerDialog.setYearRange(2015, 2020);
        datePickerDialog.setCloseOnSingleTapDay(false);//触摸日期不关闭
        datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_START_TAG);
    }

    /**
     * 点击选择结束日期
     */
    @Click(R.id.sale_layout_end)
    void onClickSelectEndDate(){
       // datePickerDialog.setVibrate(true);//是否震动
        endDatePickerDialog.setYearRange(2015, 2020);
        endDatePickerDialog.setCloseOnSingleTapDay(false);//触摸日期不关闭
        endDatePickerDialog.show(getSupportFragmentManager(), DATEPICKER_END_TAG);
    }


    /**
     * 保存数据
     */
    private void saveData(Double price){
        RequestParams params=new RequestParams();
        params.put("productCode",proCode);
        params.put("salePrice",price);
        params.put("startDate",sale_start_date.getText().toString());
        params.put("endDate", sale_end_date.getText().toString());
        getClient().get(DataUrlContents.SERVER_HOST + DataUrlContents.update_product_sale_detail, params, new BaseJsonHttpResponseHandler<String>() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, String obj) {
                if (null != obj) {
                    Toast.makeText(AddProductSaleActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                    DynamicManageActivity_.intent(AddProductSaleActivity.this).start();
                    finish();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, String errorResponse) {
                Toast.makeText(AddProductSaleActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected String parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return gson.fromJson(rawJsonData, String.class);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_product_sale, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sale_menu_done) {
            String salePrice=sale_price.getText().toString();
            if(null==salePrice||"".equals(salePrice)){
                Toast.makeText(AddProductSaleActivity.this, "请填写折扣价格!", Toast.LENGTH_LONG).show();
            }else {
                Double price=Double.valueOf(salePrice);
                if(price>proPrice){
                    Toast.makeText(AddProductSaleActivity.this, "折扣价格不能大于原价!", Toast.LENGTH_LONG).show();
                }else{
                    String _startDate=sale_start_date.getText().toString();
                    String _endDate=sale_end_date.getText().toString();
                    if(!"".equals(_startDate)&&!"".equals(_endDate)){
                        long theday=DateProcess.timeDiffrenceDate(_startDate, _endDate);
                        if(theday<0){
                            Toast.makeText(AddProductSaleActivity.this, "结束时间不能早于开始时间", Toast.LENGTH_LONG).show();
                        }else{
                            //提交数据
                            saveData(price);
                        }
                    }
                }
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        //Toast.makeText(AddProductSaleActivity.this, "new date:" + year + "-" + (month+1) + "-" + day, Toast.LENGTH_LONG).show();
        if(DATEPICKER_START_TAG.equals(datePickerDialog.getTag())){//开始时间设置
            String date=year + "-" + + (month+1) + "-" + day;
            sale_start_date.setText(date);
        }else if(DATEPICKER_END_TAG.equals(datePickerDialog.getTag())){
            String date=year + "-" + + (month+1) + "-" + day;
            sale_end_date.setText(date);
        }
    }
}
