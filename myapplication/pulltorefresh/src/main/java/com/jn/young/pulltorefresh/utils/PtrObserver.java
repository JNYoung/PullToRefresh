package com.jn.young.pulltorefresh.utils;

import com.jn.young.pulltorefresh.lifecircle.IPtrActionCirlce;

/**
 * Created by zjy on 2017/7/16.
 * Should be used only in PtrHandler instead of EVERYWHERE
 */

public abstract class PtrObserver implements IPtrActionCirlce {
    public abstract boolean canPull();

    /**
     * used when start another fetch Opration
     * @return if the fetch stops
     */
    public abstract boolean stopFetchOps();
}
