package com.jn.young.pulltorefresh.utils;

import android.util.Log;

/**
 * Created by zjy on 2017/7/15.
 */

public class PtrIndicator {
    private static String LOG_TAG = "PTR_Indicator";
    private float mCurrentX, mCurrentY;
    private float mOriginX, mOriginY;
    private float mMovementX, mMovementY;
    private float mLastX, mLastY;
    private float mOffsetX, mOffsetY;
    private boolean mIsBesingDragged;

    public boolean isReachedOriginPos(){
       return  true;
    }

    public void setOriginPos(float x, float y) {
        mCurrentX = mLastX = mOriginX = x;
        mCurrentY = mLastY = mOriginY = y;
        mIsBesingDragged = false;
    }
    public void setCurrentPos(float x, float y) {
        mCurrentX = x;
        mCurrentY= y;
        PtrLog.i(LOG_TAG, "mCurrentX is %s, mCurrentY is %s", x, y);
        mMovementX = mCurrentX - mLastX;
        mMovementY = mCurrentY - mLastY;
        PtrLog.i(LOG_TAG, "mMovementX is %s, mMovementY is %s", x, y);
        mOffsetX = mCurrentX - mOriginX;
        mOffsetY = mCurrentY - mOriginY;
        PtrLog.i(LOG_TAG, "mMovementX is %s, mMovementY is %s", x, y);
        mLastY = mCurrentY;
        mLastX = mCurrentX;
        PtrLog.i(LOG_TAG, "mLastX is %s, mLast is %s", x, y);

    }

    public void resetPos(){
        setOriginPos(0f, 0f);
    }

    public float getOffsetY(){
        return mOffsetY;
    }
    public float getMovementY(){
        return mMovementY;
    }
    public float getMovementX(){
        return mMovementX;
    }
}
