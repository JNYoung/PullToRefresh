package com.jn.young.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import com.jn.young.pulltorefresh.header.DefaultHeader;
import com.jn.young.pulltorefresh.header.IPtrHeader;
import com.jn.young.pulltorefresh.lifecircle.PtrState;
import com.jn.young.pulltorefresh.utils.PtrHandler;
import com.jn.young.pulltorefresh.utils.PtrIndicator;
import com.jn.young.pulltorefresh.utils.PtrLog;
import com.jn.young.pulltorefresh.utils.PtrObserver;

/**
 * Created by zjy on 2017/7/15.
 */

public class PtrFrame extends ViewGroup {

    private final String LOG_TAG = "PTR_FRAME";
    PtrIndicator mIndicator;
    PtrObserver mObserver;
    PtrHandler mHandler;

    private boolean mIsBeingDragged;

    private Runnable mIdleExposeRunnable;

    public float mResilience = 1f;
    public boolean mPinnedContent = false;
    View mHeader;
    View mContent;
    private int mHeaderHeight;
    private int mTouchSlop;
    private ScrollHelper mScroller;

    public PtrFrame(Context context) {
        super(context);
        _init(context, null);
    }

    public PtrFrame(Context context, AttributeSet attrs) {
        super(context, attrs);
        _init(context, attrs);
    }

    public PtrFrame(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        _init(context, attrs);
    }

    private void _init(Context context, AttributeSet attributeSet) {
        mIndicator = new PtrIndicator();
        mHandler = new PtrHandler();
        ViewConfiguration config = ViewConfiguration.get(context);
        mTouchSlop = config.getScaledTouchSlop();
        mScroller = new ScrollHelper(new DecelerateInterpolator() );

    }

    public void setScrollInterpolator(Interpolator interpolator){
        mScroller.setInterpolator(interpolator);
    }

    @Override
    protected void onFinishInflate() {

        int childrenCount = getChildCount();
        if (childrenCount > 2) {
            throw new IllegalArgumentException("should have children less than 3");
        } else if (childrenCount == 2) {
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

    @Override
    protected void onDetachedFromWindow() {
        removeCallbacks(mIdleExposeRunnable);
        mScroller.destroy();
        super.onDetachedFromWindow();
    }

    private View getDefaultHeader() {
        DefaultHeader header = new DefaultHeader(getContext());
        header.setMinimumHeight(900);
        header.setMinimumWidth(1920);
        return header;

    }

    public void setObserver(PtrObserver ptrObserver) {
        mObserver = ptrObserver;
    }

    public View getHeader() {
        return mHeader;
    }

    public void setHeader(View header) {
        if (!(header instanceof IPtrHeader)) {
            throw new IllegalArgumentException("header should implements IPtrHeader");
        }
        mHeader = header;
        invalidate();
        requestLayout();
    }

    /**
     * 取数据，不需要显示刷新状态
     * TODO: do think twice here
     *
     * @param forceRefresh 是否需要强制刷新
     */
    public void refetchData(boolean forceRefresh) {
        if (forceRefresh) {
            if (mObserver != null && mHandler != null) {
                if (mObserver.stopFetchOps()) {
                    mHandler.onRefetchData(mObserver);
                }
            }
        } else {
            if (mObserver != null && mHandler != null) {
                if (mHandler.getCurrentState() != PtrState.RESET) {
                    return;
                }
                mHandler.onRefetchData(mObserver);
            }
        }
    }

    public void onFetchComplete(){
        mHandler.onRefreshComplete(mHeader, this);
    }

    /**
     * 从外部调用刷新状态
     *
     * @param needScroll
     * @param forceRefresh
     */
    public void setRefreshing(boolean needScroll, boolean forceRefresh) {
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
        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                if (mHandler.canPull(mHeader, mContent, mObserver)) {
                    mIndicator.setCurrentPos(ev.getX(), ev.getY());
                    //TODO:NOT good declare here
                    float movementX = mIndicator.getMovementX();
                    float movementY = mIndicator.getMovementY();
                    if (mIndicator.getOffsetY() > mTouchSlop/2 && Math.abs(movementY) > Math.abs(movementX)) {
                        mIsBeingDragged = true;
                    }
                }
                break;
            case MotionEvent.ACTION_DOWN:
                if (mHandler.canPull(mHeader, mContent, mObserver)) {
                    mIsBeingDragged = false;
                    mIndicator.setOriginPos(ev.getX(), ev.getY());
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;

        }
        return mIsBeingDragged;
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                if (mIsBeingDragged) {
                    mIndicator.setCurrentPos(ev.getX(), ev.getY());
                    float offSetY = -mIndicator.getOffsetY();
                    float moveMentY = -mIndicator.getMovementY();
//                    PtrLog.i(LOG_TAG, "offset is %s, moveMentY is %s", offSetY, moveMentY);
                    if (mHandler.getCurrentState() != PtrState.REFRESHING) {
                        if (offSetY < 0 && (mHeader == null || Math.abs(offSetY) < ((IPtrHeader) mHeader).getMaxPullLenth())) {
                            onPull((int) offSetY, (int) moveMentY);
                        }
                    } else {
                        if (offSetY > 0 && ((IPtrHeader)mHeader).getIdleExposeTime() <= 0) {
                            doScrollBack();
                        } else {

                        }
                    }

                }
                break;
            case MotionEvent.ACTION_DOWN:
                if (mHandler.canPull(mHeader, mContent, mObserver)) {
                    mIndicator.setOriginPos(ev.getX(), ev.getY());
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mIsBeingDragged) {
                    if (mHandler.getCurrentState() == PtrState.PULLTOREFRESH) {
                        mHandler.onRefresh(mHeader, mObserver);
                    } else {
                        if (((IPtrHeader)mHeader).getIdleExposeTime() > 0) {
                            if (mIdleExposeRunnable == null) {
                                mIdleExposeRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        doScrollBack();
                                    }
                                };
                            }
                            postDelayed(mIdleExposeRunnable, ((IPtrHeader)mHeader).getIdleExposeTime());
                        } else {
                            doScrollBack();
                        }
                    }
                }
                break;

        }
        return super.onTouchEvent(ev);
    }

    /**
     * 要处理总长度：比如多重下拉
     * 也要处理距离的变化
     * 可能不同长度的Resilience不同，之后实现
     * @param len
     * @param deltaLen
     */
    private void onPull(int len, int deltaLen) {

        scrollBy(0, (int) (deltaLen * mHandler.onPull(len, mHeader, mObserver, mResilience)));
//        PtrLog.i(LOG_TAG, "pull ScrollY is %s", getScrollY());
    }

    /**
     * 类似于onreset或者resetstate
     */
    public void doScrollBack(){
        mScroller.smoothScrollTo(0, new OnScrollFinishListener() {
            @Override
            public void doOnscrollFinish() {
                mHandler.onReset(mHeader, mObserver);
            }
        });
        mHandler.onReset(mHeader, mObserver);
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

    interface OnScrollFinishListener{
        void doOnscrollFinish();
    }

    class ScrollHelper implements Runnable {
        public static final int SMOOTH_SCROLL_DURATION_MS = 200;
        private boolean mIsRunning = false;
        private int mFromY;
        private int mToY;
        private int mDuration;
        private Scroller mScroller;
        private long mStartTime = -1;

        OnScrollFinishListener mListener;
        private  Interpolator mInterpolator;

        public ScrollHelper(Interpolator interpolator){
            mInterpolator = interpolator;
            mScroller = new Scroller(getContext());
        }

        public void setInterpolator(Interpolator interpolator){
            mInterpolator = interpolator;
        }

        public void smoothScrollTo(int to, OnScrollFinishListener listener) {
            smoothScrollTo(getScrollY(), to, SMOOTH_SCROLL_DURATION_MS, listener);
        }

        public void smoothScrollTo(int from, int to, OnScrollFinishListener listener){
            smoothScrollTo(from, to, SMOOTH_SCROLL_DURATION_MS, listener);
        }

        public void smoothScrollTo(int from, int to, int duration, OnScrollFinishListener listener){
            mIsRunning = true;
            mFromY = from;
            mToY = to;
            mDuration = duration;
            mListener = listener;
            if (!mScroller.isFinished()) {
                mScroller.forceFinished(true);
            }
            mScroller.startScroll(0, 0, 0, Math.abs(mFromY - mToY), duration);

            post(this);

        }


        @Override
        public void run() {
            boolean finish = (mToY > 0 && getScrollY() < mToY) || (mToY <= 0 && getScrollY() >=0 );

            if (!finish) {
                if (mStartTime == -1) {
                    mStartTime = System.currentTimeMillis();
                    PtrLog.i(LOG_TAG, "mFromY is %s", mFromY);
                    PtrLog.i(LOG_TAG, "mStartTime is %s", mStartTime);
                    PtrLog.i(LOG_TAG, "mToY is %s", mToY);
                } else {
                    long normalizedTime = (1000 * (System.currentTimeMillis() - mStartTime)) / mDuration;
                    PtrLog.i(LOG_TAG, "normalizedTime is %s", normalizedTime);

                    normalizedTime = Math.max(Math.min(normalizedTime, 1000), 0);
                    final int deltaY = Math.round((mToY - mFromY)
                            * mInterpolator.getInterpolation(normalizedTime / 1000f));
                    final int finalDelta = Math.min(deltaY, getScrollY() - mToY);
//                    PtrLog.i(LOG_TAG, "deltaY is %s", deltaY);
//                    PtrLog.i(LOG_TAG, "finalDelta is %s", finalDelta);
//                    PtrLog.i(LOG_TAG, "ScrollY is %s \n", getScrollY());
                    scrollBy(0, deltaY);
                }
                postOnAnimation(this);
            } else {
                PtrLog.i(LOG_TAG, "finish, %s", System.currentTimeMillis());

                finish();
            }
        }

        public void destroy() {
            finish();
        }

        private void finish(){
            mIsRunning = false;
            mStartTime = -1;
            removeCallbacks(this);
            if (mListener != null) {
                mListener.doOnscrollFinish();
            }
        }
    }
}
