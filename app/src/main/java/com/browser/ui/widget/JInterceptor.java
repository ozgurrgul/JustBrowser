package com.browser.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;

/**
 * Created by ozgur on 06.06.2016.
 */
public class JInterceptor extends ViewGroup {

    private EmptySpaceClickListener emptySpaceClickListener;

    public JInterceptor(Context context) {
        super(context);
    }

    public JInterceptor(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JInterceptor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //Log("onInterceptTouchEvent");
        //use it here
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: clicked(); break;
        }

        return true;
    }

    private void clicked() {
        if(emptySpaceClickListener != null) {
            emptySpaceClickListener.onEmptySpaceClicked(getId());
        }
    }

    private void Log(String e) {
        Log.d(getClass().getSimpleName(), e);
    }

    public void setEmptySpaceClickListener(EmptySpaceClickListener emptySpaceClickListener) {
        this.emptySpaceClickListener = emptySpaceClickListener;
    }

    public interface EmptySpaceClickListener {
        void onEmptySpaceClicked(int id);
    }

    public void hide() {
        if(isShown())
            setVisibility(GONE);
    }

    public void show() {
        setVisibility(VISIBLE);
    }
}
