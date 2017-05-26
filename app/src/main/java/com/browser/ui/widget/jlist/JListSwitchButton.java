package com.browser.ui.widget.jlist;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.AttributeSet;

import com.browser.theme.themes.ThemeController;
import com.browser.theme.themes.ThemeListener;
import com.browser.ui.widget.JSwitchButton;

/**
 * Created by ozgur on 11.08.2016.
 */
public class JListSwitchButton extends JSwitchButton implements ThemeListener {

    public JListSwitchButton(Context context) {
        super(context);
        _init2();
    }

    public JListSwitchButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        _init2();
    }

    public JListSwitchButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        _init2();
    }

    private void _init2() {
        ThemeController.getInstance().register(this);
        changeTheme();
    }

    @Override
    public void themeChanged() {
        changeTheme();
    }

    @Override
    public void changeTheme() {
        getBackground().setColorFilter(ThemeController.getInstance().getCurrentTheme().jListViewTxtColor, PorterDuff.Mode.SRC_ATOP);
    }
}
