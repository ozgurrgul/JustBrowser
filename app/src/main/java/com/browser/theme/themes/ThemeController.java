package com.browser.theme.themes;

import android.content.Context;
import android.text.TextUtils;

import com.browser.browser.CPM;
import com.browser.Constants;

import java.util.ArrayList;

/**
 * Created by ozgur on 10/8/15 at 7:31 PM.
 */

public class ThemeController {

    private static ThemeController instance = new ThemeController();

    CPM cpm;

    /**/
    private ThemeModel mCurrentTheme;
    private String curThemeName;

    /**/
    private ArrayList<ThemeListener> themeListeners = new ArrayList<>();

    /**/
    private ArrayList<ThemeModel> themeModels = new ArrayList<>();

    /**/
    public static ThemeController getInstance() {
        return instance;
    }

    private ThemeController() {}

    public void init(Context c) {
        cpm = new CPM(c);
        curThemeName = cpm.getTheme();
        bindThemes();
    }

    public void bindThemes() {
        themeModels = new ArrayList<>();
        initThemes();
        setTheme();
    }

    public ArrayList<ThemeModel> getThemeModels() {
        return themeModels;
    }

    private void initThemes() {
        themeModels.add(new StandardTheme());
        themeModels.add(new NightTheme());
        themeModels.add(new TealTheme());
        themeModels.add(new OrangeTheme());
        themeModels.add(new BelizeTheme());
        themeModels.add(new AlizarinTheme());
    }

    public void register(final ThemeListener themeListener){
        themeListeners.add(themeListener);
    }

    private void broadCast(){
        for(ThemeListener t: themeListeners) {
            t.themeChanged();
        }
    }

    public ThemeModel getCurrentTheme() {
        return mCurrentTheme;
    }

    private void setTheme() {
        if(TextUtils.isEmpty(curThemeName)) {
            curThemeName = Constants.Preference.PREF_DEFAULT_THEME_NAME;
        }

        mCurrentTheme = getThemeModelByName(curThemeName);

        if(mCurrentTheme == null) {
            mCurrentTheme = new StandardTheme();
        }

        mCurrentTheme.setUsing(true);
    }

    public void changeTheme(String themeName){
        ThemeModel themeModel = getThemeModelByName(themeName);

        if(themeModel == null) {
            themeModel = new StandardTheme();
        }

        if(!TextUtils.isEmpty(themeName) && !NightTheme.NAME.equals(themeName)) {
            cpm.setLastTheme(themeModel.name);
        }

        cpm.setTheme(themeModel.name);

        /* for theme activity */
        for (ThemeModel t: themeModels)
            t.setUsing(false);

        themeModel.setUsing(true);
        mCurrentTheme = themeModel;

        /**/
        broadCast();
    }


    public ThemeModel getThemeModelByName(String name){
        ThemeModel themeModel = null;

        for(ThemeModel t: themeModels)
            if(t.name.equals(name)) {
                themeModel = t;
                break;
            }

        return themeModel;
    }

    public boolean isNightMode() {
        return mCurrentTheme instanceof NightTheme;
    }


}
