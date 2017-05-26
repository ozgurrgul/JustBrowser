package com.browser.tab.component;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Message;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;

import com.browser.history.HistoryDb;
import com.browser.js.JsAdCleaner;
import com.browser.js.JsManager;
import com.browser.tab.TabView;

/**
 * Created by ozgur on 03.08.2016.
 */
public class JustWebClient extends JustShouldOverrideWebClient {

    public JustWebClient(TabView tabView) {
        super(tabView);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        if(tabView.getTabData().isDestroying() || tabView.isHome()) return;
        tabView.getTabData().setUrl(url);
        tabView.isOnceOpened = true;
        tabView.getUiController().onPageStarted(tabView);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if(tabView.getTabData().isDestroying() || tabView.isHome()) return;
        tabView.changeTheme();
        tabView.getTabData().setUrl(url);
        tabView.getTabData().setTitle(view.getTitle());
        tabView.getUiController().onPageFinished(tabView);

        if(!tabView.getTabData().isIncognito()) HistoryDb.getInstance(view.getContext()).save(url, tabView.getTabData().getTitle());
        if(tabView.getUiController().getCpm().isAdBlockEnabled()){ view.loadUrl(JsManager.getInstance().getJs(JsAdCleaner.class).getCode()); }
    }

    @Override
    public void onFormResubmission(WebView view, Message dontResend, Message resend){
        if(tabView.getTabData().isDestroying() || tabView.isHome()) return;
        tabView.getUiController().getDialogManager().onFormResubmission(dontResend, resend);
    }

    @Override
    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
        if(tabView.getTabData().isDestroying() || tabView.isHome()) return;
        tabView.getUiController().getDialogManager().onReceivedHttpRequest(handler);
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        if(tabView.getTabData().isDestroying() || tabView.isHome()) return;
        tabView.getUiController().getDialogManager().onReceivedSslError(handler, error);
    }



}
