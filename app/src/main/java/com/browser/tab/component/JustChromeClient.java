package com.browser.tab.component;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.webkit.GeolocationPermissions;
import android.webkit.WebView;

import com.browser.R;
import com.browser.adblock.AdBlockManager;
import com.browser.tab.TabView;
import com.browser.ui.dialog.JDialog;

/**
 * Created by ozgur on 03.08.2016.
 */
public class JustChromeClient extends JustFileChooserChromeClient {

    public JustChromeClient(TabView tabView) {
        super(tabView);
        this.tabView = tabView;
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        if(tabView.getTabData().isDestroying() || tabView.isHome()) return;
        tabView.getUiController().getDialogManager().onGeolocationPermissionsShowPrompt(origin, callback);
    }

    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        if(tabView.getTabData().isDestroying()) return;
        tabView.getTabData().setFavicon(icon);
        tabView.getUiController().onReceivedIcon(tabView);
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if(tabView.getTabData().isDestroying()) return;
        tabView.getTabData().setProgress(newProgress);
        tabView.getUiController().onPageProgress(tabView);
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        if(tabView.getTabData().isDestroying()) return;
        tabView.getTabData().setTitle(title);
        tabView.getUiController().onReceivedTitle(tabView);
        tabView.changeTheme();
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, final Message resultMsg) {
        if(tabView.getTabData().isDestroying()) return false;

        if(isDialog && tabView.getUiController().getCpm().isAdBlockEnabled()) {
            AdBlockManager.getInstance().saveBlockedUrl(view.getUrl());
            tabView.getUiController().toast(R.string.popup_blocked);

            //ask user to open blocked popup
            JDialog c = new JDialog(tabView.getUiController().getActivity());
            c.setTitle(tabView.getUiController().getString(R.string.popup_blocked) + "!");
            c.setContent(view.getUrl());
            c.setYesButtonText(R.string.a_open);
            c.setNoButtonText(R.string.a_cancel);
            c.setCancelable(false);
            c.setDialogClickListener(new JDialog.DialogClickListener() {
                @Override
                public void onYesOrNoClick(JDialog.ButtonType buttonType) {
                    if(buttonType == JDialog.ButtonType.POSITIVE) {
                        openPopup(resultMsg);
                    }

                    if(buttonType == JDialog.ButtonType.NEGATIVE) {
                        return;
                    }
                }
            });

            c.show();

            return true;
        }

        openPopup(resultMsg);

        return true;
    }

    private void openPopup(final Message resultMsg) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                String parentId = tabView.getUiController().getTab().getTabData().getId();
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                String tabId = tabView.getUiController().addTab("", tabView.getTabData().isIncognito(), false, true, false);
                transport.setWebView(tabView.getUiController().getTabById(tabId).getWebView());

                tabView.getUiController().getTabById(tabId).getTabData().setCloseTabWhenBack(true);
                tabView.getUiController().getTabById(tabId).getTabData().setParentId(parentId);

                resultMsg.sendToTarget();
            }
        }, 100);
    }


    @Override
    public void onCloseWindow(WebView window) {
        boolean exitAppIfLastTab = false;
        tabView.getUiController().onCloseTab(tabView.getTabData());
    }
}
