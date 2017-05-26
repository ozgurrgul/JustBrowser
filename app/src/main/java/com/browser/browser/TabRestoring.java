package com.browser.browser;

import android.os.Bundle;
import android.os.Parcel;
import android.webkit.WebBackForwardList;

import com.browser.tab.TabView;
import com.browser.tab.TabsController;
import com.browser.browser.uictrl.UIController;
import com.browser.browser.uictrl.UILifeCycle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by ozgur on 12.08.2016.
 */
public class TabRestoring implements UILifeCycle {

    private final UIController uiController;
    private final TabsController tabsController;
    private static final String BUNDLE_KEY = "W_";
    private static final String BUNDLE_STORAGE = "tabs.parcel";
    private File filesDir;

    public TabRestoring (UIController uiController, TabsController tabsController) {

        this.uiController = uiController;
        this.tabsController = tabsController;
        this.filesDir = uiController.getActivity().getApplication().getFilesDir();

        read();
        empty();
    }

    private void read() {
        Bundle savedState = null;
        File inputFile = new File(filesDir, BUNDLE_STORAGE);
        FileInputStream inputStream = null;
        try {
            //noinspection IOResourceOpenedButNotSafelyClosed
            inputStream = new FileInputStream(inputFile);
            Parcel parcel = Parcel.obtain();
            byte[] data = new byte[(int) inputStream.getChannel().size()];

            //noinspection ResultOfMethodCallIgnored
            inputStream.read(data, 0, data.length);
            parcel.unmarshall(data, 0, data.length);
            parcel.setDataPosition(0);
            savedState = parcel.readBundle(ClassLoader.getSystemClassLoader());
            savedState.putAll(savedState);
            parcel.recycle();
        }
        catch (Exception ignored) {}
        finally {
            inputFile.delete();
            try {  inputStream.close(); } catch (Exception ignored) { }
        }

        if(savedState == null) {
            return;
        }

        for (String key : savedState.keySet()) {
            if (key.startsWith(BUNDLE_KEY)) {
                String tabId = tabsController.addTab(null, false, true, false, false);
                tabsController.getTabById(tabId).getWebView().restoreState(savedState.getBundle(key));
                tabsController.getTabById(tabId).hideHome();
            }
        }
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {
        empty();

        if(!uiController.isExitedManual()) {
            write();
        }
    }

    private void write() {
        final Bundle outState = new Bundle(ClassLoader.getSystemClassLoader());
        int n = 0;

        //write
        for (TabView t: tabsController.getTabs()) {

            Bundle state = new Bundle(ClassLoader.getSystemClassLoader());

            if (t.getWebView() != null && !t.getTabData().isIncognito() && !t.isHome()) {

                WebBackForwardList mList = t.getWebView().saveState(state);

                if(mList != null) {
                    outState.putBundle(BUNDLE_KEY + n, state);
                    n++;
                }

            }
        }

        //save
        new Thread(new Runnable() {
            @Override
            public void run() {
                File outputFile = new File(filesDir, BUNDLE_STORAGE);
                FileOutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(outputFile);
                    Parcel parcel = Parcel.obtain();
                    parcel.writeBundle(outState);
                    outputStream.write(parcel.marshall());
                    outputStream.flush();
                    parcel.recycle();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {outputStream.close();} catch (Exception e) {}
                }
            }
        }).start();
    }

    private void empty() {
        //delete
        new Thread(new Runnable() {
            @Override
            public void run() {
                File outputFile = new File(filesDir, BUNDLE_STORAGE);
                if (outputFile.exists()) {
                    outputFile.delete();
                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {}
}
