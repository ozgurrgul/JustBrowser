package com.browser.odm;

import android.content.ContentValues;
import android.util.Log;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;

/**
 * Created by ozgur on 05.07.2016.
 */
public class ODMWorker implements Runnable, Callback {

    /* inital state */
    public static final String WORKER_WAITING = "WAITING";

    /* downloading */
    public static final String WORKER_RUNNING = "RUNNING";

    /* paused by user */
    public static final String WORKER_PAUSED = "PAUSED";

    /* finished */
    public static final String WORKER_COMPLETED = "COMPLETED";

    /* cancelled by user */
    public static final String WORKER_CANCELLED = "CANCELLED";

    /* cancelled by other & server error */
    public static final String WORKER_ERROR = "ERROR";

    /*deleted*/
    public static final String WORKER_DELETED = "DELETED";

    /* parent pool object to access ctx etc */
    private final ODMPool mPool;

    /* id of downloading file (did in db) */
    public final String did;

    /* downloading url */
    private final String mUrl;

    /**/
    private final String mPath;

    /* name of file */
    public final String mFileName;

    /* progress */
    public int mProgress;

    /* size of file */
    public int mFileSize;

    /* state of worker */
    public String mState;

    /* downloaded bytes so far */
    public int mDlTotal;

    /* is supports range */
    private int mCanRangeDownload = 0;

    /* http client object */
    private OkHttpClient mHttpClient;
    private Call mCall;
    private RandomAccessFile mSaveFile;
    private int lastProgress;
    public int notiyId;

    public ODMWorker(ODMWorkerBuilder builder) {
        mPool = builder.mPool;
        did = builder.did;
        mUrl = builder.mUrl;
        mPath = builder.mPath;
        mFileName = builder.mFileName;
        mFileSize = builder.mFileSize;
        mState = builder.mState;
        mHttpClient = builder.mHttpClient;
        mProgress = builder.mProgress;
        mDlTotal = builder.mDlTotal;
        mCanRangeDownload = builder.mCanRangeDownload;
        notiyId = new Random().nextInt(1000);

        Log("mPath: " + mPath);

        try {
            mSaveFile = new RandomAccessFile(mPath, "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if(mFileSize == 0 && mDlTotal == 0) {

        } else {
            try {
                mSaveFile.setLength(mFileSize);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {

        //
        Log("mState: " + mState);

        //get file ifo and update db
        if(WORKER_WAITING.equals(mState)) {
            getBaseInfo();
        }

        //run it
        if(WORKER_RUNNING.equals(mState)) {
            startDownload();
        }

        //
        Log("mState: " + mState);
    }

    public void getBaseInfo() {

        Log("in getBaseInfo()");
        /**/
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(mUrl);
        requestBuilder.header("User-Agent", Config.USER_AGENT);
        requestBuilder.header("Accept-Encoding", "identity"); // range download doesn't support gzip
        requestBuilder.header(Header.RANGE, "bytes=0-10");

        /**/
        Request request = requestBuilder.build();
        Response response = null;
        Call call = mHttpClient.newCall(request);

        try {
            response = call.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (call != null) {
            call.cancel();
        }

        /**/
        String headValue = null;
        int statusCode = 0;

        try {
            statusCode = response.code();

            if (statusCode == 206) {
                headValue = response.header(Header.CONTENT_RANGE);
                if (headValue != null) {
                    int n = headValue.indexOf('/');
                    if (n >= 0 && n < (headValue.length() - 1)) {
                        mFileSize = Integer.parseInt(headValue.substring(n + 1));
                    }
                }
            } else {
                headValue = response.header(Header.CONTENT_TRANSFER_ENCODING);
                if (headValue == null || !headValue.contains("chunked")) {
                    headValue = response.header(Header.CONTENT_LENGTH);
                    if (headValue != null) {
                        mFileSize = Integer.parseInt(headValue);
                    }
                }
            }
        } catch (Exception e) {
            // we still don't need to call the onError
            mFileSize = -1;
            e.printStackTrace();

            //
            setState(WORKER_ERROR);
        }

        if (mFileSize > 0) {
            if (response != null) {
                headValue = response.header(Header.ACCEPTRANGE);
            }

            if (headValue == null && statusCode == 206) {
                headValue = response.header(Header.CONTENT_RANGE);
            }

            if (headValue != null) {
                mCanRangeDownload = headValue.toLowerCase().contains("bytes") ? 1: 0;
            }

            try {
                mSaveFile.setLength(mFileSize);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log("mCanRangeDownload: " + mCanRangeDownload);

        //update this state and size in db etc
        if(mFileSize != -1) {
            ContentValues c = new ContentValues();
            c.put(ODMDatabase.COLUMN_FILESIZE, mFileSize);
            c.put(ODMDatabase.COLUMN_STATE, WORKER_RUNNING);
            c.put(ODMDatabase.COLUMN_CAN_RANGE_DOWNLOAD, mCanRangeDownload);
            mState = WORKER_RUNNING;
            mPool.getDatabase().update(did, c);
        }
    }

    private void startDownload() {

        Log("in startDownload()");
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(mUrl);
        requestBuilder.header("User-Agent", Config.USER_AGENT);
        requestBuilder.header("Accept-Encoding", "identity"); // range download doesn't support gzip

        Log("mCanRangeDownload: " + mCanRangeDownload);
        Log("mDlTotal: " + mDlTotal);

        if(mCanRangeDownload == 1 && mDlTotal != 0) {
            String rangeStr = "bytes=" + mDlTotal + "-";
            Log("rangeStr:"  + rangeStr);
            new File(mPath).length();
            requestBuilder.header(Header.RANGE, rangeStr);
        }

        /**/
        Request request = requestBuilder.build();
        mCall = mHttpClient.newCall(request);
        mCall.enqueue(this);
    }

    private void Log(String e) {
        Log.d("ODMWorker", e);
    }

    @Override
    public void onFailure(Request request, IOException e) {
        mPool.onWorkerError(did);
    }

    public void cancel() {
        mCall.cancel();

        try {
            mSaveFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        setState(WORKER_CANCELLED);
    }

    public void pause() {
        setState(WORKER_PAUSED);
    }

    public void destroy() {
        ContentValues c = new ContentValues();
        c.put(ODMDatabase.COLUMN_STATE, WORKER_PAUSED);
        mPool.getDatabase().update(did, c);
    }

    public void resume() {

        Log("mDlTotal in resume(): " + mDlTotal);

        try {
            mSaveFile.seek(mDlTotal);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //use range
        setState(WORKER_RUNNING);

        mPool.runOnExecutor(this);
    }

    public void downloadAgain() {
        //change state to waiting and run everything from scratch
        ContentValues c = new ContentValues();
        c.put(ODMDatabase.COLUMN_STATE, WORKER_WAITING);
        c.put(ODMDatabase.COLUMN_DL_TOTAL, 0);
        c.put(ODMDatabase.COLUMN_PROGRESS, 0);
        mPool.getDatabase().update(did, c);
        try {
            mSaveFile.seek(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mDlTotal = 0;
        mFileSize = 0;
        mProgress = 0;
        lastProgress = 0;
        mState = WORKER_WAITING;
        mPool.runOnExecutor(this);
    }

    @Override
    public void onResponse(Response response) throws IOException {

        if(response == null || response.body() == null) {
            setState(WORKER_ERROR);
            return;
        }

        final ResponseBody body = response.body();

        try {
            if (response.code() / 100 != 2) {
                body.close();
                Log("onResponse returned code: " + response.code());
                setState(WORKER_ERROR);

            } else {

                byte[] buffer = new byte[1024];
                int offset;

                while ((offset = body.byteStream().read(buffer, 0, 1024)) != -1) {

                    if(mState.equals(WORKER_CANCELLED)) {
                        break;
                    }

                    if(mState.equals(WORKER_DELETED)) {
                        break;
                    }

                    if(mState.equals(WORKER_PAUSED)) {
                        break;
                    }

                    mSaveFile.write(buffer, 0, offset);
                    mDlTotal += offset;


                    //prevent 'java.lang.ArithmeticException: divide by zero'
                    if(mFileSize != 0) {
                        mProgress = ((mDlTotal * 100) / mFileSize);
                    }


                    if(mProgress - lastProgress > 1) {
                        setState(WORKER_RUNNING);
                        lastProgress = mProgress;
                    }

                }

                /* finished */
                if (offset == -1) {
                    setState(WORKER_COMPLETED);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();

            //if we pause or cancel this, do not give error
            if(!(WORKER_PAUSED.equals(mState) || WORKER_CANCELLED.equals(mState))) {
                setState(WORKER_ERROR);
            }


        } finally {
            body.close();
        }
    }

    private void setState(String state) {
        mState = state;

        //Log("mFileName: " + mFileName + ", state: " + state + ", %" + mProgress);
        //update state in db
        ContentValues c = new ContentValues();
        c.put(ODMDatabase.COLUMN_STATE, state);

        /* if completed, update db */
        if(mState.equals(WORKER_COMPLETED)) {
            mProgress = 100;
            c.put(ODMDatabase.COLUMN_PROGRESS, mProgress);
            c.put(ODMDatabase.COLUMN_DL_TOTAL, mFileSize);
            mPool.getDatabase().update(did, c);
            mPool.onWorkerFinished(did);
        }

        /* update stuff when dl running */
        if(mState.equals(WORKER_RUNNING)) {
            c.put(ODMDatabase.COLUMN_PROGRESS, mProgress);
            c.put(ODMDatabase.COLUMN_DL_TOTAL, mDlTotal);
            mPool.getDatabase().update(did, c);
            mPool.onWorkerProgress(did);
        }

        /* error */
        if(mState.equals(WORKER_ERROR)) {
            c.put(ODMDatabase.COLUMN_PROGRESS, mProgress);
            mPool.getDatabase().update(did, c);
            mPool.onWorkerError(did);
        }

        /* error */
        if(mState.equals(WORKER_CANCELLED)) {
            c.put(ODMDatabase.COLUMN_PROGRESS, mProgress);
            c.put(ODMDatabase.COLUMN_DL_TOTAL, mDlTotal);
            mPool.getDatabase().update(did, c);
            mPool.onWorkerCancelled(did);
        }

        /* error */
        if(mState.equals(WORKER_PAUSED)) {
            c.put(ODMDatabase.COLUMN_PROGRESS, mProgress);
            c.put(ODMDatabase.COLUMN_DL_TOTAL, mDlTotal);
            mPool.getDatabase().update(did, c);
            mPool.onWorkerPaused(did);
        }

        if(mState.equals(WORKER_DELETED)) {
            mPool.getDatabase().update(did, c);
            mPool.onWorkerDeleted(did);
        }

    }

    public void delete() {
        setState(WORKER_DELETED);
    }
}

