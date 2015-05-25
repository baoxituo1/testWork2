package com.trade.bluehole.trad.util.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 用于嵌套可滚动控件内部的ListView,根据子视图高度自动修正自身高度
 */
public class InnerListView extends ListView {
	public InnerListView(Context context) {
		super(context);
	}

	public InnerListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public InnerListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
