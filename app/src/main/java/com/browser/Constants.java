package com.browser;

public class Constants {

    public static final int API = android.os.Build.VERSION.SDK_INT;
    public static final String GOOGLE_SEARCH_URL = "http://www.google.com/search?client=justbr&ie=UTF-8&oe=UTF-8&q=";
    public static final String URL_INTENT_ORIGIN_SELF = "URL_INTENT_ORIGIN_SELF";

    /* ads */
    public static final String LANG = "en";
    public static final int SUGGESTION_LIST_GOOGLE_ITEM_COUNT = 5;
    public static final int MAXIMUM_TAB_COUNT = 15;

    public interface Preference {
        String PREF_THEME_NAME = "PREF_THEME_NAME";
        String PREF_DEFAULT_THEME_NAME = "standard";
        String BLOCK_IMAGE = "PREF_BLOCK_IMAGE";
        String BLOCK_JS = "PREF_BLOCK_JS";
        String BLOCK_AD = "PREF_BLOCK_AD";
        String ENABLE_COOKIES = "PREF_ENABLE_COOKIES";
        String TEXT_SIZE = "PREF_TEXT_SIZE";
        String PREF_SEARCH_ENGINE = "PREF_SEARCH_ENGINE";

        String ADOBE_FLASH_SUPPORT = "PREF_ENABLE_FLASH";
        String SAVE_HISTORY = "PREF_SAVE_HISTORY";
        String OVERVIEW_MODE = "PREF_ENABLE_OVERVIEW_MODE";
        String POPUPS = "PREF_ENABLE_POPUPS";
        String RESTORE_LOST_TABS = "PREF_RESTORE_LOST_TABS";
        String TEXT_REFLOW = "PREF_TEXT_REFLOW";
        String USE_WIDE_VIEWPORT = "PREF_USE_VIEWPORT";
        String LANG = "PREF_LANG";
        String MOUSE_PAD = "PREF_MOUSE_PAD";
        String PREF_DESKTOP_MODE = "PREF_DESKTOP_MODE";
        String PREF_SHOW_LOGO = "PREF_SHOW_LOGO";
        String PREF_START_COPY_TEXT_LISTENER = "PREF_START_COPY_TEXT_LISTENER";
    }

	public static final String WEBVIEW_HOME = "just:home";
    public static final String HTTP = "http://";
    public static final String HTTPS = "https://";
    public static final String FILE = "file:///";
    public static final String VERSION_CHECK_URL = "http://104.236.15.65/jb/ver";
    public static final String VERSION_APK_URL = "http://104.236.15.65/jb/justbrowser.apk";

    public static final String UA_MOBILE = "Mozilla/5.0 (Linux; U; Android 4.0.3; ko-kr; LG-L160L Build/IML74K) AppleWebkit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30";
    public static final String UA_DESKTOP = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/53.0.2785.143 Chrome/53.0.2785.143 Safari/537.36";
}
