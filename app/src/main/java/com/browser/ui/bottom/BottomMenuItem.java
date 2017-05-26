package com.browser.ui.bottom;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.browser.R;
import com.browser.theme.themes.ThemeController;
import com.browser.theme.themes.ThemeListener;
import com.browser.theme.themes.ThemeModel;
import com.browser.ui.widget.JTV;
import com.browser.ui.widget.JView;

/**
 * Created by ozgur on 03.08.2016.
 */
public class BottomMenuItem extends LinearLayout implements JView, ThemeListener {

    private ImageView mImageViewIcon;
    private JTV mTabsCountTextView;

    /**/
    private int iconColor;
    private int iconDisabledColor;
    private int hoverColor;

    /**/
    private Rect rect;

    public BottomMenuItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        _init();
    }

    private void _init() {
        View wrapper = inflate(getContext(), R.layout.bottom_menu_item, this);
        mImageViewIcon = (ImageView) wrapper.findViewById(R.id.ImageViewIcon);
        mTabsCountTextView = (JTV) wrapper.findViewById(R.id.TabsCountTextView);

        ThemeController.getInstance().register(this);
        changeTheme();

        /* for color effect */
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN: hold(); break;
                    case MotionEvent.ACTION_UP: filter(); break;
                    case MotionEvent.ACTION_CANCEL: filter(); break;
                    case MotionEvent.ACTION_MOVE:
                        if(!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())){
                            filter();
                        }
                        break;
                }
                return false;
            }
        });
    }

    public BottomMenuItem setIcon(int resId) {
        mImageViewIcon.setImageResource(resId);
        return this;
    }

    private void reset() {
        mImageViewIcon.setColorFilter(iconColor, PorterDuff.Mode.SRC_ATOP);
        mTabsCountTextView.setTextColor(iconColor);
    }

    @Override
    public void show() {
        startAnimation(BottomMenuItemAnimHolder.showAnim);
    }

    @Override
    public void hide() {
        startAnimation(BottomMenuItemAnimHolder.hideAnim);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    private void filter() {
        setBackgroundColor(Color.TRANSPARENT);
    }

    private void hold() {
        rect = new Rect(getLeft(), getTop(), getRight(), getBottom());
        setBackgroundColor(hoverColor);
    }

    public void setTabsCount(int count) {

        if(!mTabsCountTextView.isShown()) {
            mTabsCountTextView.setVisibility(VISIBLE);
        }

        mTabsCountTextView.setText(String.valueOf(count));
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        if(enabled) {
            reset();
        } else {
            mImageViewIcon.setColorFilter(iconDisabledColor, PorterDuff.Mode.SRC_ATOP);
        }
    }

    @Override
    public void themeChanged() {
        changeTheme();
    }

    @Override
    public void changeTheme() {

        ThemeModel t = ThemeController.getInstance().getCurrentTheme();

        iconColor = t.bmiIconColor;
        iconDisabledColor = t.bmiIconDisabledColor;
        hoverColor = t.bmiBgHoverColor;

        if(isEnabled()) {
            reset();
        } else {
            mImageViewIcon.setColorFilter(iconDisabledColor, PorterDuff.Mode.SRC_ATOP);
        }
    }
}
