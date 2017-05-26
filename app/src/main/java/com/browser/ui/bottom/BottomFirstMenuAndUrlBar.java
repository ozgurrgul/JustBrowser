package com.browser.ui.bottom;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;

import com.browser.R;
import com.browser.tab.TabView;
import com.browser.browser.uictrl.UIControllerImpl;
import com.browser.ui.bottom.menu.BottomFirstMenu;
import com.browser.ui.widget.JView;

/**
 * Created by ozgur on 04.08.2016.
 */
public class BottomFirstMenuAndUrlBar extends LinearLayout implements JView {

    public Urlbar urlbar;
    protected BottomFirstMenu bottomFirstMenu;
    private UIControllerImpl uiController;

    public BottomFirstMenuAndUrlBar(Context context) {
        super(context);
        _init();
    }

    private void _init() {
        View wrapper = inflate(getContext(), R.layout.bottom_menu_and_url_bar, this);

        /**/
        bottomFirstMenu = (BottomFirstMenu) wrapper.findViewById(R.id.BottomMenu);
        urlbar = (Urlbar) wrapper.findViewById(R.id.Urlbar);
        bottomFirstMenu.setParent(this);
        urlbar.setParent(this);
    }

    public void setUiController(UIControllerImpl uiController) {
        this.uiController = uiController;
        bottomFirstMenu.setUiController(uiController);
    }

    @Override public void show() {}

    @Override public void hide() {}

    @Override
    public boolean onBackPressed() {
        return bottomFirstMenu.onBackPressed();
    }

    public void setTabsCount(int count) {
        urlbar.setTabsCount(count);
    }

    public void showBottomMenu() {
        bottomFirstMenu.show();
        urlbar.hide();
    }

    public void hideBottomMenu() {
        bottomFirstMenu.hide();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                urlbar.show();
            }
        }, BottomFirstMenu.BOTTOM_MENU_ITEM_HIDE_ANIM_DURATION);
    }

    public void onPageFinished(TabView tabView) {
        bottomFirstMenu.onPageFinished(tabView);
        urlbar.onPageFinished(tabView);
    }

    public void onPageStarted(TabView tabView) {
        bottomFirstMenu.onPageStarted(tabView);
        urlbar.onPageStarted(tabView);
    }

    public void onShowHome(TabView tabView) {
        bottomFirstMenu.onShowHome(tabView);
        urlbar.onShowHome(tabView);
    }

    public void onHideHome(TabView tabView) {
        bottomFirstMenu.onHideHome(tabView);
        urlbar.onHideHome(tabView);
    }

    public boolean isUrlBarExpanded () {
        return urlbar.isUrlBarExpanded();
    }

    public UIControllerImpl getUiController() {
        return uiController;
    }
}
