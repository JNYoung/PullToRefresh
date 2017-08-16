package com.jn.young.pulltorefresh.utils;

import android.view.View;
import android.widget.AbsListView;

import com.jn.young.pulltorefresh.header.IPtrHeader;
import com.jn.young.pulltorefresh.lifecircle.PtrState;

/**
 * Created by zjy on 2017/7/16.
 */

public class PtrHandler {

    private PtrState mState = PtrState.RESET;//刷新过程中的
    /**
     * 判断是否可以下拉
     * @param header 如果头部为空，认为不能下拉
     * @param content 列表或是什么
     * @param ptrObserver 用户控制的
     * @return
     */
    public boolean canPull(View header, View content,  PtrObserver ptrObserver) {
        if (header == null) {
            return false;
        }
        if (!(header instanceof IPtrHeader)) {
            throw new IllegalArgumentException("header should implements IPtrHeader");
        }

        return ((IPtrHeader) header).canPull()
                && (ptrObserver == null || ptrObserver.canPull())
                && canContentPull(content);
    }


    /**
     * 内容是否可以下拉
     * @param content
     * @return
     */
    private boolean canContentPull(View content){
        return !canContentScroll(content);
    }

    private boolean canContentScroll (View view) {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (view instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) view;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return view.getScrollY() > 0;
            }
        } else {
            return view.canScrollVertically(-1);
        }
    }

    public void onReset(View header, PtrObserver ptrObserver) {
        ((IPtrHeader)header).onReset();
        if (ptrObserver != null) {
            ptrObserver.onReset();
        }
        setState(PtrState.RESET);

    }
    public void onPull(float lenth, View header,  PtrObserver ptrObserver) {
        ((IPtrHeader)header).onPull(lenth);
        if (ptrObserver != null) {
            ptrObserver.onPull(lenth);
        }
    }

    public void onPulltoRefresh ( View header,  PtrObserver ptrObserver) {
        ((IPtrHeader)header).onPullToRefresh();
        if (ptrObserver != null) {
            ptrObserver.onPullToRefresh();
        }
        setState(PtrState.PULLTOREFRESH);
    }

    public void onRefresh(View header, PtrObserver observer) {
        ((IPtrHeader)header).onRefreshing();
        if (observer != null) {
            observer.onRefreshing();
        }
        setState(PtrState.REFRESHING);
    }

    public void onRefetchData(PtrObserver observer) {
        setState(PtrState.REFRESHING);
        if (observer != null) {
            observer.onRefreshing();
        }
    }

    public void onRefreshComplete(View header, PtrObserver ptrObserver) {
        ((IPtrHeader)header).onRefreshComplete();
        if (ptrObserver != null) {
            ptrObserver.onRefreshComplete();
        }
    }

    public PtrState getCurrentState(){
        return mState;
    }

    private void setState(PtrState state){
        mState = state;
    }
}
