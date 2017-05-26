package com.browser.odm;

/**
 * Created by ozgur on 08.07.2016.
 */
public class ODMItem {

    public String did;
    public String url;
    public String path;
    public String filename;
    public String state;
    public int filesize;
    public int dltotal;
    public int progress;
    public int rangesupport;

    public ODMItem (ODMWorkerBuilder odmWorkerBuilder) {
        did = odmWorkerBuilder.did;
        url = odmWorkerBuilder.mUrl;
        path = odmWorkerBuilder.mPath;
        filename = odmWorkerBuilder.mFileName;
        state = odmWorkerBuilder.mState;
        filesize = odmWorkerBuilder.mFileSize;
        dltotal = odmWorkerBuilder.mDlTotal;
        progress = odmWorkerBuilder.mProgress;
        rangesupport = odmWorkerBuilder.mCanRangeDownload;
    }
}
