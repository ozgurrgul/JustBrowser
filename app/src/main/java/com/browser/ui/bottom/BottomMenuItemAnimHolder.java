package com.browser.ui.bottom;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.browser.R;

/**
 * Created by ozgur on 04.08.2016.
 */
public class BottomMenuItemAnimHolder {

    public static Animation showAnim;
    public static Animation hideAnim;

    public BottomMenuItemAnimHolder (Context context) {
        showAnim = AnimationUtils.loadAnimation(context, R.anim.bottom_menu_item_show);
        hideAnim = AnimationUtils.loadAnimation(context, R.anim.bottom_menu_item_hide);
    }

}
