package com.browser.odm;

import com.squareup.okhttp.OkHttpClient;

public class ODMWorkerBuilder {

    public ODMPool mPool;
    public String did;
    public String mUrl;
    public String mPath;
    public String mFileName;
    public int mFileSize;
    public String mState;
    public OkHttpClient mHttpClient;
    public int mProgress;
    public int mDlTotal;
    public int mCanRangeDownload;

    public ODMWorkerBuilder() {}

    public ODMWorkerBuilder setParentPool(ODMPool c) {
        mPool = c;
        return this;
    }

    public ODMWorkerBuilder setDid(String d) {
        did = d;
        return this;
    }

    public ODMWorkerBuilder setUrl(String d) {
        mUrl = d;
        return this;
    }

    public ODMWorkerBuilder setPath(String d) {
        mPath = d;
        return this;
    }

    public ODMWorkerBuilder setFileName(String d) {
        mFileName = d;
        return this;
    }

    public ODMWorkerBuilder setFileSize(int d) {
        mFileSize = d;
        return this;
    }

    public ODMWorkerBuilder setState(String d) {
        mState = d;
        return this;
    }

    public ODMWorkerBuilder setHttpClient(OkHttpClient d) {
        mHttpClient = d;
        return this;
    }

    public ODMWorkerBuilder setProgress(int p) {
        mProgress = p;
        return this;
    }

    public ODMWorker build() {
        return new ODMWorker(this);
    }

    public ODMWorkerBuilder setDlTotal(int dlTotal) {
        mDlTotal = dlTotal;
        return this;
    }

    public ODMWorkerBuilder setCanRangeDownload(int i) {
        mCanRangeDownload = i;
        return this;
    }
}