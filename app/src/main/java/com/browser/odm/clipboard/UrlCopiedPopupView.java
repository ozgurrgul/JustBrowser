package com.browser.odm.clipboard;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.browser.App;
import com.browser.R;
import com.browser.browser.MainActivity;
import com.browser.odm.ui.DownloadsActivity;
import com.browser.ui.widget.JButton;
import com.browser.ui.widget.JEditText;

public class UrlCopiedPopupView implements View.OnClickListener, View.OnTouchListener, UrlCopiedPopupViewContainer.KeyEventHandler {

    public static final String URL_FROM_POPUP_SERVICE = "dk.ozgur.odm.intent.URL_FROM_POPUP_SERVICE";
    private WindowManager mWindowManager;
    private App app;
    private UrlCopiedPopupViewContainer mWholeView;
    private View mPopViewWrapper;
    private ViewDismissHandler mViewDismissHandler;
    private CharSequence mContent;
    private JEditText mUrlEditText;
    private JButton mDownloadButton;
    private JButton mSearchButton;

    public UrlCopiedPopupView(App application, CharSequence content) {
        app = application;
        mContent = content;
        mWindowManager = (WindowManager) application.getSystemService(Context.WINDOW_SERVICE);
    }

    public void setViewDismissHandler(ViewDismissHandler viewDismissHandler) {
        mViewDismissHandler = viewDismissHandler;
    }

    public void updateContent(CharSequence content) {
        mContent = content;

        if(mUrlEditText != null) {
            mUrlEditText.setText(mContent);
        }
    }

    public void show() {

        UrlCopiedPopupViewContainer view = (UrlCopiedPopupViewContainer) View.inflate(app, R.layout.copied_popup, null);

        // display content
        mDownloadButton = (JButton) view.findViewById(R.id.DownloadButton);
        mSearchButton = (JButton) view.findViewById(R.id.SearchButton);
        mUrlEditText = (JEditText) view.findViewById(R.id.UrlEditText);
        mUrlEditText.setText(mContent);

        mWholeView = view;
        mPopViewWrapper = view.findViewById(R.id.PopViewWrapper);

        // event listeners
        mPopViewWrapper.setOnClickListener(this);
        mWholeView.setOnTouchListener(this);
        mWholeView.setKeyEventHandler(this);
        mDownloadButton.setOnClickListener(this);
        mSearchButton.setOnClickListener(this);

        int w = WindowManager.LayoutParams.MATCH_PARENT;
        int h = WindowManager.LayoutParams.MATCH_PARENT;

        int flags = 0;
        int type = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            type = WindowManager.LayoutParams.TYPE_TOAST;
        } else {
            type = WindowManager.LayoutParams.TYPE_PHONE;
        }

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(w, h, type, flags, PixelFormat.TRANSLUCENT);
        layoutParams.gravity = Gravity.TOP;

        mWindowManager.addView(mWholeView, layoutParams);
    }

    @Override
    public void onClick(View v) {

        String url = mUrlEditText.getText().toString();
        removePoppedViewAndClear();

        if(v == mDownloadButton) {
            Intent i = new Intent();
            i.setAction(URL_FROM_POPUP_SERVICE);
            i.putExtra("url", url);
            i.setClass(app, DownloadsActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            app.startActivity(i);
        }

        else if(v == mSearchButton) {

            Intent i =  new Intent();
            i.setClass(app, MainActivity.class);
            i.setData(Uri.parse(url));
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.URI_INTENT_SCHEME);

            app.startActivity(i);
        }

    }

    private void removePoppedViewAndClear() {

        // remove view
        if (mWindowManager != null && mWholeView != null) {
            mWindowManager.removeView(mWholeView);
        }

        if (mViewDismissHandler != null) {
            mViewDismissHandler.onViewDismiss();
        }

        // remove listeners
        mPopViewWrapper.setOnClickListener(null);
        mWholeView.setOnTouchListener(null);
        mWholeView.setKeyEventHandler(null);
    }

    /**
     * touch the outside of the content view, remove the popped view
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        Rect rect = new Rect();
        mPopViewWrapper.getGlobalVisibleRect(rect);
        if (!rect.contains(x, y)) {
            removePoppedViewAndClear();
        }
        return false;
    }

    @Override
    public void onKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            removePoppedViewAndClear();
        }
    }

    public interface ViewDismissHandler {
        void onViewDismiss();
    }
}