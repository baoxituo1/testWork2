package com.trade.bluehole.trad.adaptor.pro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.cengalabs.flatui.views.FlatCheckBox;
import com.trade.bluehole.trad.R;
import com.trade.bluehole.trad.entity.pro.ShopCoverType;

import java.util.HashMap;
import java.util.List;

/**
 * 商品类别选择适配器
 *
 */
public class ProductCoverAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private boolean isGrid;
    List<ShopCoverType> covers;
    //已经选中的标签
    List<String>myCheckCovers;
    Context ctx;
    //记录checkbox的状态
    public HashMap<Integer, Boolean> state2 = new HashMap<Integer, Boolean>();


    public void setCovers(List<ShopCoverType> covers,List<String> checkCovers) {
        this.covers = covers;
        myCheckCovers=checkCovers;
    }

    public ProductCoverAdapter(Context context, boolean isGrid) {
        ctx=context;
        layoutInflater = LayoutInflater.from(context);
        this.isGrid = isGrid;
    }

    @Override
    public int getCount() {
        return covers==null?0:covers.size();
    }

    @Override
    public Object getItem(int position) {
        return null==covers?null:covers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int p, View convertView, ViewGroup parent) {
        final int position=p;
       // System.out.print("adapter:" + position + ",coverName:" + covers.get(position).getCoverTypeName());
       // System.out.println(state2);
       // ViewHolder viewHolder;
        //View view = convertView;
        ShopCoverType cover=covers.get(position);
        /*if (view == null) {
            if (isGrid) {
                view = layoutInflater.inflate(R.layout.i_pro_cover_grid_item, parent, false);
            } else {*/
        View    view = layoutInflater.inflate(R.layout.i_pro_cover_list_item, parent, false);
           // }
          //  viewHolder = new ViewHolder();
        FlatCheckBox checkBox = (FlatCheckBox) view.findViewById(R.id.pro_checkbox_cover);
            //记录选中项变化值
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // Toast.makeText(ctx, "checkBox change", Toast.LENGTH_LONG).show();
                    String _code=covers.get(position).getCoverTypeCode();
                    if (isChecked) {
                        state2.put(position, isChecked);
                        //判断选中的项是否存在以选择列表，不存在 加进去 ok？yes I Know...
                        if(!myCheckCovers.contains(_code)){
                            myCheckCovers.add(_code);
                        }
                    } else {
                        state2.remove(position);
                        //相应的未选中 干掉它！
                        if(myCheckCovers.contains(_code)){
                            myCheckCovers.remove(_code);
                        }
                    }
                }
            });
          //  view.setTag(viewHolder);
       /* } else {
            viewHolder = (ViewHolder) view.getTag();
        }*/
        checkBox.setText(cover.getCoverTypeName());
         //判断是否选中
        //String _code=covers.get(position).getCoverTypeCode();
       // System.out.println("_code:"+_code);
        if(null!=myCheckCovers&&myCheckCovers.contains(cover.getCoverTypeCode())){
            checkBox.setChecked(true);
            //如果是选中的 添加到选中的变化map中
            state2.put(position, true);
        }else{
            checkBox.setChecked(false);
            //如果未选中 看看map中是否包涵 包涵的话清除掉
            if(state2.containsKey(position)){
                state2.remove(position);
            }

        }
        //清除掉缓存造成的初始值
       // state2.clear();


        return view;
    }

    static class ViewHolder {
        TextView textView;
        FlatCheckBox checkBox;
    }
}
