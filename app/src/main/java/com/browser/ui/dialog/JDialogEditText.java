package com.browser.ui.dialog;

import android.content.Context;
import android.util.AttributeSet;

import com.browser.theme.themes.ThemeController;
import com.browser.theme.themes.ThemeListener;
import com.browser.theme.themes.ThemeModel;
import com.browser.ui.widget.JEditText;

/**
 * Created by ozgur on 05.08.2016.
 */
public class JDialogEditText extends JEditText implements ThemeListener {

    public JDialogEditText(Context context) {
        super(context);
        _init();
    }

    public JDialogEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        _init();
    }

    public JDialogEditText(Context context, AttributeSet attrs, int defStyleAttr) {
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
        setBackgroundResource(t.dialogEditTextBg);
        setTextColor(t.dialogEditTextColor);
    }
}
