package com.marc.nelnet.nelnetpayexperience.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Marc on 3/10/2017.
 */

public class AuthorizationSheetBehavior<V extends View> extends BottomSheetBehavior<V> {

    // Constructors are necessary. Do not delete.
    public AuthorizationSheetBehavior() {
        super();
    }

    public AuthorizationSheetBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
        boolean rVal = true;
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_SCROLL:
            case MotionEvent.ACTION_UP:

                break;
            default:
                rVal = super.onTouchEvent(parent, child, event);
                break;
        }
        return rVal;
    }
}
