package com.browser.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.browser.R;
import com.browser.theme.themes.ThemeController;
import com.browser.theme.themes.ThemeListener;
import com.browser.theme.themes.ThemeModel;

/**
 * Created by ozgur on 06.08.2016.
 */
public class JTB extends LinearLayout implements ThemeListener {

    private JTV titleTextView;

    public JTB(Context context) {
        super(context);
        _init();
    }

    public JTB(Context context, AttributeSet attrs) {
        super(context, attrs);
        _init();
    }

    public JTB(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        _init();
    }

    private void _init() {
        View wrapper = inflate(getContext(), R.layout.top_bar, this);
        titleTextView = (JTV) wrapper.findViewById(R.id.TitleTextView);
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
        titleTextView.setTextColor(t.barItemColor);
    }

    public void setTitle(int resId) {
        titleTextView.setText(resId);
    }
}
