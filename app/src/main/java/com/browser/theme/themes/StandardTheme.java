package com.browser.theme.themes;

import android.graphics.Color;

import com.browser.Constants;
import com.browser.R;

/**
 * Created by ozgur on 3/3/16 at 6:40 PM.
 */
public class StandardTheme extends ThemeModel {

    public static final String NAME = Constants.Preference.PREF_DEFAULT_THEME_NAME;

    public StandardTheme() {
        name = NAME;

        actBg = color("#f2f2f2");

        /* bottom */
        bottomBg = Color.WHITE;
        bottomDivider = color("#cccccc");
        bmiIconColor = Color.BLACK;
        bmiIconDisabledColor = Color.GRAY;
        bmiBgHoverColor = Color.LTGRAY;
        urlBarBg = R.drawable.urlbar;
        urlBarTxtColor = Color.BLACK;
        urlBarHintTxtColor = Color.LTGRAY;

        /* dialog */
        dialogBg = Color.WHITE;
        dialogTitleBg = color("#EAD8C1");
        dialogTitleColor = Color.BLACK;
        dialogContentTxtColor = Color.GRAY;
        dialogButtonTxtColor = Color.GRAY;
        dialogEditTextBg = R.drawable.jdialog_edittext_bg;
        dialogEditTextColor = Color.BLACK;

        /**/
        tabsListBg = Color.WHITE;
        tabsListItemBg = R.drawable.tabs_list_item_bg;
        tabsListItemTxtColor = Color.BLACK;

        /**/
        jListViewBg = R.drawable.jlist_bg;
        jListViewTxtColor = Color.BLACK;
        jListViewTxt2Color = Color.GRAY;

        /**/
        barBg = color("#EAD8C1");
        barItemColor = Color.BLACK;

        /**/
        dialItemBg = R.drawable.dial;
        dialItemTxtColor = Color.BLACK;

    }

}
