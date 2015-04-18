package com.trade.bluehole.trad.adaptor.cover;

import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.trade.bluehole.trad.CoverManageActivity;
import com.trade.bluehole.trad.HeaderAnimatorActivity;
import com.trade.bluehole.trad.R;
import com.trade.bluehole.trad.entity.pro.ProductCoverRelVO;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页商品分类适配器
 * Created by Administrator on 2015-04-17.
 */
public class CoverManageListAdapter extends BaseAdapter {
    CoverManageActivity ctx;
    List<ProductCoverRelVO> lists;
    List<LinearLayout> coverLayouts=new ArrayList<LinearLayout>();
    private final ColorGenerator mGenerator;


    public List<LinearLayout> getCoverLayouts() {
        return coverLayouts;
    }

    public void setCoverLayouts(List<LinearLayout> coverLayouts) {
        this.coverLayouts = coverLayouts;
    }

    LayoutInflater inflater;

    public CoverManageListAdapter(CoverManageActivity ctx){
        inflater= LayoutInflater.from(ctx);
        this.ctx=ctx;
        mGenerator = ColorGenerator.DEFAULT;
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
        ViewHolder holdObject;
        View view=convertView;
        if(view==null){
            view=inflater.inflate(R.layout.i_manage_covers_list_item,parent,false);
            holdObject=new ViewHolder(view);
            // holdObject.coverName=(TextView)view.findViewById(R.id.main_cover_name);
            //holdObject.coverNumber=(TextView)view.findViewById(R.id.main_cover_number);


            view.setTag(holdObject);
        }else{
            holdObject=(ViewHolder)view.getTag();
        }
        if(null==obj.getCoverName()||"".equals(obj.getCoverName())){
            holdObject.coverName.setText("未分类");
        }else{
            holdObject.coverName.setText(obj.getCoverName());
        }

        String title = obj.getCoverName();
        String header = "未";
        if (null != title && title.length() > 2) {
            header = title.substring(0, 1);
        }
        final TextDrawable drawable = TextDrawable.builder().buildRoundRect(header, mGenerator.getColor(header), toPx(10));
        holdObject.imageView.setImageDrawable(drawable);
        //判断是否是未分类
        if(null==title||"".equals(title)){
            holdObject.coverName.setText("未分类");
            //隐藏删除编辑图片
            holdObject.btn_cover_layout.setVisibility(View.GONE);
        }else{
            holdObject.coverName.setText(title);
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

    static class ViewHolder{
        ImageView imageView;
        TextView coverName;
        TextView coverNumber;
        LinearLayout editImage;
        LinearLayout delImage;
        LinearLayout btn_cover_layout;
        private ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.imageView);
            coverName = (TextView) view.findViewById(R.id.textView);
            coverNumber = (TextView) view.findViewById(R.id.pro_number);
            editImage=(LinearLayout)view.findViewById(R.id.main_cover_item_edit);
            delImage=(LinearLayout)view.findViewById(R.id.main_cover_item_del);
            btn_cover_layout=(LinearLayout)view.findViewById(R.id.btn_cover_layout);
        }
    }




    public List<ProductCoverRelVO> getLists() {
        return lists;
    }

    public void setLists(List<ProductCoverRelVO> lists) {
        this.lists = lists;
    }


    public int toPx(int dp) {
        Resources resources = ctx.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
    }
}