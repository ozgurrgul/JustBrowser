package com.browser.ui.widget;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.browser.R;
import com.browser.theme.themes.ThemeController;
import com.browser.theme.themes.ThemeListener;
import com.browser.theme.themes.ThemeModel;

/**
 * Created by ozgur on 06.08.2016.
 */
public class JBB extends LinearLayout implements ThemeListener, View.OnClickListener {

    private ImageView mBackButton, mRemoveButton;
    private JBottomBarClickListener jBottomBarClickListener;

    public void setjBottomBarClickListener(JBottomBarClickListener jBottomBarClickListener) {
        this.jBottomBarClickListener = jBottomBarClickListener;
    }

    public interface JBottomBarClickListener {
        void onBackClick();
        void onRemoveClick();
    }

    public JBB(Context context) {
        super(context);
        _init();
    }

    public JBB(Context context, AttributeSet attrs) {
        super(context, attrs);
        _init();
    }

    public JBB(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        _init();
    }

    private void _init() {
        View wrapper = inflate(getContext(), R.layout.bottom_bar, this);
        mBackButton = (ImageView) wrapper.findViewById(R.id.BackButton);
        mRemoveButton = (ImageView) wrapper.findViewById(R.id.RemoveButton);
        mRemoveButton.setOnClickListener(this);
        mBackButton.setOnClickListener(this);
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
        mBackButton.setColorFilter(t.barItemColor, PorterDuff.Mode.SRC_ATOP);
        mRemoveButton.setColorFilter(t.barItemColor, PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public void onClick(View view) {
        if(view == mBackButton) {
            if(jBottomBarClickListener != null)
                jBottomBarClickListener.onBackClick();
        }

        if(view == mRemoveButton) {
            if(jBottomBarClickListener != null)
                jBottomBarClickListener.onRemoveClick();
        }
    }

    public void hideRemove() {
        mRemoveButton.setVisibility(GONE);
    }
}
