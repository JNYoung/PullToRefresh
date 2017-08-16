package com.jn.young.pulltorefresh.lifecircle;

/**
 * Created by zjy on 2017/7/16.
 * deal with manual scroll
 * reset
 * ↓
 * onpull
 * ↓
 * onPullToRefresh
 * ↓
 * onRefreshing
 * ↓
 * onRefreshComplete
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

    /**
     * 下拉过程中的操作
     * @param len 下拉的距离
     * @return 是否到达了PUllTOREFRESH
     */
    boolean onPull(float len);

    /**
     * 刷新完成的操作，接下来是回滚
     * @return 还需要延迟的时间
     */
    int onRefreshComplete();

}
