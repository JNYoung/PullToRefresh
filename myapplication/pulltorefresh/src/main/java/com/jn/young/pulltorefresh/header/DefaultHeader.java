package com.jn.young.pulltorefresh.header;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by zjy on 2017/7/16.
 */

public class DefaultHeader extends View implements IPtrHeader {
    public DefaultHeader(Context context) {
        super(context);
    }

    public DefaultHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean canPull() {
        return false;
    }
}
