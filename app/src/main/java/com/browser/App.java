package com.browser;

import android.app.Application;
import android.content.Intent;

import com.browser.adblock.AdBlockManager;
import com.browser.browser.IntentManager;
import com.browser.js.JsManager;
import com.browser.odm.ODM;
import com.browser.theme.themes.ThemeController;

/**
 * Created by ozgur on 04.08.2016.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ThemeController.getInstance().init(this);
        JsManager.getInstance().init(this);
        ODM.getInstance().init(this);
        AdBlockManager.getInstance().init(this);
    }

    public void loadUrl(String url, boolean inCurrentTab) {
        Intent intent = new Intent(IntentManager.LOAD_URL);
        intent.putExtra("url", url);
        intent.putExtra("inCurrentTab", inCurrentTab);
        sendBroadcast(intent);
    }

}
