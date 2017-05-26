package com.browser.mic;

import android.content.Context;
import android.graphics.PorterDuff;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.browser.R;
import com.browser.theme.themes.ThemeController;
import com.browser.theme.themes.ThemeListener;
import com.browser.ui.widget.JView;
import com.browser.ui.widget.jlist.JLTV;
import com.browser.browser.uictrl.UIControllerImpl;

/**
 * Created by ozgur on 08.08.2016.
 */
public class MicrophoneBigAnim extends LinearLayout implements JView, ThemeListener {

    private ImageView imageView;
    private JLTV textView;
    private UIControllerImpl uiController;

    public MicrophoneBigAnim(Context context, AttributeSet attrs) {
        super(context, attrs);
        _init();
    }

    private void _init() {
        View wrapper = inflate(getContext(), R.layout.mic_big_anim, this);
        imageView = (ImageView) wrapper.findViewById(R.id.ImageView);
        textView = (JLTV) wrapper.findViewById(R.id.TextView);

        ThemeController.getInstance().register(this);
        changeTheme();

        Animation fadeOut = new AlphaAnimation(1, 0.5f);
        fadeOut.setDuration(1000);
        fadeOut.setRepeatCount(Animation.INFINITE);
        fadeOut.setFillAfter(true);

        ScaleAnimation scale = new ScaleAnimation(1, 1.2f, 1, 1.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setRepeatCount(Animation.INFINITE);
        scale.setDuration(1000);
        scale.setFillAfter(true);

        AnimationSet animation = new AnimationSet(true);
        animation.addAnimation(scale);
        animation.addAnimation(fadeOut);
        animation.setRepeatMode(Animation.REVERSE);
        imageView.setAnimation(animation);

    }

    public void updateVoiceResult(String txt) {

        if(TextUtils.isEmpty(txt)) {
            return;
        }

        textView.setText(txt);
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

        if(isShown()) {
            hide();
            uiController.exitVoiceRecogMode();
            return true;
        }

        return false;
    }

    public void setUiController(UIControllerImpl uiController) {
        this.uiController = uiController;
    }

    @Override
    public void themeChanged() {
        changeTheme();
    }

    @Override
    public void changeTheme() {
        imageView.setColorFilter(ThemeController.getInstance().getCurrentTheme().jListViewTxtColor, PorterDuff.Mode.SRC_ATOP);
    }
}
