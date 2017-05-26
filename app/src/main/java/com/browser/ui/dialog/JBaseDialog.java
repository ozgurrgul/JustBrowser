package com.browser.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.view.WindowManager;

/**
 * Created by ozgur on 21.06.2016.
 */
public abstract class JBaseDialog extends Dialog  {

    public JBaseDialog(Context context) {
        super(context);
    }

    protected void setWH(int width, int height) {
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();

        lp.width = width;
        lp.height = height;
        getWindow().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getContext(), android.R.color.transparent)));
    }

}
