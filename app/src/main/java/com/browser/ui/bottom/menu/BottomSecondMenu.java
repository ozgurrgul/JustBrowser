package com.browser.ui.bottom.menu;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.browser.R;
import com.browser.tab.TabView;
import com.browser.theme.themes.NightTheme;
import com.browser.theme.themes.ThemeController;
import com.browser.theme.themes.ThemeListener;
import com.browser.theme.themes.ThemeModel;
import com.browser.browser.uictrl.UIController;
import com.browser.ui.bottom.BottomMenuItem;
import com.browser.ui.bottom.BottomPart;
import com.browser.ui.widget.JView;

/**
 * Created by ozgur on 03.08.2016.
 */
public class BottomSecondMenu extends LinearLayout implements View.OnClickListener, JView, ThemeListener {

    /* items */
    private BottomMenuItem historyItem;
    private BottomMenuItem downloadsItem;
    private BottomMenuItem nightModeItem;
    private BottomMenuItem bookmarksItem;
    private BottomMenuItem themeItem;
    private BottomMenuItem settingsItem;
    private UIController uiController;
    private BottomPart parent;

    public BottomSecondMenu(Context context) {
        super(context);
        _init();
    }

    private void _init() {
        View wrapper = inflate(getContext(), R.layout.bottom_second_menu, this);
        historyItem = (BottomMenuItem) wrapper.findViewById(R.id.BottomSecMenuHistory);
        downloadsItem = (BottomMenuItem) wrapper.findViewById(R.id.BottomSecMenuDownloads);
        nightModeItem = (BottomMenuItem) wrapper.findViewById(R.id.BottomSecMenuNightMode);
        bookmarksItem = (BottomMenuItem) wrapper.findViewById(R.id.BottomSecBookmarks);
        themeItem = (BottomMenuItem) wrapper.findViewById(R.id.BottomSecMenuTheme);
        settingsItem = (BottomMenuItem) wrapper.findViewById(R.id.BottomSecMenuSettings);

        /**/
        historyItem.setIcon(R.drawable.ic_history).setOnClickListener(this);
        downloadsItem.setIcon(R.drawable.ic_downloads).setOnClickListener(this);
        nightModeItem.setIcon(R.drawable.ic_night).setOnClickListener(this);
        bookmarksItem.setIcon(R.drawable.ic_bookmark).setOnClickListener(this);
        themeItem.setIcon(R.drawable.ic_theme).setOnClickListener(this);
        settingsItem.setIcon(R.drawable.ic_settings).setOnClickListener(this);

        /**/
        ThemeController.getInstance().register(this);
        changeTheme();
    }

    @Override public void show() {}

    @Override public void hide() {}

    @Override
    public boolean onBackPressed() {
        return false;
    }

    public void setUiController(UIController uiController) {
        this.uiController = uiController;
    }

    @Override
    public void onClick(final View view) {

        parent.movePager(0);

        //put a little delay
        postDelayed(new Runnable() {
            @Override
            public void run() {
                uiController.onSecondBottomMenuEvent(view.getId());
            }
        }, 100);
    }

    public void setParent(BottomPart parent) {
        this.parent = parent;
    }

    @Override
    public void themeChanged() {
        changeTheme();
    }

    @Override
    public void changeTheme() {
        ThemeModel t = ThemeController.getInstance().getCurrentTheme();
        if(t instanceof NightTheme) {
            nightModeItem.setIcon(R.drawable.ic_day).setOnClickListener(this);
        } else {
            nightModeItem.setIcon(R.drawable.ic_night).setOnClickListener(this);
        }
    }

    public void onShowHome(TabView tabView) {
        update(tabView);
    }

    public void onHideHome(TabView tabView) {
        update(tabView);
    }

    private void update(TabView tabView) {

        /*
        if(tabView.isHome()) {
            bookmarksItem.setEnabled(false);
        } else {
            bookmarksItem.setEnabled(true);
        }
        */
    }

}
