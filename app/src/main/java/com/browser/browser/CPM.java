package com.browser.browser;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.browser.Constants;
import com.google.common.util.concurrent.ExecutionError;

public class CPM implements Constants.Preference {

    private final SharedPreferences mPrefs;

    public CPM(final Context context) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isAdBlockEnabled() {
        return getBoolean(BLOCK_AD, true);
    }

    public boolean isOverviewModeEnabled() {
        return getBoolean(OVERVIEW_MODE, true);
    }

    public String getSearchEngineUrl() {
        return mPrefs.getString(PREF_SEARCH_ENGINE, Constants.GOOGLE_SEARCH_URL);
    }

    public boolean getTextReflowEnabled() {
        return getBoolean(TEXT_REFLOW, false);
    }

    public int getTextSize() {
        return Integer.valueOf(mPrefs.getString(TEXT_SIZE, "100"));
    }

    public boolean isRestoreLostTabs() {
        return getBoolean(RESTORE_LOST_TABS, true);
    }

    public boolean isEnableCookies() {
        return getBoolean(ENABLE_COOKIES, true);
    }

    public boolean isBlockJavascript() {
        return getBoolean(BLOCK_JS, false);
    }

    public boolean isBlockImages() {
        return getBoolean(BLOCK_IMAGE, false);
    }

    public void putBoolean(@NonNull String name, boolean value) {
        mPrefs.edit().putBoolean(name, value).apply();
    }

    public void putInt(@NonNull String name, int value) {
        mPrefs.edit().putInt(name, value).apply();
    }

    public void putString(@NonNull String name, @Nullable String value) {
        mPrefs.edit().putString(name, value).apply();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return mPrefs.getBoolean(key, defaultValue);
    }

    public String getTheme() {
        return mPrefs.getString(PREF_THEME_NAME, PREF_DEFAULT_THEME_NAME);
    }

    public String getLastTheme() {
        return mPrefs.getString("PREF_LAST_THEME", PREF_DEFAULT_THEME_NAME);
    }

    public void setTheme(String themeName) {
        putString(PREF_THEME_NAME, themeName);
    }
    public void setLastTheme(String themeName) {
        putString("PREF_LAST_THEME", themeName);
    }

    public String getLang() {
        return mPrefs.getString(LANG, null);
    }

    public void setLang(String language) {
        putString(LANG, language);
    }

    public boolean getUseWideViewportEnabled() {
        return mPrefs.getBoolean(USE_WIDE_VIEWPORT, true);
    }

    public boolean isAlreadyPro() {
        return mPrefs.getBoolean("PREF_ALREADY_PRO", false);
    }

    public void setAlreadyPro() {
        mPrefs.edit().putBoolean("PREF_ALREADY_PRO", true).apply();
    }

    public boolean isSecondMenuRemindDialogShown() {
        return mPrefs.getBoolean("PREF_SEC_MENU_REMIND", false);
    }

    public void setSecondMenuRemindDialogShown() {
        mPrefs.edit().putBoolean("PREF_SEC_MENU_REMIND", true).apply();
    }

    /* stats */
    public int getTotalTabCount() {
        return mPrefs.getInt("TOTAL_TAB_COUNT", 0);
    }

    public void increaseTotalTabCount() {
        mPrefs.edit().putInt("TOTAL_TAB_COUNT", getTotalTabCount() + 1).apply();
    }

    public int getTotalOpenCount() {
        return mPrefs.getInt("TOTAL_OPEN_COUNT", 0);
    }

    public void increaseTotalOpenCount() {
        mPrefs.edit().putInt("TOTAL_OPEN_COUNT", getTotalOpenCount() + 1).apply();
    }

    public int getTotalUrlCount() {
        return mPrefs.getInt("TOTAL_URL_COUNT", 0);
    }

    public void increaseTotalUrlCount() {
        mPrefs.edit().putInt("TOTAL_URL_COUNT", getTotalUrlCount() + 1).apply();
    }

    public boolean isShowMousePad() {
        return mPrefs.getBoolean(Constants.Preference.MOUSE_PAD, false);
    }

    public boolean isDesktopMode() {
        return getBoolean(PREF_DESKTOP_MODE, false);
    }

    public void setDesktopMode() {
        putBoolean(PREF_DESKTOP_MODE, true);
    }

    public void setPhoneMode() {
        putBoolean(PREF_DESKTOP_MODE, false);
    }

    public boolean isShowLogo() {
        return getBoolean(PREF_SHOW_LOGO, true);
    }

    public boolean isStartCopyTextService() {
        return getBoolean(PREF_START_COPY_TEXT_LISTENER, true);
    }

    public boolean isBeProDialog2Showed() {
        return mPrefs.getBoolean("BE_PRO_2", false);
    }

    public void setBeProDialog2Showed() {
        mPrefs.edit().putBoolean("BE_PRO_2", true).apply();
    }

}