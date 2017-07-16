package com.jn.young.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.jn.young.pulltorefresh.header.DefaultHeader;
import com.jn.young.pulltorefresh.header.IPtrHeader;
import com.jn.young.pulltorefresh.utils.MultiDIstanceListener;
import com.jn.young.pulltorefresh.utils.PtrHandler;
import com.jn.young.pulltorefresh.utils.PtrIndicator;
import com.jn.young.pulltorefresh.utils.PtrObserver;

/**
 * Created by zjy on 2017/7/15.
 */

public class PtrFrame extends ViewGroup {

    PtrIndicator mIndicator;
    MultiDIstanceListener mMultiDIstanceListener;
    PtrObserver mObserver;
    PtrHandler mHandler;

    View mHeader;
    View mContent;

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


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {

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


}
