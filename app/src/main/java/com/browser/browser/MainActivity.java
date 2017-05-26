package com.browser.browser;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.browser.R;
import com.browser.odm.clipboard.ListenClipboardService;
import com.browser.perm.PermissionsManager;
import com.browser.perm.PermissionsResultAction;
import com.browser.browser.uictrl.UIController;
import com.browser.browser.uictrl.UIControllerImpl;

public class MainActivity extends Activity {

    UIController uiController;
    private boolean isKeyboardOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uiController = new UIControllerImpl(this);
        requestPermission();
        keyboard();
    }

    private void requestPermission() {
        PermissionsManager
                .getInstance()
                .requestAllManifestPermissionsIfNecessary(
                        this,
                        new PermissionsResultAction() {
                            @Override
                            public void onGranted() {

                            }

                            @Override
                            public void onDenied(String permission) {}
                        });
    }

    private void keyboard() {
        //kb
        final ViewGroup v = (ViewGroup) findViewById(R.id.RootLayout);
        v.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                v.getWindowVisibleDisplayFrame(r);
                int screenHeight = v.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;
                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    if(!isKeyboardOpen) {
                        isKeyboardOpen = true;
                        uiController.onKeyboardOpen();
                    }
                }
                else {
                    if(isKeyboardOpen) {
                        isKeyboardOpen = false;
                        uiController.onKeyboardHidden();
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        if(uiController.onBackPressed()) {
            return;
        }

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        uiController.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        uiController.onResume();
        ListenClipboardService.stop(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        uiController.onPause();

        if(uiController.getCpm().isStartCopyTextService()) {
            ListenClipboardService.start(this);
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        uiController._onConfigurationChanged(newConfig);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(intent != null && !TextUtils.isEmpty(intent.getDataString())) {
            uiController.getIntentManager().onNewIntent(intent);
            setIntent(null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        uiController.onActivityResult(requestCode, resultCode, intent);
    }

    //called from ui
    public void superOnActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }
}
