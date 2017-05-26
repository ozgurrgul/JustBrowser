package com.browser.tab.component;

import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class FullscreenHolder extends FrameLayout {

    public FullscreenHolder(Context context) {
        super(context);
        this.setBackgroundColor(Color.BLACK);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}