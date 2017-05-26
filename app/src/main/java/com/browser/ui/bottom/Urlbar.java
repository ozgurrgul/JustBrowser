package com.browser.ui.bottom;

import android.content.Context;
import android.graphics.PorterDuff;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.browser.Constants;
import com.browser.R;
import com.browser.tab.TabView;
import com.browser.theme.themes.ThemeController;
import com.browser.theme.themes.ThemeListener;
import com.browser.theme.themes.ThemeModel;
import com.browser.ui.widget.JEditText;
import com.browser.ui.widget.JView;
import com.browser.utils.Utils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ozgur on 04.08.2016.
 */
public class Urlbar extends LinearLayout implements JView, View.OnClickListener, ThemeListener, TextWatcher {

    private static final long REMOVE_EDIT_TEXT_HIDE_ANIM_DUR = 200;
    private BottomFirstMenuAndUrlBar parent;
    private BottomMenuItem mButtonShowMenu;
    private FrameLayout mRemoveEditTextContent;
    private ImageView mRemoveEditTextContentImg;
    private Animation showAnim;
    private Animation hideAnim;
    private ImageView mGoOrCancel;
    private GoState goOrCancelState = GoState.CANCEL;
    private JEditText editText;
    private FrameLayout mStopOrReload;
    private ImageView mStopOrReloadImg;
    private boolean urlBarExpanded;
    private ReloadState reloadState = ReloadState.RELOAD;
    private BottomMenuItem mButtonTools;
    private BottomMenuItem tabsItem;
    private ProgressBar progressBar;

    /* for query suggestion event, put some delay*/
    private Timer mTimer = new Timer();
    private final long SUGGESTION_DELAY = 75; // milliseconds

    public Urlbar(Context context) {
        super(context);
        _init();
    }

    public Urlbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        _init();
    }

    private void _init() {
        View wrapper = inflate(getContext(), R.layout.urlbar, this);


        showAnim = AnimationUtils.loadAnimation(getContext(), R.anim.remove_edittext_content_show_anim);
        hideAnim = AnimationUtils.loadAnimation(getContext(), R.anim.remove_edittext_content_hide_anim);

        editText = (JEditText) wrapper.findViewById(R.id.EditText);
        mGoOrCancel = (ImageView) wrapper.findViewById(R.id.GoOrCancel);

        mRemoveEditTextContent = (FrameLayout) wrapper.findViewById(R.id.RemoveEditTextContent);
        mRemoveEditTextContentImg = (ImageView) wrapper.findViewById(R.id.RemoveEditTextContentImg);

        mStopOrReload = (FrameLayout) wrapper.findViewById(R.id.StopOrReload);
        mStopOrReloadImg = (ImageView) wrapper.findViewById(R.id.StopOrReloadImg);

        mButtonShowMenu = (BottomMenuItem) wrapper.findViewById(R.id.ButtonShowMenu);
        mButtonShowMenu.setIcon(R.drawable.ic_menu);
        mButtonShowMenu.setOnClickListener(this);

        mButtonTools = (BottomMenuItem) wrapper.findViewById(R.id.ButtonToolsMenu);
        mButtonTools.setIcon(R.drawable.ic_tool);
        mButtonTools.setOnClickListener(this);

        tabsItem = (BottomMenuItem) wrapper.findViewById(R.id.ButtonShowTabs);
        tabsItem.setIcon(R.drawable.ic_tabs);
        tabsItem.setTabsCount(1);
        tabsItem.setOnClickListener(this);


        editText.addTextChangedListener(this);
        editText.setSelectAllOnFocus(true);
        editText.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus) {
                    expand();
                }
            }

        });

        mRemoveEditTextContent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
            }
        });

        mGoOrCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                goOrCancelClick();
            }
        });

        mStopOrReload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(reloadState == ReloadState.RELOAD) {
                    parent.getUiController().reloadCurrentTab();
                } else if(reloadState == ReloadState.STOP) {
                    parent.getUiController().getTab().stopLoading();
                }
            }
        });

        editText.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            goOrCancelClick();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        progressBar = (ProgressBar) wrapper.findViewById(R.id.ProgressBar);

        ThemeController.getInstance().register(this);
        changeTheme();
    }

    private void goOrCancelClick() {
        if(goOrCancelState == GoState.GO) {
            parent.getUiController().search(editText.getText().toString());
            collapse();
        } else if(goOrCancelState == GoState.CANCEL){
            collapse();
        }
    }

    @Override
    public void show() {
        editText.setEnabled(true);
        setVisibility(VISIBLE);
    }

    @Override
    public void hide() {
        editText.setEnabled(false);
        setVisibility(GONE);
    }

    @Override
    public boolean onBackPressed() {

        if(urlBarExpanded) {
            collapse();
            return true;
        }

        return false;
    }

    @Override
    public void onClick(View view) {

        if(view == mButtonShowMenu) {
            parent.showBottomMenu();
        }

        if(view == mButtonTools) {
            parent.getUiController().showToolsMenu();
        }

        if(view == tabsItem) {
            parent.getUiController().onBottomMenuEvent(R.id.ButtonShowTabs);
        }
    }

    public void setParent(BottomFirstMenuAndUrlBar parent) {
        this.parent = parent;
    }

    @Override
    public void themeChanged() {
        changeTheme();
    }

    @Override
    public void changeTheme() {
        ThemeModel t = ThemeController.getInstance().getCurrentTheme();
        editText.setBackgroundResource(t.urlBarBg);
        editText.setTextColor(t.urlBarTxtColor);
        editText.setHintTextColor(t.urlBarHintTxtColor);
        mRemoveEditTextContentImg.setColorFilter(t.urlBarTxtColor, PorterDuff.Mode.SRC_ATOP);
        mStopOrReloadImg.setColorFilter(t.urlBarTxtColor, PorterDuff.Mode.SRC_ATOP);
        mGoOrCancel.setColorFilter(t.bmiIconColor, PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void onTextChanged(final CharSequence text, int i, int i1, int i2) {

        if(TextUtils.isEmpty(text)) {

            goOrCancelState = GoState.CANCEL;
            mGoOrCancel.setImageResource(R.drawable.ic_close);
            mGoOrCancel.startAnimation(showAnim);
            //mGoOrCancel.setColorFilter(ThemeController.getInstance().getCurrentTheme().bmiIconColor, PorterDuff.Mode.SRC_ATOP);
            hideRemoveEditText();
        } else {

            //already go state
            if(GoState.GO == goOrCancelState) {
                //return;
            }

            goOrCancelState = GoState.GO;
            mGoOrCancel.setImageResource(R.drawable.ic_go);
            //mGoOrCancel.setColorFilter(ThemeController.getInstance().getCurrentTheme().bmiIconColor, PorterDuff.Mode.SRC_ATOP);

            showRemoveEditText();
        }

        mTimer.cancel();
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(TextUtils.isEmpty(text)) {
                    return;
                }
                parent.getUiController().getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        parent.getUiController().updateSuggestionList(text.toString());
                    }
                });
            }
        }, SUGGESTION_DELAY);
    }

    @Override
    public void afterTextChanged(Editable editable) {}

    public void expand() {


        if(urlBarExpanded) {
            return;
        }

        urlBarExpanded = true;
        //anim
        mButtonShowMenu.setVisibility(GONE);
        mButtonTools.setVisibility(GONE);

        //mic.setVisibility(GONE);
        tabsItem.setVisibility(GONE);
        mGoOrCancel.setVisibility(VISIBLE);
        mGoOrCancel.setImageResource(R.drawable.ic_close);
        mGoOrCancel.setColorFilter(ThemeController.getInstance().getCurrentTheme().urlBarTxtColor);
        goOrCancelState = GoState.CANCEL;
        mGoOrCancel.startAnimation(showAnim);
        mStopOrReload.setVisibility(GONE);

        if(!TextUtils.isEmpty(editText.getText().toString())) {
            showRemoveEditText();
        }

        parent.getUiController().onEnterQueryMode();
    }

    //TODO  OOOOOOOOOOOOOOOOOOOO
    public void collapse() {

        if(urlBarExpanded == false) {
            return;
        }

        urlBarExpanded = false;

        hideRemoveEditText();
        editText.clearFocus();
        goOrCancelState = GoState.CANCEL;

        mGoOrCancel.setVisibility(GONE);

        //mic.setVisibility(VISIBLE);
        tabsItem.setVisibility(VISIBLE);
        mButtonShowMenu.setVisibility(VISIBLE);
        mButtonTools.setVisibility(VISIBLE);

        if(!parent.getUiController().getTab().isHome()) {
            mStopOrReload.setVisibility(VISIBLE);
        }

        Utils.hideSoftKeyboard(parent.getUiController().getActivity());
        parent.getUiController().onExitQueryMode();

    }

    public void onPageStarted(TabView tabView) {

        if(tabView.getTabData().isInBackground()) {
            return;
        }

        updateEditText(tabView.getTabData().getUrl());

        mStopOrReloadImg.setImageResource(R.drawable.ic_close);
        mStopOrReloadImg.setColorFilter(ThemeController.getInstance().getCurrentTheme().urlBarTxtColor);

        reloadState = ReloadState.STOP;
    }

    public void onPageFinished(TabView tabView) {

        progressBar.setProgress(0);

        if(parent.getUiController().isEnterQueryMode()) {
            return;
        }

        if(tabView.getTabData().isInBackground()) {
            return;
        }

        updateEditText(tabView.getTabData().getUrl());

        mStopOrReloadImg.setImageResource(R.drawable.ic_reload);
        mStopOrReloadImg.setColorFilter(ThemeController.getInstance().getCurrentTheme().urlBarTxtColor);

        reloadState = ReloadState.RELOAD;
    }

    public void onShowHome(TabView tabView) {
        //reloadState = ReloadState.RELOAD;
        updateEditText(Constants.WEBVIEW_HOME);
        mStopOrReload.setVisibility(GONE);
    }

    public void onHideHome(TabView tabView) {

        if(tabView.getTabData().isInBackground()) {
            return;
        }

        updateEditText(tabView.getTabData().getUrl());
        mStopOrReload.setVisibility(VISIBLE);
    }

    public void updateEditText(String url) {

        if(TextUtils.isEmpty(url)) {
            return;
        }

        if(url.startsWith(Constants.HTTPS)) {
            //mHttpsIcon.setVisibility(VISIBLE);
        } else {
            //mHttpsIcon.setVisibility(GONE);
        }

        editText.removeTextChangedListener(this); //remove before changing text
        editText.setText(String.valueOf(url));
        editText.addTextChangedListener(this); //add again
    }

    public boolean isUrlBarExpanded () {
        return urlBarExpanded;
    }

    private void showRemoveEditText() {

        if(mRemoveEditTextContent.isShown()) {
            return;
        }

        mRemoveEditTextContent.setVisibility(VISIBLE);
        mRemoveEditTextContent.startAnimation(showAnim);
    }

    private void hideRemoveEditText() {

        //already hidden
        if(!mRemoveEditTextContent.isShown()) {
            return;
        }

        mRemoveEditTextContent.startAnimation(hideAnim);

        mRemoveEditTextContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRemoveEditTextContent.setVisibility(GONE);
            }
        }, REMOVE_EDIT_TEXT_HIDE_ANIM_DUR);
    }

    public void setTabsCount(int count) {
        tabsItem.setTabsCount(count);
    }

    public void onPageProgress(TabView tabView) {

        int progress = tabView.getTabData().getProgress();

        if(tabView.getTabData().isInBackground() || tabView.isHome()) {
            return;
        }

        if(progress == 100) {
            progressBar.setProgress(0);
        } else {
            progressBar.setProgress(progress);
        }
    }

    public void hideProgress() {
        progressBar.setVisibility(GONE);
    }

    public void showProgress() {
        progressBar.setVisibility(VISIBLE);
    }
}
