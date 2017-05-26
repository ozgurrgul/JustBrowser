package com.browser.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.browser.R;
import com.browser.theme.themes.ThemeController;
import com.browser.theme.themes.ThemeListener;

/**
 * Created by ozgur on 07.08.2016.
 */
public class JBubble extends LinearLayout implements ThemeListener {

    public JBubble(Context context, AttributeSet attrs) {
        super(context, attrs);
        _init();
    }

    private void _init() {
        View wrapper = inflate(getContext(), R.layout.bubble, this);
        ThemeController.getInstance().register(this);
        changeTheme();
    }


    @Override
    public void themeChanged() {

    }

    @Override
    public void changeTheme() {

    }
}
