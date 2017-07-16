package com.jn.young.pulltorefresh.utils;

import android.view.View;

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
                && ptrObserver != null && ptrObserver.canPull()
                && canContentPull(content);
    }

    /**
     * 内容是否可以下拉
     * @param content
     * @return
     */
    private boolean canContentPull(View content){
        return true;
    }

    public void onPull(float lenth, View header,  PtrObserver ptrObserver) {
        ((IPtrHeader)header).onPull(lenth);
        if (ptrObserver != null) {
            ptrObserver.onPull(lenth);
        }
    }

    public PtrState getCurrentState(){
        return mState;
    }

    private void setState(PtrState state){
        mState = state;
    }
}
