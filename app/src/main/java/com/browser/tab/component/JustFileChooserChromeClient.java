package com.browser.tab.component;

import android.net.Uri;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.browser.tab.TabView;

/**
 * Created by ozgur on 09.08.2016.
 */
public class JustFileChooserChromeClient extends VideoEnabledWebChromeClient {

    public JustFileChooserChromeClient(TabView tabView) {
        super(tabView);
    }

    /* file chooser stuff */
    @SuppressWarnings("unused")
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        tabView.getUiController().openFileChooser(uploadMsg);
    }

    @SuppressWarnings("unused")
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
        tabView.getUiController().openFileChooser(uploadMsg);
    }

    @SuppressWarnings("unused")
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        tabView.getUiController().openFileChooser(uploadMsg);
    }

    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                     WebChromeClient.FileChooserParams fileChooserParams) {
        tabView.getUiController().showFileChooser(filePathCallback);
        return true;
    }
}