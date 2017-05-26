package com.browser.browser.uictrl;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.ValueCallback;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.browser.browser.CPM;
import com.browser.browser.IntentManager;
import com.browser.mic.VoiceRecognition;
import com.browser.odm.ODM;
import com.browser.tab.TabData;
import com.browser.tab.TabView;
import com.browser.tab.TabsController;
import com.browser.ui.MousePad;
import com.browser.ui.bottom.BottomPart;
import com.browser.ui.home.HomeView;
import com.browser.dial.DialItems;
import com.browser.theme.themes.ThemeListener;

import java.util.ArrayList;

/**
 * Created by ozgur on 03.08.2016.
 */
public interface UIController extends UIKeyboard, UIFind, UILifeCycle, UIConfiguration, VoiceRecognition.VoiceRecogListener, ThemeListener {

    /* misc */
    Activity getActivity();

    /* actions */
    boolean onBackPressed();
    boolean isKeyboardOpen ();

    /* ui update */
    void onNewTabAdded(TabView tabView);
    void onPageFinished(TabView tabView);
    void onPageProgress(TabView tabView);
    void onPageStarted(TabView tabView);
    void onReceivedTitle(TabView tabView);
    void onReceivedIcon(TabView tabView);
    void onTabChosen(TabView tabView);
    void onTabClosed(TabView tabView);
    void onShowHome(TabView tabView);
    void onHideHome(TabView tabView);

    /* ui objects */
    FrameLayout getTabContainer();
    RelativeLayout getRootLayout();
    HomeView getHomeView();
    TabView getTab();
    BottomPart getBottomPart();
    MousePad getMousePad();

    /**/
    void onBottomMenuEvent(int resId);
    void onSecondBottomMenuEvent(int resId);

    /**/
    void reloadCurrentTab();
    void reloadAllTabs();

    /**/
    void newTab();
    void newIncognitoTab();
    String addTab(String url, boolean isIncognito, boolean inBackground, boolean closeWhenBackTab, boolean showAlert);
    void loadUrl(String url, boolean inCurrentTab);
    void search(String txt);

    /**/
    DialogManager getDialogManager();
    ODM getODM();
    DialItems getDialItems();
    CPM getCpm();
    IntentManager getIntentManager();
    TabsController getTabsCtrl();

    /**/
    ArrayList<TabData> getTabsData();
    void showTabsList();
    void hideTabsList();

    String getString(int resId);

    void onChooseTab(TabData tabData);
    void onCloseTab(TabData tabData);

    void enterDialEditMode();
    void exitDialEditMode();

    void onExitQueryMode();
    void onEnterQueryMode();
    boolean isEnterQueryMode();

    void showSuggestionList();
    void hideSuggestionList();
    void updateSuggestionList(String txt);

    void expandUrlbar();
    void collapseUrlbar();

    void enterVoiceRecogMode();
    void exitVoiceRecogMode();
    boolean isVoiceRecogMode();

    void toast(int resId);

    TabView getTabById(String tabId);

    void copyToClipboard(String url);
    void share(String url);
    void bookmark(String url, String title);
    void startAct(Class c);
    boolean isStartedSettings();

    boolean isDesktopMode();
    void setDesktopMode();
    void setPhoneMode();

    void showToolsMenu();
    void hideToolsMenu();

    void setExitedManual(boolean state);
    void exitManual();
    boolean isExitedManual();

    void openFileChooser(ValueCallback<Uri> uploadMsg);
    void showFileChooser(ValueCallback<Uri[]> filePathCallback);
    void onActivityResult(int requestCode, int resultCode, Intent intent);
}
