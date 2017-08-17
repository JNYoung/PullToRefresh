package com.jn.young.pulltorefresh.lifecircle;

import com.jn.young.pulltorefresh.PtrFrame;

/**
 * Created by zjy on 2017/7/16.
 * deal with manual scroll
 * reset
 * ↓
 * onpull (交给头部来做）
 * ↓
 * onPullToRefresh
 * ↓
 * onRefreshing
 * ↓
 * onRefreshComplete (交给头部来做）
 * ↓
 * （Scroll back）
 * ↓
 * reset
 */

public interface IPtrActionCirlce {

    /**
     * 回复到初始位置之后调用
     * 回复过程中是不能有任何操作
     */
    void onReset();

    /**
     * 到达刷新位置的操作
     * 同样，从原始位置滚动到刷新位置也是不能有任何操作
     */
    void onRefreshing();

    /**
     * 到达可以刷新的位置之后的操作
     */
    void onPullToRefresh();



}
