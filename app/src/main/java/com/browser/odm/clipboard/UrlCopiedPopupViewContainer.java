package com.browser.odm.clipboard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.FrameLayout;

public class UrlCopiedPopupViewContainer extends FrameLayout {

    public KeyEventHandler mKeyEventHandler;

    public UrlCopiedPopupViewContainer(Context context) {
        super(context);
    }

    public UrlCopiedPopupViewContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UrlCopiedPopupViewContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setKeyEventHandler(KeyEventHandler handler) {
        mKeyEventHandler = handler;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mKeyEventHandler != null) {
            mKeyEventHandler.onKeyEvent(event);
        }
        return super.dispatchKeyEvent(event);
    }

    public interface KeyEventHandler {
        void onKeyEvent(KeyEvent event);
    }
}