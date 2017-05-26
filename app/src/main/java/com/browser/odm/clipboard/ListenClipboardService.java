package com.browser.odm.clipboard;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.text.TextUtils;

import com.browser.App;
import com.browser.odm.ODMUtils;

public final class ListenClipboardService extends Service implements UrlCopiedPopupView.ViewDismissHandler {

    private static final String KEY_FOR_WAKE_LOCK = "wake-lock";

    private static CharSequence sLastContent = null;
    private ClipboardManagerCompat mClipboardWatcher;
    private UrlCopiedPopupView mUrlCopiedPopupView;
    private ClipboardManagerCompat.OnPrimaryClipChangedListener mOnPrimaryClipChangedListener = new ClipboardManagerCompat.OnPrimaryClipChangedListener() {
        public void onPrimaryClipChanged() {
            performClipboardCheck();
        }
    };

    public static void start(Context context) {
        Intent serviceIntent = new Intent(context, ListenClipboardService.class);
        context.startService(serviceIntent);
    }

    public static void stop(Context context) {
        Intent serviceIntent = new Intent(context, ListenClipboardService.class);
        context.stopService(serviceIntent);
    }

    public static void startForWeakeLock(Context context, Intent intent) {

        Intent serviceIntent = new Intent(context, ListenClipboardService.class);
        context.startService(serviceIntent);

        intent.putExtra(ListenClipboardService.KEY_FOR_WAKE_LOCK, true);
        Intent myIntent = new Intent(context, ListenClipboardService.class);

        // using wake lock to start service
        WakefulBroadcastReceiver.startWakefulService(context, myIntent);
    }

    @Override
    public void onCreate() {
        mClipboardWatcher = ClipboardManagerCompat.create(this);
        mClipboardWatcher.addPrimaryClipChangedListener(mOnPrimaryClipChangedListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mClipboardWatcher.removePrimaryClipChangedListener(mOnPrimaryClipChangedListener);

        sLastContent = null;
        if (mUrlCopiedPopupView != null) {
            mUrlCopiedPopupView.setViewDismissHandler(null);
            mUrlCopiedPopupView = null;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            // remove wake lock
            if (intent.getBooleanExtra(KEY_FOR_WAKE_LOCK, false)) {
                BootCompletedReceiver.completeWakefulIntent(intent);
            }
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void performClipboardCheck() {
        CharSequence content = mClipboardWatcher.getText();
        if (TextUtils.isEmpty(content)) {
            return;
        }
        showContent(content);
    }

    private void showContent(CharSequence content) {

        if (sLastContent != null && sLastContent.equals(content) || content == null) {
            return;
        }

        if(!ODMUtils.isValidUrl(content.toString())) {
            //return;
        }

        sLastContent = content;

        if (mUrlCopiedPopupView != null) {
            mUrlCopiedPopupView.updateContent(content);
        } else {
            mUrlCopiedPopupView = new UrlCopiedPopupView((App) getApplication(), content);
            mUrlCopiedPopupView.setViewDismissHandler(this);
            mUrlCopiedPopupView.show();
        }
    }

    @Override
    public void onViewDismiss() {
        sLastContent = null;
        mUrlCopiedPopupView = null;
    }
}