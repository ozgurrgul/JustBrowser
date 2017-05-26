package com.browser.settings;

import android.content.Context;
import android.preference.SwitchPreference;
import android.util.AttributeSet;

import com.browser.R;

public class JPref extends SwitchPreference {

    public JPref(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.j_pref);
    }

}
