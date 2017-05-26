package com.browser.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by ozgur on 04.08.2016.
 */
public class JTV extends TextView {

    public JTV(Context context) {
        super(context);
        _init();
    }

    public JTV(Context context, AttributeSet attrs) {
        super(context, attrs);
        _init();
    }

    public JTV(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        _init();
    }

    private void _init() {
        //setTypeface(FontCache.getFont(getContext(), FontCache.OPEN_SANS));
    }
}
