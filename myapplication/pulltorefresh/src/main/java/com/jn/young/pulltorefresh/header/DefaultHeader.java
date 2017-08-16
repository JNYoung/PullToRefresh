package com.jn.young.pulltorefresh.header;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.jn.young.pulltorefresh.R;

/**
 * Created by zjy on 2017/7/16.
 */

public class DefaultHeader extends View implements IPtrHeader {
    public DefaultHeader(Context context) {
        super(context);
    }

    public DefaultHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.height = 500;
        layoutParams.width = 1080;
        setBackgroundColor(getContext().getResources().getColor(android.R.color.holo_green_light));
    }

    @Override
    public boolean canPull() {
        return true;
    }

    @Override
    public int getMaxPullLenth() {
        return 500;
    }

    @Override
    public int getIdleExposeTime() {
        return 0;
    }

    @Override
    public void onReset() {

    }

    @Override
    public void onRefreshing() {

    }

    @Override
    public void onPullToRefresh() {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(1080, 500);

    }

    @Override
    public boolean onPull(float len) {
        if (len > 200) {
            return true;
        }
        return false;
    }

    @Override
    public int onRefreshComplete() {
        return 0;
    }
}
