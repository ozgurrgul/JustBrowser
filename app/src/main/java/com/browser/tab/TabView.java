package com.browser.tab;

import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.browser.Constants;
import com.browser.R;
import com.browser.js.JsAdCleanerInterface;
import com.browser.js.JsDayMode;
import com.browser.js.JsManager;
import com.browser.js.JsNightMode;
import com.browser.tab.component.JustChromeClient;
import com.browser.tab.component.JustWebClient;
import com.browser.tab.component.JustWebView;
import com.browser.theme.themes.NightTheme;
import com.browser.theme.themes.ThemeController;
import com.browser.theme.themes.ThemeListener;
import com.browser.theme.themes.ThemeModel;
import com.browser.ui.widget.JView;
import com.browser.browser.uictrl.UIController;

import java.lang.ref.WeakReference;

/**
 * Created by ozgur on 03.08.2016.
 */
public class TabView extends LinearLayout implements JView, DownloadListener, ThemeListener {

    private FrameLayout mContentFrame;
    private TabData tabData;
    private JustWebView mWebView;
    private JustChromeClient justChromeClient;
    private JustWebClient justWebClient;
    private UIController uiController;
    private boolean isHome;
    public boolean isOnceOpened = false; //check if an url is opened at least once, in CustomWebClient, for forward button

    //for long press
    private WebViewHandler mWebViewHandler = new WebViewHandler(this);
    private GestureDetector mGestureDetector;


    public TabView(UIController uiController, TabData tabData) {
        super(uiController.getActivity());
        this.uiController = uiController;
        this.tabData = tabData;
        _init();
    }

    private void _init() {
        View wrapper = inflate(getContext(), R.layout.tab, this);
        mContentFrame = (FrameLayout) wrapper.findViewById(R.id.ContentFrame);
        justChromeClient = new JustChromeClient(this);
        justWebClient = new JustWebClient(this);
        initWebView();
        ThemeController.getInstance().register(this);
        changeTheme();
    }

    private void initWebView() {
        mWebView = new JustWebView(this);
        mContentFrame.removeAllViews();
        mContentFrame.addView(mWebView);
        mWebView.setWebChromeClient(justChromeClient);
        mWebView.setWebViewClient(justWebClient);
        mWebView.setDownloadListener(this);
        mWebView.addJavascriptInterface(new JsAdCleanerInterface(this), "JB_ADCLNR");
        mGestureDetector = new GestureDetector(getContext(), new CustomGestureListener());
        mWebView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mGestureDetector.onTouchEvent(motionEvent);
                return false;
            }
        });
    }

    public void fresh() {
        initWebView();
        isOnceOpened = false;
    }

    public TabData getTabData() {
        return tabData;
    }

    public void onResume() {
        mWebView.onResume();
        mWebView.resumeTimers();
    }

    public void onPause() {
        mWebView.onPause();
        mWebView.pauseTimers();
    }

    public void onDestroy() {
        mWebView.destroy();
        tabData.setDestroying(true);
    }

    public void query(String url) {

        if(TextUtils.isEmpty(url)) {
            show();
            return;
        }

        if(!url.startsWith(Constants.HTTP) && !url.startsWith(Constants.HTTPS)) {
            if(Constants.WEBVIEW_HOME.equals(url)) {

            } else if(url.startsWith(Constants.FILE)) {
                //
            } else {
                url = Constants.HTTP + url;
            }
        }

        tabData.setUrl(url);

        if(Constants.WEBVIEW_HOME.equals(url)) {
            showHome();
        } else {

            if(!getTabData().isInBackground()) {
                hideHome();
            }
            mWebView.loadUrl(url);
        }

    }

    public void hideHome() {
        isHome = false;
        getTabData().setTitle(mWebView.getTitle());
        show();
        uiController.getHomeView().hide();
        uiController.onHideHome(this);
    }

    public void showHome() {
        isHome = true;
        getTabData().setTitle(Constants.WEBVIEW_HOME);
        hide();
        uiController.getHomeView().show();
        uiController.onShowHome(this);
    }

    @Override
    public void show() {
        setVisibility(VISIBLE);
    }

    @Override
    public void hide() {
        setVisibility(GONE);
    }

    @Override
    public boolean onBackPressed() {

        if(justChromeClient == null) {
            return true;
        }

        if(justChromeClient.onBackPressed()) {
            return true;
        }

        if(uiController.getHomeView().onBackPressed() && isHome()) {
            return true;
        }

        if(mWebView.canGoBack()) {
            stopLoading();
            mWebView.goBack();
            return true;
        } else {

            if(tabData.isCloseTabWhenBack()) {
                boolean exitAppIfLastTab = true;
                uiController.onCloseTab(getTabData());
                return true;
            }

            if(isHome) {
                return false;
            } else {
                showHome();
                return true;
            }
        }

    }

    public void onForwardPressed() {

        if(!isHome) {
            //if in home, he already has first page webView, (GONE)
            //so only forward when in not home
            mWebView.goForward();
        }

        hideHome();
    }

    public boolean canTabGoBack() {

        Log.d("TTTTA", "tabData.isCloseTabWhenBack:" + tabData.isCloseTabWhenBack());

        if(isHome) {
            return false;
        }

        if(mWebView == null) {
            return false;
        }

        if(mWebView.canGoBack()) {
            return true;
        }

        if(tabData.isCloseTabWhenBack()) {
            return false;
        }

        if(!isHome) { //if first page of webview and not in home, we will go to home
            return true;
        }

        return false;
    }

    public boolean canTabGoForward() {

        if(mWebView == null) {
            return false;
        }

        if(mWebView.canGoForward()) {
            return true;
        }

        return isOnceOpened && isHome;
    }

    public void stopLoading() {
        mWebView.stopLoading();
    }

    public void reload() {
        mWebView.reload();
    }

    public UIController getUiController () {
        return uiController;
    }

    public JustWebView getWebView() {
        return mWebView;
    }

    public boolean isHome() {
        return isHome;
    }

    @Override
    public void onDownloadStart(final String url, String userAgent, String contentDisposition, final String mimetype, final long contentLength) {
        uiController.getDialogManager().downloadDialog(url, contentLength);
    }

    @Override
    public void themeChanged() {
        changeTheme();
    }

    @Override
    public void changeTheme() {
        ThemeModel t = ThemeController.getInstance().getCurrentTheme();

        if(t instanceof NightTheme) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) mWebView.evaluateJavascript(JsManager.getInstance().getJs(JsNightMode.class).getPureCode(), null);
            else mWebView.loadUrl(JsManager.getInstance().getJs(JsNightMode.class).getCode());
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) mWebView.evaluateJavascript(JsManager.getInstance().getJs(JsDayMode.class).getPureCode(), null);
            else mWebView.loadUrl(JsManager.getInstance().getJs(JsDayMode.class).getCode());
        }

    }

    private class CustomGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        private boolean mCanTriggerLongPress = true;

        @Override
        public void onLongPress(MotionEvent e) {

            if (mCanTriggerLongPress) {
                Message msg = mWebViewHandler.obtainMessage();
                if (msg != null) {
                    msg.setTarget(mWebViewHandler);
                    if (mWebView == null) {
                        return;
                    }
                    mWebView.requestFocusNodeHref(msg);
                }
            }
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            mCanTriggerLongPress = false;
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            mCanTriggerLongPress = true;
        }
    }

    private static class WebViewHandler extends Handler {

        private final WeakReference<TabView> mReference;

        public WebViewHandler(TabView tab) {
            mReference = new WeakReference<>(tab);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final String url = msg.getData().getString("url");
            TabView tab = mReference.get();
            if (tab != null) {
                tab.longClickPage(url);
            }
        }
    }

    private void longClickPage(String url) {
        WebView.HitTestResult result = mWebView.getHitTestResult();

        if (url != null) {
            if (result != null) {
                if (result.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE || result.getType() == WebView.HitTestResult.IMAGE_TYPE) {
                    getUiController().getDialogManager().showTabLongPressImage(url);
                } else {
                    getUiController().getDialogManager().showTabLongPressUrl(url);
                }
            } else {
                getUiController().getDialogManager().showTabLongPressUrl(url);

            }
        } else if (result != null && result.getExtra() != null) {
            final String newUrl = result.getExtra();
            if (result.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE || result.getType() == WebView.HitTestResult.IMAGE_TYPE) {
                getUiController().getDialogManager().showTabLongPressImage(newUrl);
            } else {
                getUiController().getDialogManager().showTabLongPressUrl(url);
            }
        }
    }

    public void setDesktopMode() {
        if(mWebView != null) {
            mWebView.setDesktopMode();
        }
    }

    public void setPhoneMode() {
        if(mWebView != null) {
            mWebView.setPhoneMode();
        }
    }

}
