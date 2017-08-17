package com.jn.young.pulltorefresh.header;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.jn.young.pulltorefresh.PtrFrame;
import com.jn.young.pulltorefresh.R;
import com.jn.young.pulltorefresh.utils.PtrHandler;

/**
 * Created by zjy on 2017/7/16.
 */

public class DefaultHeader extends View implements IPtrHeader {
    public DefaultHeader(Context context) {
        super(context);
        init();
    }

    public DefaultHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public DefaultHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

    }

    private void init(){

    }

    @Override
    public boolean canPull() {
        return true;
    }

    @Override
    public int getMaxPullLenth() {
        return 900;
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
        super.onMeasure(1080, 900);
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        setBackgroundColor(getContext().getResources().getColor(android.R.color.holo_green_light));

    }

    @Override
    public PtrHandler.PullResult onPull(float len, float resilience) {
        PtrHandler.PullResult result = new PtrHandler.PullResult();
        if (len > 200 && len < 500) {
            result.atPullToRequestRealm = true;
            result.newResilience = resilience;
        }
        if (len > 500) {
            result.atPullToRequestRealm = true;
            result.newResilience = 0.5f;
        }
        return result;
    }

    @Override
    public int onRefreshComplete(PtrFrame frame) {
        frame.doScrollBack();
        return 0;
    }
}
