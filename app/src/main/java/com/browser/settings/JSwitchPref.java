package com.browser.settings;

import android.content.Context;
import android.preference.SwitchPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import com.browser.R;
import com.browser.ui.widget.jlist.JListSwitchButton;

public class JSwitchPref extends SwitchPreference {

    private JSwitchPrefListener jSwitchPrefListener;

    public void setjSwitchPrefListener(JSwitchPrefListener jSwitchPrefListener) {
        this.jSwitchPrefListener = jSwitchPrefListener;
    }

    public JSwitchPref(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.j_pref_switch);
    }

    @Override
    protected void onBindView(View wrapper) {
        super.onBindView(wrapper);
        initView(wrapper);
    }

    private void initView(View wrapper) {

        wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("JSwitchPref", "clicked");
                setChecked(!isChecked());
            }
        });

        /**/
        JListSwitchButton switchCompat = (JListSwitchButton) wrapper.findViewById(R.id.SwitchButton);

        /* default value */
        switchCompat.setChecked(
                isChecked()
        );

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean value) {
                Log.d("JSwitchPref", "key: "+ getKey()+ ", new value: " + value);
                setChecked(value);

                if(jSwitchPrefListener != null) {
                    jSwitchPrefListener.onChange(value);
                }
            }
        });
    }

}
