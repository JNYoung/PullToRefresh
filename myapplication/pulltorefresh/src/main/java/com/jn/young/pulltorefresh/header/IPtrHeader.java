package com.jn.young.pulltorefresh.header;

import com.jn.young.pulltorefresh.lifecircle.IPtrActionCirlce;

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

}
