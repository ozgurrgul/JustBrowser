package com.browser.js;

import android.content.Context;

import java.util.HashMap;

/**
 * Created by ozgur on 20.06.2016.
 */
public class JsManager {

    private static JsManager ourInstance = new JsManager();
    public static JsManager getInstance() {
        return ourInstance;
    }

    private HashMap<Class, BaseJs> objects = new HashMap<Class, BaseJs>();

    private JsManager() {}

    public void init(Context context) {
        objects.put(JsDayMode.class, new JsDayMode(context));
        objects.put(JsNightMode.class, new JsNightMode(context));
        objects.put(JsAdCleaner.class, new JsAdCleaner(context));
    }

    public BaseJs getJs(Class c) {
        return objects.get(c);
    }
}
