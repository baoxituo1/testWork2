package com.trade.bluehole.trad.adaptor.msg;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.trade.bluehole.trad.fragment.msg.NoticeFragment;
import com.trade.bluehole.trad.fragment.msg.NoticeFragment_;

/**
 * 消息页面适配器
 * Created by Administrator on 2015-04-19.
 */
public class MessagePageAdapter extends FragmentStatePagerAdapter {

    public MessagePageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment=new NoticeFragment_();
        Bundle args = new Bundle();
        args.putInt(NoticeFragment.ARG_OBJECT,position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "OBJECT " + (position + 1);
    }
}
