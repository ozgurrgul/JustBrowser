package com.browser.odm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.browser.R;
import com.browser.browser.MainActivity;
import com.squareup.okhttp.OkHttpClient;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * Created by ozgur on 05.07.2016.
 */
public class ODMPool {

    public static final String BROADCAST_ACTION_STARTED = "dk.ozgur.com.just.odm.BROADCAST_ACTION_STARTED";
    public static final String BROADCAST_ACTION_PROGRESS = "dk.ozgur.com.just.odm.BROADCAST_ACTION_PROGRESS";
    public static final String BROADCAST_ACTION_FINISHED = "dk.ozgur.com.just.odm.BROADCAST_ACTION_FINISHED";
    public static final String BROADCAST_ACTION_ERROR = "dk.ozgur.com.just.odm.BROADCAST_ACTION_ERROR";
    public static final String BROADCAST_ACTION_CANCELLED = "dk.ozgur.com.just.odm.BROADCAST_ACTION_CANCELLED";
    public static final String BROADCAST_ACTION_PAUSED = "dk.ozgur.com.just.odm.BROADCAST_ACTION_PAUSED";
    public static final String BROADCAST_ACTION_DELETED = "dk.ozgur.com.just.odm.BROADCAST_ACTION_DELETED";
    private ArrayList<ODMWorker> workers;
    private ODMDatabase mOdmDatabase;
    private ODM mOdm;
    private OkHttpClient mHttpClient;
    private ExecutorService mExecutorService;

    public ODMPool (ODM odm) {
        initHttp();
        mOdm = odm;
        mExecutorService = Executors.newCachedThreadPool();
        mOdmDatabase = ODMDatabase.getInstance(odm.getContext());
        workers = mOdmDatabase.getUnCompletedWorkers(this);
        Log("getUnCompletedWorkers size: " + workers.size());

        for (ODMWorker w: workers) {
            runOnExecutor(w);
        }
    }

    private void initHttp() {
        mHttpClient = new OkHttpClient();
        mHttpClient.setFollowRedirects(true);
        mHttpClient.setFollowSslRedirects(false);
        mHttpClient.setRetryOnConnectionFailure(true);
        mHttpClient.setConnectTimeout(Config.CONNECT_TIMEOUT, TimeUnit.SECONDS);
        mHttpClient.setReadTimeout(Config.READ_TIMEOUT, TimeUnit.SECONDS);
        mHttpClient.getDispatcher().setMaxRequests(Config.MAX_THREAD_CNT);
    }

    public void newWorker(String did, String folder, String fileName, String url) {
        /* first, add to db */
        String path = folder + "/" + fileName;

        Log("com.just.odm pool, path set to: " + path);

        if(mOdmDatabase.add(did, fileName, url, path, 0) != -1) {

            //create new worker object
            ODMWorker w =
                new ODMWorkerBuilder()
                    .setParentPool(this)
                    .setDid(did)
                    .setFileName(fileName)
                    .setPath(path)
                    .setUrl(url)
                    .setFileSize(0)
                    .setState(ODMWorker.WORKER_WAITING)
                    .setHttpClient(mHttpClient)
                    .setProgress(0)
                    .setCanRangeDownload(0)
                    .setDlTotal(0)
                    .build();

            workers.add(w);
            runOnExecutor(w);
        } else {
            Log("did already exists, do not create worker");
        }
    }

    public void runOnExecutor(ODMWorker w) {
        mExecutorService.submit(w, null);

        //inform ui list
        Intent intent = new Intent(BROADCAST_ACTION_STARTED);
        intent.putExtra("did", w.did);
        mOdm.getContext().sendBroadcast(intent);
    }

    private ODMWorker getByDid(String did) {
        ODMWorker odmWorker = null;

        for (ODMWorker o: workers) {
            if(o.did.equals(did)) {
                odmWorker = o;
                break;
            }
        }

        return odmWorker;
    }

    public OkHttpClient getHttpClient() {
        return mHttpClient;
    }

    public ODMDatabase getDatabase() {
        return mOdmDatabase;
    }

    private void Log(String e) {
        Log.d("ODMPool", e);
    }

    public void onWorkerProgress(String did) {
        Intent intent = new Intent(BROADCAST_ACTION_PROGRESS);
        ODMWorker w = getByDid(did);
        intent.putExtra("did", did);
        intent.putExtra("progress", w.mProgress);
        intent.putExtra("dltotal", w.mDlTotal);
        mOdm.getContext().sendBroadcast(intent);
        updateNotify(did);
    }

    public void onWorkerFinished(String did) {
        Intent intent = new Intent(BROADCAST_ACTION_FINISHED);
        intent.putExtra("did", did);
        mOdm.getContext().sendBroadcast(intent);
        updateNotify(did);
        workers.remove(getByDid(did));
    }

    public void onWorkerCancelled(String did) {
        Intent intent = new Intent(BROADCAST_ACTION_CANCELLED);
        intent.putExtra("did", did);
        mOdm.getContext().sendBroadcast(intent);
        updateNotify(did);
        workers.remove(getByDid(did));
    }

    public void onWorkerError(String did) {
        Intent intent = new Intent(BROADCAST_ACTION_ERROR);
        intent.putExtra("did", did);
        mOdm.getContext().sendBroadcast(intent);
        updateNotify(did);
        workers.remove(getByDid(did));
    }

    public void onWorkerPaused(String did) {
        Intent intent = new Intent(BROADCAST_ACTION_PAUSED);
        intent.putExtra("did", did);
        mOdm.getContext().sendBroadcast(intent);
        updateNotify(did);
    }

    public void onWorkerDeleted(String did) {
        Intent intent = new Intent(BROADCAST_ACTION_DELETED);
        intent.putExtra("did", did);
        mOdm.getContext().sendBroadcast(intent);
        workers.remove(getByDid(did));
    }

    public void cancel(String did) {
        try {
            getByDid(did).cancel();
        } catch (Exception ignored) {}
    }

    public void pause(String did) {
        try {
            getByDid(did).pause();
        } catch (Exception ignored) {}
    }

    public void resume(String did) {
        try {
            getByDid(did).resume();
        } catch (Exception ignored) {}
    }

    public void downloadAgain(String did) {
        try {
            getByDid(did).downloadAgain();
        } catch (Exception ignored) {}
    }

    private void updateNotify(String did) {
        ODMWorker w = getByDid(did);

        if(w == null) {
            return;
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mOdm.getContext());
        mBuilder.setSmallIcon(R.drawable.ic_downloads);

        String title = null;
        String content = null;

        if(w.mState.equals(ODMWorker.WORKER_RUNNING)) {
            title = w.mFileName + " " + mOdm.getContext().getString(R.string.w_running);
            content = "%" + w.mProgress + " ("+ODMUtils.getSizeString(w.mDlTotal) + "/" + ODMUtils.getSizeString(w.mProgress) +")";
        }

        if(w.mState.equals(ODMWorker.WORKER_CANCELLED)) {
            title = w.mFileName;
            content = mOdm.getContext().getString(R.string.w_cancelled);
        }

        if(w.mState.equals(ODMWorker.WORKER_PAUSED)) {
            title = w.mFileName;
            content = "%" + w.mProgress +  " " + mOdm.getContext().getString(R.string.w_paused);
        }

        if(w.mState.equals(ODMWorker.WORKER_COMPLETED)) {
            title = w.mFileName;
            content = mOdm.getContext().getString(R.string.w_completed);
        }

        if(w.mState.equals(ODMWorker.WORKER_ERROR)) {
            title = w.mFileName + "";
            content = mOdm.getContext().getString(R.string.w_error);
        }

        mBuilder.setContentTitle(title);
        mBuilder.setContentText(content);
        mBuilder.setContentIntent(getPendingIntent());
        showNotify(did, mBuilder);
    }

    private NotificationManager notifyMgr() {
        return (NotificationManager) mOdm.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private PendingIntent getPendingIntent() {
        Intent resultIntent = new Intent(mOdm.getContext(), MainActivity.class);
        return PendingIntent.getActivity(
                    mOdm.getContext(),
                    0,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
    }

    private void showNotify(String did, NotificationCompat.Builder mBuilder) {
        notifyMgr().notify(getByDid(did).notiyId, mBuilder.build());
    }


    public void delete(String did) {
        ODMWorker w = getByDid(did);

        //delete it easy if exists in pool
        if(w != null) {
            w.delete();
            workers.remove(w);
        } else {
            //else delete it from db
            ContentValues c = new ContentValues();
            c.put(ODMDatabase.COLUMN_STATE, ODMWorker.WORKER_DELETED);
            getDatabase().update(did, c);
        }
    }

    public void onDestroy() {
        for (ODMWorker w: workers) {
            w.destroy();
        }
    }

}
