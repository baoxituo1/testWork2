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
import com.trade.bluehole.trad.entity.pro.ProductLabel;
import com.trade.bluehole.trad.entity.pro.ShopCoverType;

import java.util.HashMap;
import java.util.List;

/**
 * 商品标签选择适配器
 * Created by Administrator on 2015-03-26.
 */
public class ProductLabelAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private boolean isGrid;
    List<ProductLabel> labels;
    //已经选中的标签
    List<String> myCheckLabels;
    Context ctx;
    //记录checkbox的状态
    public HashMap<Integer, Boolean> state = new HashMap<Integer, Boolean>();


    public void setLabels(List<ProductLabel> covers, List<String> checkLabels) {
        this.labels = covers;
        myCheckLabels = checkLabels;
    }

    public ProductLabelAdapter(Context context, boolean isGrid) {
        ctx = context;
        layoutInflater = LayoutInflater.from(context);
        this.isGrid = isGrid;
    }

    @Override
    public int getCount() {
        return labels == null ? 0 : labels.size();
    }

    @Override
    public Object getItem(int position) {
        return null == labels ? null : labels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int p, View convertView, ViewGroup parent) {
        final int position=p;
        View view = convertView;
        ProductLabel cover = labels.get(position);
        if (isGrid) {
            view = layoutInflater.inflate(R.layout.i_pro_cover_grid_item, parent, false);
        } else {
            view = layoutInflater.inflate(R.layout.i_pro_cover_list_item, parent, false);
        }

        FlatCheckBox checkBox = (FlatCheckBox) view.findViewById(R.id.pro_checkbox_cover);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Toast.makeText(ctx, "checkBox change", Toast.LENGTH_LONG).show();
                String _code = labels.get(position).getLabelCode();
                if (isChecked) {
                    state.put(position, isChecked);
                    //判断选中的项是否存在以选择列表，不存在 加进去 ok？yes I Know...
                    if (!myCheckLabels.contains(_code)) {
                        myCheckLabels.add(_code);
                    }
                } else {
                    state.remove(position);
                    //相应的未选中 干掉它！
                    if (myCheckLabels.contains(_code)) {
                        myCheckLabels.remove(_code);
                    }
                }
            }
        });
        checkBox.setText(cover.getLabelName());
        //判断是否选中
        if (null != myCheckLabels && myCheckLabels.contains(cover.getLabelCode())) {
           checkBox.setChecked(true);
            //如果是选中的 添加到选中的变化map中
            state.put(position, true);
        } else {
            checkBox.setChecked(false);
            //如果未选中 看看map中是否包涵 包涵的话清除掉
            if (state.containsKey(position)) {
                state.remove(position);
            }
        }

        return view;
    }

    static class ViewHolder {
        TextView textView;
        FlatCheckBox checkBox;
    }
}

