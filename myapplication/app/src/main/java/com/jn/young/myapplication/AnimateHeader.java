package com.jn.young.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.jn.young.pulltorefresh.PtrFrame;
import com.jn.young.pulltorefresh.header.IPtrHeader;
import com.jn.young.pulltorefresh.utils.PtrHandler;

/**
 * Created by zhengjinyang on 2017/8/22.
 */

@SuppressLint("AppCompatCustomView")
public class AnimateHeader extends TextView implements IPtrHeader {
    private boolean isShowingTip = false;

    private YdAnimate ydAnimate;
    public AnimateHeader(Context context, int color) {
        this(context, color,  null);
    }

    public AnimateHeader(Context context, int color, AttributeSet attrs) {
        this(context, color, attrs, 0);
    }

    public AnimateHeader(Context context, int color, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, color);

    }
    private void init(Context context, int color) {
        ydAnimate = new YdAnimate(context, this, color);
//        setTextColor(color);
        setGravity(Gravity.CENTER);
        setDesiredSize();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (ydAnimate != null) {
            ydAnimate.draw(canvas);
        }
    }

    //设置高宽
    private void setDesiredSize() {
        WindowManager wmg = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wmg.getDefaultDisplay().getMetrics(dm);
        setWidth(dm.widthPixels);
        setHeight(getPxForDifferentScreen(68));
    }


    public int getPxForDifferentScreen(int designSize) {
        WindowManager wmg = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wmg.getDefaultDisplay().getMetrics(dm);
        return (int) (designSize / 2 * dm.density);
    }

    @Override
    public boolean canPull() {
        return true;
    }

    @Override
    public int getMaxPullLenth() {
        return 300;
    }

    @Override
    public int getIdleExposeTime() {
        return 0;
    }

    @Override
    public int onRefreshComplete(PtrFrame frame, String refreshTip) {
        if (isShowingTip) {
            isShowingTip = false;
        }
        isShowingTip = true;
        if (TextUtils.isEmpty(refreshTip)){
            refreshTip = "暂无更新，休息一会儿";
        }
        if (ydAnimate != null) {
            ydAnimate.setVisible(false, false);
            ydAnimate.setReset();
            ydAnimate.invalidateSelf();
            invalidate();
        }
        setText(refreshTip);
        frame.doScrollBackAfter(1500);
        return 0;
    }

    @Override
    public PtrHandler.PullResult onPull(float len, float resilience) {

        if (ydAnimate != null) {
            ydAnimate.setLength(len);
            setText("");
        }
        PtrHandler.PullResult result = new PtrHandler.PullResult(resilience);
        return result;
    }

    @Override
    public int getRefreshingPos() {
        return 200;
    }

    @Override
    public void onReset() {
        ydAnimate.setReset();
        setText("");
    }

    public void setPaintColor(int color) {
        ydAnimate.setColor(color);
        setTextColor(color);
    }

    @Override
    public void onRefreshing() {
        ydAnimate.setRefreshing();
        setText("");
    }

    @Override
    public void onPullToRefresh() {

    }
}
