package com.trade.bluehole.trad.adaptor.pro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.trade.bluehole.trad.R;
import com.trade.bluehole.trad.entity.pro.ProductCoverRelVO;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by Administrator on 2015-04-02.
 */
public class ProductCoverNumberAdapter extends BaseAdapter {
    public List<ProductCoverRelVO> getLists() {
        return lists;
    }

    public void setLists(List<ProductCoverRelVO> lists) {
        this.lists = lists;
    }

    List<ProductCoverRelVO> lists;
    LayoutInflater inflater;

    public ProductCoverNumberAdapter(Context ctx){
        inflater= LayoutInflater.from(ctx);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ProductCoverRelVO obj=lists.get(position);
        HoldObject holdObject;
        View view=convertView;
        if(view==null){
            view=inflater.inflate(R.layout.i_pro_covers_list_item,parent,false);
            holdObject=new HoldObject();
            holdObject.coverName=(TextView)view.findViewById(R.id.main_cover_name);
            holdObject.coverNumber=(TextView)view.findViewById(R.id.main_cover_number);
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

        return view;
    }

    static class HoldObject{
        TextView coverName;
        TextView coverNumber;
    }
}
