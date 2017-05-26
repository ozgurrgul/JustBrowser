package com.browser.browser.uictrl;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.speech.SpeechRecognizer;
import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.browser.Constants;
import com.browser.R;
import com.browser.bookmark.BookmarkDb;
import com.browser.bookmark.BookmarksActivity;
import com.browser.browser.CPM;
import com.browser.browser.IntentManager;
import com.browser.browser.MainActivity;
import com.browser.browser.TabRestoring;
import com.browser.dial.DialItems;
import com.browser.history.HistoryActivity;
import com.browser.mic.MicrophoneBigAnim;
import com.browser.mic.VoiceRecognition;
import com.browser.odm.ODM;
import com.browser.odm.ui.DownloadsActivity;
import com.browser.pro.ProChecker;
import com.browser.settings.SettingsActivity;
import com.browser.suggestion.SuggestionList;
import com.browser.tab.TabData;
import com.browser.tab.TabView;
import com.browser.tab.TabsController;
import com.browser.tab.TabsListView;
import com.browser.theme.ThemesActivity;
import com.browser.ui.FindInPageView;
import com.browser.ui.MousePad;
import com.browser.ui.bottom.BottomPart;
import com.browser.ui.home.HomeView;
import com.browser.theme.themes.NightTheme;
import com.browser.theme.themes.ThemeController;
import com.browser.theme.themes.ThemeModel;
import com.browser.utils.Utils;

import java.util.ArrayList;
import java.util.logging.Handler;

/**
 * Created by ozgur on 03.08.2016.
 */
public class UIControllerImpl extends UIFileChooserImpl {

    private FrameLayout tabContainer;
    private RelativeLayout rootLayout;
    private BottomPart bottomPart;
    private HomeView homeView;
    private TabsController tabsCtrl;
    private TabsListView tabsListView;
    private SuggestionList suggestionList;
    private CPM cpm;
    private boolean isKeyboardOpen;
    private DialogManager dialogManager;
    private ODM odm;
    private DialItems dialItems;
    private boolean enterQueryMode;
    private VoiceRecognition voiceRecognition;
    private MicrophoneBigAnim microphoneBigAnim;
    private FindInPageView findInPageView;
    private IntentManager intentManager;
    private UIPrefListener uiPrefListener;
    private boolean startedSettings;
    private ProChecker proChecker;
    private UIDownloadReceiver uiDownloadReceiver;
    private TabRestoring tabRestoring;
    private boolean exitedManual;
    //private VersionChecker versionChecker;
    private MousePad mousePad;

    public UIControllerImpl(MainActivity _activity) {
        super(_activity);

        /* views */
        cpm = new CPM(activity);
        bottomPart = (BottomPart) activity.findViewById(R.id.BottomPart);
        tabContainer = (FrameLayout) activity.findViewById(R.id.TabContainer);
        rootLayout = (RelativeLayout) activity.findViewById(R.id.RootLayout);
        homeView = (HomeView) activity.findViewById(R.id.HomeView);
        tabsListView = (TabsListView) activity.findViewById(R.id.TabsListView);
        suggestionList = (SuggestionList) activity.findViewById(R.id.SuggestionList);
        microphoneBigAnim = (MicrophoneBigAnim) activity.findViewById(R.id.MicrophoneBigAnim);
        findInPageView = (FindInPageView) activity.findViewById(R.id.FindInPage);
        mousePad = (MousePad) activity.findViewById(R.id.MousePad);
        tabsListView.setUiController(this);
        bottomPart.setUiController(this);
        suggestionList.setUiController(this);
        microphoneBigAnim.setUiController(this);
        mousePad.setUiController(this, (ImageView) activity.findViewById(R.id.MouseIcon), bottomPart.getHeight());
        findInPageView.setFindCtrl(this);

        /* ctrls */
        dialogManager = new DialogManager(this);
        tabsCtrl = new TabsController(this);
        odm = ODM.getInstance();
        dialItems = new DialItems(activity);
        intentManager = new IntentManager(this);
        uiPrefListener = new UIPrefListener(this);
        proChecker = new ProChecker(this);
        uiDownloadReceiver = new UIDownloadReceiver(this);
        tabRestoring = new TabRestoring(this, tabsCtrl);
        //versionChecker = new VersionChecker(this);

        //after dialItems
        homeView.setUiController(this);

        //
        voiceRecognition = new VoiceRecognition(activity, this);

        //
        ThemeController.getInstance().register(this);
        changeTheme();

        Intent intent = activity.getIntent();

        if(intent != null && !TextUtils.isEmpty(intent.getDataString())) {
            intentManager.handleIntent(intent);
        } else {
            newTab();
        }

        //check mouse pad
        if(cpm.isShowMousePad()) {
            mousePad.show();
        }

        cpm.increaseTotalOpenCount();

        /* check if he is not pro and he likes the app */
        if(!proChecker.isPro() && cpm.getTotalOpenCount() % 30 == 0) {
            dialogManager.showBeProDialog2();
        }

        /**/
        if(!cpm.isSecondMenuRemindDialogShown()) {
            dialogManager.showSecondMenuRemindDialog();
            cpm.setSecondMenuRemindDialogShown();
        }

    }

    @Override
    public boolean onBackPressed() {
        return findInPageView.onBackPressed() ||
                microphoneBigAnim.onBackPressed() ||
                homeView.onBackPressed() ||
                tabsListView.onBackPressed() ||
                bottomPart.onBackPressed() ||
                tabsCtrl.currentTab().onBackPressed();
    }

    @Override
    public Activity getActivity() {
        return activity;
    }

    @Override
    public void onNewTabAdded(TabView tabView) {
        bottomPart.setTabsCount(tabsCtrl.tabsCount());
        tabsListView.onNewTabAdded(tabView);
        tabsListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideTabsList();
            }
        }, 250);
        cpm.increaseTotalTabCount();

        if(tabsCtrl.tabsCount() >= Constants.MAXIMUM_TAB_COUNT && !proChecker.isPro())  {
            dialogManager.showBeProDialog();
        }
    }

    @Override
    public void onPageFinished(TabView tabView) {
        bottomPart.onPageFinished(tabView);
        tabsListView.onPageFinished(tabView);
    }

    @Override
    public void onPageProgress(TabView tabView) {
        bottomPart.onPageProgress(tabView);
    }

    @Override
    public void onPageStarted(TabView tabView) {
        bottomPart.onPageStarted(tabView);
        tabsListView.onPageStarted(tabView);
    }

    @Override
    public void onReceivedTitle(TabView tabView) {
        tabsListView.onReceivedTitle(tabView);
    }

    @Override
    public void onReceivedIcon(TabView tabView) {
        tabsListView.onReceivedIcon(tabView);
    }

    @Override
    public void onTabChosen(TabView tabView) {
        tabsListView.onTabChosen(tabView);
    }

    @Override
    public void onTabClosed(TabView tabView) {
        tabsListView.onTabClosed();
        bottomPart.setTabsCount(tabsCtrl.tabsCount());
    }

    @Override
    public void onShowHome(TabView tabView) {
        bottomPart.onShowHome(tabView);
        bottomPart.hideProgress();
        tabsListView.onShowHome(tabView);
    }

    @Override
    public void onHideHome(TabView tabView) {
        bottomPart.onHideHome(tabView);
        bottomPart.showProgress();
        bottomPart.onPageProgress(tabView);
        tabsListView.onHideHome(tabView);
    }

    @Override
    public FrameLayout getTabContainer() {
        return tabContainer;
    }

    @Override
    public RelativeLayout getRootLayout() {
        return rootLayout;
    }

    @Override
    public HomeView getHomeView() {
        return homeView;
    }

    @Override
    public TabView getTab() {
        return tabsCtrl.currentTab();
    }

    @Override
    public BottomPart getBottomPart() {
        return bottomPart;
    }

    @Override
    public MousePad getMousePad() {
        return mousePad;
    }

    @Override
    public void onBottomMenuEvent(int resId) {
        switch (resId) {
            case R.id.BottomMenuBack: tabsCtrl.currentTab().onBackPressed(); break;
            case R.id.BottomMenuForward: tabsCtrl.currentTab().onForwardPressed(); break;
            case R.id.BottomMenuHome: tabsCtrl.currentTab().showHome(); break;
            case R.id.BottomMenuMic: enterVoiceRecogMode(); break;
            case R.id.ButtonShowTabs: handleTabsList(); break;
        }
    }

    @Override
    public void onSecondBottomMenuEvent(int resId) {
        switch (resId) {
            case R.id.BottomSecMenuNightMode: handleNightMode(); break;
            case R.id.BottomSecMenuHistory: startAct(HistoryActivity.class); break;
            case R.id.BottomSecMenuDownloads: startAct(DownloadsActivity.class); break;
            case R.id.BottomSecBookmarks: startAct(BookmarksActivity.class); break;
            case R.id.BottomSecMenuTheme: startAct(ThemesActivity.class); break;
            case R.id.BottomSecMenuSettings: startAct(SettingsActivity.class); break;
        }
    }

    @Override
    public void reloadCurrentTab() {
        tabsCtrl.currentTab().reload();
    }

    @Override
    public void reloadAllTabs() {
        for (TabView tabView: tabsCtrl.getTabs()) {
            tabView.reload();
        }
    }

    @Override
    public void newTab() {
        addTab(Constants.WEBVIEW_HOME, false, false, false, false);
    }

    @Override
    public void newIncognitoTab() {
        addTab(Constants.WEBVIEW_HOME, true, false, false, false);
    }

    @Override
    public String addTab(String url, boolean isIncognito, boolean inBackground, boolean closeWhenBackTab, boolean showAlert) {
        return tabsCtrl.addTab(url, isIncognito, inBackground, false, showAlert);
    }

    @Override
    public void loadUrl(String url, boolean inCurrentTab) {
        if(inCurrentTab) {
            tabsCtrl.currentTab().query(url);
        } else {
            addTab(url, false, true, false, false);
        }
    }

    @Override
    public void search(String txt) {

        if(TextUtils.isEmpty(txt))
            return;

        if(Utils.isValidUrl(txt)) {
            loadUrl(txt, true);
        } else {
            tabsCtrl.currentTab().query(cpm.getSearchEngineUrl() + txt);
        }
    }

    @Override
    public DialogManager getDialogManager() {
        return dialogManager;
    }

    @Override
    public ODM getODM() {
        return odm;
    }

    @Override
    public DialItems getDialItems() {
        return dialItems;
    }

    @Override
    public CPM getCpm() {
        return cpm;
    }

    @Override
    public IntentManager getIntentManager() {
        return intentManager;
    }

    @Override
    public TabsController getTabsCtrl() {
        return tabsCtrl;
    }

    @Override
    public ArrayList<TabData> getTabsData() {
        return tabsCtrl.getTabsData();
    }

    @Override
    public void showTabsList() {
        tabsListView.show();
    }

    @Override
    public void hideTabsList() {
        tabsListView.hide();
    }

    @Override
    public String getString(int resId) {
        return activity.getString(resId);
    }

    @Override
    public void onChooseTab(TabData tabData) {
        tabsCtrl.onChooseTab(tabData);
        hideTabsList();
    }

    @Override
    public void onCloseTab(TabData tabData) {
        tabsCtrl.onCloseTab(tabData);
    }

    @Override
    public void enterDialEditMode() {
        bottomPart.hide();
        homeView.enterDialEditMode();
    }

    @Override
    public void exitDialEditMode() {
        bottomPart.show();
        homeView.exitDialEditMode();
    }

    @Override
    public void onExitQueryMode() {

        if(!enterQueryMode) {
            return;
        }

        enterQueryMode = false;
        bottomPart.unlockPager();
        hideSuggestionList();
        collapseUrlbar();
    }

    @Override
    public void onEnterQueryMode() {

        if(enterQueryMode) {
            return;
        }

        enterQueryMode = true;
        showSuggestionList();
        bottomPart.postDelayed(new Runnable() {
            @Override
            public void run() {
                bottomPart.lockPager();
            }
        }, 100);
        //expandUrlbar();
    }

    @Override
    public boolean isEnterQueryMode() {
        return enterQueryMode;
    }

    @Override
    public void showSuggestionList() {
        suggestionList.show();
    }

    @Override
    public void hideSuggestionList() {
        suggestionList.hide();
    }

    @Override
    public void updateSuggestionList(String txt) {
        suggestionList.updateSuggestionList(txt);
    }

    @Override
    public void expandUrlbar() {
        bottomPart.bottomFirstMenuAndUrlBar.urlbar.expand();
    }

    @Override
    public void collapseUrlbar() {
        bottomPart.bottomFirstMenuAndUrlBar.urlbar.collapse();
    }

    @Override
    public void enterVoiceRecogMode() {
        voiceRecognition.startListen();
        microphoneBigAnim.show();
    }

    @Override
    public void exitVoiceRecogMode() {
        voiceRecognition.stop();
        microphoneBigAnim.hide();
    }

    @Override
    public boolean isVoiceRecogMode() {
        return false;
    }

    @Override
    public void handleFindDialog() {

        if(getTab().isHome()) {
            toast(R.string.not_works_in_home);
            return;
        }

        dialogManager.findDialog();
    }

    @Override
    public void enterFindMode() {
        bottomPart.hide();
        findInPageView.show();
    }

    @Override
    public void exitFindMode() {
        bottomPart.show();
        findInPageView.hide();
        getTab().getWebView().clearMatches();
    }

    @Override
    public void findInPage(String txt) {
        getTab().getWebView().findAllAsync(txt);
    }

    @Override
    public void findNext() {
        getTab().getWebView().findNext(true);
    }

    @Override
    public void findPrevious() {
        getTab().getWebView().findNext(false);
    }

    @Override
    public void toast(int resId) {
        Toast.makeText(activity, resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public TabView getTabById(String tabId) {
        return tabsCtrl.getTabById(tabId);
    }

    @Override
    public void copyToClipboard(String url) {

        if(getTab().isHome()) {
            toast(R.string.not_works_in_home);
            return;
        }

        Utils.copyToClipboard(activity, url);
        toast(R.string.copied);
    }

    @Override
    public void share(String url) {

        if(getTab().isHome()) {
            toast(R.string.not_works_in_home);
            return;
        }

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, "Share");
        i.putExtra(Intent.EXTRA_TEXT, url);
        getActivity().startActivity(Intent.createChooser(i, "Share"));
    }

    @Override
    public void bookmark(String url, String title) {

        if(getTab().isHome()) {
            toast(R.string.not_works_in_home);
            return;
        }

        if(TextUtils.isEmpty(url) || TextUtils.isEmpty(title)) {
            return;
        }

        BookmarkDb.getInstance(activity).save(url, title);
        toast(R.string.added);
    }

    @Override
    public void showToolsMenu() {
        dialogManager.showToolsMenu();
    }

    @Override
    public void hideToolsMenu() {}

    @Override
    public void setExitedManual(boolean state) {
        exitedManual = state;
    }

    @Override
    public void exitManual() {
        setExitedManual(true);
        activity.finish();
    }

    @Override
    public boolean isExitedManual() {
        return exitedManual;
    }

    @Override
    public void _onConfigurationChanged(Configuration newConfig) {
        homeView._onConfigurationChanged(newConfig);
    }

    @Override
    public void onKeyboardOpen() {
        isKeyboardOpen = true;
        bottomPart.onKeyboardOpen();
    }

    @Override
    public void onKeyboardHidden() {
        isKeyboardOpen = false;
        bottomPart.onKeyboardHidden();
    }

    @Override
    public boolean isKeyboardOpen() {
        return isKeyboardOpen;
    }

    private void handleNightMode() {
        if(isNightMode()) {
            ThemeController.getInstance().changeTheme(cpm.getLastTheme());
        } else {
            ThemeController.getInstance().changeTheme(NightTheme.NAME);
        }
    }

    private void handleTabsList() {
        if(tabsListView.isShown()) {
            hideTabsList();
        } else {
            showTabsList();
        }
    }

    public boolean isNightMode() {
        return ThemeController.getInstance().isNightMode();
    }

    @Override
    public void startAct(Class _class) {
        if(_class == SettingsActivity.class) {
            startedSettings = true;
        }
        activity.startActivity(new Intent(activity, _class));
    }

    @Override
    public boolean isStartedSettings() {
        return startedSettings;
    }

    @Override
    public boolean isDesktopMode() {
        return getCpm().isDesktopMode();
    }

    @Override
    public void setDesktopMode() {
        getCpm().setDesktopMode();
        // ui updating from UIPrefLıstener
    }

    @Override
    public void setPhoneMode() {
        getCpm().setPhoneMode();
        // ui updating from UIPrefLıstener
    }

    @Override
    public void onVoiceRecogError(int errNo) {

        if(errNo == SpeechRecognizer.ERROR_NETWORK) {
            toast(R.string.error_voice_recog);
            return;
        }

        microphoneBigAnim.hide();
        voiceRecognition.stop();
    }

    @Override
    public void onVoiceRecogStarted() {
        microphoneBigAnim.updateVoiceResult(getString(R.string.speak_now));
    }

    @Override
    public void onVoiceRecogFinished(String txt) {
        voiceRecognition.stop();

        if(TextUtils.isEmpty(txt)) {
            return;
        }

        bottomPart.bottomFirstMenuAndUrlBar.urlbar.updateEditText(txt);

        search(txt);

        microphoneBigAnim.updateVoiceResult(txt);
        microphoneBigAnim.postDelayed(new Runnable() {
            @Override
            public void run() {
                microphoneBigAnim.hide();
            }
        }, 100);
        bottomPart.hide();
    }

    @Override
    public void onVoiceRecogPartial(String partial) {
        bottomPart.bottomFirstMenuAndUrlBar.urlbar.updateEditText(partial);
        microphoneBigAnim.updateVoiceResult(partial);
    }

    @Override
    public void onVoiceRecogReady() {}

    @Override
    public void themeChanged() {
        changeTheme();
    }

    @Override
    public void changeTheme() {
        ThemeModel t = ThemeController.getInstance().getCurrentTheme();
        rootLayout.setBackgroundColor(t.actBg);
    }

    @Override
    public void onDestroy() {
        tabsCtrl.onDestroy();
        odm.onDestroy();
        proChecker.onDestroy();
        uiDownloadReceiver.onDestroy();
    }

    @Override
    public void onResume() {
        startedSettings = false;
        tabsCtrl.onResume();
        uiPrefListener.onResume();
        proChecker.onResume();
        uiDownloadReceiver.onResume();
    }

    @Override
    public void onPause() {
        tabsCtrl.onPause();
        uiPrefListener.onPause();
        proChecker.onPause();
        uiDownloadReceiver.onPause();
        tabRestoring.onPause();
        //versionChecker.onPause();
    }
}
