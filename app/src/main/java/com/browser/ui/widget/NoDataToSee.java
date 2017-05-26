package com.browser.ui.widget;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.browser.R;
import com.browser.theme.themes.ThemeController;

/**
 * Created by ozgur on 09.08.2016.
 */
public class NoDataToSee extends LinearLayout implements JView {

    private ImageView mImageView;

    public NoDataToSee(Context context) {
        super(context);
        _init();
    }

    public NoDataToSee(Context context, AttributeSet attrs) {
        super(context, attrs);
        _init();
    }

    public NoDataToSee(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        _init();
    }

    private void _init() {
        View wrapper = inflate(getContext(), R.layout.no_data, this);
        mImageView = (ImageView) wrapper.findViewById(R.id.ImageView);
        mImageView.setColorFilter(ThemeController.getInstance().getCurrentTheme().jListViewTxtColor, PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public void show() {
        setVisibility(VISIBLE);
    }

    @Override
    public void hide() {
        setVisibility(GONE);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
