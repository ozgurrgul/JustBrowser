package com.browser.settings;

import android.content.Context;
import android.graphics.PorterDuff;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.browser.R;
import com.browser.theme.themes.ThemeController;
import com.browser.theme.themes.ThemeListener;
import com.browser.theme.themes.ThemeModel;
import com.browser.ui.widget.jlist.JLTV;

public class JPrefCategory extends PreferenceCategory implements ThemeListener {

    public JPrefCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.j_pref_cat);
    }

    @Override
    protected void onBindView(View wrapper) {
        super.onBindView(wrapper);
        initView(wrapper);
    }

    private void initView(View wrapper) {

        ThemeModel t = ThemeController.getInstance().getCurrentTheme();

        ImageView imageView = (ImageView) wrapper.findViewById(R.id.ImageView);

        ((JLTV) wrapper.findViewById(R.id.TitleTextView)).setText(getTitle());
        ((JLTV) wrapper.findViewById(R.id.TitleTextView)).setTextColor(t.jListViewTxtColor);

        if(getTitleRes() == R.string.settings) {
            imageView.setImageResource(R.drawable.ic_settings);
            imageView.setColorFilter(t.jListViewTxtColor, PorterDuff.Mode.SRC_ATOP);
        }

        else if(getTitleRes() == R.string.app_name) {
            imageView.setImageResource(R.drawable.ic2);
            imageView.clearColorFilter();
        }

        ThemeController.getInstance().register(this);
        changeTheme();
    }

    @Override
    public void themeChanged() {
        changeTheme();
    }

    @Override
    public void changeTheme() {}
}
