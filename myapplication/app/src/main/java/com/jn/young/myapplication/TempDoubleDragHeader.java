package com.jn.young.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import com.jn.young.pulltorefresh.PtrFrame;
import com.jn.young.pulltorefresh.header.IPtrHeader;
import com.jn.young.pulltorefresh.utils.PtrHandler;

/**
 * Created by zhengjinyang on 2017/8/25.
 */

@SuppressLint("AppCompatCustomView")
public class TempDoubleDragHeader extends TextView implements IPtrHeader{
    public TempDoubleDragHeader(Context context) {
        super(context);
    }

    @Override
    public boolean canPull() {
        return true;
    }

    @Override
    public int getMaxPullLenth() {
        return 500;
    }

    @Override
    public int getIdleExposeTime() {
        return 200;
    }

    @Override
    public int onRefreshComplete(PtrFrame frame, String refreshTip) {
        setBackgroundColor(getContext().getResources().getColor(android.R.color.holo_blue_bright));
        frame.doScrollBackAfter(1500);
        return 0;
    }

    @Override
    public PtrHandler.PullResult onPull(float len, float resilience) {
        PtrHandler.PullResult result = new PtrHandler.PullResult();
        if (len<200){
            result.atPullToRequestRealm = false;
            result.newResilience = resilience;
        } else if (len < 300) {
            result.atPullToRequestRealm = true;
            result.newResilience = resilience;

        } else {
            result.atPullToRequestRealm = false;
            result.newResilience = 0.5f;
            ((Activity)getContext()).startActivity(new Intent(getContext(), JumpActivity.class));
        }

        return result;
    }

    @Override
    public int getRefreshingPos() {
        return 300;
    }

    @Override
    public void onReset() {
        setBackgroundColor(getContext().getResources().getColor(android.R.color.holo_green_light));

    }

    @Override
    public void onRefreshing() {

    }

    @Override
    public void onPullToRefresh() {

    }
}
