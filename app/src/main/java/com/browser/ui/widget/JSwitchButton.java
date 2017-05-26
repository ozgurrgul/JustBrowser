package com.browser.ui.widget;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ToggleButton;

import com.browser.R;

/**
 * Created by ozgur on 11.08.2016.
 */
public class JSwitchButton extends ToggleButton {

    public JSwitchButton(Context context) {
        super(context);
        _init();
    }

    public JSwitchButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        _init();
    }

    public JSwitchButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        _init();
    }

    private void _init() {
        setBackground(ContextCompat.getDrawable(getContext(), R.drawable.jswitch));
        setTextOff("");
        setTextOn("");
    }
}
