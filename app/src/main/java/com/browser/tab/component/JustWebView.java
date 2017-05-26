package com.browser.tab.component;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.v4.util.ArrayMap;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;

import com.browser.Constants;
import com.browser.browser.CPM;
import com.browser.tab.TabData;
import com.browser.tab.TabView;

import java.util.Map;

/**
 * Created by ozgur on 03.08.2016.
 */
public class JustWebView extends VideoEnabledWebView {

    private final Map<String, String> mRequestHeaders = new ArrayMap<>();

    /**/
    public static final String HEADER_REQUESTED_WITH = "X-Requested-With";
    public static final String HEADER_WAP_PROFILE = "X-Wap-Profile";
    private static final String HEADER_DNT = "DNT";

    /**/
    private CPM cpm;
    private TabData tabData;

    public JustWebView(TabView tabView) {
        super(tabView.getContext());

        cpm = new CPM(getContext());
        tabData = tabView.getTabData();

        loadPreferences();
        initializeSettings(getSettings());
    }



    @Override
    public void loadUrl(final String url) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                loadUrl(url, mRequestHeaders);
            }
        }, 10);
    }

    @Override
    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        super.loadUrl(url, additionalHttpHeaders);
    }

    @SuppressLint({"NewApi", "SetJavaScriptEnabled"})
    public void loadPreferences() {

        WebSettings settings = getSettings();

        if(cpm.isDesktopMode()) {
            setDesktopMode();
        } else {
            setPhoneMode();
        }

        /*
        if (cpm.getDoNotTrackEnabled()) {
            mRequestHeaders.put(HEADER_DNT, "1");
        } else {
            mRequestHeaders.remove(HEADER_DNT);
        }
        */

        /*
        if (cpm.getRemoveIdentifyingHeadersEnabled()) {
            mRequestHeaders.put(HEADER_REQUESTED_WITH, "");
            mRequestHeaders.put(HEADER_WAP_PROFILE, "");
        } else {
            mRequestHeaders.remove(HEADER_REQUESTED_WITH);
            mRequestHeaders.remove(HEADER_WAP_PROFILE);
        }
        */

        settings.setDefaultTextEncodingName("UTF-8");

        if (!tabData.isIncognito()) {
            settings.setGeolocationEnabled(true);
        } else {
            settings.setGeolocationEnabled(false);
        }


        if (!tabData.isIncognito()) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                settings.setSavePassword(true);
            }
            //settings.setSaveFormData(true);
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                //noinspection deprecation
                settings.setSavePassword(false);
            }
            settings.setSaveFormData(false);
        }

        settings.setJavaScriptEnabled(!cpm.isBlockJavascript());
        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        if (cpm.getTextReflowEnabled()) {
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
            if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                try {
                    settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
                } catch (Exception ignored) { }
            }
        } else {
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }

        settings.setSupportMultipleWindows(true);
        settings.setUseWideViewPort(cpm.getUseWideViewportEnabled());
        settings.setLoadWithOverviewMode(cpm.isOverviewModeEnabled());

        setTextSize();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true);
        }

        /**/
        CookieManager cookieManager = CookieManager.getInstance();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(getContext());
        }

        cookieManager.setAcceptCookie(cpm.isEnableCookies());

        getSettings().setBlockNetworkImage(cpm.isBlockImages());
        getSettings().setLoadsImagesAutomatically(!cpm.isBlockImages());
    }

    public void setTextSize() {

        if(cpm == null) {
            cpm = new CPM(getContext());
        }

        switch (cpm.getTextSize()) {
            case 150: getSettings().setTextZoom(150);break;
            case 125: getSettings().setTextZoom(125);break;
            case 100: getSettings().setTextZoom(100);break;
            case 75: getSettings().setTextZoom(75);break;
            case 50: getSettings().setTextZoom(50);break;
        }
    }

    public void initializeSettings(WebSettings settings) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            //noinspection deprecation
            settings.setAppCacheMaxSize(Long.MAX_VALUE);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            //noinspection deprecation
            settings.setEnableSmoothTransition(true);
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                settings.setMediaPlaybackRequiresUserGesture(true);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !tabData.isIncognito()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                settings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // We're in Incognito mode, reject
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
            }
        }
        if (!tabData.isIncognito()) {
            settings.setDomStorageEnabled(true);
            settings.setAppCacheEnabled(true);
            settings.setCacheMode(WebSettings.LOAD_DEFAULT);
            settings.setDatabaseEnabled(true);
        } else {
            settings.setDomStorageEnabled(false);
            settings.setAppCacheEnabled(false);
            settings.setDatabaseEnabled(false);
            settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        }
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setAllowContentAccess(true);
        settings.setAllowFileAccess(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            settings.setAllowFileAccessFromFileURLs(true);
            settings.setAllowUniversalAccessFromFileURLs(true);
        }

    }

    public void setDesktopMode() {
        if(getSettings() != null) {
            getSettings().setUserAgentString(Constants.UA_DESKTOP);
        }
    }

    public void setPhoneMode() {
        if(getSettings() != null) {
            getSettings().setUserAgentString(Constants.UA_MOBILE);
        }
    }

}
