package com.browser.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by ozgur on 05.08.2016.
 */
public class JEditText extends EditText {

    public JEditText(Context context) {
        super(context);
        _init();
    }

    public JEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        _init();
    }

    public JEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        _init();
    }

    private void _init() {
        //setTypeface(FontCache.getFont(getContext(), FontCache.OPEN_SANS));
    }

}
