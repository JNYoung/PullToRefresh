package com.jn.young.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.jn.young.pulltorefresh.header.DefaultHeader;
import com.jn.young.pulltorefresh.header.IPtrHeader;
import com.jn.young.pulltorefresh.lifecircle.PtrState;
import com.jn.young.pulltorefresh.utils.MultiDIstanceListener;
import com.jn.young.pulltorefresh.utils.PtrHandler;
import com.jn.young.pulltorefresh.utils.PtrIndicator;
import com.jn.young.pulltorefresh.utils.PtrObserver;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by zjy on 2017/7/15.
 */

public class PtrFrameNestedScroll extends ViewGroup {

    private final String LOG_TAG = "PTR_FRAME";
    PtrIndicator mIndicator;
    MultiDIstanceListener mMultiDIstanceListener;
    PtrObserver mObserver;
    PtrHandler mHandler;

    View mHeader;
    View mContent;
    private int mHeaderHeight;

    public PtrFrameNestedScroll(Context context) {
        super(context);
        _init(context, null);
    }

    public PtrFrameNestedScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
        _init(context, attrs);
    }

    public PtrFrameNestedScroll(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        _init(context, attrs);
    }

    private void _init(Context context, AttributeSet attributeSet) {
        mIndicator = new PtrIndicator();
        mHandler = new PtrHandler();

    }

    @Override
    protected void onFinishInflate() {

        int childrenCount = getChildCount();
        if (childrenCount >2) {
            throw new IllegalArgumentException("should have children less than 3");
        } else if (childrenCount == 2){
            View child1 = getChildAt(0);
            View child2 = getChildAt(1);
            if (child2 instanceof IPtrHeader) {
                mHeader = child2;
                mContent = child1;
            } else {
                //两种情况，一个是child1是一个IptrHeader实例
                //但是更多的可能性是都没有属性，所以默认第一个是头，第二个是内容
                mHeader = child1;
                mContent = child2;
            }
        } else if (childrenCount == 1) {
            //大部分情况下都可以使用这种情况，因为头部默认是写死的或者是可以自由改变的，所以只用写内容，头部自动生成就可以了
            mContent = getChildAt(0);
            mHeader = getDefaultHeader();
            addView(mHeader, 0);
        } else {

        }

        //进行类型检查
        if (!(mHeader instanceof IPtrHeader)) {
            throw new IllegalArgumentException("header should implements IPtrHeader");
        }
        super.onFinishInflate();
    }


    private View getDefaultHeader(){
        return new DefaultHeader(getContext());
    }

    public void setObserver(PtrObserver ptrObserver){
        mObserver = ptrObserver;
    }

    /**
     * 为了应对多重下拉做的东西，拉到不同长度有不同的反应
     * @param listener
     */
    public void setMultiDIstanceListener(MultiDIstanceListener listener) {
        mMultiDIstanceListener = listener;
    }

    public void setHeader(View header) {
        if (!(header instanceof IPtrHeader)) {
            throw new IllegalArgumentException("header should implements IPtrHeader");
        }
        mHeader = header;
        invalidate();
        requestLayout();
    }

    public View getHeader() {
        return mHeader;
    }

    /**
     * 取数据，不需要显示刷新状态
     * TODO: do think twice here
     * @param forceRefresh 是否需要强制刷新
     */
    public void refetchData(boolean forceRefresh) {
        if (forceRefresh) {
            Log.i(LOG_TAG, "forceRefresh may lead to some uncertain status");
            if (mObserver != null && mHandler != null) {
                if (mObserver.stopFetchOps()) {
                    mHandler.onRefetchData(mObserver);
                }
            }
        } else {
            if (mObserver != null && mHandler != null) {
                if (mHandler.getCurrentState() != PtrState.RESET){
                    return;
                }
                mHandler.onRefetchData(mObserver);
            }
        }
    }

    /**
     * 从外部调用刷新状态
     * @param needScroll
     * @param forceRefresh
     */
    public void setRefreshing(boolean needScroll, boolean forceRefresh){
        if (!needScroll) {
            refetchData(forceRefresh);
        } else {
            //1.判断是否允许强制刷新，如果允许的话，则判断当前的状态，如果为正在刷新状态，则重新取回数据，如果是正在滚动状态，需要停止滚动，并且滚动到刷新位置进行下一次刷新
            //2.observer的状态需要正确置位
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mHeader != null) {
            measureChild(mHeader, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams lp = (MarginLayoutParams) mHeader.getLayoutParams();
            mHeaderHeight = mHeader.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
        }

        if (mContent != null) {
            measureContent(mContent, widthMeasureSpec, heightMeasureSpec);
        }


    }

    private void measureContent(View content,
                                int parentWidthMeasureSpec,
                                int parentHeightMeasureSpec) {
        final MarginLayoutParams lp = (MarginLayoutParams) content.getLayoutParams();

        final int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin, lp.width);
        final int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec,
                getPaddingTop() + getPaddingBottom() + lp.topMargin, lp.height);

        content.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        layoutChildren();
    }

    private void layoutChildren() {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        if (mHeader != null) {
            MarginLayoutParams lp = (MarginLayoutParams) mHeader.getLayoutParams();
            final int left = paddingLeft + lp.leftMargin;
            final int top = paddingTop + lp.topMargin - mHeaderHeight;
            final int right = left + mHeader.getMeasuredWidth();
            final int bottom = top + mHeader.getMeasuredHeight();
            mHeader.layout(left, top, right, bottom);
        }
        if (mContent != null) {
            MarginLayoutParams lp = (MarginLayoutParams) mContent.getLayoutParams();
            final int left = paddingLeft + lp.leftMargin;
            final int top = paddingTop + lp.topMargin;
            final int right = left + mContent.getMeasuredWidth();
            final int bottom = top + mContent.getMeasuredHeight();
            mContent.layout(left, top, right, bottom);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!mHandler.canPull(mHeader, mContent, mObserver)) {
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    public static class LayoutParams extends MarginLayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        @SuppressWarnings({"unused"})
        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }
}
