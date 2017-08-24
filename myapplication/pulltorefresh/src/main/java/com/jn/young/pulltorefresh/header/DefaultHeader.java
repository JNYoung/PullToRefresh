package com.jn.young.pulltorefresh.header;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jn.young.pulltorefresh.PtrFrame;
import com.jn.young.pulltorefresh.R;
import com.jn.young.pulltorefresh.utils.PtrHandler;

/**
 * Created by zjy on 2017/7/16.
 */

public class DefaultHeader extends android.support.v7.widget.AppCompatTextView implements IPtrHeader {
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
        setBackgroundColor(getContext().getResources().getColor(android.R.color.holo_green_light));

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
        if (len < 200) {
            result.atPullToRequestRealm = false;
            result.newResilience = resilience;
        }
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
    public int getRefreshingPos() {
        return 500;
    }

    @Override
    public int onRefreshComplete(final PtrFrame frame, String tip) {
        setText("刷新成功");
        setBackgroundColor(getContext().getResources().getColor(android.R.color.holo_blue_bright));
        postDelayed(new Runnable() {
            @Override
            public void run() {
                frame.doScrollBack();

            }
        }, 500);
        return 0;
    }
}
