package com.jn.young.pulltorefresh.utils;

import com.jn.young.pulltorefresh.lifecircle.IPtrActionCirlce;

/**
 * Created by zjy on 2017/7/16.
 *
 */

public abstract class PtrObserver implements IPtrActionCirlce {
    public abstract boolean canPull();
}
