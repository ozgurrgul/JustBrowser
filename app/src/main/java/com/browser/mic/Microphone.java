package com.browser.mic;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.browser.R;
import com.browser.theme.themes.ThemeController;
import com.browser.theme.themes.ThemeListener;
import com.browser.theme.themes.ThemeModel;

/**
 * Created by ozgur on 1/30/16 at 11:43 AM.
 */
public class Microphone extends ImageView implements ThemeListener {

    private boolean running;

    public Microphone(Context context) {
        super(context);
        init();
    }

    public Microphone(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Microphone(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setImageResource(R.drawable.ic_mic);
        ThemeController.getInstance().register(this);
        changeTheme();
    }

    public boolean isRunning () {
        return running;
    }

    protected void Log(String e) {
        Log.d(getClass().getSimpleName(), e);
    }

    @Override
    public void themeChanged() {
        Log("themeChanged action");
        changeTheme();
    }

    @Override
    public void changeTheme() {
        ThemeModel themeModel = ThemeController.getInstance().getCurrentTheme();
        setColorFilter(themeModel.bmiIconColor, PorterDuff.Mode.SRC_ATOP);
    }

    public void setRunning(boolean running) {
        this.running = running;

        if(running) {
            setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP);
        } else {
            changeTheme();
        }
    }
}
