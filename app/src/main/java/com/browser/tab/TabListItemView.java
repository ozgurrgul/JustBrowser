package com.browser.tab;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.browser.R;
import com.browser.theme.themes.ThemeController;
import com.browser.theme.themes.ThemeListener;
import com.browser.theme.themes.ThemeModel;
import com.browser.ui.widget.JTV;

/**
 * Created by ozgur on 06.08.2016.
 */
public class TabListItemView extends LinearLayout implements ThemeListener{

    public LinearLayout mWrapper;
    private JTV titleTextView;
    public ImageView iconImageView;
    public ImageView closeImageView;
    public FrameLayout closeImageViewWrapper;

    public TabListItemView(Context context) {
        super(context);
        _init();
    }

    private void _init() {
        View wrapper = inflate(getContext(), R.layout.tabs_list_item, this);

        /**/
        mWrapper = (LinearLayout) wrapper.findViewById(R.id.Wrapper);
        titleTextView = (JTV) wrapper.findViewById(R.id.TitleTextView);
        iconImageView = (ImageView) wrapper.findViewById(R.id.IconImageView);
        closeImageView = (ImageView) wrapper.findViewById(R.id.CloseImageView);
        closeImageViewWrapper = (FrameLayout) wrapper.findViewById(R.id.CloseImageViewWrapper);

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
        mWrapper.setBackgroundResource(t.tabsListItemBg);
        titleTextView.setTextColor(t.tabsListItemTxtColor);
        closeImageView.setColorFilter(t.tabsListItemTxtColor, PorterDuff.Mode.SRC_ATOP);
    }

    public void setTabTitle(String title) {
        if(titleTextView != null) titleTextView.setText(title);
    }
}
