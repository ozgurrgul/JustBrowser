package com.browser.ui.widget.jlist;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.browser.theme.themes.ThemeController;
import com.browser.theme.themes.ThemeListener;
import com.browser.theme.themes.ThemeModel;

/**
 * Created by ozgur on 06.08.2016.
 */
public class JLBG extends LinearLayout implements ThemeListener {

    public JLBG(Context context) {
        super(context);
        _init();
    }

    public JLBG(Context context, AttributeSet attrs) {
        super(context, attrs);
        _init();
    }

    public JLBG(Context context, AttributeSet attrs, int defStyleAttr) {
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
        ThemeModel t = ThemeController.getInstance().getCurrentTheme();
        setBackgroundResource(t.jListViewBg);
    }
}
