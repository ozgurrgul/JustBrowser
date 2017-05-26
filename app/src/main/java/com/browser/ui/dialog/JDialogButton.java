package com.browser.ui.dialog;

import android.content.Context;
import android.util.AttributeSet;

import com.browser.ui.widget.JTV;
import com.browser.utils.Utils;

/**
 * Created by ozgur on 05.08.2016.
 */
public class JDialogButton extends JTV {

    public JDialogButton(Context context) {
        super(context);
        _init();
    }

    public JDialogButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        _init();
    }

    public JDialogButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        _init();
    }

    private void _init() {
        int pad = Utils.dpToPx(getContext(), 3);
        setPadding(pad, pad, pad, pad);
    }
}
