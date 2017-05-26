package com.browser.browser.uictrl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.browser.R;
import com.browser.odm.ODMPool;

/**
 * Created by ozgur on 11.08.2016.
 */
public class UIDownloadReceiver implements UILifeCycle {

    private final UIController uiController;
    private BroadcastReceiver startReceiver;
    private BroadcastReceiver finishReceiver;
    private BroadcastReceiver progressReceiver;
    private BroadcastReceiver errorReceiver;

    public UIDownloadReceiver(UIController uiController) {
        this.uiController = uiController;
    }

    @Override
    public void onResume() {

        // started
        startReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                uiController.toast(R.string.download_started);
            }
        };

        // finished
        finishReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                uiController.toast(R.string.download_finished);
            }
        };

        // finished
        errorReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                uiController.toast(R.string.download_error);
            }
        };

        // progress
        progressReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

            }
        };

        uiController.getActivity().registerReceiver(startReceiver, new IntentFilter(ODMPool.BROADCAST_ACTION_STARTED));
        uiController.getActivity().registerReceiver(finishReceiver, new IntentFilter(ODMPool.BROADCAST_ACTION_FINISHED));
        uiController.getActivity().registerReceiver(progressReceiver, new IntentFilter(ODMPool.BROADCAST_ACTION_PROGRESS));
        uiController.getActivity().registerReceiver(errorReceiver, new IntentFilter(ODMPool.BROADCAST_ACTION_ERROR));
    }

    @Override
    public void onPause() {
        uiController.getActivity().unregisterReceiver(startReceiver);
        uiController.getActivity().unregisterReceiver(finishReceiver);
        uiController.getActivity().unregisterReceiver(progressReceiver);
        uiController.getActivity().unregisterReceiver(errorReceiver);
    }

    @Override
    public void onDestroy() {}
}
