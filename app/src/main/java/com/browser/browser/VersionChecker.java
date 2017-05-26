package com.browser.browser;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.browser.BuildConfig;
import com.browser.Constants;
import com.browser.odm.Config;
import com.browser.browser.uictrl.UIController;
import com.browser.browser.uictrl.UILifeCycle;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by ozgur on 14.08.2016.
 */
public class VersionChecker implements UILifeCycle {

    private OkHttpClient mHttpClient;
    private Call mCall;
    private boolean canceling;

    public VersionChecker(final UIController uiController) {
        mHttpClient = new OkHttpClient();
        mHttpClient.setFollowRedirects(true);
        mHttpClient.setFollowSslRedirects(false);
        mHttpClient.setRetryOnConnectionFailure(true);
        mHttpClient.setConnectTimeout(Config.CONNECT_TIMEOUT, TimeUnit.SECONDS);
        mHttpClient.setReadTimeout(Config.READ_TIMEOUT, TimeUnit.SECONDS);
        mHttpClient.getDispatcher().setMaxRequests(Config.MAX_THREAD_CNT);
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(Constants.VERSION_CHECK_URL);

        /**/
        Request request = requestBuilder.build();
        Response response = null;

        if(canceling) {
            return;
        }

        mCall = mHttpClient.newCall(request);
        new AsyncTask<Void, Void, Response>() {

            @Override
            protected Response doInBackground(Void... voids) {
                try {
                    return mCall.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Response response) {

                if(response == null) {
                    return;
                }

                String newVersionString = null;

                try {
                    newVersionString = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(TextUtils.isEmpty(newVersionString)) {
                    return;
                }

                int newVersion = Integer.valueOf(newVersionString);
                int curVersion = Integer.valueOf(BuildConfig.VERSION_NAME);

                Log.d("VCHEK", "cur: " + curVersion);
                Log.d("VCHEK", "new: " + newVersion);

                if(curVersion >= newVersion) {
                    return;
                }

                Log.d("VCHEK", "should update");
                uiController.getDialogManager().showUpdateDialog();
            }
        }.execute();

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {
        canceling = true;
        if(mCall != null) {
            mCall.cancel();
        }
    }

    @Override
    public void onDestroy() {

    }
}
