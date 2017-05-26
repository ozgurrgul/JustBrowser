package com.browser.tab;

import android.text.TextUtils;

import com.browser.R;
import com.browser.browser.uictrl.UIController;
import com.browser.browser.uictrl.UILifeCycle;

import java.util.ArrayList;

/**
 * Created by ozgur on 03.08.2016.
 */
public class TabsController implements UILifeCycle {

    private final UIController uiController;
    private ArrayList<TabView> tabs;
    private TabView currentTabView;

    public TabsController (UIController uiController) {
        this.uiController = uiController;
        this.tabs = new ArrayList<>();
    }

    public String addTab(String url, boolean isIncognito, boolean inBackground, boolean closeWhenBackTab, boolean showAlert) {

        TabData tabData = new TabData();
        tabData.setIncognito(isIncognito);
        tabData.setUrl(url);
        tabData.setCloseTabWhenBack(closeWhenBackTab);
        tabData.setInBackground(inBackground);

        TabView tabView = new TabView(uiController, tabData);
        tabView.query(url);

        if(tabsCount() > 0) { //control jic
            tabs.add(tabs.indexOf(currentTab()) + 1, tabView);
        } else {
            tabs.add(tabView);
        }

        if(inBackground && showAlert) {
            uiController.onNewTabAdded(tabView);
            uiController.toast(R.string.tab_opened_in_bg);
        } else {
            setCurrent(tabView, true);
        }

        return tabData.getId();
    }

    private void setCurrent(TabView tabView, boolean newTab) {

        if(tabView == null) {
            return;
        }

        /**/
        if(tabView == currentTabView) {
            //updateUI();
            return;
        }

        //all tabs
        for(TabView t: tabs) {
            t.getTabData().setBrowsing(false);
            t.getTabData().setInBackground(true);
        }

        tabView.getTabData().setBrowsing(true);
        tabView.getTabData().setInBackground(false);
        tabView.onResume();

        uiController.getTabContainer().removeAllViews();
        uiController.getTabContainer().addView(tabView);

        if(tabView.isHome()) {
            tabView.showHome();
        } else {
            tabView.hideHome();
        }

        currentTabView = tabView;

        if(newTab) {
            uiController.onNewTabAdded(currentTabView);
        } else {
            uiController.onTabChosen(currentTabView);
        }

    }

    public int tabsCount() {
        return tabs.size();
    }

    public TabView currentTab() {
        return currentTabView;
    }

    public ArrayList<TabData> getTabsData() {

        ArrayList<TabData> s = new ArrayList<>();

        for (TabView t: tabs) {
            s.add(t.getTabData());
        }

        return s;
    }

    public ArrayList<TabView> getTabs() {
        return tabs;
    }

    public void onChooseTab(TabData tabData) {

        for(TabView t: tabs) {
            if(!t.getTabData().getId().equals(tabData.getId()))
                t.onPause();
        }

        TabView chosen = getTabById(tabData.getId());

        if(chosen != null) {
            setCurrent(chosen, false);
        }
    }

    public void onCloseTab(TabData tabData) {

        TabView chosen = getTabById(tabData.getId());
        String parentId = null;

        if(chosen != null) {
            parentId = chosen.getTabData().getParentId();
            chosen.onDestroy();
            tabs.remove(chosen);
            uiController.onTabClosed(null);
        }

        if(!TextUtils.isEmpty(parentId)) {

            TabView parent = getTabById(parentId);

            if(parent == null) {
                _closeTab();
            } else {
                setCurrent(getTabById(parentId), false);
            }

            return;
        }

        _closeTab();
    }

    private void  _closeTab() {
        if(tabsCount() > 0) {
            //load first tab
            setCurrent(tabs.get(tabsCount() - 1), false);
        } else {
            uiController.newTab();
        }
    }

    public TabView getTabById(String tabId) {

        TabView chosen = null;

        for (TabView t: tabs) {
            if(t.getTabData().getId().equals(tabId)) {
                chosen = t;
                break;
            }
        }

        return chosen;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }
}
