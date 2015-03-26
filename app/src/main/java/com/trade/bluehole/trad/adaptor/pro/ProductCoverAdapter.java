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
    Context ctx;
    //记录checkbox的状态
    public HashMap<Integer, Boolean> state = new HashMap<Integer, Boolean>();


    public void setCovers(List<ShopCoverType> covers) {
        this.covers = covers;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View view = convertView;
        ShopCoverType cover=covers.get(position);
        if (view == null) {
            if (isGrid) {
                view = layoutInflater.inflate(R.layout.i_pro_cover_grid_item, parent, false);
            } else {
                view = layoutInflater.inflate(R.layout.i_pro_cover_list_item, parent, false);
            }
            viewHolder = new ViewHolder();

            viewHolder.checkBox = (FlatCheckBox) view.findViewById(R.id.pro_checkbox_cover);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
         viewHolder.checkBox.setText(cover.getCoverTypeName());
        //记录选中项变化值
        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               // Toast.makeText(ctx, "checkBox change", Toast.LENGTH_LONG).show();
                if (isChecked) {
                    state.put(position, isChecked);
                } else {
                    state.remove(position);
                }
            }
        });

       /* viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ctx, "checkBox clicked", Toast.LENGTH_LONG).show();
            }
        });*/

        return view;
    }

    static class ViewHolder {
        TextView textView;
        FlatCheckBox checkBox;
    }
}
