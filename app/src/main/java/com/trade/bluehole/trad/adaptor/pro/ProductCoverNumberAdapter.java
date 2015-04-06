package com.trade.bluehole.trad.adaptor.pro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.trade.bluehole.trad.HeaderAnimatorActivity;
import com.trade.bluehole.trad.R;
import com.trade.bluehole.trad.activity.shop.ProductClassifyActivity;
import com.trade.bluehole.trad.entity.pro.ProductCoverRelVO;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by Administrator on 2015-04-02.
 */
public class ProductCoverNumberAdapter extends BaseAdapter {
    HeaderAnimatorActivity ctx;
    List<ProductCoverRelVO> lists;
    List<LinearLayout> coverLayouts=new ArrayList<LinearLayout>();

    public List<LinearLayout> getCoverLayouts() {
        return coverLayouts;
    }

    public void setCoverLayouts(List<LinearLayout> coverLayouts) {
        this.coverLayouts = coverLayouts;
    }

    LayoutInflater inflater;

    public ProductCoverNumberAdapter(HeaderAnimatorActivity ctx){
        inflater= LayoutInflater.from(ctx);
        this.ctx=ctx;
    }

    @Override
    public int getCount() {
        return lists==null?0:lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists==null?null:lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ProductCoverRelVO obj=lists.get(position);
        HoldObject holdObject;
        View view=convertView;
        if(view==null){
            view=inflater.inflate(R.layout.i_pro_covers_list_item,parent,false);
            holdObject=new HoldObject();
            holdObject.coverName=(TextView)view.findViewById(R.id.main_cover_name);
            holdObject.coverNumber=(TextView)view.findViewById(R.id.main_cover_number);
            holdObject.coverLayout=(LinearLayout)view.findViewById(R.id.btn_cover_layout);
            holdObject.editImage=(LinearLayout)view.findViewById(R.id.main_cover_item_edit);
            holdObject.delImage=(LinearLayout)view.findViewById(R.id.main_cover_item_del);
            //留着控制隐藏显示
            LinearLayout temp_layout=(LinearLayout)view.findViewById(R.id.btn_cover_layout);
            coverLayouts.add(temp_layout);

            view.setTag(holdObject);
        }else{
            holdObject=(HoldObject)view.getTag();
        }
        if(null==obj.getCoverName()||"".equals(obj.getCoverName())){
            holdObject.coverName.setText("未分类");
        }else{
            holdObject.coverName.setText(obj.getCoverName());
        }
        holdObject.coverNumber.setText("共"+obj.getProNumber()+"件商品");
        //添加点击事件

        holdObject.editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 //Toast.makeText(ctx, "editImage is click"+position, Toast.LENGTH_SHORT).show();
                 ctx.showCoverEditClick(obj.getCoverName(),obj.getCoverCode(),position);
            }
        });
        holdObject.delImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 //Toast.makeText(ctx, "deleditImage is click:"+position, Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }

    static class HoldObject{
        TextView coverName;
        TextView coverNumber;
        LinearLayout coverLayout;
        LinearLayout editImage;
        LinearLayout delImage;
    }


    public void showCoverEdit(){
        for(LinearLayout ls:coverLayouts){
            ls.setVisibility(View.VISIBLE);
        }
    }
    public void hideCoverEdit(){
        for(LinearLayout ls:coverLayouts){
            ls.setVisibility(View.GONE);
        }
    }



    public List<ProductCoverRelVO> getLists() {
        return lists;
    }

    public void setLists(List<ProductCoverRelVO> lists) {
        this.lists = lists;
    }
}
