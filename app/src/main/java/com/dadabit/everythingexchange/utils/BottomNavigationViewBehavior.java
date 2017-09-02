package com.dadabit.everythingexchange.utils;

import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.view.View;

public class BottomNavigationViewBehavior extends CoordinatorLayout.Behavior<BottomNavigationView> {

    private int height;
    private BottomNavigationView mBottomNavigationView;
    public boolean isDown;

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, BottomNavigationView child, int layoutDirection) {
        height = child.getHeight();
        return super.onLayoutChild(parent, child, layoutDirection);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, BottomNavigationView child, View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, BottomNavigationView child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        mBottomNavigationView = child;
        if (dyConsumed > 0) {
            slideDown();
        } else if (dyConsumed < 0) {
            slideUp();
        }
    }

    public void slideUp() {
        if (mBottomNavigationView.getVisibility() == View.INVISIBLE){
            mBottomNavigationView.setVisibility(View.VISIBLE);
        }
        mBottomNavigationView.clearAnimation();
        mBottomNavigationView.animate().translationY(0).setDuration(200);
        isDown = false;
    }

    public void slideDown() {
        mBottomNavigationView.clearAnimation();
        mBottomNavigationView.animate().translationY(height).setDuration(200);
        isDown = true;
    }


}