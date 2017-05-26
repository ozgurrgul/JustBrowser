package com.browser.odm;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import java.io.IOException;

/**
 * Created by ozgur on 05.07.2016.
 */
public class ODM {

    private Context context;
    private ODMPool odmPool;

    private static ODM ourInstance = new ODM();
    public static ODM getInstance() {
        return ourInstance;
    }
    private ODM() {}

    public void init(Context context) {
        this.context = context;
        this.odmPool = new ODMPool(this);
    }

    public Context getContext() {
        return context;
    }

    public void download(String folder, String fileName, String url) {

        String did = ODMUtils.getRandomString(10);

        if(TextUtils.isEmpty(folder)) {
            try {
                folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getCanonicalPath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(TextUtils.isEmpty(fileName)) {
            fileName = guessFileName(url);
        }

        odmPool.newWorker(did, folder, fileName, url);
    }

    public static String guessFileName(String url) {

        if(TextUtils.isEmpty(url)) {
            return "file";
        }

        String fileName = null;

        //try get &filename= from querystring
        Uri uri = null;
        try {
            uri = Uri.parse(url);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(uri != null) {

            String fName = uri.getQueryParameter("filename");
            String fName2 = uri.getQueryParameter("FileName");
            String fName3 = uri.getQueryParameter("file");

            if(!TextUtils.isEmpty(fName)) {
                fileName = fName;
            }
            else if(!TextUtils.isEmpty(fName2)) {
                fileName = fName2;
            }
            else if(!TextUtils.isEmpty(fName3)) {
                fileName = fName3;
            } else {

                if(!TextUtils.isEmpty(url) && url.contains("/")) {
                    fileName = url.substring(url.lastIndexOf('/') + 1);
                } else {
                    fileName = url;
                }
            }

        } else {
            if(!TextUtils.isEmpty(url) && url.contains("/")) {
                fileName = url.substring(url.lastIndexOf('/') + 1);
            } else {
                fileName = url;
            }
        }

        if(TextUtils.isEmpty(fileName)) {
            fileName = url;
        }

        return fileName;
    }

    public void cancel(String did) {
        odmPool.cancel(did);
    }

    public void pause(String did) {
        odmPool.pause(did);
    }

    public void resume(String did) {
        odmPool.resume(did);
    }

    public void downloadAgain(String did) {
        odmPool.downloadAgain(did);
    }

    public void delete(String did) {
        odmPool.delete(did);
    }

    public void onDestroy() {
        odmPool.onDestroy();
    }
}