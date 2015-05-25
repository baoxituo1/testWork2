package com.trade.bluehole.trad.util.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 用于嵌套可滚动控件内部的GridView,根据子视图高度自动修正自身高度
 */
public class InnerGridView extends GridView {
	public InnerGridView(Context context) {
		super(context);
	}

	public InnerGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public InnerGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
