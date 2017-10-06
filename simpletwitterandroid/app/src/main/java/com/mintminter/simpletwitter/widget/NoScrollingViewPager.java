package com.mintminter.simpletwitter.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Irene on 10/5/17.
 */

public class NoScrollingViewPager extends ViewPager {
    private boolean isScrollingEnabled = true;

    public NoScrollingViewPager(Context context) {
        super(context);
    }

    public NoScrollingViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollingEnabled(boolean b){
        isScrollingEnabled = b;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return isScrollingEnabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return isScrollingEnabled && super.onInterceptTouchEvent(event);
    }

}

