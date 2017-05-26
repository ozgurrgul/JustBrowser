package com.browser.pro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import com.browser.browser.uictrl.UIController;
import com.browser.browser.uictrl.UILifeCycle;

import java.util.List;

/**
 * Created by ozgur on 11.08.2016.
 */
public class ProChecker implements UILifeCycle {

    private final UIController uiController;
    private String proPack;
    private boolean isPro = false;
    private AsyncTask<Void, Void, Void> asyncTask;
    private final PackageManager pm;
    private BroadcastReceiver installReceiver;
    private IntentFilter intentFilter;
    private boolean alreadyPro;

    public ProChecker(UIController uiController) {

        this.uiController = uiController;

        this.pm = uiController.getActivity().getPackageManager();
        this.proPack = uiController.getActivity().getPackageName() + ".pro";
        this.alreadyPro = uiController.getCpm().isAlreadyPro();

        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        intentFilter.addDataScheme("package");

        checkPro();
    }

    private void checkPro() {

       asyncTask =  new AsyncTask<Void, Void, Void>() {

           @Override
           protected Void doInBackground(Void... voids) {

               try {
                   List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
                   for (ApplicationInfo packageInfo : packages) {
                       if (proPack.equals(packageInfo.packageName)) {
                           isPro = true;
                           break;
                       }
                   }
               } catch (Exception e) {
                   e.printStackTrace();
               }

               return null;
           }

           @Override
           protected void onPostExecute(Void aVoid) {

               if(!alreadyPro && isPro) {
                   uiController.getDialogManager().showProThanksDialog();
                   uiController.getCpm().setAlreadyPro();
               }

           }

       }.execute();
    }


    @Override
    public void onResume() {
        installReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                checkPro();
            }
        };

        if(uiController.getActivity() != null) {
            uiController.getActivity().registerReceiver(installReceiver, intentFilter);
        }
    }

    @Override
    public void onPause() {
        if(asyncTask != null) {
            asyncTask.cancel(true);
        }

        if(installReceiver != null && uiController.getActivity() != null) {
            uiController.getActivity().unregisterReceiver(installReceiver);
        }
    }

    @Override
    public void onDestroy() {
        if(asyncTask != null) {
            asyncTask.cancel(true);
        }
    }

    public boolean isPro() {
        return isPro;
    }
}
