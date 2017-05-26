package com.browser.browser.uictrl;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.browser.Constants;
import com.browser.tab.TabView;

import java.util.ArrayList;

/**
 * Created by ozgur on 11.08.2016.
 */
public class UIPrefListener implements UILifeCycle, SharedPreferences.OnSharedPreferenceChangeListener {

    private final UIController uiController;

    public UIPrefListener(UIController uiController) {
        this.uiController = uiController;
    }

    @Override
    public void onResume() {
        PreferenceManager.getDefaultSharedPreferences(uiController.getActivity()).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {

        if(uiController.isStartedSettings()) {
            return;
        }

        PreferenceManager.getDefaultSharedPreferences(uiController.getActivity()).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case Constants.Preference.BLOCK_IMAGE:
            case Constants.Preference.BLOCK_JS:
            case Constants.Preference.ENABLE_COOKIES:
            case Constants.Preference.TEXT_SIZE: initPrefs(); break;
            case Constants.Preference.MOUSE_PAD: mousePad(); break;
            case Constants.Preference.PREF_DESKTOP_MODE: desktopMode(); break;
            case Constants.Preference.PREF_SHOW_LOGO: uiController.getHomeView().updateIcon(); break;
        }
    }

    private void desktopMode() {

        if(uiController.getCpm().isDesktopMode()) {
            uiController.getTabsCtrl().currentTab().setDesktopMode();
        } else {
            uiController.getTabsCtrl().currentTab().setPhoneMode();
        }

        uiController.reloadCurrentTab();
    }

    private void mousePad() {
        if(uiController.getCpm().isShowMousePad()) {
            uiController.getMousePad().show();
        } else {
            uiController.getMousePad().hide();
        }
    }

    private void initPrefs() {
        ArrayList<TabView> tabs = uiController.getTabsCtrl().getTabs();

        for (TabView t: tabs) {
            t.getWebView().loadPreferences();
        }
    }

}
