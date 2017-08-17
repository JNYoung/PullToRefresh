package com.jn.young.pulltorefresh.header;

import com.jn.young.pulltorefresh.PtrFrame;
import com.jn.young.pulltorefresh.lifecircle.IPtrActionCirlce;
import com.jn.young.pulltorefresh.utils.PtrHandler;

/**
 * Created by zjy on 2017/7/16.
 */

public interface IPtrHeader extends IPtrActionCirlce{
    boolean canPull();

    int getMaxPullLenth();

    /**
     * 下拉之后的空闲曝光时间，即使不刷新也显示
     * @return
     */
    int getIdleExposeTime();


    /**
     * 刷新完成的操作，接下来是回滚
     * @return 还需要延迟的时间
     */
    int onRefreshComplete(PtrFrame frame);


    /**
     * 下拉过程中的操作
     * @param len 下拉的距离
     * @return 是否到达了PUllTOREFRESH
     */
    PtrHandler.PullResult onPull(float len, float resilience);

}
