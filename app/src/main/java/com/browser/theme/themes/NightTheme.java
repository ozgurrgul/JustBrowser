package com.browser.theme.themes;

import android.graphics.Color;

import com.browser.R;

/**
 * Created by ozgur on 3/3/16 at 6:40 PM.
 */
public class NightTheme extends StandardTheme {

    public static final String NAME = "night";

    public NightTheme() {
        name = NAME;

        actBg = color("#1A2025");

        /**/
        bottomBg = color("#1A2025");
        bottomDivider = Color.GRAY;
        bmiIconColor = Color.LTGRAY;
        bmiIconDisabledColor = Color.DKGRAY;
        bmiBgHoverColor = Color.DKGRAY;
        urlBarBg = R.drawable.urlbar_night;
        urlBarTxtColor = Color.GRAY;
        urlBarHintTxtColor = Color.LTGRAY;

        /* dialog */
        dialogBg = color("#FF5B5C5C");
        dialogTitleBg = color("#FF363636");
        dialogTitleColor = Color.LTGRAY;
        dialogContentTxtColor = Color.GRAY;
        dialogButtonTxtColor = Color.GRAY;
        dialogEditTextBg = R.drawable.jdialog_edittext_bg_night;
        dialogEditTextColor = Color.LTGRAY;

        /**/
        tabsListBg = color("#1A2025");
        tabsListItemBg = R.drawable.tabs_list_item_bg_night;
        tabsListItemTxtColor = Color.LTGRAY;

        /**/
        jListViewBg = R.drawable.jlist_bg_night;
        jListViewTxtColor = Color.GRAY;
        jListViewTxt2Color = Color.DKGRAY;

        /**/
        barBg = color("#FF5B5C5C");
        barItemColor = Color.GRAY;

        /**/
        dialItemBg = R.drawable.dial_night;
        dialItemTxtColor = Color.GRAY;
    }

}
