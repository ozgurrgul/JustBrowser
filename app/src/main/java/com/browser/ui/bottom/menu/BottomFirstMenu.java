package com.browser.ui.bottom.menu;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.browser.R;
import com.browser.tab.TabView;
import com.browser.browser.uictrl.UIController;
import com.browser.ui.bottom.BottomFirstMenuAndUrlBar;
import com.browser.ui.bottom.BottomMenuItem;
import com.browser.ui.bottom.BottomMenuItemAnimHolder;
import com.browser.ui.widget.JView;

/**
 * Created by ozgur on 03.08.2016.
 */
public class BottomFirstMenu extends LinearLayout implements View.OnClickListener, JView {

    /* items */
    private BottomMenuItem backItem;
    private BottomMenuItem forwardItem;
    private BottomMenuItem micItem;
    private BottomMenuItem homeItem;
    private BottomMenuItem closeItem;

    private BottomMenuItemAnimHolder bottomMenuItemAnimHolder;
    public static final int BOTTOM_MENU_ITEM_HIDE_ANIM_DURATION = 100;

    private boolean hiding;
    private boolean menuShowing;


    private UIController uiController;
    private BottomFirstMenuAndUrlBar parent;

    public BottomFirstMenu(Context context) {
        super(context);
        _init();
    }

    public BottomFirstMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        _init();
    }

    private void _init() {
        View wrapper = inflate(getContext(), R.layout.bottom_first_menu, this);
        bottomMenuItemAnimHolder = new BottomMenuItemAnimHolder(getContext());
        backItem = (BottomMenuItem) wrapper.findViewById(R.id.BottomMenuBack);
        forwardItem = (BottomMenuItem) wrapper.findViewById(R.id.BottomMenuForward);
        micItem = (BottomMenuItem) wrapper.findViewById(R.id.BottomMenuMic);
        homeItem = (BottomMenuItem) wrapper.findViewById(R.id.BottomMenuHome);
        closeItem = (BottomMenuItem) wrapper.findViewById(R.id.BottomMenuClose);

        /**/
        backItem.setIcon(R.drawable.ic_back).setOnClickListener(this);
        forwardItem.setIcon(R.drawable.ic_forward).setOnClickListener(this);
        micItem.setIcon(R.drawable.ic_mic).setOnClickListener(this);
        homeItem.setIcon(R.drawable.ic_home).setOnClickListener(this);
        closeItem.setIcon(R.drawable.ic_close).setOnClickListener(this);
    }

    @Override
    public void show() {

        if(menuShowing) {
            return;
        }

        menuShowing = true;
        setVisibility(VISIBLE);

        backItem.show();
        forwardItem.show();
        micItem.show();
        homeItem.show();
        closeItem.show();
    }

    @Override
    public void hide() {

        if(hiding) {
            return;
        }

        hiding = true;

        backItem.hide();
        forwardItem.hide();
        micItem.hide();
        homeItem.hide();
        closeItem.hide();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hiding = false;
                menuShowing = false;
                setVisibility(GONE);
            }
        }, BOTTOM_MENU_ITEM_HIDE_ANIM_DURATION);
    }

    @Override
    public boolean onBackPressed() {

        if(menuShowing) {
            parent.hideBottomMenu();
            return true;
        }

        return false;
    }

    public void setUiController(UIController uiController) {
        this.uiController = uiController;
    }

    @Override
    public void onClick(View view) {
        if (view == closeItem) {
            parent.hideBottomMenu();
        } else {
            uiController.onBottomMenuEvent(view.getId());
        }
    }

    public void setParent(BottomFirstMenuAndUrlBar parent) {
        this.parent = parent;
    }

    public void onPageFinished(TabView tabView) {
        update(tabView);
    }

    public void onPageStarted(TabView tabView) {
        update(tabView);
    }

    public void onShowHome(TabView tabView) {
        update(tabView);
    }

    public void onHideHome(TabView tabView) {
        update(tabView);
    }

    private void update(TabView tabView) {

        if(tabView.getTabData().isInBackground())
            return;

        if(tabView.canTabGoBack()) {
            backItem.setEnabled(true);
        } else {
            backItem.setEnabled(false);
        }

        if(tabView.canTabGoForward()) {
            forwardItem.setEnabled(true);
        } else {
            forwardItem.setEnabled(false);
        }

        if(tabView.isHome()) {
            //already in home, disable item
            homeItem.setEnabled(false);
        } else {
            homeItem.setEnabled(true);
        }
    }
}
