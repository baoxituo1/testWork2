package com.trade.bluehole.trad.util.view;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.dialogplus.Holder;
import com.orhanobut.dialogplus.ViewHolder;

/**
 * 自定义弹出窗口实现类
 * Created by Administrator on 2015-04-06.
 */
public class MyViewHold implements Holder {
    private static final String TAG = ViewHolder.class.getSimpleName();
    private static final int INVALID = -1;
    private int backgroundColor;
    private ViewGroup headerContainer;
    private ViewGroup footerContainer;
    private View.OnKeyListener keyListener;
    public View contentView;
    private int viewResourceId = -1;

    public MyViewHold(int viewResourceId) {
        this.viewResourceId = viewResourceId;
    }

    public MyViewHold(View contentView) {
        this.contentView = contentView;
    }

    public void addHeader(View view) {
        if(view != null) {
            this.headerContainer.addView(view);
        }
    }

    public void addFooter(View view) {
        if(view != null) {
            this.footerContainer.addView(view);
        }
    }

    public void setBackgroundColor(int colorResource) {
        this.backgroundColor = colorResource;
    }

    public View getView(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(com.orhanobut.dialogplus.R.layout.dialog_view, parent, false);
        ViewGroup contentContainer = (ViewGroup)view.findViewById(com.orhanobut.dialogplus.R.id.view_container);
        contentContainer.setBackgroundColor(parent.getResources().getColor(this.backgroundColor));
        contentContainer.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(MyViewHold.this.keyListener == null) {
                    throw new NullPointerException("keyListener should not be null");
                } else {
                    return MyViewHold.this.keyListener.onKey(v, keyCode, event);
                }
            }
        });
        this.addContent(inflater, parent, contentContainer);
        this.headerContainer = (ViewGroup)view.findViewById(com.orhanobut.dialogplus.R.id.header_container);
        this.footerContainer = (ViewGroup)view.findViewById(com.orhanobut.dialogplus.R.id.footer_container);
        return view;
    }

    private void addContent(LayoutInflater inflater, ViewGroup parent, ViewGroup container) {
        if(this.viewResourceId != -1) {
            contentView = inflater.inflate(this.viewResourceId, parent, false);
            container.addView(contentView);
        } else {
            container.addView(this.contentView);
        }
    }

    public void setOnKeyListener(View.OnKeyListener keyListener) {
        this.keyListener = keyListener;
    }
}