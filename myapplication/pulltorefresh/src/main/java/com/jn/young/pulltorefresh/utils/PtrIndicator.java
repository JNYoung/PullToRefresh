package com.jn.young.pulltorefresh.utils;

/**
 * Created by zjy on 2017/7/15.
 */

public class PtrIndicator {
    private float mCurrentX, mCurrentY;
    private float mMovementX, mMovementY;
    private float mLastX, mLastY;

    public boolean isReachedOriginPos(){
       return  true;
    }

    public void setOriginPos(float x, float y) {
        mCurrentX = mLastX = x;
        mCurrentY = mLastY = y;
    }
    public void setCurrentPos(float x, float y) {
        mCurrentX = x;
        mCurrentY= y;
        mMovementX = mCurrentX - mLastX;
        mMovementY = mCurrentY - mLastY;
        mLastY = mCurrentY;
        mLastX = mCurrentX;
    }

    public void resetPos(){
        setOriginPos(0f, 0f);
    }

    public float getMovementY(){
        return mMovementY;
    }
    public float getMovementX(){
        return mMovementX;
    }
}
