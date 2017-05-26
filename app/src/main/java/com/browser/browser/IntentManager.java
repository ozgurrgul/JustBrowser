package com.browser.browser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.browser.Constants;
import com.browser.browser.uictrl.UIController;
import com.browser.browser.uictrl.UILifeCycle;
import com.browser.utils.Utils;

/**
 * Created by ozgur on 11.08.2016.
 */
public class IntentManager implements UILifeCycle {

    public static final String LOAD_URL = "loadurl";

    private final UIController uiController;
    private BroadcastReceiver urlReceiver;

    public IntentManager(final UIController uiController) {
        this.uiController = uiController;

        urlReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String url = intent.getStringExtra("url");
                boolean inCurrentTab = intent.getBooleanExtra("inCurrentTab", false);

                if(Utils.isValidUrl(url)) {
                    uiController.loadUrl(url, inCurrentTab);
                } else {
                    uiController.search(url);
                }

            }
        };

        uiController.getActivity().registerReceiver(urlReceiver, new IntentFilter(LOAD_URL));
    }

    public void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    public void handleIntent(Intent intent) {

        final String url;

        if (intent != null) {

            url = intent.getDataString();

            int num = 0;

            if (intent.getExtras() != null) {
                num = intent.getExtras().getInt(Constants.URL_INTENT_ORIGIN_SELF);
            }

            if(num == 1) {
                //redirected from WebClient
                uiController.loadUrl(url, true);
                return;
            }

        } else {
            url = Constants.WEBVIEW_HOME;
        }

        if(Utils.isValidUrl(url)) {
            uiController.addTab(url, false, false, true, false);
        } else {
            uiController.search(url);
        }

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {
        //do not unregister onPause because we need it
    }

    @Override
    public void onDestroy() {
        if(urlReceiver != null && uiController != null) {
            uiController.getActivity().unregisterReceiver(urlReceiver);
        }
    }
}
