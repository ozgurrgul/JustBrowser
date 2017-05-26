package com.browser.theme.themes;

import android.graphics.Color;

import java.io.Serializable;

/**
 * Created by ozgur on 9/14/15 at 4:12 PM.
 */
public class ThemeModel implements Serializable{

    public String name;
    private boolean isUsing;

    public int actBg;

    /**/
    public int bottomBg;
    public int bottomDivider;
    public int bmiIconColor;
    public int bmiIconDisabledColor;
    public int bmiBgHoverColor;
    public int urlBarBg;
    public int urlBarTxtColor;
    public int urlBarHintTxtColor;


    /**/
    public int dialogBg;
    public int dialogTitleBg;
    public int dialogTitleColor;
    public int dialogContentTxtColor;
    public int dialogButtonTxtColor;
    public int dialogEditTextBg;
    public int dialogEditTextColor;

    /**/
    public int tabsListBg;
    public int tabsListItemBg;
    public int tabsListItemTxtColor;

    /**/
    public int jListViewBg;
    public int jListViewTxtColor;
    public int jListViewTxt2Color;

    /**/
    public int barBg;
    public int barItemColor;

    /**/
    public int dialItemBg;
    public int dialItemTxtColor;

    public ThemeModel(){

    }

    public void setUsing(boolean isUsing) {
        this.isUsing = isUsing;
    }

    public boolean isUsing() {
        return isUsing;
    }

    public static int color(String hex) {
        return Color.parseColor(hex);
    }



}
