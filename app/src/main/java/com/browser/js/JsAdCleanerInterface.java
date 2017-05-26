package com.browser.js;

import android.util.Log;
import android.webkit.JavascriptInterface;

import com.browser.adblock.AdBlockDb;
import com.browser.tab.TabView;

/**
 * Created by ozgur on 25.08.2016.
 */
public class JsAdCleanerInterface {

    private TabView tabView;

    public JsAdCleanerInterface(TabView tabView) {
        this.tabView = tabView;
    }

    @JavascriptInterface
    public void divsHidden(String url, int amount) {
        Log.d("Asdhsadn", "divsHidden222: " + amount);
        AdBlockDb.getInstance(tabView.getUiController().getActivity()).increaseAdBlockCount(url, amount);
    }

}
