package com.browser.tab.component;

import android.annotation.TargetApi;
import android.os.Build;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.browser.adblock.AdBlockManager;
import com.browser.tab.TabView;

import java.io.ByteArrayInputStream;

/**
 * Created by ozgur on 06.08.2016.
 */
public class JustAdBlockWebClient extends WebViewClient {

    protected final TabView tabView;

    public JustAdBlockWebClient(TabView tabView) {
        this.tabView = tabView;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {

        if(tabView.getTabData().isDestroying()) return empty();
        if (AdBlockManager.getInstance().isBlocked(request.getUrl().toString(), tabView.getTabData().getUrl()))return empty();

        return super.shouldInterceptRequest(view, request);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {

        if(tabView.getTabData().isDestroying()) return empty();
        if (AdBlockManager.getInstance().isBlocked(url, tabView.getTabData().getUrl()))return empty();

        return null;
    }

    private WebResourceResponse empty() {
        return new WebResourceResponse("text/plain", "utf-8", new ByteArrayInputStream("".getBytes()));
    }
}
