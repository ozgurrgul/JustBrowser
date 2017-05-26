package com.browser.odm.clipboard;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.browser.browser.CPM;

public class BootCompletedReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if(new CPM(context).isStartCopyTextService()) {
            ListenClipboardService.startForWeakeLock(context, intent);
        }

    }
}