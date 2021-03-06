package com.trade.bluehole.trad.activity.photo;

import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.trade.bluehole.trad.NewProductActivity;
import com.trade.bluehole.trad.R;
import com.trade.bluehole.trad.activity.BaseActionBarActivity;
import com.trade.bluehole.trad.adaptor.photo.ImageDynamicOrderAdapter;
import com.trade.bluehole.trad.entity.photo.Photo;
import com.trade.bluehole.trad.util.temp.Cheeses;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.askerov.dynamicgrid.DynamicGridView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * 修改图片展示顺序
 */
@EActivity(R.layout.activity_image_order_change)
public class ImageOrderChangeActivity extends BaseActionBarActivity {
    private static final String TAG = ImageOrderChangeActivity.class.getName();

   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_order_change);
    }*/

    @ViewById(R.id.order_dynamic_grid)
    public DynamicGridView gridView;//gridView 显示容器
    LinkedList<Photo> mTempList=new LinkedList<>();//临时排序列表
    Photo mTempPhoto;//临时类
    ImageDynamicOrderAdapter adapter;
    int startFlag;//移动开始位置
    int endFlag;//移动结束位置
    /**
     * 初始化数据
     */
    @AfterViews
    void  initData(){
        mTempList.addAll(NewProductActivity.mList);
        //移除最后一个选择按钮
        mTempPhoto=mTempList.get(mTempList.size()-1);//临时
        mTempList.remove(mTempList.size()-1);
        adapter=new ImageDynamicOrderAdapter(this, mTempList, getResources().getInteger(R.integer.column_count));
        //设置适配器
        gridView.setAdapter(adapter);
        //开启排序模式
        //gridView.startEditMode(0);
         gridView.startEditMode();
        //设置监听
        gridView.setOnDragListener(new DynamicGridView.OnDragListener() {
            @Override
            public void onDragStarted(int position) {
                Log.d(TAG, "drag started at position " + position);
                startFlag=position;
            }

            @Override
            public void onDragPositionsChanged(int oldPosition, int newPosition) {
                Log.d(TAG, String.format("drag item position changed from %d to %d", oldPosition, newPosition));
                sortAgainData(startFlag,oldPosition,newPosition);
            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                gridView.startEditMode(position);
                return true;
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    gridView.startEditMode(position);
               // Toast.makeText(ImageOrderChangeActivity.this, parent.getAdapter().getItem(position).toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 重新排列集合
     */
    void sortAgainData(int startPosition,int oldPosition, int newPosition){//6,1
       /* Photo tem_p=mTempList.get(oldPosition);
        mTempList.set(oldPosition,mTempList.get(newPosition));
        mTempList.set(newPosition,tem_p);*/
        Photo tem_p=mTempList.get(oldPosition);
        mTempList.remove(oldPosition);
        mTempList.add(newPosition, tem_p);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_order_change, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.photo_order_done) {
            if (gridView.isEditMode()) {
                gridView.stopEditMode();
            }
            NewProductActivity.mList.clear();
            for(Photo p:mTempList){
                NewProductActivity.mList.add(p);
            }
            NewProductActivity.mList.add(mTempPhoto);
            setResult(RESULT_OK, null);
            //mTempList.clear();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (gridView.isEditMode()) {
            gridView.stopEditMode();
        } else {
            super.onBackPressed();
        }
    }
}
