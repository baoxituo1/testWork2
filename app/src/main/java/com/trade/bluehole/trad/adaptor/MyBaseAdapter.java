package com.trade.bluehole.trad.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * 基础适配器
 *
 */
public abstract class MyBaseAdapter<T> extends BaseAdapter
{
    protected List<T> mList;
    protected LayoutInflater mInflater;

    public MyBaseAdapter(Context context, List<T> list)
    {
        mList = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount()
    {
        return mList == null ? 0 : mList.size();
    }

    public void setmList(List<T> mList) {
        this.mList = mList;
    }

    @Override
    public Object getItem(int position)
    {
        if (mList == null)
        {
            return null;
        }

        if (position < 0 || position >= mList.size())
        {
            return null;
        }

        return mList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }
}
