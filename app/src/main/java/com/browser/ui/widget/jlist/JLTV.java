package com.browser.ui.widget.jlist;

import android.content.Context;
import android.util.AttributeSet;

import com.browser.theme.themes.ThemeController;
import com.browser.theme.themes.ThemeListener;
import com.browser.ui.widget.JTV;

/**
 * Created by ozgur on 06.08.2016.
 */
public class JLTV extends JTV implements ThemeListener {

    public JLTV(Context context) {
        super(context);
        _init();
    }

    public JLTV(Context context, AttributeSet attrs) {
        super(context, attrs);
        _init();
    }

    public JLTV(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        _init();
    }

    private void _init() {
        ThemeController.getInstance().register(this);
        changeTheme();
    }

    @Override
    public void themeChanged() {
        changeTheme();
    }

    @Override
    public void changeTheme() {
        setTextColor(ThemeController.getInstance().getCurrentTheme().jListViewTxtColor);
    }
}
